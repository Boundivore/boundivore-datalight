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
import cn.boundivore.dl.base.request.impl.master.JobDetailRequest;
import cn.boundivore.dl.base.request.impl.master.JobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractJobVo;
import cn.boundivore.dl.base.response.impl.master.AbstractServiceComponentVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceManifest;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceDetail;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Description: 用于操作 Ranger Plugin 的启用或停用
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/9/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MasterSwitchRangerPluginService {

    private final MasterJobService masterJobService;

    private final MasterServiceService masterServiceService;

    private final MasterComponentService masterComponentService;

    private final MasterConfigService masterConfigService;

    private final MasterInitProcedureService masterInitProcedureService;

    /**
     * Description: 开始生成启用、停用 Ranger Plugin 的部署计划，并执行计划
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request Job 异步任务请求的封装，请求中必须包含支持 Ranger 插件的服务
     * @return Result<AbstractJobVo.JobIdVo> 集群 ID & JobID
     */
    public Result<AbstractJobVo.JobIdVo> switchRangerPlugin(JobRequest request) throws Exception {
        // 1. 判断是否为 ENABLE_RANGER_PLUGIN 或 DISABLE_RANGER_PLUGIN 操作
        ActionTypeEnum actionTypeEnum = request.getActionTypeEnum();
        Assert.isTrue(
                actionTypeEnum == ActionTypeEnum.ENABLE_RANGER_PLUGIN
                        || actionTypeEnum == ActionTypeEnum.DISABLE_RANGER_PLUGIN ,
                () -> new IllegalArgumentException(String.format("错误的意图: %s", actionTypeEnum))
        );

        // 2. 判断是否安装了 RangerAdmin
        AbstractServiceComponentVo.ComponentListVo rangerAdminList = this.masterComponentService.getComponentListByComponentName(
                        request.getClusterId(),
                        "RANGER",
                        "RangerAdmin"
                )
                .getData();

        // 查找有效的 RangerAdmin 组件
        AbstractServiceComponentVo.ComponentSimpleVo rangerAdmin = rangerAdminList.getComponentSimpleList()
                .stream()
                .filter(i -> i.getScStateEnum() != SCStateEnum.REMOVED
                        && i.getScStateEnum() != SCStateEnum.SELECTED
                        && i.getScStateEnum() != SCStateEnum.UNSELECTED
                )
                .collect(Collectors.toList())
                .get(0);

        Assert.notNull(
                rangerAdmin,
                () -> new BException("请先部署 RangerAdmin 组件")
        );


        // 3. 判断服务列表中是否均为支持 Ranger Plugin 的服务
        List<String> rangerServiceRelativeList = ResolverYamlServiceManifest
                .MANIFEST_SERVICE_MAP
                .get("RANGER")
                .getRelatives();

        request.getServiceNameList()
                .forEach(serviceName -> {
                    Assert.isTrue(
                            rangerServiceRelativeList.contains(serviceName),
                            () -> new BException(
                                    String.format(
                                            "%s 服务暂不支持 RangerPlugin",
                                            serviceName
                                    )
                            )
                    );
                });

        // 4. 通过 JobRequest 和相关逻辑，解析出 JobDetailRequest 用于封装具体需要执行的节点
        // 因为并不是每个服务都需要在每个节点执行插件操作，比如 HDFS，只需要在 NameNode 节点执行插件操作
        // 且如果是 HBase 这种服务，可能会穿 HMaster 和 HRegionServer 部署在同一节点的情况，此时并不需要重复执行 enable plugin，因此还需要去重
        JobDetailRequest jobDetailRequest = new JobDetailRequest();
        jobDetailRequest.setClusterId(request.getClusterId());
        jobDetailRequest.setActionTypeEnum(request.getActionTypeEnum());
        jobDetailRequest.setIsOneByOne(request.getIsOneByOne());

        // 4.1 根据服务名称，找到允许执行 RangerPlugin 的组件

        // <ServiceName, List<ComponentName>>
        Map<String, List<String>> serviceNameComponentNameListWithPluginMap = request.getServiceNameList()
                .stream()
                .collect(Collectors.toMap(
                        serviceName -> serviceName,
                        serviceName -> {
                            List<YamlServiceDetail.Component> components = ResolverYamlServiceDetail.COMPONENT_LIST_MAP.get(serviceName);

                            // 如果 components 可能为 null，需进行空检查
                            if (components == null) {
                                return Collections.emptyList();
                            }

                            return components.stream()
                                    .filter(c -> c.getActions() != null && c.getActions()
                                            .stream()
                                            .anyMatch(action -> action.getType() == ActionTypeEnum.ENABLE_RANGER_PLUGIN)
                                    )
                                    .map(YamlServiceDetail.Component::getName)
                                    .collect(Collectors.toList());
                        }
                ));

        // 4.2 根据 4.1 的组件名称，找到组件这些组件的分布节点，如果同服务下，不同的组件，存在于相同节点，则跳过该组件(即仅执行一次Ranger组件操作即可)
        List<JobDetailRequest.JobDetailServiceRequest> jobDetailServiceRequestList = serviceNameComponentNameListWithPluginMap.keySet()
                .stream()
                .map(serviceName -> {
                    List<AbstractServiceComponentVo.ComponentSummaryVo> componentSummaryList = this.masterComponentService.getComponentList(
                            request.getClusterId(),
                            serviceName
                    ).getData().getServiceComponentSummaryList().
                            get(0)
                            .getComponentSummaryList()
                            .stream()
                            .filter(i -> serviceNameComponentNameListWithPluginMap.get(serviceName).contains(i.getComponentName()))
                            .collect(Collectors.toList());

                    // 同服务节点去重
                    Set<Long> alreadyNodeIdSet = ConcurrentHashMap.newKeySet();

                    // Map<ComponentName, List<JobDetailNodeRequest>>
                    Map<String, List<JobDetailRequest.JobDetailNodeRequest>> componentJobDetailNodeRequestMap = new HashMap<>();

                    for (AbstractServiceComponentVo.ComponentSummaryVo componentSummaryVo : componentSummaryList) {
                        for (AbstractServiceComponentVo.ComponentNodeVo componentNodeVo : componentSummaryVo.getComponentNodeList()) {
                            // 同服务节点去重
                            if(alreadyNodeIdSet.add(componentNodeVo.getNodeId())){
                                JobDetailRequest.JobDetailNodeRequest jobDetailNodeRequest = new JobDetailRequest.JobDetailNodeRequest();
                                jobDetailNodeRequest.setNodeId(componentNodeVo.getNodeId());
                                jobDetailNodeRequest.setNodeIp(componentNodeVo.getNodeIp());
                                jobDetailNodeRequest.setHostname(componentNodeVo.getHostname());

                                // 将当前组件以及所在节点信息添加到集合
                                componentJobDetailNodeRequestMap.computeIfAbsent(
                                        componentSummaryVo.getComponentName(),
                                        k -> new ArrayList<>()
                                ).add(jobDetailNodeRequest);
                            }
                        }

                    }

                    List<String> componentNameList = serviceNameComponentNameListWithPluginMap.get(serviceName);

                    List<JobDetailRequest.JobDetailComponentRequest> jobDetailComponentRequestList = componentNameList
                            .stream()
                            .filter(componentName -> componentJobDetailNodeRequestMap.get(componentName) != null)
                            .map(componentName -> {
                                JobDetailRequest.JobDetailComponentRequest jobDetailComponentRequest = new JobDetailRequest.JobDetailComponentRequest();
                                jobDetailComponentRequest.setComponentName(componentName);
                                jobDetailComponentRequest.setJobDetailNodeList(componentJobDetailNodeRequestMap.get(componentName));

                                return jobDetailComponentRequest;
                            })
                            .collect(Collectors.toList());

                    JobDetailRequest.JobDetailServiceRequest jobDetailServiceRequest = new JobDetailRequest.JobDetailServiceRequest();

                    jobDetailServiceRequest.setServiceName(serviceName);
                    jobDetailServiceRequest.setJobDetailComponentList(jobDetailComponentRequestList);

                    return jobDetailServiceRequest;
                })
                .collect(Collectors.toList());

        jobDetailRequest.setJobDetailServiceList(jobDetailServiceRequestList);

        log.info(jobDetailRequest.toString());

        // 5. 执行部署
        Long jobId = this.masterJobService.initJob(
                jobDetailRequest,
                this.masterJobService.isPriorityAsc(request.getActionTypeEnum())
        );

        // 6. 记录部署 Procedure
        this.masterInitProcedureService.persistServiceComponentProcedure(
                request.getClusterId(),
                jobId,
                ProcedureStateEnum.PROCEDURE_RANGER_PLUGIN_DEPLOYING
        );

        return Result.success(
                new AbstractJobVo.JobIdVo(
                        request.getClusterId(),
                        jobId
                )
        );
    }
}
