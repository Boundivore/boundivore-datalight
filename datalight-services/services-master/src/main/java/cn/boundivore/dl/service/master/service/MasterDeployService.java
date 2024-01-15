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
import cn.boundivore.dl.orm.po.single.TDlService;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: 即将执行部署
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class MasterDeployService {

    private final MasterJobService masterJobService;

    private final MasterServiceService masterServiceService;

    private final MasterConfigPreService masterConfigPreService;

    private final MasterInitProcedureService masterInitProcedureService;

    /**
     * Description: 开始生成部署计划，并部署服务、组件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param request 将要部署的目标集群和将要部署的服务
     * @return Result<AbstractJobVo.JobIdVo> 集群 ID & JobID
     */
    public Result<AbstractJobVo.JobIdVo> deploy(JobRequest request) throws Exception {
        //检查当前操作意图是否正确
        ActionTypeEnum actionTypeEnum = request.getActionTypeEnum();
        Assert.isTrue(
                actionTypeEnum == ActionTypeEnum.DEPLOY,
                () -> new IllegalArgumentException(String.format("错误的意图: %s", actionTypeEnum))
        );

        // 检查部署意图的合理性
        this.checkDeployLegality(
                request.getClusterId(),
                request.getServiceNameList()
        );

        // 单独处理 Zookeeper 这种特殊情况，即：myid 必须在数据目录下，而数据目录又可以被用户预先指定
        this.masterConfigPreService.replaceZookeeperMyIdDir2DataDir(request.getClusterId());

        Long jobId = masterJobService.initJob(request, true);

        // 记录部署 Procedure
        this.masterInitProcedureService.persistServiceComponentProcedure(
                request.getClusterId(),
                jobId,
                ProcedureStateEnum.PROCEDURE_DEPLOYING
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
     * Creation time: 2023/7/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId       集群 ID
     * @param serviceNameList 服务部署列表
     */
    private void checkDeployLegality(Long clusterId,
                                     List<String> serviceNameList) {

        final Map<String, TDlService> tDlServiceMap = this.masterServiceService
                .getTDlServiceList(clusterId)
                .stream()
                .filter(i -> i.getServiceState() == SCStateEnum.SELECTED
                        || i.getServiceState() == SCStateEnum.SELECTED_ADDITION
                        || i.getServiceState() == SCStateEnum.CHANGING)
                .collect(Collectors.toMap(TDlService::getServiceName, i -> i));

        serviceNameList.forEach(i ->
                Assert.notNull(
                        tDlServiceMap.get(i),
                        () -> new BException(
                                String.format(
                                        "部署操作必须选择处于 %s、%s、%s状态的服务",
                                        SCStateEnum.SELECTED,
                                        SCStateEnum.SELECTED_ADDITION,
                                        SCStateEnum.CHANGING
                                )
                        )
                )
        );

        Assert.isTrue(
                tDlServiceMap.size() == serviceNameList.size(),
                () -> new BException(
                        String.format(
                                "请明确前后部署意图一致，本将部署的服务: %s, 本次部署传递的服务: %s",
                                tDlServiceMap.values()
                                        .stream()
                                        .map(TDlService::getServiceName)
                                        .collect(Collectors.toList()),
                                serviceNameList
                        )
                )
        );

    }
}
