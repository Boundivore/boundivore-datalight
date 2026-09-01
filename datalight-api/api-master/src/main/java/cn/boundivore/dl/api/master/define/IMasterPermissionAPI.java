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
package cn.boundivore.dl.api.master.define;

import cn.boundivore.dl.base.response.impl.master.AbstractRolePermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 权限相关接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：权限相关", description = "IMasterPermissionAPI")
@FeignClient(
        name = "IMasterPermissionAPI",
        contextId = "IMasterPermissionAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterPermissionAPI {

    //测试接口权限
    @GetMapping(value = "/permission/testPermissionInterface")
    @Operation(summary = "测试接口权限", description = "测试接口权限")
    Result<String> testPermissionInterface() throws Exception;

    @GetMapping(value = "/permission/getPermissionById")
    @Operation(summary = "获取当前权限详情", description = "获取当前权限详情")
    Result<AbstractRolePermissionRuleVo.PermissionRuleInterfaceDetailVo> getPermissionById(
            @Parameter(name = "PermissionId", description = "权限 Id", example = "1")
            @RequestParam(value = "PermissionId", required = true)
            Long permissionId
    ) throws Exception;

    @GetMapping(value = "/permission/getPermissionList")
    @Operation(summary = "获取所有权限列表", description = "获取所有权限列表")
    Result<AbstractRolePermissionRuleVo.PermissionListVo> getPermissionList() throws Exception;

    @GetMapping(value = "/permission/getPermissionListByUserId")
    @Operation(summary = "查询当前用户权限列表", description = "查询当前用户权限列表")
    Result<AbstractRolePermissionRuleVo.PermissionListVo> getPermissionListByUserId(
            @Parameter(name = "UserId", description = "用户 ID", example = "")
            @RequestParam(value = "UserId", required = true)
            Long userId
    ) throws Exception;

    @GetMapping(value = "/permission/getPermissionListByRoleId")
    @Operation(summary = "根据角色 ID 获取权限信息", description = "根据角色 ID 获取权限信息")
    Result<AbstractRolePermissionRuleVo.PermissionListVo> getPermissionListByRoleId(
            @Parameter(name = "RoleId", description = "角色 ID", example = "1")
            @RequestParam(value = "RoleId", required = true)
            Long roleId
    ) throws Exception;

}
