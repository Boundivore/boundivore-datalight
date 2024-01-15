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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

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
@Api(value = "IMasterJobAPI", tags = {"Master 接口：作业异步任务管理相关"})
@FeignClient(
        name = "IMasterJobAPI",
        contextId = "IMasterJobAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterJobAPI {

    @GetMapping(value = "/job/progress")
    @ApiOperation(notes = "获取作业任务进度", value = "获取作业任务进度")
    Result<AbstractJobVo.JobProgressVo> getNodeJobProgress(
            @ApiParam(name = "JobId", value = "JobId")
            @RequestParam(value = "JobId", required = true)
            Long jobId
    ) throws Exception;


    @GetMapping(value = "/job/getJobLogList")
    @ApiOperation(notes = "获取作业日志信息列表", value = "获取作业日志信息列表")
    Result<AbstractJobVo.JobLogListVo> getJobLogList(
            @ApiParam(name = "集群 ID", value = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            @NotNull
            Long clusterId,

            @ApiParam(name = "作业 ID", value = "节点作业 ID")
            @RequestParam(value = "JobId", required = true)
            @NotNull
            Long jobId,

            @ApiParam(name = "阶段 ID", value = "阶段 ID")
            @RequestParam(value = "StageId", required = true)
            @NotNull
            Long stageId,

            @ApiParam(name = "任务 ID", value = "任务 ID")
            @RequestParam(value = "TaskId", required = true)
            Long taskId,

            @ApiParam(name = "步骤 ID", value = "步骤 ID")
            @RequestParam(value = "StepId", required = true)
            Long stepId
    ) throws Exception;

}