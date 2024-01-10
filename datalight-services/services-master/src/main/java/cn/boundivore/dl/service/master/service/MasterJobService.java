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

import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.master.JobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractClusterVo;
import cn.boundivore.dl.base.response.impl.master.AbstractJobVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.mapper.custom.ComponentNodeMapper;
import cn.boundivore.dl.orm.po.custom.ComponentNodeDto;
import cn.boundivore.dl.orm.po.single.TDlService;
import cn.boundivore.dl.service.master.manage.service.bean.ClusterMeta;
import cn.boundivore.dl.service.master.manage.service.bean.JobMeta;
import cn.boundivore.dl.service.master.manage.service.job.Intention;
import cn.boundivore.dl.service.master.manage.service.job.Job;
import cn.boundivore.dl.service.master.manage.service.job.JobCache;
import cn.boundivore.dl.service.master.manage.service.job.Plan;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /**
     * Description: 开始生成部署计划，并部署服务、组件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param request       将要部署的目标集群和将要部署的服务
     * @param isPriorityAsc 决定按照服务以及组件的正序优先级还是逆序优先级生成任务，
     *                      例如先启动的服务，在执行关闭相关操作的计划时，可能最后关闭
     * @return Long 任务构建成功且开始运行，返回 JobId
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Long initJob(JobRequest request, boolean isPriorityAsc) throws Exception {

        // 之前必须经过检查，以确保下面的数据的严谨性和操作的严谨性
        // 对准备部署的服务按照服务优先级进行排序，部署过程中，将按照服务优先级升序逐个部署，即此时 isPriorityAsc = true
        // 根据优先级升序或降序排序服务列表, 如果是其他操作，可通过 isPriorityAsc 调整执行顺序（true 为正序，false 为倒序）
        List<TDlService> tDlServiceList = this.masterServiceService.getTDlServiceListSorted(
                request.getClusterId(),
                request.getServiceNameList(),
                isPriorityAsc
        );

        // 根据当前集群 ID 获取本集群信息
        AbstractClusterVo.ClusterVo currentCluster = this.masterClusterService
                .getClusterById(request.getClusterId())
                .getData();
        ClusterMeta clusterMeta = new ClusterMeta()
                .setCurrentClusterId(currentCluster.getClusterId())
                .setCurrentClusterName(currentCluster.getClusterName())
                .setCurrentClusterTypeEnum(currentCluster.getClusterTypeEnum());

        // 当前集群如果是计算集群，获取所依赖的集群信息
        if (currentCluster.getClusterTypeEnum() == ClusterTypeEnum.COMPUTE) {
            AbstractClusterVo.ClusterVo relativeCluster = this.masterClusterService
                    .getClusterRelative(request.getClusterId())
                    .getData();

            clusterMeta
                    .setRelativeCusterId(relativeCluster.getClusterId())
                    .setRelativeClusterName(relativeCluster.getClusterName())
                    .setRelativeClusterTypeEnum(relativeCluster.getClusterTypeEnum());
        }


        // 创建 Job 任务意图
        final Intention intention = new Intention()
                .setClusterMeta(clusterMeta)
                .setActionTypeEnum(request.getActionTypeEnum())
                .setServiceList(
                        tDlServiceList.stream()
                                .map(i -> this.intentionService(
                                                i,
                                                isPriorityAsc
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
    private Intention.Service intentionService(TDlService tDlService, boolean isPriorityAsc) {

        //通过 JOIN 关联 Component 和 Node 表，查找出所有组件分布到所有节点的情况
        List<ComponentNodeDto> componentNodeDtoList = componentNodeMapper.selectComponentNodeInStatesDto(
                tDlService.getClusterId(),
                tDlService.getServiceName(),
                CollUtil.newArrayList(
                        SCStateEnum.SELECTED
                )
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
        Job job = JobCache.getInstance().get(jobId);

        Assert.notNull(
                job,
                () -> new BException("JobId 错误或内存缓存信息已失效，如有必要后续将支持从数据库中读取")
        );

        // 获取 Job 的元数据信息
        JobMeta jobMeta = job.getJobMeta();
        // 获取 Job 的计划信息
        Plan plan = job.getPlan();

        // 获取集群 ID
        Long clusterId = jobMeta.getClusterMeta().getCurrentClusterId();

        // 创建结果对象
        AbstractJobVo.JobProgressVo jobProgressVo = new AbstractJobVo.JobProgressVo()
                .setJobId(jobId)
                .setClusterId(clusterId);

        // 组装计划进度信息
        AbstractJobVo.JobPlanProgressVo jobPlanProgressVo = this.createJobPlanProgressVo(
                clusterId,
                jobId,
                jobMeta,
                plan
        );
        jobProgressVo.setJobPlanProgressVo(jobPlanProgressVo);

        // 组装执行进度信息
        AbstractJobVo.JobExecProgressVo jobExecProgressVo = this.createJobExecProgressVo(
                jobId,
                clusterId,
                plan
        );
        jobProgressVo.setJobExecProgressVo(jobExecProgressVo);

        // 组装每个节点的执行进度信息
        List<AbstractJobVo.ExecProgressPerNodeVo> execProgressPerNodeList = this.createExecProgressPerNodeList(jobMeta);
        jobExecProgressVo.setExecProgressPerNodeList(execProgressPerNodeList);

        return Result.success(jobProgressVo);
    }

    /**
     * Description: 创建 Job 计划制定进度 Vo
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @param jobId     Job ID
     * @param jobMeta   Job 元数据信息
     * @param plan      计划信息
     * @return AbstractJobVo.JobPlanProgressVo 计划制定进度
     */
    private AbstractJobVo.JobPlanProgressVo createJobPlanProgressVo(Long clusterId,
                                                                    Long jobId,
                                                                    JobMeta jobMeta,
                                                                    Plan plan) {

        AbstractJobVo.JobPlanProgressVo jobPlanProgressVo = new AbstractJobVo.JobPlanProgressVo();

        return jobPlanProgressVo;
    }

    /**
     * Description: 创建当前 Job 执行进度 Vo
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param jobId     Job ID
     * @param clusterId 集群 ID
     * @param plan      计划信息
     * @return AbstractJobVo.JobExecProgressVo Job 执行进度
     */
    private AbstractJobVo.JobExecProgressVo createJobExecProgressVo(Long jobId,
                                                                    Long clusterId,
                                                                    Plan plan) {
        AbstractJobVo.JobExecProgressVo jobExecProgressVo = new AbstractJobVo.JobExecProgressVo();

        return jobExecProgressVo;
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
        return null;
    }

}
