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
import cn.boundivore.dl.base.response.impl.master.AbstractRolePermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlPermission;
import cn.boundivore.dl.orm.po.single.TDlRuleInterface;
import cn.boundivore.dl.orm.service.single.impl.TDlPermissionServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlRuleInterfaceServiceImpl;
import cn.boundivore.dl.service.master.bean.PermissionBean;
import cn.boundivore.dl.service.master.converter.IPermissionRuleConverter;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 权限管理相关
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterPermissionService {

    private final TDlPermissionServiceImpl tDlPermissionService;
    private final TDlRuleInterfaceServiceImpl tDlRuleInterfaceService;

    private final MasterPermissionHandlerService masterPermissionHandlerService;

    private final IPermissionRuleConverter iPermissionRuleConverter;


    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @PostConstruct
    public void init() {
        this.initPermissionInDB();
    }

    /**
     * Description: 初始化数据库中的权限信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     * - DatabaseException: 当更新表 t_dl_permission_templated 或 t_dl_rule_interface_templated 失败时抛出
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void initPermissionInDB() {
        // <PermissionCode, PermissionBean> 获取扫描注解得到的所有接口信息
        Map<String, PermissionBean> permissionBeanMap = masterPermissionHandlerService.getPermissionBeanMap();

        if (permissionBeanMap.isEmpty()) {
            return;
        }

        // <PermissionCode, TDlPermission>
        Map<String, TDlPermission> tDlPermissionCodeMap = getTDlPermissionCodeMap();

        // <TDlRuleInterfaceId, TDlRuleInterface>
        Map<Long, TDlRuleInterface> tDlRuleInterfaceIdMap = getTDlRuleInterfaceIdMap();

        // 分别定义上述 2 张表的实体集合
        List<TDlPermission> tDlPermissionList = new ArrayList<>();
        List<TDlRuleInterface> tDlRuleInterfaceList = new ArrayList<>();

        // 原则：有则更新，无则添加
        permissionBeanMap.values().stream()
                .sorted(
                        Comparator
                                .comparing(PermissionBean::getHttpMethod)
                                .thenComparing(PermissionBean::getPath, String.CASE_INSENSITIVE_ORDER)
                )
                .forEach(permissionBean -> {

                    TDlPermission tDlPermission = Optional
                            .ofNullable(tDlPermissionCodeMap.get(permissionBean.getCode()))
                            .orElseGet(() -> createInterfaceTDlPermission(permissionBean));

                    TDlRuleInterface tDlRuleInterface = Optional
                            .ofNullable(tDlRuleInterfaceIdMap.get(tDlPermission.getRuleId()))
                            .orElseGet(() -> createTDlRuleInterface(permissionBean));

                    // 设置权限与规则关联 ID
                    tDlPermission.setRuleId(tDlRuleInterface.getId());
                    // 防止之前被删除的权限，本次又重新添加后，删除标记无法变更的问题
                    tDlPermission.setIsDeleted(false);

                    tDlPermissionList.add(tDlPermission);
                    tDlRuleInterfaceList.add(tDlRuleInterface);
                });

        // 最终检查：如果原有的旧数据中存在不包含的接口，则将该条权限标记为删除，反之标记为未删除，一共页面查看知晓确认，并通过手动删除
        tDlPermissionCodeMap.forEach((permissionCode, tDlPermission) -> {
            if (!permissionBeanMap.containsKey(permissionCode)) {
                tDlPermission.setIsDeleted(true);
                tDlPermissionList.add(tDlPermission);
                log.info(
                        "检测到废弃权限，请前往平台页面确认后手动删除: Name: {}",
                        tDlPermission.getPermissionName()
                );
            }
        });

        // 根据扫描信息批量保存到表 t_dl_permission_templated
        Assert.isTrue(
                tDlPermissionService.saveOrUpdateBatch(tDlPermissionList),
                () -> new DatabaseException("更新表 t_dl_permission_templated 失败")
        );

        // 根据扫描信息批量保存到表 t_dl_rule_interface_templated
        Assert.isTrue(
                tDlRuleInterfaceService.saveOrUpdateBatch(tDlRuleInterfaceList),
                () -> new DatabaseException("更新表 t_dl_rule_interface_templated 失败")
        );
    }

    /**
     * Description: 创建 TDlPermission 对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    private TDlPermission createInterfaceTDlPermission(PermissionBean permissionBean) {
        TDlPermission tDlPermission = new TDlPermission();
        tDlPermission.setId(IdWorker.getId());
        tDlPermission.setVersion(0L);
        tDlPermission.setIsDeleted(false);
        tDlPermission.setEnabled(true);
        tDlPermission.setPermissionCode(permissionBean.getCode());
        tDlPermission.setPermissionName(permissionBean.getName());
        tDlPermission.setPermissionType(PermissionTypeEnum.PERMISSION_INTERFACE);
        tDlPermission.setRejectPermissionCode(null);
        tDlPermission.setPermissionWeight(1L);
        tDlPermission.setPermissionComment(null);
        return tDlPermission;
    }

    /**
     * Description: 创建 TDlRuleInterface 对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    private TDlRuleInterface createTDlRuleInterface(PermissionBean permissionBean) {
        TDlRuleInterface tDlRuleInterface = new TDlRuleInterface();
        tDlRuleInterface.setId(IdWorker.getId());
        tDlRuleInterface.setVersion(0L);
        tDlRuleInterface.setRuleInterfaceUri(permissionBean.getPath());
        tDlRuleInterface.setRuleInterfaceMethod(permissionBean.getHttpMethod().name());
        return tDlRuleInterface;
    }


    /**
     * Description: 将 TDlPermission 列表转换为 <PermissionCode, TDlPermission> 映射
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 转换后的 <PermissionCode, TDlPermission> 映射
     */
    private Map<String, TDlPermission> getTDlPermissionCodeMap() {
        List<TDlPermission> tDlPermissionList = this.tDlPermissionService.list();
        return tDlPermissionList.stream()
                .collect(Collectors.toMap(TDlPermission::getPermissionCode, p -> p));
    }

    /**
     * Description: 将 TDlRuleInterface 列表转换为 <TDlRuleInterfaceId, TDlRuleInterface> 映射
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 转换后的 <TDlRuleInterfaceId, TDlRuleInterface> 映射
     */
    private Map<Long, TDlRuleInterface> getTDlRuleInterfaceIdMap() {
        List<TDlRuleInterface> tDlRuleInterfaceList = this.tDlRuleInterfaceService.list();
        return tDlRuleInterfaceList.stream()
                .collect(Collectors.toMap(TDlRuleInterface::getId, r -> r));
    }


    /**
     * Description: 测试接口权限
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 通过则返回成功
     */
    public Result<String> testPermissionInterface() {
        return Result.success();
    }

    /**
     * Description: 获取当前权限详情
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param permissionId 权限 ID
     * @return Result<AbstractRolePermissionRuleVo.PermissionRuleInterfaceDetailVo> 指定权限详情
     */
    public Result<AbstractRolePermissionRuleVo.PermissionRuleInterfaceDetailVo> permissionDetails(Long permissionId) {
        TDlPermission tDlPermission = this.tDlPermissionService.getById(permissionId);
        Assert.notNull(
                tDlPermission,
                () -> new BException("不存在的权限 ID")
        );

        TDlRuleInterface tDlRuleInterface = this.tDlRuleInterfaceService.getById(tDlPermission.getRuleId());
        Assert.notNull(
                tDlRuleInterface,
                () -> new BException("不存在的权限规则关联")
        );

        return Result.success(
                new AbstractRolePermissionRuleVo.PermissionRuleInterfaceDetailVo(
                        this.iPermissionRuleConverter.convert2PermissionVo(tDlPermission),
                        this.iPermissionRuleConverter.convert2RuleInterfaceVo(tDlRuleInterface)
                )
        );
    }

    /**
     * Description: 查询当前用户接口权限列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param userId           用户 ID
     * @param ruleInterfaceUri 规则接口 URI
     * @return Result<AbstractRolePermissionRuleVo.PermissionRuleInterfaceListVo> 接口权限规则列表
     */
    public Result<AbstractRolePermissionRuleVo.PermissionRuleInterfaceListVo> listPermissionRuleInterface(Long userId,
                                                                                                          String ruleInterfaceUri) {
        return null;
    }

    /**
     * Description: 根据角色 ID 获取权限信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param roleId 角色 ID
     * @return Result<AbstractRolePermissionRuleVo.PermissionRuleInterfaceListVo> 该角色下所有权限以及接口规则信息
     */
    public Result<AbstractRolePermissionRuleVo.PermissionRuleInterfaceListVo> listPermissionByRoleId(Long roleId) {
        return null;
    }
}
