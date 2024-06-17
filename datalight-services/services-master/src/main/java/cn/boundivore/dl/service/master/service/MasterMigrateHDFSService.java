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
package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ProcedureStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.master.JobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractJobVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 即将执行迁移部署
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/17
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class MasterMigrateHDFSService {

    private final MasterJobService masterJobService;

    private final MasterServiceService masterServiceService;

    private final MasterConfigService masterConfigService;

    private final MasterInitProcedureService masterInitProcedureService;

    /**
     * Description: 开始生成迁移部署计划，并执行计划
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/17
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param request 将要迁移部署的目标集群和将要迁移部署的服务，必须有且仅有 HDFS
     * @return Result<AbstractJobVo.JobIdVo> 集群 ID & JobID
     */
    public Result<AbstractJobVo.JobIdVo> migrate(JobRequest request) throws Exception {
        //检查当前操作意图是否正确
        ActionTypeEnum actionTypeEnum = request.getActionTypeEnum();
        Assert.isTrue(
                actionTypeEnum == ActionTypeEnum.MIGRATE,
                () -> new IllegalArgumentException(String.format("错误的意图: %s", actionTypeEnum))
        );

        // 检查迁移部署意图的合理性
        this.checkMigrateNameNodeLegality(
                request.getClusterId(),
                request.getServiceNameList()
        );


        Long jobId = this.masterJobService.initJob(request, true);

        // 记录部署 Procedure
        this.masterInitProcedureService.persistServiceComponentProcedure(
                request.getClusterId(),
                jobId,
                ProcedureStateEnum.PROCEDURE_MIGRATE_DEPLOYING
        );

        return Result.success(
                new AbstractJobVo.JobIdVo(
                        request.getClusterId(),
                        jobId
                )
        );
    }

    /**
     * Description: 检查部署的合理性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/17
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId       集群 ID
     * @param serviceNameList 服务部署列表
     */
    private void checkMigrateNameNodeLegality(Long clusterId,
                                              List<String> serviceNameList) {

        Assert.isTrue(
                serviceNameList.size() == 1,
                () -> new BException("迁移部署一次只能进行一次操作")
        );

        Assert.isTrue(
                serviceNameList.contains("HDFS"),
                () -> new BException("迁移部署仅针对 HDFS 有效")
        );


        SCStateEnum hdfsServiceState = this.masterServiceService.getServiceState(
                clusterId,
                "HDFS"
        );

        Assert.isTrue(
                hdfsServiceState == SCStateEnum.SELECTED
                        || hdfsServiceState == SCStateEnum.SELECTED_ADDITION
                        || hdfsServiceState == SCStateEnum.CHANGING,
                () -> new BException(
                        String.format(
                                "迁移部署操作必须选择处于 %s、%s、%s状态的服务",
                                SCStateEnum.SELECTED,
                                SCStateEnum.SELECTED_ADDITION,
                                SCStateEnum.CHANGING
                        )
                )
        );
    }
}
