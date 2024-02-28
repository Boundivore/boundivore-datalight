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

import cn.boundivore.dl.base.request.impl.master.AbstractWebStateRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractWebStateVo;
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
 * Description: 集群管理的相关接口定义
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/2/28
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterWebStateAPI", tags = {"Master 接口：前端状态缓存相关"})
@FeignClient(
        name = "IMasterWebStateAPI",
        contextId = "IMasterWebStateAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterWebStateAPI {

    @PostMapping(value = "/web/state/save")
    @ApiOperation(notes = "缓存状态信息", value = "缓存状态信息")
    Result<String> saveWebState(
            @RequestBody
            @Valid
            AbstractWebStateRequest.SaveStateRequest request
    ) throws Exception;

    @GetMapping(value = "/web/state/get")
    @ApiOperation(notes = "查询 Web 缓存状态信息", value = "查询 Web 缓存状态信息")
    Result<AbstractWebStateVo.WebStateMapVo> getWebStateMap(
            @ApiParam(name = "ClusterId", value = "集群 ID")
            @RequestParam(value = "ClusterId", required = false)
            Long clusterId,

            @ApiParam(name = "UserId", value = "用户 ID")
            @RequestParam(value = "UserId", required = false)
            Long userId,

            @ApiParam(name = "WebKey", value = "缓存键")
            @RequestParam(value = "WebKey", required = false)
            String webKey
    ) throws Exception;

    @PostMapping(value = "/web/state/removeByKey")
    @ApiOperation(notes = "缓存状态信息", value = "缓存状态信息")
    Result<String> removeByKey(
            @RequestBody
            @Valid
            AbstractWebStateRequest.RemoveStateRequest request
    ) throws Exception;


    @PostMapping(value = "/web/state/clearByClusterId")
    @ApiOperation(notes = "缓存状态信息", value = "缓存状态信息")
    Result<String> clearByClusterId(
            @RequestBody
            @Valid
            AbstractWebStateRequest.ClearStateRequest request
    ) throws Exception;

}
