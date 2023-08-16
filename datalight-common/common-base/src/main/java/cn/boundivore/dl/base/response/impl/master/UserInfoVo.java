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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(
        value = "UserInfoVo",
        description = "UserInfoVo: 用户信息"
)
public class UserInfoVo implements IVo {

    @ApiModelProperty(name = "UserId", value = "用户 ID", required = true)
    @JsonProperty(value = "UserId", required = true)
    private Long userId;

    @ApiModelProperty(name = "CreateTime", value = "创建时间", required = true)
    @JsonProperty(value = "CreateTime", required = true)
    private Long createTime;

    @ApiModelProperty(name = "UpdateTime", value = "更新时间", required = true)
    @JsonProperty(value = "UpdateTime", required = true)
    private Long updateTime;

    @ApiModelProperty(name = "Nickname", value = "昵称", required = true)
    @JsonProperty(value = "Nickname", required = true)
    private String nickname;

    @ApiModelProperty(name = "Realname", value = "真实姓名", required = true)
    @JsonProperty(value = "Realname", required = true)
    private String realname;

    @ApiModelProperty(name = "Avatar", value = "头像地址", required = true)
    @JsonProperty(value = "Avatar", required = true)
    private String avatar;

    @ApiModelProperty(name = "LastLogin", value = "上次登录时间", required = true)
    @JsonProperty(value = "LastLogin", required = true)
    private Long lastLogin = -1L;

}
