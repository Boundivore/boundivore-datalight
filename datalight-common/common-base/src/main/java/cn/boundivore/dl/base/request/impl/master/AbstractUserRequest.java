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
package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.enumeration.impl.IdentityTypeEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Description: 用户相关 Request 集合
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/17
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractUserRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @ApiModel(
            value = "AbstractUserRequest.UserAuthRequest",
            description = "AbstractUserRequest.UserAuthRequest: 用户登录、认证 请求体"
    )
    public static class UserAuthRequest implements IRequest {

        @ApiModelProperty(name = "IdentityType", value = "认证类型", required = true, example = "枚举：EMAIL, PHONE, USERNAME")
        @JsonProperty(value = "IdentityType", required = true)
        @NotNull
        private IdentityTypeEnum identityType;

        @ApiModelProperty(name = "Principal", value = "认证主体", required = true)
        @JsonProperty(value = "Principal", required = true)
        @NotBlank
        private String principal;

        @ApiModelProperty(name = "Credential", value = "登录凭证", required = true)
        @JsonProperty(value = "Credential", required = true)
        @NotBlank
        private String credential;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @ApiModel(
            value = "AbstractUserRequest.UserBaseRequest",
            description = "AbstractUserRequest.UserBaseRequest: 用户基础信息 请求体"
    )
    public static class UserBaseRequest implements IRequest {

        @ApiModelProperty(name = "Nickname", value = "用户昵称", required = true)
        @JsonProperty(value = "Nickname", required = true)
        private String nickname;

        @ApiModelProperty(name = "Realname", value = "真实姓名", required = true)
        @JsonProperty(value = "Realname", required = true)
        private String realname;

        @ApiModelProperty(name = "Avatar", value = "头像地址", required = true)
        @JsonProperty(value = "Avatar", required = true)
        private String avatar;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @ApiModel(
            value = "AbstractUserRequest.UserRegisterRequest",
            description = "AbstractUserRequest.UserRegisterRequest: 用户注册 请求体"
    )
    public static class UserRegisterRequest implements IRequest {

        @ApiModelProperty(name = "UserAuth", value = "平台用户登录、认证", required = true)
        @JsonProperty(value = "UserAuth", required = true)
        @Valid
        private UserAuthRequest userAuth;

        @ApiModelProperty(name = "UserBase", value = "平台用户登录、认证", required = true)
        @JsonProperty(value = "UserBase", required = true)
        @Valid
        private UserBaseRequest userBase;

    }
}
