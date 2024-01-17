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
package cn.boundivore.dl.base.response.impl.master;


import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "UserInfoVo",
        description = "UserInfoVo 用户信息"
)
public class UserInfoVo implements IVo {

    @Schema(name = "UserId", title = "用户 ID", required = true)
    @JsonProperty(value = "UserId", required = true)
    private Long userId;

    @Schema(name = "CreateTime", title = "创建时间", required = true)
    @JsonProperty(value = "CreateTime", required = true)
    private Long createTime;

    @Schema(name = "UpdateTime", title = "更新时间", required = true)
    @JsonProperty(value = "UpdateTime", required = true)
    private Long updateTime;

    @Schema(name = "Nickname", title = "昵称", required = true)
    @JsonProperty(value = "Nickname", required = true)
    private String nickname;

    @Schema(name = "Realname", title = "真实姓名", required = true)
    @JsonProperty(value = "Realname", required = true)
    private String realname;

    @Schema(name = "Avatar", title = "头像地址", required = true)
    @JsonProperty(value = "Avatar", required = true)
    private String avatar;

    @Schema(name = "LastLogin", title = "上次登录时间", required = true)
    @JsonProperty(value = "LastLogin", required = true)
    private Long lastLogin = -1L;

    @Schema(name = "Token", title = "Token", required = true)
    @JsonProperty(value = "Token", required = true)
    private String token;

    @Schema(name = "TokenTimeout", title = "获取指定 token 剩余有效时间(单位 秒。-1 永久有效，-2 没有这个值)", required = true)
    @JsonProperty(value = "TokenTimeout", required = true)
    private Long tokenTimeout;

    @Schema(name = "IsNeedChangePassword", title = "是否建议修改密码)", required = true)
    @JsonProperty(value = "IsNeedChangePassword", required = true)
    private Boolean isNeedChangePassword;

}
