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

import cn.boundivore.dl.api.master.define.IMasterAgentAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.response.impl.common.AbstractLogFileVo;
import cn.boundivore.dl.base.response.impl.master.AbstractAgentVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterAgentService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterAgentController
 * 面向 AIAgent 的数据接口。日志内容体积大，统一关闭返回体打印
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/1
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = false)
public class MasterAgentController implements IMasterAgentAPI {

    private final MasterAgentService masterAgentService;

    @Override
    @SaCheckLogin
    public Result<AbstractAgentVo.ClusterSnapshotVo> getClusterSnapshot(Long clusterId) throws Exception {
        return this.masterAgentService.getClusterSnapshot(clusterId);
    }

    @Override
    @SaCheckLogin
    public Result<AbstractAgentVo.HealthSummaryVo> getHealthSummary(Long clusterId) throws Exception {
        return this.masterAgentService.getHealthSummary(clusterId);
    }

    @Override
    @SaCheckLogin
    public Result<AbstractAgentVo.JobHistoryVo> getJobHistory(Long clusterId, Integer limit) throws Exception {
        return this.masterAgentService.getJobHistory(clusterId, limit);
    }

    @Override
    @SaCheckLogin
    public Result<AbstractLogFileVo.LogFileContentVo> tailComponentLog(Long clusterId,
                                                                      Long nodeId,
                                                                      String serviceName,
                                                                      String componentName,
                                                                      Integer lines) throws Exception {
        return this.masterAgentService.tailComponentLog(
                clusterId,
                nodeId,
                serviceName,
                componentName,
                lines
        );
    }
}
