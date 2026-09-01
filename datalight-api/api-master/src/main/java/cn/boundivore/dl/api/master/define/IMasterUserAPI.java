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

import cn.boundivore.dl.base.request.impl.master.AbstractUserRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractUserVo;
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
 * Description: 关于用户的相关接口定义
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：用户相关", description = "IMasterUserAPI")
@FeignClient(
        name = "IMasterUserAPI",
        contextId = "IMasterUserAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterUserAPI {

    @PostMapping(value = "/user/register")
    @Operation(summary = "用户注册", description = "用户注册")
    Result<AbstractUserVo.UserInfoVo> register(
            @RequestBody
            @Valid
            AbstractUserRequest.UserRegisterRequest request
    ) throws Exception;

    @PostMapping(value = "/user/removeById")
    @Operation(summary = "移除用户", description = "移除用户")
    Result<String> removeById(
            @RequestBody
            @Valid
            AbstractUserRequest.UserIdRequest request
    ) throws Exception;

    @PostMapping(value = "/user/login")
    @Operation(summary = "用户登录", description = "用户登录")
    Result<AbstractUserVo.UserInfoVo> login(
            @RequestBody
            @Valid
            AbstractUserRequest.UserAuthRequest request
    ) throws Exception;

    @GetMapping(value = "/user/logout")
    @Operation(summary = "用户登出", description = "用户登出")
    Result<String> logout() throws Exception;

    @GetMapping(value = "/user/isLogin")
    @Operation(summary = "判断当前会话是否登录", description = "判断当前会话是否登录")
    Result<Boolean> isLogin() throws Exception;

    @PostMapping(value = "/user/changePassword")
    @Operation(summary = "修改密码", description = "修改密码")
    Result<String> changePassword(
            @RequestBody
            @Valid
            AbstractUserRequest.UserChangePasswordRequest request
    ) throws Exception;

    @GetMapping(value = "/user/getUserDetailById")
    @Operation(summary = "根据用户 ID 获取用户详细信息", description = "根据用户 ID 获取用户详细信息")
    Result<AbstractUserVo.UserInfoVo> getUserDetailById(
            @Parameter(name = "UserId", description = "用户 Id", example = "1")
            @RequestParam(value = "UserId", required = true)
            Long userId
    ) throws Exception;

    @GetMapping(value = "/user/getUserDetailList")
    @Operation(summary = "获取已有的用户列表", description = "获取已有的用户列表")
    Result<AbstractUserVo.UserInfoListVo> getUserDetailList() throws Exception;
}
