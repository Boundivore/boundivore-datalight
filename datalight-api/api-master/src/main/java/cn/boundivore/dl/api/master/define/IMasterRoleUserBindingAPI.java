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
import cn.boundivore.dl.base.request.impl.master.AbstractUserRequest;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 角色用户绑定关系相关接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterRoleUserBindingAPI", tags = {"Master 接口：角色用户绑定关系相关"})
@FeignClient(
        name = "IMasterRoleUserBindingAPI",
        contextId = "IMasterRoleUserBindingAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterRoleUserBindingAPI {

    @PostMapping(value = "/role/relation/attachRoleUserByRoleUserId")
    @ApiOperation(notes = "根据角色用户 ID 建立角色用户绑定关系", value = "根据角色用户 ID 建立角色用户绑定关系")
    Result<String> attachRoleUserByRoleUserId(
            @RequestBody
            @Valid
            AbstractRoleRequest.RoleUserIdListRequest request
    ) throws Exception;

    @PostMapping(value = "/role/relation/detachRoleUserByUserId")
    @ApiOperation(notes = "根据角色用户 ID 移除角色用户绑定关系", value = "根据角色用户 ID 移除角色用户绑定关系")
    Result<String> detachRoleUserByUserId(
            @RequestBody
            @Valid
            AbstractRoleRequest.RoleUserIdListRequest request
    ) throws Exception;

    @PostMapping(value = "/role/relation/detachRoleUser")
    @ApiOperation(notes = "根据用户 ID 移除角色用户绑定关系", value = "根据用户 ID 移除角色用户绑定关系")
    Result<String> detachRoleUser(
            @RequestBody
            @Valid
            AbstractUserRequest.UserIdListRequest request
    ) throws Exception;
}
