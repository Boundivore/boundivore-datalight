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

import cn.boundivore.dl.base.request.impl.master.AbstractRoleRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractRolePermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 角色相关接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：角色相关", description = "IMasterRoleAPI")
@FeignClient(
        name = "IMasterRoleAPI",
        contextId = "IMasterRoleAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterRoleAPI {

    @PostMapping(value = "/role/newRole")
    @Operation(summary = "新建角色", description = "新建角色")
    Result<AbstractRolePermissionRuleVo.RoleVo> newRole(
            @RequestBody
            @Valid
            AbstractRoleRequest.NewRoleRequest request
    ) throws Exception;

    @PostMapping(value = "/role/switchRoleEnabled")
    @Operation(summary = "切换角色是否启用", description = "切换角色是否启用")
    Result<AbstractRolePermissionRuleVo.RoleVo> switchRoleEnabled(
            @RequestBody
            @Valid
            AbstractRoleRequest.SwitchRoleEnabledRequest request
    ) throws Exception;

    @GetMapping(value = "/role/getRoleById")
    @Operation(summary = "根据角色 ID 获取角色信息", description = "根据角色 ID 获取角色信息")
    Result<AbstractRolePermissionRuleVo.RoleVo> getRoleById(
            @Parameter(name = "RoleId", description = "RoleId")
            @RequestParam(value = "RoleId", required = true)
            Long roleId
    ) throws Exception;

    @GetMapping(value = "/role/getRoleList")
    @Operation(summary = "获取角色信息列表", description = "获取角色信息列表")
    Result<AbstractRolePermissionRuleVo.RoleListVo> getRoleList() throws Exception;


    @GetMapping(value = "/role/getRoleListByUserId")
    @Operation(summary = "根据用户 ID 获取角色信息列表", description = "根据用户 ID 获取角色信息列表")
    Result<AbstractRolePermissionRuleVo.RoleListVo> getRoleListByUserId(
            @Parameter(name = "UserId", description = "UserId")
            @RequestParam(value = "UserId", required = true)
            Long userId
    ) throws Exception;


    @PostMapping(value = "/role/removeRoleBatchByIdList")
    @Operation(summary = "根据角色 ID 列表移除角色", description = "根据角色 ID 列表移除角色")
    Result<String> removeRoleBatchByIdList(
            @RequestBody
            @Valid
            AbstractRoleRequest.RoleIdListRequest request
    ) throws Exception;

}
