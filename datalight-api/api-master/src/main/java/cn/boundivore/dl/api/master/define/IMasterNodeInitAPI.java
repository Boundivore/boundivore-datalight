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

import cn.boundivore.dl.base.request.impl.master.AbstractNodeInitRequest;
import cn.boundivore.dl.base.request.impl.master.NodeJobRequest;
import cn.boundivore.dl.base.request.impl.master.ParseHostnameRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeInitVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.response.impl.master.ParseHostnameVo;
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
 * Description: 节点管理的相关接口定义
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterNodeInitAPI", tags = {"Master 接口：节点初始化相关"})
@FeignClient(
        name = "IMasterNodeInitAPI",
        contextId = "IMasterNodeInitAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterNodeInitAPI {

    @PostMapping(value = "/node/init/hostname/parse")
    @ApiOperation(notes = "解析节点主机名", value = "解析节点主机名")
    Result<ParseHostnameVo> parseHostname(
            @RequestBody
            @Valid
            ParseHostnameRequest request
    ) throws Exception;

    @PostMapping(value = "/node/init/detect")
    @ApiOperation(notes = "节点异步探测连通性", value = "节点异步探测连通性")
    Result<AbstractNodeJobVo.NodeJobIdVo> detectNode(
            @RequestBody
            @Valid
            NodeJobRequest request
    ) throws Exception;

    @PostMapping(value = "/node/init/check")
    @ApiOperation(notes = "节点初始化检查", value = "节点初始化检查")
    Result<AbstractNodeJobVo.NodeJobIdVo> checkNode(
            @RequestBody
            @Valid
            NodeJobRequest request
    ) throws Exception;

    @PostMapping(value = "/node/init/dispatch")
    @ApiOperation(notes = "分发节点安装包", value = "分发节点安装包")
    Result<AbstractNodeJobVo.NodeJobIdVo> dispatchNode(
            @RequestBody
            @Valid
            NodeJobRequest request
    ) throws Exception;

    @GetMapping(value = "/node/init/parse/list")
    @ApiOperation(notes = "获取节点初始化列表， Parse 执行之后", value = "获取节点初始化列表， Parse 执行之后")
    Result<AbstractNodeInitVo.NodeInitVo> initParseList(
            @ApiParam(name = "ClusterId", value = "ClusterId")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @GetMapping(value = "/node/init/detect/list")
    @ApiOperation(notes = "获取节点初始化列表， Detect 执行之后", value = "获取节点初始化列表， Detect 执行之后")
    Result<AbstractNodeInitVo.NodeInitVo> initDetectList(
            @ApiParam(name = "ClusterId", value = "ClusterId")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;



    @GetMapping(value = "/node/init/check/list")
    @ApiOperation(notes = "获取节点初始化列表， Check 执行之后", value = "获取节点初始化列表， Check 执行之后")
    Result<AbstractNodeInitVo.NodeInitVo> initCheckList(
            @ApiParam(name = "ClusterId", value = "ClusterId")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @GetMapping(value = "/node/init/dispatch/list")
    @ApiOperation(notes = "获取节点初始化列表， Dispatch 执行之后", value = "获取节点初始化列表， Dispatch 执行之后")
    Result<AbstractNodeInitVo.NodeInitVo> initDispatchList(
            @ApiParam(name = "ClusterId", value = "ClusterId")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @PostMapping(value = "/node/init/add")
    @ApiOperation(notes = "服役节点到指定集群", value = "服役节点到指定集群")
    Result<String> addNode(
            @RequestBody
            @Valid
            AbstractNodeInitRequest.NodeInitInfoListRequest request
    ) throws Exception;

}
