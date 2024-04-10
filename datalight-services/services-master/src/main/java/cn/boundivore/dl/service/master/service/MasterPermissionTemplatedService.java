/**
 * Copyright (C) <2023> <Boundivore> <boundivore@foxmail.com>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Apache License, Version 2.0
 * as published by the Apache Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License, Version 2.0 for more details.
 * <p>
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program; if not, you can obtain a copy at
 * http://www.apache.org/licenses/LICENSE-2.0.
 */
package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.base.enumeration.impl.PermissionTypeEnum;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlPermissionRuleRelationTemplated;
import cn.boundivore.dl.orm.po.single.TDlPermissionTemplated;
import cn.boundivore.dl.orm.po.single.TDlRuleInterfaceTemplated;
import cn.boundivore.dl.orm.service.single.impl.TDlPermissionRuleRelationTemplatedServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlPermissionTemplatedServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlRuleInterfaceTemplatedServiceImpl;
import cn.boundivore.dl.service.master.bean.PermissionTemplated;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description: 权限模板控制逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/10
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterPermissionTemplatedService {

    private final MasterPermissionHandlerService masterPermissionHandlerService;

    private final TDlPermissionTemplatedServiceImpl tDlPermissionTemplatedService;
    private final TDlRuleInterfaceTemplatedServiceImpl tDlRuleInterfaceTemplatedService;
    private final TDlPermissionRuleRelationTemplatedServiceImpl tDlPermissionRuleRelationTemplatedService;

    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @PostConstruct
    public void init() {
        this.initPermissionInDB();
    }

    /**
     * Description: 将权限相关的接口保存到数据库模板
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void initPermissionInDB() {
        // <PermissionCode,  PermissionTemplated> 获取扫描注解得到的所有接口信息
        Map<String, PermissionTemplated> permissionTemplatedMap = this.masterPermissionHandlerService.getPermissionTemplatedMap();

        if (permissionTemplatedMap.isEmpty()) {
            return;
        }

        // 清理旧的信息
        this.tDlPermissionTemplatedService.remove(new QueryWrapper<>());
        this.tDlRuleInterfaceTemplatedService.remove(new QueryWrapper<>());
        this.tDlPermissionRuleRelationTemplatedService.remove(new QueryWrapper<>());

        // 分别定义上述 3 张表的实体集合
        List<TDlPermissionTemplated> tDlPermissionTemplatedList = new ArrayList<>();
        List<TDlRuleInterfaceTemplated> tDlRuleInterfaceTemplatedList = new ArrayList<>();
        List<TDlPermissionRuleRelationTemplated> tDlPermissionRuleRelationTemplatedList = new ArrayList<>();

        final AtomicLong idCounter = new AtomicLong(0);
        permissionTemplatedMap.values()
                .stream()
                .sorted((o1, o2) -> {
                            // 条件1：比较 HTTP Method
                            int httpMethodComparison = o1.getHttpMethod().compareTo(o2.getHttpMethod());
                            if (httpMethodComparison != 0) {
                                return httpMethodComparison;
                            }
                            // 条件2：条件1相同，比较路径，忽略大小写
                            return o1.getPath().compareToIgnoreCase(o2.getPath());
                        }
                )
                .forEach(i -> {
                            /*
                                在架构设计上，一个权限可以对应多个接口规则，但是此处一个模板权限仅对应一个模板接口规则，
                                因此此处三张表各自的主键 ID，使用数值相同的 ID 值即可，降低操作复杂度，提升初始化效率。
                                如果后续有 "权限 一对多 接口规则" 的情况，可修改下述代码逻辑。
                            */
                            Long id = idCounter.incrementAndGet();

                            // 添加到集合用于后续批量保存
                            tDlPermissionTemplatedList.add(this.getTDlNewPermissionTemplated(id, i));
                            tDlRuleInterfaceTemplatedList.add(this.getTDlRuleInterfaceTemplated(id, i));
                            tDlPermissionRuleRelationTemplatedList.add(this.getTDlRuleInterfaceTemplated(id, id, id, null));
                        }
                );


        // 根据扫描信息批量保存到表 t_dl_permission_templated
        Assert.isTrue(
                this.tDlPermissionTemplatedService.saveBatch(tDlPermissionTemplatedList),
                () -> new DatabaseException("更新表 t_dl_permission_templated 失败")
        );

        // 根据扫描信息批量保存到表 t_dl_rule_interface_templated
        Assert.isTrue(
                this.tDlRuleInterfaceTemplatedService.saveBatch(tDlRuleInterfaceTemplatedList),
                () -> new DatabaseException("更新表 t_dl_rule_interface_templated 失败")
        );

        // 关联上述两表的信息，批量保存到表: t_dl_permission_rule_relation_templated
        Assert.isTrue(
                this.tDlPermissionRuleRelationTemplatedService.saveBatch(tDlPermissionRuleRelationTemplatedList),
                () -> new DatabaseException("更新表 t_dl_permission_rule_relation_templated 失败")
        );
    }

    /**
     * Description: 根据扫描信息保存到表 t_dl_permission_templated
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param id                      主键 ID
     * @param permissionTemplatedBean 接口信息 Bean
     * @return TDlPermissionTemplated 数据库实体
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public TDlPermissionTemplated getTDlNewPermissionTemplated(Long id, PermissionTemplated permissionTemplatedBean) {
        TDlPermissionTemplated tDlPermissionTemplated = new TDlPermissionTemplated();
        tDlPermissionTemplated.setId(id);
        tDlPermissionTemplated.setVersion(0L);
        tDlPermissionTemplated.setIsDeleted(false);
        tDlPermissionTemplated.setEnabled(true);
        tDlPermissionTemplated.setPermissionCode(permissionTemplatedBean.getCode());
        tDlPermissionTemplated.setPermissionName(permissionTemplatedBean.getName());
        tDlPermissionTemplated.setPermissionType(PermissionTypeEnum.PERMISSION_INTERFACE);
        tDlPermissionTemplated.setRejectPermissionCode(null);
        tDlPermissionTemplated.setRejectPermissionCode(null);
        tDlPermissionTemplated.setPermissionWeight(1L);
        tDlPermissionTemplated.setPermissionComment(null);

        return tDlPermissionTemplated;

    }

    /**
     * Description: 根据扫描信息保存到表 t_dl_permission_templated
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param id                      主键 ID
     * @param permissionTemplatedBean 接口信息 Bean
     * @return TDlRuleInterfaceTemplated 数据库实体
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public TDlRuleInterfaceTemplated getTDlRuleInterfaceTemplated(Long id, PermissionTemplated permissionTemplatedBean) {
        TDlRuleInterfaceTemplated tDlRuleInterfaceTemplated = new TDlRuleInterfaceTemplated();
        tDlRuleInterfaceTemplated.setId(id);
        tDlRuleInterfaceTemplated.setVersion(0L);
        tDlRuleInterfaceTemplated.setRuleInterfaceUri(permissionTemplatedBean.getPath());
        tDlRuleInterfaceTemplated.setRuleInterfaceMethod(permissionTemplatedBean.getHttpMethod().name());

        return tDlRuleInterfaceTemplated;

    }

    /**
     * Description: 根据扫描信息保存到表 t_dl_permission_templated
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param id              主键 ID
     * @param permissionId    权限模板主键 ID
     * @param ruleInterfaceId 接口规则模板主键 ID
     * @param pageId          前端页面规则主键 ID
     * @return TDlRuleInterfaceTemplated 数据库实体
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public TDlPermissionRuleRelationTemplated getTDlRuleInterfaceTemplated(Long id,
                                                                           Long permissionId,
                                                                           Long ruleInterfaceId,
                                                                           Long pageId) {

        TDlPermissionRuleRelationTemplated tDlPermissionRuleRelationTemplated = new TDlPermissionRuleRelationTemplated();
        tDlPermissionRuleRelationTemplated.setId(id);
        tDlPermissionRuleRelationTemplated.setVersion(0L);
        tDlPermissionRuleRelationTemplated.setPermissionId(permissionId);
        tDlPermissionRuleRelationTemplated.setRuleInterfaceId(ruleInterfaceId);
        tDlPermissionRuleRelationTemplated.setRulePageId(pageId);

        return tDlPermissionRuleRelationTemplated;

    }
}
