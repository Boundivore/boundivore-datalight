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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

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
@Api(value = "IMasterRoleAPI", tags = {"Master 接口：角色相关"})
@FeignClient(
        name = "IMasterRoleAPI",
        contextId = "IMasterRoleAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterRoleAPI {

    @PostMapping(value = "/role/newRole")
    @ApiOperation(notes = "新建角色", value = "新建角色")
    Result<AbstractRolePermissionRuleVo.RoleVo> newRole(
            @RequestBody
            @Valid
            AbstractRoleRequest.NewRoleRequest request
    ) throws Exception;

    @GetMapping(value = "/role/getRoleById")
    @ApiOperation(notes = "根据角色 ID 获取角色信息", value = "根据角色 ID 获取角色信息")
    Result<AbstractRolePermissionRuleVo.RoleVo> getRoleById(
            @ApiParam(name = "RoleId", value = "RoleId")
            @RequestParam(value = "RoleId", required = true)
            Long roleId
    ) throws Exception;

    @GetMapping(value = "/role/getRoleList")
    @ApiOperation(notes = "获取角色信息列表", value = "获取角色信息列表")
    Result<AbstractRolePermissionRuleVo.RoleListVo> getRoleList() throws Exception;


    @GetMapping(value = "/role/getRoleListByUserId")
    @ApiOperation(notes = "根据用户 ID 获取角色信息列表", value = "根据用户 ID 获取角色信息列表")
    Result<AbstractRolePermissionRuleVo.RoleListVo> getRoleListByUserId(
            @ApiParam(name = "UserId", value = "UserId")
            @RequestParam(value = "UserId", required = true)
            Long userId
    ) throws Exception;


    @PostMapping(value = "/role/removeRoleBatchByIdList")
    @ApiOperation(notes = "根据角色 ID 列表移除角色", value = "根据角色 ID 列表移除角色")
    Result<String> removeRoleBatchByIdList(
            @RequestBody
            @Valid
            AbstractRoleRequest.RoleIdListRequest request
    ) throws Exception;

}
