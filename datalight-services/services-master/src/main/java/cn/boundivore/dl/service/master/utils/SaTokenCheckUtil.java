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
package cn.boundivore.dl.service.master.utils;

import cn.boundivore.dl.exception.PermissionInterfaceDeniedException;
import cn.dev33.satoken.stp.StpUtil;

import java.util.List;

/**
 * Description: SaToken 扩展角色或权限检查工具类
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/10
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class SaTokenCheckUtil {

    /**
     * Description: 检查是否具备指定角色或权限
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: SaTokenException 如果用户没有相应的角色或权限
     *
     * @param roleList       角色列表
     * @param permissionList 权限列表
     */
    public static void checkRoleOrPermission(List<String> roleList, List<String> permissionList) {
        // 检查角色
        boolean hasRole = roleList.stream().anyMatch(StpUtil::hasRole);
        // 检查权限
        boolean hasPermission = permissionList.stream().anyMatch(StpUtil::hasPermission);

        // 如果用户既没有列表中的角色也没有权限，则抛出异常
        if (!hasRole && !hasPermission) {
            throw new PermissionInterfaceDeniedException("权限拒绝");
        }
    }
}
