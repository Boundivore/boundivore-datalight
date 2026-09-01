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

import cn.boundivore.dl.base.request.impl.master.AbstractPermissionRuleRequest;
import cn.boundivore.dl.base.request.impl.master.AbstractRoleRequest;
import cn.boundivore.dl.base.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 权限角色绑定关系相关接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：权限角色绑定关系相关", description = "IMasterPermissionRoleBindingAPI")
@FeignClient(
        name = "IMasterPermissionRoleBindingAPI",
        contextId = "IMasterPermissionRoleBindingAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterPermissionRoleBindingAPI {

    @PostMapping(value = "/permission/relation/attachPermissionRoleByPermissionRoleId")
    @Operation(summary = "绑定权限角色映射关系", description = "绑定权限角色映射关系")
    Result<String> attachPermissionRoleByPermissionRoleId(
            @RequestBody
            @Valid
            AbstractPermissionRuleRequest.PermissionRoleIdListRequest request
    ) throws Exception;

    @PostMapping(value = "/permission/relation/detachPermissionRoleByPermissionRoleId")
    @Operation(summary = "根据权限角色 ID 移除权限角色绑定关系", description = "根据权限角色 ID 移除权限角色绑定关系")
    Result<String> detachPermissionRoleByPermissionRoleId(
            @RequestBody
            @Valid
            AbstractPermissionRuleRequest.PermissionRoleIdListRequest request
    ) throws Exception;

    @PostMapping(value = "/permission/relation/detachPermissionRoleByRoleId")
    @Operation(summary = "根据角色 ID 移除权限角色绑定关系", description = "根据角色 ID 移除权限角色绑定关系")
    Result<String> detachPermissionRoleByRoleId(
            @RequestBody
            @Valid
            AbstractRoleRequest.RoleIdListRequest request
    ) throws Exception;
}
