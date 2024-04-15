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

import cn.boundivore.dl.base.response.impl.master.AbstractRolePermissionRuleVo;
import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: SaToken 角色权限信息获取回调服务
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SaTokenRolePermissionCallbackService implements StpInterface {

    private final MasterRoleService masterRoleService;

    private final MasterPermissionService masterPermissionService;

    /**
     * Description: 获取当前登录用户的权限内容列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param loginId   账号 ID
     * @param loginType 账号类型
     * @return List<String> 权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> permissionCodeList = this.masterPermissionService.getPermissionListByUserId(
                        Long.parseLong(loginId.toString())
                ).getData()
                .getPermissionList()
                .stream()
                .map(AbstractRolePermissionRuleVo.PermissionVo::getPermissionCode)
                .collect(Collectors.toList());

        if (log.isDebugEnabled()) {
            log.debug(
                    "用户: {}, 绑定权限: {}",
                    loginId,
                    permissionCodeList
            );
        }

        return permissionCodeList;
    }

    /**
     * Description: 获取当前登录用户的角色内容列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param loginId   账号 ID
     * @param loginType 账号类型
     * @return List<String>角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roleNameList = this.masterRoleService.getRoleListByUserId(
                        Long.parseLong(loginId.toString())
                ).getData()
                .getRoleList()
                .stream()
                .map(AbstractRolePermissionRuleVo.RoleVo::getRoleName)
                .collect(Collectors.toList());

        if (log.isDebugEnabled()) {
            log.debug(
                    "用户: {}, 绑定角色: {}",
                    loginId,
                    roleNameList
            );
        }

        return roleNameList;

    }

}
