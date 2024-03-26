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
package cn.boundivore.dl.service.master.controller;

import cn.boundivore.dl.api.master.define.IMasterJobAPI;
import cn.boundivore.dl.base.response.impl.master.AbstractJobVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterJobService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterJobController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/1/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@SaCheckLogin
public class MasterJobController implements IMasterJobAPI {

    private final MasterJobService masterJobService;


    @Override
    public Result<AbstractJobVo.JobIdVo> getActiveJobId() throws Exception {
        return this.masterJobService.getActiveJobId();
    }

    @Override
    public Result<AbstractJobVo.JobProgressVo> getJobProgress(Long jobId) throws Exception {
        return this.masterJobService.getJobProgress(jobId);
    }

    @Override
    public Result<AbstractJobVo.JobPlanProgressVo> getActiveJobPlanProgress() throws Exception {
        return this.masterJobService.getActiveJobPlanProgress();
    }

    @Override
    public Result<AbstractJobVo.JobLogListVo> getJobLogList(Long clusterId,
                                                            Long jobId,
                                                            Long stageId,
                                                            Long taskId,
                                                            Long stepId) throws Exception {
        return this.masterJobService.getJobLogList(
                clusterId,
                jobId,
                stageId,
                taskId,
                stepId
        );
    }


}
