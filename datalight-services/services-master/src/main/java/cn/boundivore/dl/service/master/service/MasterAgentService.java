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

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.response.impl.common.AbstractLogFileVo;
import cn.boundivore.dl.base.response.impl.master.AbstractAgentVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.po.single.TDlCluster;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.po.single.TDlJob;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.orm.po.single.TDlService;
import cn.boundivore.dl.orm.service.single.impl.TDlClusterServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlComponentServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlJobServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlNodeServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlServiceServiceImpl;
import cn.boundivore.dl.service.master.resolver.ResolverYamlDirectory;
import cn.boundivore.dl.service.master.resolver.yaml.YamlDirectory;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 面向智能体服务的数据聚合逻辑。
 * datalight-services-ai 不直连数据库，集群元数据与节点日志一律经由这里取。
 * 这些方法对人同样开放，前端需要相同的聚合视图时可直接复用。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/1
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterAgentService {

    /**
     * 作业历史的默认返回条数与上限。上限防止一次把整张表拉进内存
     */
    private static final int JOB_HISTORY_DEFAULT_LIMIT = 20;
    private static final int JOB_HISTORY_MAX_LIMIT = 200;

    /**
     * 日志尾部的默认行数与上限
     */
    private static final int LOG_TAIL_DEFAULT_LINES = 200;
    private static final int LOG_TAIL_MAX_LINES = 2000;

    /**
     * 估算单行日志的平均字节数，用于把「读多少行」换算成偏移量区间。
     * 大数据组件的日志行普遍较长，取值偏大以免读不满预期行数
     */
    private static final int ESTIMATED_BYTES_PER_LINE = 260;

    /**
     * 视为节点异常的状态集合
     */
    private static final Set<NodeStateEnum> ABNORMAL_NODE_STATES = Set.of(
            NodeStateEnum.INACTIVE,
            NodeStateEnum.CHECK_ERROR,
            NodeStateEnum.PUSH_ERROR,
            NodeStateEnum.START_WORKER_ERROR
    );

    /**
     * 视为组件异常的状态集合。变更中与停止中属于过程态，不计入异常
     */
    private static final Set<SCStateEnum> ABNORMAL_COMPONENT_STATES = Set.of(
            SCStateEnum.STOPPED,
            SCStateEnum.DECOMMISSIONED
    );

    private final TDlClusterServiceImpl tDlClusterService;

    private final TDlNodeServiceImpl tDlNodeService;

    private final TDlServiceServiceImpl tDlServiceService;

    private final TDlComponentServiceImpl tDlComponentService;

    private final TDlJobServiceImpl tDlJobService;

    private final MasterLogFileReaderService masterLogFileReaderService;

    /**
     * Description: 获取集群全景快照。
     * 一次返回集群、节点、服务、组件与当前活跃作业，让调用方一次建立完整上下文，
     * 不必为拼一张全景图反复往返。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException 集群不存在时抛出
     *
     * @param clusterId 集群 ID
     * @return Result<AbstractAgentVo.ClusterSnapshotVo> 集群全景快照
     */
    public Result<AbstractAgentVo.ClusterSnapshotVo> getClusterSnapshot(Long clusterId) {

        final TDlCluster tDlCluster = this.tDlClusterService.getById(clusterId);
        Assert.notNull(
                tDlCluster,
                () -> new BException(String.format("集群不存在: ClusterId: %s", clusterId))
        );

        // 节点、服务、组件各查一次，关联在内存里拼，遵守单表查询的约定
        final List<TDlNode> tDlNodeList = this.tDlNodeService.lambdaQuery()
                .eq(TDlNode::getClusterId, clusterId)
                .list();

        final List<TDlService> tDlServiceList = this.tDlServiceService.lambdaQuery()
                .eq(TDlService::getClusterId, clusterId)
                .list();

        final List<TDlComponent> tDlComponentList = this.tDlComponentService.lambdaQuery()
                .eq(TDlComponent::getClusterId, clusterId)
                .list();

        // <NodeId, Hostname> 供组件反查主机名
        final Map<Long, String> nodeHostnameMap = tDlNodeList.stream()
                .collect(Collectors.toMap(TDlNode::getId, TDlNode::getHostname, (a, b) -> a));

        // <NodeId, 该节点上的组件数量>
        final Map<Long, Integer> nodeComponentCountMap = new HashMap<>();
        tDlComponentList.forEach(component ->
                nodeComponentCountMap.merge(component.getNodeId(), 1, Integer::sum)
        );

        final List<AbstractAgentVo.NodeBriefVo> nodeBriefList = tDlNodeList.stream()
                .sorted(Comparator.comparing(TDlNode::getHostname))
                .map(node -> AbstractAgentVo.NodeBriefVo.builder()
                        .nodeId(node.getId())
                        .hostname(node.getHostname())
                        .nodeState(node.getNodeState())
                        .cpuArch(node.getCpuArch())
                        .cpuCores(node.getCpuCores())
                        .ram(node.getRam())
                        .disk(node.getDisk())
                        .osVersion(node.getOsVersion())
                        .componentCount(nodeComponentCountMap.getOrDefault(node.getId(), 0))
                        .build()
                )
                .collect(Collectors.toList());

        // <ServiceName, 组件实例列表>
        final Map<String, List<AbstractAgentVo.ComponentBriefVo>> componentsByService = tDlComponentList.stream()
                .collect(Collectors.groupingBy(
                        TDlComponent::getServiceName,
                        Collectors.mapping(
                                component -> this.toComponentBrief(component, nodeHostnameMap),
                                Collectors.toList()
                        )
                ));

        final List<AbstractAgentVo.ServiceBriefVo> serviceBriefList = tDlServiceList.stream()
                .sorted(Comparator.comparing(TDlService::getPriority))
                .map(service -> AbstractAgentVo.ServiceBriefVo.builder()
                        .serviceName(service.getServiceName())
                        .serviceState(service.getServiceState())
                        .priority(service.getPriority())
                        .componentList(
                                componentsByService.getOrDefault(service.getServiceName(), new ArrayList<>())
                        )
                        .build()
                )
                .collect(Collectors.toList());

        return Result.success(
                AbstractAgentVo.ClusterSnapshotVo.builder()
                        .clusterId(tDlCluster.getId())
                        .clusterName(tDlCluster.getClusterName())
                        .clusterType(tDlCluster.getClusterType())
                        .clusterState(tDlCluster.getClusterState())
                        .clusterDesc(tDlCluster.getClusterDesc())
                        .dlcVersion(tDlCluster.getDlcVersion())
                        .snapshotTime(System.currentTimeMillis())
                        .nodeList(nodeBriefList)
                        .serviceList(serviceBriefList)
                        .activeJobId(this.findActiveJobId(clusterId))
                        .build()
        );
    }

    /**
     * Description: 获取集群健康摘要。
     * 只返回需要关注的异常项，正常的东西不占篇幅。调用方据此判断要不要深入排查。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException 集群不存在时抛出
     *
     * @param clusterId 集群 ID
     * @return Result<AbstractAgentVo.HealthSummaryVo> 健康摘要
     */
    public Result<AbstractAgentVo.HealthSummaryVo> getHealthSummary(Long clusterId) {

        final TDlCluster tDlCluster = this.tDlClusterService.getById(clusterId);
        Assert.notNull(
                tDlCluster,
                () -> new BException(String.format("集群不存在: ClusterId: %s", clusterId))
        );

        final List<TDlNode> tDlNodeList = this.tDlNodeService.lambdaQuery()
                .eq(TDlNode::getClusterId, clusterId)
                .list();

        final List<TDlComponent> tDlComponentList = this.tDlComponentService.lambdaQuery()
                .eq(TDlComponent::getClusterId, clusterId)
                .list();

        final Map<Long, String> nodeHostnameMap = tDlNodeList.stream()
                .collect(Collectors.toMap(TDlNode::getId, TDlNode::getHostname, (a, b) -> a));

        final List<String> issueList = new ArrayList<>();

        final List<AbstractAgentVo.NodeBriefVo> abnormalNodeList = tDlNodeList.stream()
                .filter(node -> ABNORMAL_NODE_STATES.contains(node.getNodeState()))
                .map(node -> AbstractAgentVo.NodeBriefVo.builder()
                        .nodeId(node.getId())
                        .hostname(node.getHostname())
                        .nodeState(node.getNodeState())
                        .cpuArch(node.getCpuArch())
                        .cpuCores(node.getCpuCores())
                        .ram(node.getRam())
                        .disk(node.getDisk())
                        .osVersion(node.getOsVersion())
                        .componentCount(0)
                        .build()
                )
                .collect(Collectors.toList());

        abnormalNodeList.forEach(node -> issueList.add(
                String.format("节点 %s 状态为 %s", node.getHostname(), node.getNodeState().getMessage())
        ));

        final List<AbstractAgentVo.ComponentBriefVo> abnormalComponentList = tDlComponentList.stream()
                .filter(component -> ABNORMAL_COMPONENT_STATES.contains(component.getComponentState()))
                .map(component -> this.toComponentBrief(component, nodeHostnameMap))
                .collect(Collectors.toList());

        abnormalComponentList.forEach(component -> issueList.add(
                String.format(
                        "组件 %s 在节点 %s 上状态为 %s",
                        component.getComponentName(),
                        component.getHostname(),
                        component.getComponentState().getMessage()
                )
        ));

        final List<AbstractAgentVo.ComponentBriefVo> needRestartComponentList = tDlComponentList.stream()
                .filter(component -> Boolean.TRUE.equals(component.getNeedRestart()))
                .map(component -> this.toComponentBrief(component, nodeHostnameMap))
                .collect(Collectors.toList());

        needRestartComponentList.forEach(component -> issueList.add(
                String.format(
                        "组件 %s 在节点 %s 上配置已变更，等待重启生效",
                        component.getComponentName(),
                        component.getHostname()
                )
        ));

        return Result.success(
                AbstractAgentVo.HealthSummaryVo.builder()
                        .clusterId(clusterId)
                        .healthy(issueList.isEmpty())
                        .snapshotTime(System.currentTimeMillis())
                        .abnormalNodeList(abnormalNodeList)
                        .abnormalComponentList(abnormalComponentList)
                        .needRestartComponentList(needRestartComponentList)
                        .issueList(issueList)
                        .build()
        );
    }

    /**
     * Description: 获取作业历史，按开始时间倒序。
     * 用于回溯集群最近做过哪些操作，排查「什么时候开始不正常的」。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @param limit     返回条数，为空取默认值，超过上限按上限截断
     * @return Result<AbstractAgentVo.JobHistoryVo> 作业历史
     */
    public Result<AbstractAgentVo.JobHistoryVo> getJobHistory(Long clusterId, Integer limit) {

        final int finalLimit = Math.min(
                limit == null || limit <= 0 ? JOB_HISTORY_DEFAULT_LIMIT : limit,
                JOB_HISTORY_MAX_LIMIT
        );

        final List<TDlJob> tDlJobList = this.tDlJobService.lambdaQuery()
                .eq(TDlJob::getClusterId, clusterId)
                .orderByDesc(TDlJob::getStartTime)
                .last(String.format("LIMIT %d", finalLimit))
                .list();

        final List<AbstractAgentVo.JobBriefVo> jobBriefList = tDlJobList.stream()
                .map(job -> AbstractAgentVo.JobBriefVo.builder()
                        .jobId(job.getId())
                        .jobName(job.getJobName())
                        .jobActionType(job.getJobActionType())
                        .jobState(job.getJobState())
                        .startTime(job.getStartTime())
                        .endTime(job.getEndTime())
                        .duration(job.getDuration())
                        .tag(job.getTag())
                        .build()
                )
                .collect(Collectors.toList());

        return Result.success(
                AbstractAgentVo.JobHistoryVo.builder()
                        .clusterId(clusterId)
                        .jobList(jobBriefList)
                        .build()
        );
    }

    /**
     * Description: 读取指定组件在指定节点上的日志尾部。
     * 日志路径由服务端根据组件元信息拼装并交由既有日志服务校验，调用方不传路径，
     * 避免开出一个任意文件读取的口子。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException 组件不存在或找不到日志文件时抛出
     *
     * @param clusterId     集群 ID
     * @param nodeId        节点 ID
     * @param serviceName   服务名称
     * @param componentName 组件名称
     * @param lines         读取行数
     * @return Result<AbstractLogFileVo.LogFileContentVo> 日志内容
     */
    public Result<AbstractLogFileVo.LogFileContentVo> tailComponentLog(Long clusterId,
                                                                      Long nodeId,
                                                                      String serviceName,
                                                                      String componentName,
                                                                      Integer lines) throws Exception {

        // 先确认该组件确实部署在该节点上，避免调用方拿别的集群的组件名来探测日志
        final TDlComponent tDlComponent = this.tDlComponentService.lambdaQuery()
                .eq(TDlComponent::getClusterId, clusterId)
                .eq(TDlComponent::getNodeId, nodeId)
                .eq(TDlComponent::getServiceName, serviceName)
                .eq(TDlComponent::getComponentName, componentName)
                .one();

        Assert.notNull(
                tDlComponent,
                () -> new BException(
                        String.format(
                                "该节点上不存在此组件: ClusterId: %s, NodeId: %s, ServiceName: %s, ComponentName: %s",
                                clusterId,
                                nodeId,
                                serviceName,
                                componentName
                        )
                )
        );

        final int finalLines = Math.min(
                lines == null || lines <= 0 ? LOG_TAIL_DEFAULT_LINES : lines,
                LOG_TAIL_MAX_LINES
        );

        final YamlDirectory.Directory directoryYaml = ResolverYamlDirectory.DIRECTORY_YAML.getDatalight();
        final String serviceLogDir = String.format(
                "%s/%s",
                StrUtil.removeSuffix(directoryYaml.getLogDir(), "/"),
                serviceName
        );

        // 扫描该服务的日志目录，挑出与组件名匹配的日志文件
        final AbstractLogFileVo.LogFileCollectionVo collection = this.masterLogFileReaderService
                .getLogCollectionWithNodeId(nodeId, serviceLogDir)
                .getData();

        final String filePath = this.matchComponentLogFile(collection, componentName);
        Assert.notBlank(
                filePath,
                () -> new BException(
                        String.format(
                                "未在 %s 下找到与组件 %s 匹配的日志文件",
                                serviceLogDir,
                                componentName
                        )
                )
        );

        // 先读一小段拿到文件总长度，再据此换算出尾部区间
        final AbstractLogFileVo.LogFileContentVo probe = this.masterLogFileReaderService
                .loadFileContentWithNodeId(nodeId, filePath, 0L, 1L)
                .getData();

        final long maxOffset = probe.getMaxOffset() == null ? 0L : probe.getMaxOffset();
        final long expectedBytes = (long) finalLines * ESTIMATED_BYTES_PER_LINE;
        final long startOffset = Math.max(0L, maxOffset - expectedBytes);

        return this.masterLogFileReaderService.loadFileContentWithNodeId(
                nodeId,
                filePath,
                startOffset,
                maxOffset
        );
    }

    /**
     * Description: 在日志目录树中递归查找与组件名匹配的日志文件。
     * 命中多个时取路径最短的那个，通常是主日志而非滚动归档文件。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param collection    日志目录树
     * @param componentName 组件名称
     * @return String 命中的日志文件绝对路径，未命中返回空串
     */
    private String matchComponentLogFile(AbstractLogFileVo.LogFileCollectionVo collection,
                                         String componentName) {
        if (collection == null) {
            return "";
        }

        final List<String> candidates = new ArrayList<>();
        this.collectLogFilePath(collection, componentName.toLowerCase(), candidates);

        return candidates.stream()
                .min(Comparator.comparingInt(String::length))
                .orElse("");
    }

    /**
     * Description: 递归收集匹配的日志文件路径
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param node          当前目录节点
     * @param keyword       小写后的组件名
     * @param candidates    命中结果收集器
     */
    private void collectLogFilePath(AbstractLogFileVo.LogFileCollectionVo node,
                                    String keyword,
                                    List<String> candidates) {
        if (node == null) {
            return;
        }

        if (node.getFilePathList() != null) {
            node.getFilePathList().stream()
                    .filter(path -> path.toLowerCase().contains(keyword))
                    .forEach(candidates::add);
        }

        if (node.getChildren() != null) {
            node.getChildren().forEach(child -> this.collectLogFilePath(child, keyword, candidates));
        }
    }

    /**
     * Description: 查询集群当前的活跃作业 ID，没有则返回 null
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Long 活跃作业 ID
     */
    private Long findActiveJobId(Long clusterId) {
        final TDlJob tDlJob = this.tDlJobService.lambdaQuery()
                .eq(TDlJob::getClusterId, clusterId)
                .eq(TDlJob::getJobState, ExecStateEnum.RUNNING)
                .orderByDesc(TDlJob::getStartTime)
                .last("LIMIT 1")
                .one();

        return tDlJob == null ? null : tDlJob.getId();
    }

    /**
     * Description: 把组件 PO 转成概览响应体
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param component       组件 PO
     * @param nodeHostnameMap 节点 ID 到主机名的映射
     * @return AbstractAgentVo.ComponentBriefVo 组件概览
     */
    private AbstractAgentVo.ComponentBriefVo toComponentBrief(TDlComponent component,
                                                             Map<Long, String> nodeHostnameMap) {
        return AbstractAgentVo.ComponentBriefVo.builder()
                .componentId(component.getId())
                .componentName(component.getComponentName())
                .nodeId(component.getNodeId())
                .hostname(nodeHostnameMap.getOrDefault(component.getNodeId(), ""))
                .componentState(component.getComponentState())
                .needRestart(component.getNeedRestart())
                .build();
    }
}
