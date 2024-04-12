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
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlPermissionRoleRelation;
import cn.boundivore.dl.orm.service.single.impl.TDlPermissionRoleRelationServiceImpl;
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
    public Result<String> attachPermissionRoleByPermissionRoleId(AbstractPermissionRuleRequest.PermissionRoleIdListRequest request) {

        // 检查是否存在指定权限及角色 ID

        // 幂等性提交绑定关系


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

        Assert.isTrue(
                this.tDlPermissionRoleRelationService.removeBatchByIds(tDlPermissionRoleRelationList),
                () -> new DatabaseException("移除权限角色绑定关系失败")
        );


        return Result.success();
    }
}
