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

import cn.boundivore.dl.base.request.impl.master.AbstractUserRequest;
import cn.boundivore.dl.service.master.service.MasterUserService;
import cn.boundivore.dl.api.master.define.IMasterUserAPI;
import cn.boundivore.dl.base.response.impl.master.UserInfoVo;
import cn.boundivore.dl.base.result.Result;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterUserController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/3/30
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
//@SaCheckLogin
public class MasterUserController implements IMasterUserAPI {

    protected final MasterUserService masterUserService;


    @Override
    public Result<UserInfoVo> register(AbstractUserRequest.UserRegisterRequest request) throws Exception {
        return this.masterUserService.register(request);
    }

    @SaIgnore
    @Override
    public Result<UserInfoVo> login(AbstractUserRequest.UserAuthRequest request) throws Exception {
        return this.masterUserService.login(request);
    }

    @SaIgnore
    @Override
    public Result<String> logout(Long userId) throws Exception {
        return this.masterUserService.logout(userId);
    }

    @SaIgnore
    @Override
    public Result<Boolean> isLogin(Long userId) throws Exception {
        return this.masterUserService.isLogin(userId);
    }
}
