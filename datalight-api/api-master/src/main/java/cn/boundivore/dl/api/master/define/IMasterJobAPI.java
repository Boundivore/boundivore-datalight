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
 * Description: 作业异步任务管理相关
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/1/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：作业异步任务管理相关", description = "IMasterJobAPI")
@FeignClient(
        name = "IMasterJobAPI",
        contextId = "IMasterJobAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterJobAPI {

    @GetMapping(value = "/job/getActiveJobId")
    @Operation(summary = "获取指定集群下正在活跃的 JobId", description = "获取指定集群下正在活跃的 JobId")
    Result<AbstractJobVo.JobIdVo> getActiveJobId() throws Exception;

    @GetMapping(value = "/job/progress")
    @Operation(summary = "获取作业任务进度", description = "获取作业任务进度")
    Result<AbstractJobVo.JobProgressVo> getJobProgress(
            @Parameter(name = "JobId", description = "JobId")
            @RequestParam(value = "JobId", required = true)
            Long jobId
    ) throws Exception;

    @GetMapping(value = "/job/activeJobPlanProgress")
    @Operation(summary = "获取作业任务计划生成进度", description = "获取作业任务计划生成进度")
    Result<AbstractJobVo.JobPlanProgressVo> getActiveJobPlanProgress() throws Exception;


    @GetMapping(value = "/job/getJobLogList")
    @Operation(summary = "获取作业日志信息列表", description = "获取作业日志信息列表")
    Result<AbstractJobVo.JobLogListVo> getJobLogList(
            @Parameter(name = "ClusterId", description = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            @NotNull(message = "集群 ID 不能为空")
            Long clusterId,

            @Parameter(name = "JobId", description = "作业 ID")
            @RequestParam(value = "JobId", required = true)
            @NotNull(message = "作业 ID 不能为空")
            Long jobId,

            @Parameter(name = "NodeId", description = "节点 ID")
            @RequestParam(value = "NodeId", required = false)
            @NotNull(message = "节点 ID 不能为空")
            Long nodeId,

            @Parameter(name = "StageId", description = "阶段 ID")
            @RequestParam(value = "StageId", required = false)
            @NotNull(message = "阶段 ID 不能为空")
            Long stageId,

            @Parameter(name = "TaskId", description = "任务 ID")
            @RequestParam(value = "TaskId", required = false)
            Long taskId,

            @Parameter(name = "StepId", description = "步骤 ID")
            @RequestParam(value = "StepId", required = false)
            Long stepId
    ) throws Exception;

}