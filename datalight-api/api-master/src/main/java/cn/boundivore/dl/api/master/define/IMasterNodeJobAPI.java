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

import cn.boundivore.dl.base.response.impl.master.AbstractJobVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.NotNull;

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
@Tag(name = "Master 接口：节点作业异步任务管理相关", description = "IMasterNodeJobAPI")
@FeignClient(
        name = "IMasterNodeJobAPI",
        contextId = "IMasterNodeJobAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterNodeJobAPI {

    @GetMapping(value = "/job/getActiveNodeJobId")
    @Operation(summary = "获取指定集群下正在活跃的 NodeJobId", description = "获取指定集群下正在活跃的 NodeJobId")
    Result<AbstractNodeJobVo.NodeJobIdVo> getActiveNodeJobId() throws Exception;

    @GetMapping(value = "/node/job/progress")
    @Operation(summary = "获取节点作业进度", description = "获取节点作业进度")
    Result<AbstractNodeJobVo.NodeJobProgressVo> getNodeJobProgress(
            @Parameter(name = "NodeJobId", description = "NodeJobId")
            @RequestParam(value = "NodeJobId", required = true)
            Long nodeJobId
    ) throws Exception;

    @GetMapping(value = "/job/activeNodeJobPlanProgress")
    @Operation(summary = "获取节点作业任务计划生成进度", description = "获取节点作业任务计划生成进度")
    Result<AbstractNodeJobVo.NodeJobPlanProgressVo> getActiveNodeJobPlanProgress() throws Exception;

    @GetMapping(value = "/node/job/dispatch/progress")
    @Operation(summary = "获取所有节点分发进度概览", description = "获取所有节点分发进度概览")
    Result<AbstractNodeJobVo.AllNodeJobTransferProgressVo> getNodeJobDispatchProgress(
            @Parameter(name = "NodeJobId", description = "NodeJobId")
            @RequestParam(value = "NodeJobId", required = true)
            Long nodeJobId
    ) throws Exception;

    @GetMapping(value = "/node/job/dispatch/progressDetail")
    @Operation(summary = "获取指定节点分发进度详情", description = "获取指定节点分发进度详情")
    Result<AbstractNodeJobVo.NodeJobTransferProgressDetailVo> getNodeJobDispatchProgressDetail(
            @Parameter(name = "NodeJobId", description = "NodeJobId")
            @RequestParam(value = "NodeJobId", required = true)
            Long nodeJobId,

            @Parameter(name = "NodeTaskId", description = "NodeTaskId")
            @RequestParam(value = "NodeTaskId", required = true)
            Long nodeTaskId,

            @Parameter(name = "NodeStepId", description = "NodeStepId")
            @RequestParam(value = "NodeStepId", required = true)
            Long nodeStepId
    ) throws Exception;

    @GetMapping(value = "/job/getNodeJobLogList")
    @Operation(summary = "获取节点作业日志信息列表", description = "获取节点作业日志信息列表")
    Result<AbstractNodeJobVo.NodeJobLogListVo> getNodeJobLogList(
            @Parameter(name = "ClusterId", description = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            @NotNull(message = "集群 ID 不能为空")
            Long clusterId,

            @Parameter(name = "NodeJobId", description = "节点作业 ID")
            @RequestParam(value = "NodeJobId", required = true)
            @NotNull(message = "节点作业 ID 不能为空")
            Long nodeJobId,

            @Parameter(name = "NodeId", description = "节点 ID")
            @RequestParam(value = "NodeId", required = false)
            @NotNull(message = "节点 ID 不能为空")
            Long nodeId,

            @Parameter(name = "NodeTaskId", description = "节点任务 ID")
            @RequestParam(value = "NodeTaskId", required = false)
            Long nodeTaskId,

            @Parameter(name = "NodeStepId", description = "节点步骤 ID")
            @RequestParam(value = "NodeStepId", required = false)
            Long nodeStepId
    ) throws Exception;

}
