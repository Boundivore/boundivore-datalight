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

import cn.boundivore.dl.base.request.impl.master.ConfigPreSaveRequest;
import cn.boundivore.dl.base.response.impl.master.ConfigPreVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 预配置相关接口定义
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/19
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterConfigPreAPI", tags = {"Master 接口：预配置相关"})
@FeignClient(
        name = "IMasterConfigPreAPI",
        contextId = "IMasterConfigPreAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterConfigPreAPI {
    @GetMapping(value = "/config/pre/list")
    @ApiOperation(notes = "根据待部署的服务组件获取预配置项", value = "根据待部署的服务组件获取预配置项")
    Result<ConfigPreVo> configPreList(
            @ApiParam(name = "ClusterId", value = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @PostMapping(value = "/config/pre/save")
    @ApiOperation(notes = "设置预配置项", value = "设置预配置项")
    Result<String> configPreSave(
            @RequestBody
            @Valid
            ConfigPreSaveRequest request
    ) throws Exception;

}
