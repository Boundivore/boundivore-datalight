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

import cn.boundivore.dl.api.master.define.IMasterUserAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractUserRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractUserVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterUserService;
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
public class MasterUserController implements IMasterUserAPI {

    protected final MasterUserService masterUserService;


    @Override
    public Result<AbstractUserVo.UserInfoVo> register(AbstractUserRequest.UserRegisterRequest request) throws Exception {
        return this.masterUserService.register(request, false);
    }

    @Override
    public Result<String> removeById(AbstractUserRequest.UserIdRequest request) throws Exception {
        return this.masterUserService.removeById(request);
    }

    @SaIgnore
    @Override
    @Logs(name = "登录", logType = LogTypeEnum.MASTER, isPrintResult = true)
    public Result<AbstractUserVo.UserInfoVo> login(AbstractUserRequest.UserAuthRequest request) throws Exception {
        return this.masterUserService.login(request);
    }

    @Override
    public Result<String> logout() throws Exception {
        return this.masterUserService.logout();
    }

    @SaIgnore
    @Override
    @Logs(name = "判断是否登录", logType = LogTypeEnum.MASTER, isPrintResult = true)
    public Result<Boolean> isLogin() throws Exception {
        return this.masterUserService.isLogin();
    }

    @Override
    public Result<String> changePassword(AbstractUserRequest.UserChangePasswordRequest request) throws Exception {
        return this.masterUserService.changePassword(request);
    }

    @Override
    public Result<AbstractUserVo.UserInfoVo> getUserDetailById(Long userId) throws Exception {
        return this.masterUserService.getUserDetailById(userId);
    }

    @Override
    public Result<AbstractUserVo.UserInfoListVo> getUserDetailList() throws Exception {
        return this.masterUserService.getUserDetailList();
    }
}
