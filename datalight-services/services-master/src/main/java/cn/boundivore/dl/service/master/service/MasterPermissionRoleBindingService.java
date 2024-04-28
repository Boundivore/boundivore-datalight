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
import cn.boundivore.dl.base.request.impl.master.AbstractPermissionRuleRequest;
import cn.boundivore.dl.base.request.impl.master.AbstractRoleRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.lock.LocalLock;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlPermission;
import cn.boundivore.dl.orm.po.single.TDlPermissionRoleRelation;
import cn.boundivore.dl.orm.po.single.TDlRole;
import cn.boundivore.dl.orm.service.single.impl.TDlPermissionRoleRelationServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlPermissionServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlRoleServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 角色与权限绑定关系操作逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterPermissionRoleBindingService {

    private final TDlPermissionRoleRelationServiceImpl tDlPermissionRoleRelationService;

    private final TDlRoleServiceImpl tDlRoleService;

    private final TDlPermissionServiceImpl tDlPermissionService;

    /**
     * Description: 绑定权限到指定角色
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 权限与角色映射关系列表
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @LocalLock
    public Result<String> attachPermissionRoleByPermissionRoleId(AbstractPermissionRuleRequest.PermissionRoleIdListRequest request) {

        // 检查是否存在指定权限及角色 ID
        List<Long> roleIdList = request.getPermissionRoleIdList()
                .stream()
                .map(AbstractPermissionRuleRequest.PermissionRoleIdRequest::getRoleId)
                .distinct()
                .collect(Collectors.toList());

        List<TDlRole> tDlRoleList = this.tDlRoleService.lambdaQuery()
                .select()
                .in(TBasePo::getId, roleIdList)
                .list();

        Assert.isTrue(
                roleIdList.size() == tDlRoleList.size(),
                () -> new BException("部分角色 ID 不存在")
        );

        List<Long> permissionIdList = request.getPermissionRoleIdList()
                .stream()
                .map(AbstractPermissionRuleRequest.PermissionRoleIdRequest::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        List<TDlPermission> tDlPermissionList = this.tDlPermissionService.lambdaQuery()
                .select()
                .in(TBasePo::getId, permissionIdList)
                .list();

        Assert.isTrue(
                permissionIdList.size() == tDlPermissionList.size(),
                () -> new BException("部分权限 ID 不存在")
        );

        request.getPermissionRoleIdList()
                .forEach(i -> {
                            TDlPermissionRoleRelation tDlPermissionRoleRelation = this.tDlPermissionRoleRelationService.lambdaQuery()
                                    .select()
                                    .eq(TDlPermissionRoleRelation::getRoleId, i.getRoleId())
                                    .eq(TDlPermissionRoleRelation::getPermissionId, i.getPermissionId())
                                    .one();

                            if (tDlPermissionRoleRelation == null) {
                                tDlPermissionRoleRelation = new TDlPermissionRoleRelation();
                                tDlPermissionRoleRelation.setVersion(0L);

                                tDlPermissionRoleRelation.setRoleId(i.getRoleId());
                                tDlPermissionRoleRelation.setPermissionId(i.getPermissionId());

                                Assert.isTrue(
                                        this.tDlPermissionRoleRelationService.save(tDlPermissionRoleRelation),
                                        () -> new DatabaseException("保存到数据库失败")
                                );
                            }

                        }
                );


        return Result.success();
    }

    /**
     * Description: 移除权限与角色的绑定关系
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 权限与角色映射关系列表
     * @return Result<String> 成功或失败
     */
    @LocalLock
    public Result<String> detachPermissionRoleByPermissionRoleId(AbstractPermissionRuleRequest.PermissionRoleIdListRequest request) {

        List<Long> permissionIdList = request.getPermissionRoleIdList()
                .stream()
                .map(AbstractPermissionRuleRequest.PermissionRoleIdRequest::getPermissionId)
                .collect(Collectors.toList());

        List<Long> roleIdList = request.getPermissionRoleIdList()
                .stream()
                .map(AbstractPermissionRuleRequest.PermissionRoleIdRequest::getRoleId)
                .collect(Collectors.toList());

        List<TDlPermissionRoleRelation> tDlPermissionRoleRelationList = this.tDlPermissionRoleRelationService.lambdaQuery()
                .select()
                .in(TDlPermissionRoleRelation::getPermissionId, permissionIdList)
                .in(TDlPermissionRoleRelation::getRoleId, roleIdList)
                .list();

        // 检查是否存在指定映射关系
        Assert.isTrue(
                tDlPermissionRoleRelationList.size() == roleIdList.size(),
                () -> new BException("参数中包含不存在的角色权限映射关系")
        );

        // 移除权限与角色的绑定关系
        Assert.isTrue(
                this.tDlPermissionRoleRelationService.removeBatchByIds(tDlPermissionRoleRelationList),
                () -> new DatabaseException("移除权限角色映射关系失败")
        );

        return Result.success();
    }

    /**
     * Description: 移除权限与角色的绑定关系
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 角色 ID 列表
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> detachPermissionRoleByRoleId(AbstractRoleRequest.RoleIdListRequest request) {

        List<TDlPermissionRoleRelation> tDlPermissionRoleRelationList = this.tDlPermissionRoleRelationService.lambdaQuery()
                .select()
                .in(TDlPermissionRoleRelation::getRoleId, request.getRoleIdList())
                .list();

        if (CollUtil.isNotEmpty(tDlPermissionRoleRelationList)) {
            Assert.isTrue(
                    this.tDlPermissionRoleRelationService.removeBatchByIds(tDlPermissionRoleRelationList),
                    () -> new DatabaseException("移除权限角色绑定关系失败")
            );
        }
        return Result.success();
    }
}
