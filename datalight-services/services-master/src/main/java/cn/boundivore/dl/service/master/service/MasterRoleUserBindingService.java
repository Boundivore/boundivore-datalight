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
import cn.boundivore.dl.base.request.impl.master.AbstractRoleRequest;
import cn.boundivore.dl.base.request.impl.master.AbstractUserRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlRole;
import cn.boundivore.dl.orm.po.single.TDlRoleUserRelation;
import cn.boundivore.dl.orm.po.single.TDlUser;
import cn.boundivore.dl.orm.service.single.impl.TDlRoleServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlRoleUserRelationServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlUserServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 角色与用户绑定关系操作逻辑
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
public class MasterRoleUserBindingService {

    private final TDlUserServiceImpl tDlUserService;

    private final TDlRoleServiceImpl tDlRoleService;

    private final TDlRoleUserRelationServiceImpl tDlRoleUserRelationService;


    /**
     * Description: 绑定角色与用户的绑定关系
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 角色用户映射列表
     * @return Result<String>成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> attachRoleUserByRoleUserId(AbstractRoleRequest.RoleUserIdListRequest request) {

        // 1. 检查将要绑定的用户 ID 是否均有效存在
        List<Long> userIdDistinctList = request.getRoleUserList()
                .stream()
                .map(AbstractRoleRequest.RoleUserIdRequest::getUserId)
                .distinct()
                .collect(Collectors.toList());

        List<TDlUser> tDlUserList = this.tDlUserService.lambdaQuery()
                .select()
                .in(TBasePo::getId, userIdDistinctList)
                .list();

        Assert.isTrue(
                userIdDistinctList.size() == tDlUserList.size(),
                () -> new BException("参数中包含无效的用户 ID")
        );

        // 2. 检查将要绑定的角色 ID 是否均有效存在
        List<Long> roleIdDistinctList = request.getRoleUserList()
                .stream()
                .map(AbstractRoleRequest.RoleUserIdRequest::getRoleId)
                .distinct()
                .collect(Collectors.toList());

        List<TDlRole> tDlRoleList = this.tDlRoleService.lambdaQuery()
                .select()
                .in(TBasePo::getId, roleIdDistinctList)
                .eq(TDlRole::getIsDeleted, false)
                .list();

        Assert.isTrue(
                roleIdDistinctList.size() == tDlRoleList.size(),
                () -> new BException("参数中包含无效的角色 ID")
        );


        // 3. 检查是否已经存在绑定关系
        @AllArgsConstructor
        @EqualsAndHashCode
        class RoleUserId {
            public Long roleId;
            public Long userId;
        }

        List<RoleUserId> oldRoleUserIdList = this.tDlRoleUserRelationService.lambdaQuery()
                .select()
                .in(TDlRoleUserRelation::getRoleId, roleIdDistinctList)
                .in(TDlRoleUserRelation::getUserId, userIdDistinctList)
                .list()
                .stream()
                .map(i -> new RoleUserId(i.getRoleId(), i.getUserId()))
                .collect(Collectors.toList());

        List<RoleUserId> newRoleUserIdList = request.getRoleUserList()
                .stream()
                .map(i -> new RoleUserId(i.getRoleId(), i.getUserId()))
                .collect(Collectors.toList());

        boolean hasIntersection = !Collections.disjoint(
                new HashSet<>(oldRoleUserIdList),
                new HashSet<>(newRoleUserIdList)
        );

        Assert.isFalse(
                hasIntersection,
                () -> new BException("参数中包含已经绑定的用户角色关系")
        );

        // 执行绑定
        List<TDlRoleUserRelation> newTDlRoleUserRelationList = request.getRoleUserList()
                .stream()
                .map(i -> new RoleUserId(i.getRoleId(), i.getUserId()))
                .distinct()
                .map(i -> {
                    TDlRoleUserRelation tDlRoleUserRelation = new TDlRoleUserRelation();
                    tDlRoleUserRelation.setRoleId(i.roleId);
                    tDlRoleUserRelation.setUserId(i.userId);

                    return tDlRoleUserRelation;
                })
                .collect(Collectors.toList());

        Assert.isTrue(
                this.tDlRoleUserRelationService.saveBatch(newTDlRoleUserRelationList),
                () -> new DatabaseException("绑定角色到用户失败")
        );

        return Result.success();
    }

    /**
     * Description: 解除角色与用户的绑定关系
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 角色用户映射列表
     * @return Result<String>成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> detachRoleUserByUserId(AbstractRoleRequest.RoleUserIdListRequest request) {

        // 检查是否为解除超级用户的绑定关系，如果是，则抛出异常（不允许解除超级用户与超级角色的绑定关系）
        request.getRoleUserList().forEach(i -> {
            Assert.isFalse(
                    i.getUserId() == 1L && i.getRoleId() == 1L,
                    () -> new BException("ADMIN 角色不允许与 admin 用户解绑")
            );
        });

        List<Long> roleIdList = request.getRoleUserList()
                .stream()
                .map(AbstractRoleRequest.RoleUserIdRequest::getRoleId)
                .collect(Collectors.toList());

        List<Long> userIdList = request.getRoleUserList()
                .stream()
                .map(AbstractRoleRequest.RoleUserIdRequest::getUserId)
                .collect(Collectors.toList());

        List<TDlRoleUserRelation> tDlRoleUserRelationList = this.tDlRoleUserRelationService.lambdaQuery()
                .select()
                .in(TDlRoleUserRelation::getRoleId, roleIdList)
                .in(TDlRoleUserRelation::getUserId, userIdList)
                .list();

        Assert.notEmpty(
                tDlRoleUserRelationList,
                () -> new BException("未找到对应绑定关系")
        );


        // 检查是否存在指定映射关系
        Assert.isTrue(
                tDlRoleUserRelationList.size() == roleIdList.size(),
                () -> new BException("参数中包含不存在的角色用户映射关系")
        );

        // 移除角色与用户的绑定关系
        Assert.isTrue(
                this.tDlRoleUserRelationService.removeBatchByIds(tDlRoleUserRelationList),
                () -> new DatabaseException("移除角色用户映射关系失败")
        );

        return Result.success();
    }

    /**
     * Description: 根据用户 ID 解绑其下所有绑定的角色
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 用户 ID 请求体
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> detachRoleUser(AbstractUserRequest.UserIdListRequest request) {

        // 检查是否为解除超级用户的绑定关系，如果是，则抛出异常（不允许解除超级用户与超级角色的绑定关系）
        request.getUserIdList().forEach(userId -> {
            Assert.isTrue(
                    userId != 1L,
                    () -> new BException("ADMIN 角色不允许与 admin 用户解绑")
            );
        });

        List<TDlRoleUserRelation> tDlRoleUserRelationList = this.tDlRoleUserRelationService.lambdaQuery()
                .select()
                .in(TDlRoleUserRelation::getUserId, request.getUserIdList())
                .list();

        if (CollUtil.isNotEmpty(tDlRoleUserRelationList)) {
            // 移除角色与用户的绑定关系
            Assert.isTrue(
                    this.tDlRoleUserRelationService.removeBatchByIds(tDlRoleUserRelationList),
                    () -> new DatabaseException("移除角色用户映射关系失败")
            );
        }


        return Result.success();
    }


}
