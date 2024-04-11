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
import cn.boundivore.dl.base.enumeration.impl.RoleTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.StaticRoleTypeEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractRoleRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractRolePermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlRole;
import cn.boundivore.dl.orm.po.single.TDlRoleUserRelation;
import cn.boundivore.dl.orm.service.single.impl.TDlRoleServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlRoleUserRelationServiceImpl;
import cn.boundivore.dl.service.master.converter.IRoleConverter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 角色操作相关逻辑
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
public class MasterRoleService {


    private final TDlRoleServiceImpl tDlRoleService;

    private final TDlRoleUserRelationServiceImpl tDlRoleUserRelationService;

    private final IRoleConverter iRoleConverter;

    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @PostConstruct
    public void init() {
        this.checkStaticRole();
    }

    /**
     * Description: 检查系统默认静态角色，如不存在，则添加
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void checkStaticRole() {
        // 检查数据库中是否存在超级角色
        TDlRole adminRole = this.tDlRoleService.lambdaQuery()
                .select()
                .eq(TDlRole::getRoleType, RoleTypeEnum.ROLE_STATIC)
                .eq(TDlRole::getRoleName, StaticRoleTypeEnum.ADMIN.name())
                .one();

        // 如果不存在，则创建
        if (adminRole == null) {
            adminRole = new TDlRole();
            adminRole.setId(1L);
            adminRole.setVersion(0L);
            adminRole.setIsDeleted(false);
            adminRole.setEditEnabled(false);
            adminRole.setRoleName(StaticRoleTypeEnum.ADMIN.name());
            adminRole.setRoleCode("ROLE00001");
            adminRole.setEnabled(true);
            adminRole.setRoleType(RoleTypeEnum.ROLE_STATIC);
            adminRole.setRoleComment("内置超级角色，不可编辑，不可删除");

            Assert.isTrue(
                    this.tDlRoleService.save(adminRole),
                    () -> new DatabaseException("保存超级角色到数据库失败")
            );
        }

        // 检查超级角色是否存在错误的标记
        if (adminRole.getIsDeleted() || adminRole.getEditEnabled()) {
            adminRole.setIsDeleted(false);
            adminRole.setEditEnabled(false);
            Assert.isTrue(
                    this.tDlRoleService.updateById(adminRole),
                    () -> new DatabaseException("更新超级角色删除标记失败")
            );
        }

        // 如果存在超级角色，则检查是否关联了超级用户
        TDlRoleUserRelation tDlRoleUserRelation = this.tDlRoleUserRelationService.lambdaQuery()
                .select()
                .eq(TDlRoleUserRelation::getRoleId, 1L)
                .eq(TDlRoleUserRelation::getUserId, 1L)
                .one();

        // 如果没有关联，则关联
        if (tDlRoleUserRelation == null) {
            tDlRoleUserRelation = new TDlRoleUserRelation();
            tDlRoleUserRelation.setId(1L);
            tDlRoleUserRelation.setVersion(0L);
            tDlRoleUserRelation.setRoleId(1L);
            tDlRoleUserRelation.setUserId(1L);

            Assert.isTrue(
                    this.tDlRoleUserRelationService.save(tDlRoleUserRelation),
                    () -> new DatabaseException("保存超级角色用户关联到数据库失败")
            );
        }
    }

    /**
     * Description: 创建一个新的角色
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 新建角色信息请求体
     * @return Result<AbstractRolePermissionRuleVo.RoleVo> 角色详细信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<AbstractRolePermissionRuleVo.RoleVo> newRole(AbstractRoleRequest.NewRoleRequest request) {
        IdUtil.fastSimpleUUID();
        // 检查角色名称是否重复

        // 入库

        return null;
    }

    /**
     * Description: 通过角色 ID 获取角色信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param roleId 角色 ID
     * @return Result<AbstractRolePermissionRuleVo.RoleVo> 角色详细信息
     */
    public Result<AbstractRolePermissionRuleVo.RoleVo> getRoleById(Long roleId) {
        TDlRole tDlRole = this.tDlRoleService.getById(roleId);
        Assert.notNull(
                tDlRole,
                () -> new BException("不存在的角色 ID")
        );

        return Result.success(
                this.iRoleConverter.convert2RoleVo(tDlRole)
        );
    }

    /**
     * Description: 通过用户 ID 获取角色信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param userId 用户 ID
     * @return Result<AbstractRolePermissionRuleVo.RoleListVo> 角色信息列表
     */
    public Result<AbstractRolePermissionRuleVo.RoleListVo> getRoleListByUserId(Long userId) {
        AbstractRolePermissionRuleVo.RoleListVo roleListVo = new AbstractRolePermissionRuleVo.RoleListVo();


        List<Long> roleIdList = this.tDlRoleUserRelationService.lambdaQuery()
                .select()
                .eq(TDlRoleUserRelation::getUserId, userId)
                .list()
                .stream()
                .map(TDlRoleUserRelation::getRoleId)
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(roleIdList)) {
            roleListVo.setRoleList(new ArrayList<>());
        } else {
            roleListVo.setRoleList(this.tDlRoleService.lambdaQuery()
                    .select()
                    .in(TBasePo::getId, roleIdList)
                    .eq(TDlRole::getEnabled, true)
                    .eq(TDlRole::getIsDeleted, false)
                    .list()
                    .stream()
                    .map(this.iRoleConverter::convert2RoleVo)
                    .collect(Collectors.toList()));
        }

        return Result.success(roleListVo);
    }

    /**
     * Description: 通过角色 ID 列表批量删除角色
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/11
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
    public Result<String> removeRoleBatchByIdList(AbstractRoleRequest.RoleIdListRequest request) {
        // 检查是否存在关联的用户

        // 删除角色

        // 调用 MasterPermissionService 中的函数删除可能关联的权限

        return Result.success();
    }
}
