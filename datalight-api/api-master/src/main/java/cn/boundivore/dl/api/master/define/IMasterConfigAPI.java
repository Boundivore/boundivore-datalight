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

import cn.boundivore.dl.base.request.impl.master.ConfigSaveByGroupRequest;
import cn.boundivore.dl.base.request.impl.master.ConfigSaveRequest;
import cn.boundivore.dl.base.response.impl.master.ConfigListByGroupVo;
import cn.boundivore.dl.base.response.impl.master.ConfigSummaryListVo;
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
 * Description: 配置相关接口定义
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/19
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterConfigAPI", tags = {"Master 接口：配置相关"})
@FeignClient(
        name = "IMasterConfigAPI",
        contextId = "IMasterConfigAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterConfigAPI {
    @PostMapping(value = "/config/save")
    @ApiOperation(notes = "保存配置项", value = "保存配置项")
    Result<String> configSave(
            @RequestBody
            @Valid
            ConfigSaveRequest request
    ) throws Exception;

    @GetMapping(value = "/config/listSummary")
    @ApiOperation(notes = "获取当前服务的配置信息概览列表", value = "获取当前服务的配置信息概览列表")
    Result<ConfigSummaryListVo> configListSummary(
            @ApiParam(name = "ClusterId", value = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId,

            @ApiParam(name = "ServiceName", value = "服务名称")
            @RequestParam(value = "ServiceName", required = true)
            String serviceName
    ) throws Exception;

    @GetMapping(value = "/config/listByGroup")
    @ApiOperation(notes = "获取服务组件的配置", value = "获取服务组件的配置")
    Result<ConfigListByGroupVo> configListByGroup(
            @ApiParam(name = "ClusterId", value = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId,

            @ApiParam(name = "ServiceName", value = "服务名称")
            @RequestParam(value = "ServiceName", required = true)
            String serviceName,

            @ApiParam(name = "Filename", value = "配置文件名称")
            @RequestParam(value = "Filename", required = true)
            String filename,

            @ApiParam(name = "ConfigPath", value = "配置文件路径")
            @RequestParam(value = "ConfigPath", required = true)
            String configPath
    ) throws Exception;

    @PostMapping(value = "/config/saveByGroup")
    @ApiOperation(notes = "根据分组保存配置项", value = "根据分组保存配置项")
    Result<String> configSaveByGroup(
            @RequestBody
            @Valid
            ConfigSaveByGroupRequest request
    ) throws Exception;
}
