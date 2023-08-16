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

import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 节点异步任务管理相关接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterNodeJobAPI", tags = {"Master 接口：节点异步任务管理 相关"})
@FeignClient(
        name = "IMasterNodeJobAPI",
        contextId = "IMasterNodeJobAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterNodeJobAPI {

    @GetMapping(value = "/node/job/progress")
    @ApiOperation(notes = "获取节点任务进度", value = "获取节点任务进度")
    Result<AbstractNodeJobVo.NodeJobProgressVo> getNodeJobProgress(
            @ApiParam(name = "NodeJobId", value = "NodeJobId")
            @RequestParam(value = "NodeJobId", required = true)
            Long nodeJobId
    ) throws Exception;

    @GetMapping(value = "/node/job/dispatch/progress")
    @ApiOperation(notes = "获取所有节点分发进度概览", value = "获取所有节点分发进度概览")
    Result<AbstractNodeJobVo.AllNodeJobTransferProgressVo> getNodeJobDispatchProgress(
            @ApiParam(name = "NodeJobId", value = "NodeJobId")
            @RequestParam(value = "NodeJobId", required = true)
            Long nodeJobId
    ) throws Exception;

    @GetMapping(value = "/node/job/dispatch/progressDetail")
    @ApiOperation(notes = "获取指定节点分发进度详情", value = "获取指定节点分发进度详情")
    Result<AbstractNodeJobVo.NodeJobTransferProgressDetailVo> getNodeJobDispatchProgressDetail(
            @ApiParam(name = "NodeJobId", value = "NodeJobId")
            @RequestParam(value = "NodeJobId", required = true)
            Long nodeJobId,

            @ApiParam(name = "NodeTaskId", value = "NodeTaskId")
            @RequestParam(value = "NodeTaskId", required = true)
            Long nodeTaskId,

            @ApiParam(name = "NodeStepId", value = "NodeStepId")
            @RequestParam(value = "NodeStepId", required = true)
            Long nodeStepId
    ) throws Exception;

}
