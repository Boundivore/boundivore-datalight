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

import cn.boundivore.dl.base.request.impl.master.AbstractNodeRequest;
import cn.boundivore.dl.base.request.impl.master.NodeJobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeVo;
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
@Tag(name = "Master 接口：节点相关", description = "IMasterNodeAPI")
@FeignClient(
        name = "IMasterNodeAPI",
        contextId = "IMasterNodeAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterNodeAPI {

    @PostMapping(value = "/node/operate")
    @Operation(summary = "节点操作", description = "节点操作")
    Result<AbstractNodeJobVo.NodeJobIdVo> operateNode(
            @RequestBody
            @Valid
            NodeJobRequest request
    ) throws Exception;

    @GetMapping(value = "/node/list")
    @Operation(summary = "获取指定集群的节点列表", description = "获取指定集群的节点列表")
    Result<AbstractNodeVo.NodeVo> getNodeList(
            @Parameter(name = "ClusterId", description = "ClusterId")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @PostMapping(value = "/node/removeBatchByIds")
    @Operation(summary = "批量移除节点", description = "批量移除节点")
    Result<String> removeBatchByIds(
            @RequestBody
            @Valid
            AbstractNodeRequest.NodeIdListRequest request
    ) throws Exception;

    @GetMapping(value = "/node/listWithComponent")
    @Operation(summary = "获取节点列表附带其组件信息", description = "获取节点列表附带其组件信息")
    Result<AbstractNodeVo.NodeWithComponentListVo> getNodeListWithComponent(
            @Parameter(name = "ClusterId", description = "ClusterId")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;


}
