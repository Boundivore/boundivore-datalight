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
package cn.boundivore.dl.service.master.controller;

import cn.boundivore.dl.api.master.define.IMasterRoleUserBindingAPI;
import cn.boundivore.dl.base.request.impl.master.AbstractRoleRequest;
import cn.boundivore.dl.base.request.impl.master.AbstractUserRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterRoleUserBindingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterRoleUserBindingController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
public class MasterRoleUserBindingController implements IMasterRoleUserBindingAPI {

    private MasterRoleUserBindingService masterRoleUserBindingService;

    @Override
    public Result<String> attachRoleUserByRoleUserId(AbstractRoleRequest.RoleUserIdListRequest request) throws Exception {
        return this.masterRoleUserBindingService.attachRoleUserByRoleUserId(request);
    }

    @Override
    public Result<String> detachRoleUserByUserId(AbstractRoleRequest.RoleUserIdListRequest request) throws Exception {
        return this.masterRoleUserBindingService.detachRoleUserByUserId(request);
    }

    @Override
    public Result<String> detachRoleUser(AbstractUserRequest.UserIdListRequest request) throws Exception {
        return this.masterRoleUserBindingService.detachRoleUser(request);
    }
}
