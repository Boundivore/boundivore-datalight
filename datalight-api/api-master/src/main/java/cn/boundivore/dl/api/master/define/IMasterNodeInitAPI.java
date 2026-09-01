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
 * Description: 节点管理的相关接口定义
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：节点初始化相关", description = "IMasterNodeInitAPI")
@FeignClient(
        name = "IMasterNodeInitAPI",
        contextId = "IMasterNodeInitAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterNodeInitAPI {

    @PostMapping(value = "/node/init/hostname/parse")
    @Operation(summary = "Parse 解析节点主机名", description = "Parse 解析节点主机名")
    Result<ParseHostnameVo> parseHostname(
            @RequestBody
            @Valid
            ParseHostnameRequest request
    ) throws Exception;

    @PostMapping(value = "/node/init/detect")
    @Operation(summary = "Detect 节点异步探测连通性", description = "Detect 节点异步探测连通性")
    Result<AbstractNodeJobVo.NodeJobIdVo> detectNode(
            @RequestBody
            @Valid
            NodeJobRequest request
    ) throws Exception;

    @PostMapping(value = "/node/init/check")
    @Operation(summary = "Check 节点初始化检查", description = "Check 节点初始化检查")
    Result<AbstractNodeJobVo.NodeJobIdVo> checkNode(
            @RequestBody
            @Valid
            NodeJobRequest request
    ) throws Exception;

    @PostMapping(value = "/node/init/dispatch")
    @Operation(summary = "Dispatch 分发节点安装包", description = "Dispatch 分发节点安装包")
    Result<AbstractNodeJobVo.NodeJobIdVo> dispatchNode(
            @RequestBody
            @Valid
            NodeJobRequest request
    ) throws Exception;

    @PostMapping(value = "/node/init/startWorker")
    @Operation(summary = "StartWorker 启动节点 Worker 进程", description = "StartWorker 启动节点 Worker 进程")
    Result<AbstractNodeJobVo.NodeJobIdVo> startNodeWorker(
            @RequestBody
            @Valid
            NodeJobRequest request
    ) throws Exception;

    @GetMapping(value = "/node/init/parse/list")
    @Operation(summary = "Parse 获取节点初始化列表", description = "Parse 获取节点初始化列表")
    Result<AbstractNodeInitVo.NodeInitVo> initParseList(
            @Parameter(name = "ClusterId", description = "ClusterId")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @PostMapping(value = "/node/init/detect/list")
    @Operation(summary = "Detect 获取节点初始化列表", description = "Detect 获取节点初始化列表")
    Result<AbstractNodeInitVo.NodeInitVo> initDetectList(
            @RequestBody
            @Valid
            AbstractNodeInitRequest.NodeInitInfoListRequest request
    ) throws Exception;



    @PostMapping(value = "/node/init/check/list")
    @Operation(summary = "Check 获取节点初始化列表", description = "Check 获取节点初始化列表")
    Result<AbstractNodeInitVo.NodeInitVo> initCheckList(
            @RequestBody
            @Valid
            AbstractNodeInitRequest.NodeInitInfoListRequest request
    ) throws Exception;

    @PostMapping(value = "/node/init/dispatch/list")
    @Operation(summary = "Dispatch 获取节点初始化列表", description = "Dispatch 获取节点初始化列表")
    Result<AbstractNodeInitVo.NodeInitVo> initDispatchList(
            @RequestBody
            @Valid
            AbstractNodeInitRequest.NodeInitInfoListRequest request
    ) throws Exception;

    @PostMapping(value = "/node/init/startWorker/list")
    @Operation(summary = "StartWorker 获取节点初始化列表", description = "StartWorker 获取节点初始化列表")
    Result<AbstractNodeInitVo.NodeInitVo> initStartWorkerList(
            @RequestBody
            @Valid
            AbstractNodeInitRequest.NodeInitInfoListRequest request
    ) throws Exception;

    @PostMapping(value = "/node/init/add")
    @Operation(summary = "Add 服役节点到指定集群", description = "Add 服役节点到指定集群")
    Result<String> addNode(
            @RequestBody
            @Valid
            AbstractNodeInitRequest.NodeInitInfoListRequest request
    ) throws Exception;

}
