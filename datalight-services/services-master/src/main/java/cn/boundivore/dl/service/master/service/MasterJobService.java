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
import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.master.JobDetailRequest;
import cn.boundivore.dl.base.request.impl.master.JobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractClusterVo;
import cn.boundivore.dl.base.response.impl.master.AbstractJobVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.mapper.custom.ComponentNodeMapper;
import cn.boundivore.dl.orm.po.custom.ComponentNodeDto;
import cn.boundivore.dl.orm.po.single.*;
import cn.boundivore.dl.orm.service.single.impl.*;
import cn.boundivore.dl.service.master.manage.service.bean.*;
import cn.boundivore.dl.service.master.manage.service.job.Intention;
import cn.boundivore.dl.service.master.manage.service.job.Job;
import cn.boundivore.dl.service.master.manage.service.job.JobCacheUtil;
import cn.boundivore.dl.service.master.manage.service.job.Plan;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 组装服务组件的操作意图，并初始化 Job，最后执行
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
public class MasterJobService {

    /**
     * 数据库操作
     */
    private final MasterClusterService masterClusterService;
    private final MasterServiceService masterServiceService;

    private final ComponentNodeMapper componentNodeMapper;

    private final TDlJobLogServiceImpl tDlJobLogService;

    private final TDlJobServiceImpl tDlJobService;
    private final TDlStageServiceImpl tDlStageService;
    private final TDlTaskServiceImpl tDlTaskService;
    private final TDlStepServiceImpl tDlStepService;

    /**
     * Description: 根据操作的行为类型，以确定后续应该获取对应的组件状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param actionTypeEnum 操作的行为类型
     * @return List<SCStateEnum> 后续应该获取对应的组件状态
     */
    public List<SCStateEnum> getComponentStateFilterList(ActionTypeEnum actionTypeEnum) {
        final List<SCStateEnum> componentSCSStateEnumList = new ArrayList<>();
        switch (actionTypeEnum) {
            case DEPLOY:
                componentSCSStateEnumList.add(SCStateEnum.SELECTED);
                componentSCSStateEnumList.add(SCStateEnum.SELECTED_ADDITION);
                break;
            case START:
                componentSCSStateEnumList.add(SCStateEnum.STOPPED);
                break;
            case STOP:
                componentSCSStateEnumList.add(SCStateEnum.STARTED);
                componentSCSStateEnumList.add(SCStateEnum.STOPPED);
                break;
            case RESTART:
                componentSCSStateEnumList.add(SCStateEnum.STARTED);
                componentSCSStateEnumList.add(SCStateEnum.STOPPED);
                break;
            case REMOVE:
                componentSCSStateEnumList.add(SCStateEnum.STOPPED);
                break;
            case DECOMMISSION:
                break;
        }

        return componentSCSStateEnumList;
    }

    /**
     * Description: 根据当前集群 ID 获取本集群以及关联集群信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return ClusterMeta 当前集群以及关联集群元数据信息
     */
    public ClusterMeta getClusterMeta(Long clusterId) {
        // 根据当前集群 ID 获取本集群信息
        AbstractClusterVo.ClusterVo currentCluster = masterClusterService
                .getClusterById(clusterId)
                .getData();
        ClusterMeta clusterMeta = new ClusterMeta()
                .setCurrentClusterId(currentCluster.getClusterId())
                .setCurrentClusterName(currentCluster.getClusterName())
                .setCurrentClusterTypeEnum(currentCluster.getClusterTypeEnum());

        // 当前集群如果是计算集群，获取所依赖的集群信息
        if (currentCluster.getClusterTypeEnum() == ClusterTypeEnum.COMPUTE) {
            AbstractClusterVo.ClusterVo relativeCluster = masterClusterService
                    .getClusterRelative(clusterId)
                    .getData();

            clusterMeta
                    .setRelativeCusterId(relativeCluster.getClusterId())
                    .setRelativeClusterName(relativeCluster.getClusterName())
                    .setRelativeClusterTypeEnum(relativeCluster.getClusterTypeEnum());
        }

        return clusterMeta;
    }

    /**
     * Description: 以 ServiceName 为入口，开始生成作业计划，
     * 并部署 or 启动 or 停止 or 重启等对应服务下的所有组件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param request       将要部署的目标集群和将要操作的服务
     * @param isPriorityAsc 决定按照服务以及组件的正序优先级还是逆序优先级生成任务，
     *                      例如先启动的服务，在执行关闭相关操作的计划时，可能最后关闭
     * @return Long 任务构建成功且开始运行，返回 JobId
     */
    public Long initJob(JobRequest request, boolean isPriorityAsc) throws Exception {

        // 之前必须经过检查，以确保下面的数据的严谨性和操作的严谨性
        // 对准备部署的服务按照服务优先级进行排序，部署过程中，将按照服务优先级升序逐个部署，即此时 isPriorityAsc = true
        // 根据优先级升序或降序排序服务列表, 如果是其他操作，可通过 isPriorityAsc 调整执行顺序（true 为正序，false 为倒序）
        List<TDlService> tDlServiceList = this.masterServiceService.getTDlServiceListSorted(
                request.getClusterId(),
                request.getServiceNameList(),
                isPriorityAsc
        );

        // 根据当前集群 ID 获取本集群以及关联集群信息
        ClusterMeta clusterMeta = this.getClusterMeta(request.getClusterId());

        // 创建 Job 任务意图
        final Intention intention = new Intention()
                .setClusterMeta(clusterMeta)
                .setActionTypeEnum(request.getActionTypeEnum())
                .setOneByOne(request.getIsOneByOne())
                .setServiceList(
                        tDlServiceList.stream()
                                .map(i -> this.intentionService(
                                                i,
                                                isPriorityAsc,
                                                this.getComponentStateFilterList(request.getActionTypeEnum())
                                        )
                                )
                                .collect(Collectors.toList())
                );

        // 创建并执行 Job
        final Job job = new Job(intention).init();
        job.start();

        return job.getJobMeta().getId();
    }


    /**
     * Description: 以 ServiceName 以及 Component 为入口，开始生成作业计划，
     * 并启动 or 停止 or 重启等对应服务下指定的组件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param request       将要部署的目标集群和将要操作的服务以及组件
     * @param isPriorityAsc 决定按照服务以及组件的正序优先级还是逆序优先级生成任务，
     *                      例如先启动的服务，在执行关闭相关操作的计划时，可能最后关闭
     * @return Long 任务构建成功且开始运行，返回 JobId
     */
    public Long initJob(JobDetailRequest request, boolean isPriorityAsc) throws Exception {

        // 之前必须经过检查，以确保下面的数据的严谨性和操作的严谨性
        // 对准备部署的服务按照服务优先级进行排序，部署过程中，将按照服务优先级升序逐个部署，即此时 isPriorityAsc = true
        // 根据优先级升序或降序排序服务列表, 如果是其他操作，可通过 isPriorityAsc 调整执行顺序（true 为正序，false 为倒序）
        List<TDlService> tDlServiceList = this.masterServiceService.getTDlServiceListSorted(
                request.getClusterId(),
                request.getJobDetailServiceList()
                        .stream()
                        .map(JobDetailRequest.JobDetailServiceRequest::getServiceName)
                        .collect(Collectors.toList()
                        ),
                isPriorityAsc
        );

        // 根据当前集群 ID 获取本集群以及关联集群信息
        ClusterMeta clusterMeta = this.getClusterMeta(request.getClusterId());

        // 获取当前服务下用户对于特定节点上的特定组件的操作列表
        // Map<ServiceName, List<JobDetailRequest.JobDetailComponentRequest>>
        Map<String, List<JobDetailRequest.JobDetailComponentRequest>> serviceNameToComponentListMap = request
                .getJobDetailServiceList()
                .stream()
                .collect(
                        Collectors.toMap(
                                // 键：ServiceName
                                JobDetailRequest.JobDetailServiceRequest::getServiceName,
                                // 值：JobDetailComponentList
                                JobDetailRequest.JobDetailServiceRequest::getJobDetailComponentList,
                                // 如果有重复的 ServiceName，合并列表
                                (existing, replacement) -> {
                                    existing.addAll(replacement);
                                    return existing;
                                }
                        )
                );


        // 创建 Job 任务意图
        final Intention intention = new Intention()
                .setClusterMeta(clusterMeta)
                .setActionTypeEnum(request.getActionTypeEnum())
                .setOneByOne(request.getIsOneByOne())
                .setServiceList(
                        tDlServiceList.stream()
                                .map(i -> this.intentionService(
                                                i,
                                                isPriorityAsc,
                                                this.getComponentStateFilterList(request.getActionTypeEnum()),
                                                serviceNameToComponentListMap.get(i.getServiceName())
                                        )
                                )
                                .collect(Collectors.toList())
                );

        // 创建并执行 Job
        final Job job = new Job(intention).init();
        job.start();

        return job.getJobMeta().getId();
    }

    /**
     * Description: 组装 Job 意图中的 Service
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlService    某个服务的相关数据库信息
     * @param isPriorityAsc 优先级正序或逆序
     * @return 返回 Job 任务意图中的 Service 信息
     */
    private Intention.Service intentionService(TDlService tDlService,
                                               boolean isPriorityAsc,
                                               List<SCStateEnum> componentSCSStateEnumList) {

        //通过 JOIN 关联 Component 和 Node 表，查找出所有组件分布到所有节点的情况
        List<ComponentNodeDto> componentNodeDtoList = this.componentNodeMapper.selectComponentNodeInStatesDto(
                tDlService.getClusterId(),
                tDlService.getServiceName(),
                componentSCSStateEnumList
        );

        Assert.notEmpty(
                componentNodeDtoList,
                () -> new BException("未找到满足操作意图的组件项")
        );


        //当前服务
        //按照组件名称进行分组，一个组件可能会在多个节点中部署
        return new Intention.Service()
                .setServiceName(tDlService.getServiceName())
                .setPriority(tDlService.getPriority())
                .setComponentList(
                        componentNodeDtoList.stream()
                                .collect(Collectors.groupingBy(ComponentNodeDto::getComponentName))
                                .values()
                                .stream()
                                .map(this::intentionComponent)
                                .sorted(
                                        (o1, o2) -> isPriorityAsc ?
                                                o1.getPriority().compareTo(o2.getPriority()) :
                                                o2.getPriority().compareTo(o1.getPriority())
                                )
                                .collect(Collectors.toList())

                );
    }

    /**
     * Description: 组装 Job 意图中的 Service
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlService    某个服务的相关数据库信息
     * @param isPriorityAsc 优先级正序或逆序
     * @return 返回 Job 任务意图中的 Service 信息
     */
    private Intention.Service intentionService(TDlService tDlService,
                                               boolean isPriorityAsc,
                                               List<SCStateEnum> componentSCSStateEnumList,
                                               List<JobDetailRequest.JobDetailComponentRequest> jobDetailComponentList) {

        //通过 JOIN 关联 Component 和 Node 表，查找出所有组件分布到所有节点的情况
        List<ComponentNodeDto> componentNodeDtoList = this.componentNodeMapper.selectComponentNodeInStatesDto(
                tDlService.getClusterId(),
                tDlService.getServiceName(),
                componentSCSStateEnumList
        );

        Assert.notEmpty(
                componentNodeDtoList,
                () -> new BException("未找到满足操作意图的组件项")
        );

        // 获取当前服务下用户对于特定节点上的特定组件的操作列表
        // Map<ComponentName, List<JobDetailRequest.JobDetailNodeRequest>>
        Map<String, List<JobDetailRequest.JobDetailNodeRequest>> componentNameToJobNodeListMap = jobDetailComponentList
                .stream()
                .collect(
                        Collectors.toMap(
                                // 键：ComponentName
                                JobDetailRequest.JobDetailComponentRequest::getComponentName,
                                // 值：JobDetailComponentRequest
                                JobDetailRequest.JobDetailComponentRequest::getJobDetailNodeList,
                                // 如果有重复的 ComponentName，合并列表
                                (existing, replacement) -> {
                                    existing.addAll(replacement);
                                    return existing;
                                }
                        )
                );

        // <ComponentName, List<ComponentNodeDto>>
        Map<String, List<ComponentNodeDto>> componentNameNodeMap = componentNodeDtoList
                .stream()
                .collect(Collectors.groupingBy(ComponentNodeDto::getComponentName));

        //当前服务
        //按照组件名称进行分组，一个组件可能会在多个节点中部署
        return new Intention.Service()
                .setServiceName(tDlService.getServiceName())
                .setPriority(tDlService.getPriority())
                .setComponentList(
                        componentNameNodeMap
                                .values()
                                .stream()
                                .filter(i -> componentNameToJobNodeListMap.containsKey(CollUtil.getFirst(i).getComponentName()))
                                .map(i -> this.intentionComponent(
                                                i,
                                                componentNameToJobNodeListMap.get(CollUtil.getFirst(i).getComponentName())
                                        )
                                )
                                .sorted(
                                        (o1, o2) -> isPriorityAsc ?
                                                o1.getPriority().compareTo(o2.getPriority()) :
                                                o2.getPriority().compareTo(o1.getPriority())
                                )
                                .collect(Collectors.toList())

                );
    }

    /**
     * Description: 组装 Job 意图中的 Component
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param componentNodeDtoList 当前服务下的某个组件（对应多个节点）
     * @return 返回 Job 任务意图中的 Component 信息
     */
    private Intention.Component intentionComponent(List<ComponentNodeDto> componentNodeDtoList) {

        //当前组中所有元素均为相同的 ComponentName，任意取出一个获得 ComponentName 和 Priority 即可
        ComponentNodeDto first = CollUtil.getFirst(componentNodeDtoList);

        //包含当前服务下，当前组件中涉及到的所有节点
        return new Intention.Component()
                .setComponentName(first.getComponentName())
                .setPriority(first.getComponentPriority())
                .setNodeList(
                        componentNodeDtoList.stream()
                                .map(c -> new Intention.Node()
                                        .setNodeId(c.getNodeId())
                                        .setNodeIp(c.getIpv4())
                                        .setHostname(c.getHostname()))
                                .collect(Collectors.toList())
                );
    }

    /**
     * Description: 组装 Job 意图中的 Component
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param componentNodeDtoList 当前服务下的某个组件（对应多个节点）
     * @param jobDetailNodeList    本次请求涉及到的节点
     * @return 返回 Job 任务意图中的 Component 信息
     */
    private Intention.Component intentionComponent(List<ComponentNodeDto> componentNodeDtoList,
                                                   List<JobDetailRequest.JobDetailNodeRequest> jobDetailNodeList) {

        //当前组中所有元素均为相同的 ComponentName，任意取出一个获得 ComponentName 和 Priority 即可
        ComponentNodeDto first = CollUtil.getFirst(componentNodeDtoList);

        // 获取当前服务下用户对于特定节点上的特定组件的操作列表
        // Map<NodeId, JobDetailRequest.JobDetailNodeRequest>
        final Map<Long, JobDetailRequest.JobDetailNodeRequest> nodeIdToNodeRequestMap = jobDetailNodeList
                .stream()
                .collect(Collectors.toMap(
                                // 键：NodeId
                                JobDetailRequest.JobDetailNodeRequest::getNodeId,
                                // 值：JobDetailNodeRequest 对象本身
                                nodeRequest -> nodeRequest
                        )
                );

        //包含当前服务下，当前组件中涉及到的所有节点
        return new Intention.Component()
                .setComponentName(first.getComponentName())
                .setPriority(first.getComponentPriority())
                .setNodeList(
                        componentNodeDtoList.stream()
                                .filter(i -> nodeIdToNodeRequestMap.containsKey(i.getNodeId()))
                                .map(c -> new Intention.Node()
                                        .setNodeId(c.getNodeId())
                                        .setNodeIp(c.getIpv4())
                                        .setHostname(c.getHostname()))
                                .collect(Collectors.toList())
                );
    }


    /**
     * Description: 检查待部署服务是否存在正确的配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException 当配置不正确，抛出 BException 异常
     *
     * @param serviceName 请求准备部署的服务
     */
    private void checkServiceSettings(String serviceName) throws BException {
        Assert.isTrue(
                ResolverYamlServiceDetail.SERVICE_MAP.containsKey(serviceName),
                () -> new BException(String.format("发现未配置的服务: %s", serviceName))
        );
    }


    /**
     * Description: 获取指定 Job 的进度信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param jobId 服务、组件相关的作业 ID
     * @return Result<AbstractJobVo.JobProgressVo> 进度信息
     */
    public Result<AbstractJobVo.JobProgressVo> getJobProgress(Long jobId) {
        JobCacheBean jobCacheBean = JobCacheUtil.getInstance().get(jobId);
        if (jobCacheBean == null) {
            // 内存缓存已失效，从数据库中读取
            jobCacheBean = this.getJobCacheBeanFromDb(jobId);
        }

        Assert.notNull(
                jobCacheBean,
                () -> new BException("JobId 不存在")
        );

        // 获取 Job 的元数据信息
        JobMeta jobMeta = jobCacheBean.getJobMeta();
        // 获取 Job 的计划信息
        Plan plan = jobCacheBean.getPlan();

        // 获取 Job 相关信息
        Long clusterId = jobMeta.getClusterMeta().getCurrentClusterId();
        ActionTypeEnum jobActionTypeEnum = jobMeta.getActionTypeEnum();
        ExecStateEnum jobExecStateEnum = jobMeta.getExecStateEnum();

        // 获取 Plan 相关信息
        String planName = plan.getPlanName();
        int planTotal = plan.getPlanTotal();
        int planCurrent = plan.getPlanCurrent();
        int planProgress = plan.getPlanProgress();

        int execTotal = plan.getExecTotal().get();
        int execCurrent = plan.getExecCurrent().get();
        int execProgress = plan.getExecProgress().get();

        // 创建结果对象
        AbstractJobVo.JobProgressVo jobProgressVo = new AbstractJobVo.JobProgressVo()
                .setJobId(jobId)
                .setClusterId(clusterId);

        // 组装计划进度信息
        AbstractJobVo.JobPlanProgressVo jobPlanProgressVo = new AbstractJobVo.JobPlanProgressVo(
                clusterId,
                jobId,
                jobActionTypeEnum,
                planTotal,
                planCurrent,
                planProgress,
                planName
        );
        jobProgressVo.setJobPlanProgressVo(jobPlanProgressVo);

        // 组装执行进度信息
        AbstractJobVo.JobExecProgressVo jobExecProgressVo = new AbstractJobVo.JobExecProgressVo(
                jobExecStateEnum,
                clusterId,
                jobId,
                execTotal,
                execCurrent,
                execProgress,
                new ArrayList<>()
        );
        jobProgressVo.setJobExecProgressVo(jobExecProgressVo);

        // 组装每个节点的执行进度信息
        List<AbstractJobVo.ExecProgressPerNodeVo> execProgressPerNodeList = this.createExecProgressPerNodeList(jobMeta);
        jobExecProgressVo.setExecProgressPerNodeList(execProgressPerNodeList);

        return Result.success(jobProgressVo);
    }


    /**
     * Description: 创建每个节点执行进度 Vo
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param jobMeta Job 元数据信息
     * @return List<AbstractJobVo.ExecProgressPerNodeVo> 每个节点进度信息
     */
    private List<AbstractJobVo.ExecProgressPerNodeVo> createExecProgressPerNodeList(JobMeta jobMeta) {
        List<AbstractJobVo.ExecProgressPerNodeVo> execProgressPerNodeList = new ArrayList<>();

        final TreeMap<String, List<StepMeta>> hostnameToOrderedStepMetaMap = this.getHostnameStepMetaTreeMap(jobMeta);

        // 将每个节点所有 Step 组装到对应集合 Vo 中
        hostnameToOrderedStepMetaMap.forEach(
                (hostname, stepMetaList) -> {
                    TaskMeta taskMeta = stepMetaList.get(0).getTaskMeta();
                    AbstractJobVo.ExecProgressPerNodeVo execProgressPerNodeVo = this.createExecProgressPerNodeVo(
                            taskMeta.getNodeId(),
                            taskMeta.getHostname(),
                            taskMeta.getNodeIp(),
                            stepMetaList
                    );
                    execProgressPerNodeList.add(execProgressPerNodeVo);

                    this.assembleExecProgressStepList(
                            execProgressPerNodeVo,
                            stepMetaList
                    );
                }
        );

        return execProgressPerNodeList;
    }

    /**
     * Description: 根据 JobMeta 获取每个节点上所有 StepMeta 集合
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param jobMeta 作业的元数据信息
     * @return TreeMap<String, List < StepMeta>> 以节点纬度，按顺序存储该节点上所有 StepMeta 信息
     */
    @NotNull
    private TreeMap<String, List<StepMeta>> getHostnameStepMetaTreeMap(JobMeta jobMeta) {
        final LinkedHashMap<Long, StageMeta> stageMetaMap = jobMeta.getStageMetaMap();

        // 以节点纬度，按顺序存储该节点上所有 StepMeta 信息 <Hostname, List<StepMeta>>
        final TreeMap<String, List<StepMeta>> nodeIdToOrderedStepMetaMap = new TreeMap<>();

        // 按照执行顺序，整理每个节点的 StepMeta，最后多个 StepMetaList 返回时按照节点 hostname 字母自然排序
        stageMetaMap.forEach(
                (stageId, stageMeta) ->
                        stageMeta.getTaskMetaMap().forEach(
                                (taskId, taskMeta) -> {
                                    // 注意这里不需要显式地放回 stepMetaList，因为 computeIfAbsent 已经做了这个工作
                                    List<StepMeta> stepMetaList = nodeIdToOrderedStepMetaMap.computeIfAbsent(
                                            taskMeta.getHostname(),
                                            k -> new ArrayList<>()
                                    );

                                    stepMetaList.addAll(new ArrayList<>(taskMeta.getStepMetaMap().values()));
                                }
                        )
        );
        return nodeIdToOrderedStepMetaMap;
    }

    /**
     * Description: 创建每个节点的执行进度信息对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param stepMetaList 某一节点下所有 Task 下所有 Step 元数据信息
     * @return 每个节点的执行进度信息对象
     */
    private AbstractJobVo.ExecProgressPerNodeVo createExecProgressPerNodeVo(
            long nodeId,
            String hostname,
            String nodeIp,
            List<StepMeta> stepMetaList) {
        int[] progressArr = this.calculatePerNodeProgress(stepMetaList);

        return new AbstractJobVo.ExecProgressPerNodeVo(
                nodeId,
                hostname,
                nodeIp,
                progressArr[0],
                progressArr[1],
                progressArr[2],
                new ArrayList<>()
        );
    }

    /**
     * Description: 计算每个节点上任务的进度
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param stepMetaList 某一节点下所有 Task 下所有 Step 元数据信息
     * @return 每个节点上任务的进度数组
     */
    private int[] calculatePerNodeProgress(List<StepMeta> stepMetaList) {

        int execTotal = stepMetaList.size();
        int execCurrent = 0;
        int execProgress = 0;

        for (StepMeta stepMeta : stepMetaList) {
            switch (stepMeta.getExecStateEnum()) {
                case SUSPEND:
                case RUNNING:
                case ERROR:
                    break;
                case OK:
                    execCurrent++;
                    break;
            }
        }

        execProgress = execCurrent * 100 / execTotal;

        return new int[]{execTotal, execCurrent, execProgress};
    }


    /**
     * Description: 填充任务的步骤执行进度信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param execProgressPerNodeVo 任务的执行进度信息对象
     * @param stepMetaList          某一节点下所有 Task 下所有 Step 元数据信息
     */
    private void assembleExecProgressStepList(AbstractJobVo.ExecProgressPerNodeVo execProgressPerNodeVo,
                                              List<StepMeta> stepMetaList) {

        stepMetaList.forEach(
                stepMeta -> {
                    AbstractJobVo.ExecProgressStepVo execProgressStepVo = new AbstractJobVo.ExecProgressStepVo(
                            stepMeta.getType(),
                            stepMeta.getId(),
                            stepMeta.getName(),
                            stepMeta.getExecStateEnum()
                    );
                    execProgressPerNodeVo.getExecProgressStepList().add(execProgressStepVo);
                }
        );
    }

    /**
     * Description: 获取作业日志列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @param jobId     作业 ID
     * @param stageId   阶段 ID
     * @param taskId    任务 ID
     * @param stepId    步骤 ID
     * @return Result<AbstractJobVo.JobLogListVo> 日志信息列表
     */
    public Result<AbstractJobVo.JobLogListVo> getJobLogList(Long clusterId,
                                                            Long jobId,
                                                            Long stageId,
                                                            Long taskId,
                                                            Long stepId) {

        LambdaQueryChainWrapper<TDlJobLog> tDlJobLogWrapper = this.tDlJobLogService.lambdaQuery()
                .select()
                .eq(TDlJobLog::getClusterId, clusterId)
                .eq(TDlJobLog::getJobId, jobId);

        if (stageId != null) {
            tDlJobLogWrapper = tDlJobLogWrapper.eq(TDlJobLog::getStageId, stageId);
        }

        if (taskId != null) {
            tDlJobLogWrapper = tDlJobLogWrapper.eq(TDlJobLog::getTaskId, taskId);
        }

        if (stepId != null) {
            tDlJobLogWrapper = tDlJobLogWrapper.eq(TDlJobLog::getStepId, stepId);
        }

        List<TDlJobLog> tDlJobLogList = tDlJobLogWrapper.list();
        String tag = tDlJobLogList.isEmpty() ? null : tDlJobLogList.get(0).getTag();

        List<AbstractJobVo.JobLogVo> jobLogList = tDlJobLogList
                .stream()
                .map(i -> new AbstractJobVo.JobLogVo(
                                i.getJobId(),
                                i.getStageId(),
                                i.getTaskId(),
                                i.getStepId(),
                                i.getLogStdout(),
                                i.getLogErrout()
                        )
                )
                .collect(Collectors.toList());

        AbstractJobVo.JobLogListVo jobLogListVo = new AbstractJobVo.JobLogListVo(
                clusterId,
                tag,
                jobLogList
        );

        return Result.success(jobLogListVo);
    }

    /**
     * Description: 从数据库缓存中获取信息并组装 JobCacheBean
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/26
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param jobId 服务与组件操作（包含部署）的任务 ID
     * @return JobCacheBean
     */
    private JobCacheBean getJobCacheBeanFromDb(Long jobId) {

        TDlJob tDlJob = this.tDlJobService.getById(jobId);

        AbstractClusterVo.ClusterVo currentClusterVo = this.masterClusterService.getClusterById(tDlJob.getClusterId()).getData();
        AbstractClusterVo.ClusterVo relativeClusterVo = this.masterClusterService.getClusterById(currentClusterVo.getRelativeClusterId()).getData();

        // 获取服务状态
        HashMap<String, TDlService> nameAndTDlServiceMap = this.masterServiceService.getTDlServiceList(tDlJob.getClusterId())
                .stream()
                .collect(Collectors.toMap(
                                TDlService::getServiceName,
                                Function.identity(),
                                (existing, replacement) -> existing,
                                HashMap::new
                        )
                );

        List<TDlStage> tDlStageList = this.tDlStageService.lambdaQuery()
                .select()
                .eq(TDlStage::getJobId, jobId)
                .list();

        List<TDlTask> tDlTaskList = this.tDlTaskService.lambdaQuery()
                .select()
                .eq(TDlTask::getJobId, jobId)
                .list();


        List<TDlStep> tDlStepList = this.tDlStepService.lambdaQuery()
                .select()
                .eq(TDlStep::getJobId, jobId)
                .list();

        // 获取 JobMeta 相关信息
        final JobMeta jobMeta = this.recoverJobMeta(
                tDlJob,
                currentClusterVo,
                relativeClusterVo,
                nameAndTDlServiceMap,
                tDlStageList,
                tDlTaskList,
                tDlStepList
        );


        // 获取 Plan 相关信息
        String planName = CollUtil.getLast(tDlStepList).getStepName();
        int planTotal = tDlStepList.size();
        int planCurrent = tDlStepList.size();
        int planProgress = 100;

        int execTotal = tDlStepList.size();
        int execCurrent = (int) tDlStepList.stream().filter(i -> i.getStepState() == ExecStateEnum.OK).count();
        int execProgress = execCurrent * 100 / execTotal;

        final Plan plan = new Plan(
                planName,
                planTotal,
                planCurrent,
                planProgress,
                execTotal,
                execCurrent,
                execProgress
        );


        return new JobCacheBean(
                jobMeta,
                plan
        );
    }

    private JobMeta recoverJobMeta(TDlJob tDlJob,
                                   AbstractClusterVo.ClusterVo currentClusterVo,
                                   AbstractClusterVo.ClusterVo relativeClusterVo,
                                   HashMap<String, TDlService> nameAndTDlServiceMap,
                                   List<TDlStage> tDlStageList,
                                   List<TDlTask> tDlTaskList,
                                   List<TDlStep> tDlStepList) {
        final JobMeta jobMeta = new JobMeta();
        jobMeta.setStartTime(tDlJob.getStartTime());
        jobMeta.setEndTime(tDlJob.getEndTime());

        jobMeta.setId(tDlJob.getId());
        jobMeta.setTag(tDlJob.getTag());
        jobMeta.setClusterMeta(
                new ClusterMeta(
                        currentClusterVo.getClusterId(),
                        currentClusterVo.getClusterName(),
                        currentClusterVo.getClusterTypeEnum(),
                        currentClusterVo.getRelativeClusterId(),
                        relativeClusterVo.getClusterName(),
                        relativeClusterVo.getClusterTypeEnum()
                )
        );
        jobMeta.setActionTypeEnum(tDlJob.getJobActionType());
        jobMeta.setName(tDlJob.getJobName());
        jobMeta.setExecStateEnum(tDlJob.getJobState());
        jobMeta.setJobResult(
                new JobMeta.JobResult(
                        tDlJob.getJobState() == ExecStateEnum.OK
                )
        );
        // <StageId, StageMeta>
        jobMeta.setStageMetaMap(new LinkedHashMap<>());

        tDlStageList.forEach(tDlStage -> {
                    final StageMeta stageMeta = this.recoverStageMeta(
                            jobMeta,
                            tDlStage,
                            nameAndTDlServiceMap,
                            tDlTaskList,
                            tDlStepList
                    );
                    jobMeta.getStageMetaMap().put(stageMeta.getId(), stageMeta);
                }
        );

        return jobMeta;
    }

    private StageMeta recoverStageMeta(JobMeta jobMeta,
                                       TDlStage tDlStage,
                                       HashMap<String, TDlService> nameAndTDlServiceMap,
                                       List<TDlTask> tDlTaskList,
                                       List<TDlStep> tDlStepList) {

        TDlService tDlService = nameAndTDlServiceMap.get(tDlStage.getServiceName());
        StageMeta stageMeta = new StageMeta();
        stageMeta.setStartTime(tDlStage.getStartTime());
        stageMeta.setEndTime(tDlStage.getEndTime());

        stageMeta.setJobMeta(jobMeta);
        stageMeta.setId(tDlStage.getId());
        stageMeta.setName(tDlStage.getStageName());
        stageMeta.setServiceName(tDlStage.getServiceName());
        stageMeta.setCurrentState(tDlService.getServiceState());
        stageMeta.setPriority(tDlService.getPriority());
        stageMeta.setStageResult(new StageMeta.StageResult(tDlStage.getStageState() == ExecStateEnum.OK));
        stageMeta.setStageStateEnum(tDlStage.getStageState());
        stageMeta.setTaskMetaMap(new LinkedHashMap<>());

        tDlTaskList.forEach(tDlTask -> {
            final TaskMeta taskMeta = this.recoverTaskMeta(
                    tDlTask,
                    stageMeta,
                    tDlStepList
            );
            stageMeta.getTaskMetaMap().put(taskMeta.getId(), taskMeta);
        });

        return stageMeta;
    }

    private TaskMeta recoverTaskMeta(TDlTask tDlTask,
                                     StageMeta stageMeta,
                                     List<TDlStep> tDlStepList) {
        return null;
    }


    private TaskMeta recoverStepMeta(TDlStep tDlStep,
                                     TaskMeta taskMeta) {
        return null;
    }

}
