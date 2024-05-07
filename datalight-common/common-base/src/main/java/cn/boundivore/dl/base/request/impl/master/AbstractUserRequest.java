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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

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
    @Schema(
            name = "AbstractUserRequest.UserAuthRequest",
            description = "AbstractUserRequest.UserAuthRequest 用户登录、认证 请求体"
    )
    public static class UserAuthRequest implements IRequest {

        private static final long serialVersionUID = -5419230796023882560L;

        @Schema(name = "IdentityType", title = "认证类型", required = true, example = "枚举：EMAIL, PHONE, USERNAME")
        @JsonProperty(value = "IdentityType", required = true)
        @NotNull(message = "认证类型不能为空")
        private IdentityTypeEnum identityType;

        @Schema(name = "Principal", title = "认证主体", required = true)
        @JsonProperty(value = "Principal", required = true)
        @NotBlank(message = "认证主体不能为空")
        private String principal;

        @Schema(name = "Credential", title = "登录凭证", required = true)
        @JsonProperty(value = "Credential", required = true)
        @NotBlank(message = "登录凭证不能为空")
        @Pattern(regexp = "^[a-f0-9]{32}$", message = "密码格式不正确")
        private String credential;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractUserRequest.UserBaseRequest",
            description = "AbstractUserRequest.UserBaseRequest 用户基础信息 请求体"
    )
    public static class UserBaseRequest implements IRequest {

        private static final long serialVersionUID = -6668480729370264603L;

        @Schema(name = "Nickname", title = "用户昵称", required = true)
        @JsonProperty(value = "Nickname", required = true)
        private String nickname;

        @Schema(name = "Realname", title = "真实姓名", required = true)
        @JsonProperty(value = "Realname", required = true)
        private String realname;

        @Schema(name = "Avatar", title = "头像地址", required = true)
        @JsonProperty(value = "Avatar", required = true)
        private String avatar;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractUserRequest.UserRegisterRequest",
            description = "AbstractUserRequest.UserRegisterRequest 用户注册请求体"
    )
    public static class UserRegisterRequest implements IRequest {

        private static final long serialVersionUID = 5637268095666465945L;

        @Schema(name = "UserAuth", title = "平台用户登录、认证", required = true)
        @JsonProperty(value = "UserAuth", required = true)
        @Valid
        private UserAuthRequest userAuth;

        @Schema(name = "UserBase", title = "平台用户登录、认证", required = true)
        @JsonProperty(value = "UserBase", required = true)
        @Valid
        private UserBaseRequest userBase;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractUserRequest.UserChangePasswordRequest",
            description = "AbstractUserRequest.UserChangePasswordRequest 用户修改密码 请求体"
    )
    public static class UserChangePasswordRequest implements IRequest {

        private static final long serialVersionUID = -6472232143086042947L;

        @Schema(name = "Principal", title = "认证主体", required = true)
        @JsonProperty(value = "Principal", required = true)
        @NotBlank
        private String principal;

        @Schema(name = "OldCredential", title = "旧登录凭证", required = true)
        @JsonProperty(value = "OldCredential", required = true)
        @NotBlank
        @Pattern(regexp = "^[a-f0-9]{32}$", message = "旧密码格式不正确")
        private String oldCredential;

        @Schema(name = "NewCredential", title = "新登录凭证", required = true)
        @JsonProperty(value = "NewCredential", required = true)
        @NotBlank
        @Pattern(regexp = "^[a-f0-9]{32}$", message = "新密码格式不正确")
        private String newCredential;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractUserRequest.UserIdRequest",
            description = "AbstractUserRequest.UserIdRequest 用户 ID 请求体"
    )
    public static class UserIdRequest implements IRequest {

        private static final long serialVersionUID = -6472232143086042947L;

        @Schema(name = "UserId", title = "用户 ID", required = true)
        @JsonProperty(value = "UserId", required = true)
        @NotNull(message = "用户 ID 不能为空")
        private Long userId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractUserRequest.UserIdListRequest",
            description = "AbstractUserRequest.UserIdListRequest 用户 ID 列表 请求体"
    )
    public static class UserIdListRequest implements IRequest {

        private static final long serialVersionUID = -6472232143086042947L;

        @Schema(name = "UserIdList", title = "用户 ID 列表", required = true)
        @JsonProperty(value = "UserIdList", required = true)
        @NotEmpty(message = "用户 ID 列表不能为空")
        private List<Long> userIdList;
    }
}
