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

import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.orm.service.single.impl.TDlUserServiceImpl;
import cn.boundivore.dl.service.master.converter.IUserConverter;
import cn.hutool.core.lang.Assert;
import cn.boundivore.dl.base.request.impl.master.AbstractUserRequest;
import cn.boundivore.dl.base.response.impl.master.UserInfoVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlUser;
import cn.boundivore.dl.orm.po.single.TDlUserAuth;
import cn.boundivore.dl.orm.service.single.impl.TDlLoginEventServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlUserAuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Description: 用户操作相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/15
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterUserService {

    protected final TDlUserServiceImpl tDlUserService;
    protected final TDlUserAuthServiceImpl tDlUserAuthService;
    protected final TDlLoginEventServiceImpl tDlLoginEventService;

    protected final IUserConverter iUserConverter;

    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<UserInfoVo> register(AbstractUserRequest.UserRegisterRequest request) throws Exception {
        //TODO 检查是否验证通过


        //保存用户基础信息
        TDlUser tUser = iUserConverter.convert2TUsers(request.getUserBase());
        Assert.isTrue(tDlUserService.save(tUser), () -> new DatabaseException("用户基础数据保存失败"));

        //保存用户认证信息
        TDlUserAuth tUserAuth = iUserConverter.convert2TUsersAuth(request.getUserAuth());
        tUserAuth.setUserId(tUser.getId());

        Assert.isTrue(tDlUserAuthService.save(tUserAuth), () -> new DatabaseException("用户凭证数据保存失败"));


        return Result.success(iUserConverter.convert2UserInfoVo(tUser));
    }

    public Result<UserInfoVo> login(AbstractUserRequest.UserAuthRequest request) throws Exception {
        return Result.success(new UserInfoVo(
                1L,
                202305110000L,
                202305110000L,
                "admin",
                "boundivore",
                "avatar.jpg",
                202305110000L
        ));
    }

    public Result<String> logout() throws Exception {
        return null;
    }

    public Result<String> isLogin() throws Exception {
        return null;
    }
}
