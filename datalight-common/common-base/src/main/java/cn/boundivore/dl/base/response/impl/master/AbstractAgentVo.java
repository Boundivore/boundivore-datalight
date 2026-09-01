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
package cn.boundivore.dl.base.response.impl.master;

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ClusterStateEnum;
import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Description: 智能体服务专用的聚合响应体。
 * datalight-services-ai 不直连数据库，所有数据都从这里取。
 * 这些接口同样对人开放，不是给 Agent 开的后门。
 * 设计上以「一次调用拿全一个语义完整的快照」为原则，减少 Agent 的往返次数与上下文消耗。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/1
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAgentVo {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAgentVo.ClusterSnapshotVo",
            description = "AbstractAgentVo.ClusterSnapshotVo 集群全景快照"
    )
    public final static class ClusterSnapshotVo implements IVo {

        private static final long serialVersionUID = 4477238120031187210L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @Schema(name = "ClusterName", title = "集群名称", required = true)
        @JsonProperty(value = "ClusterName", required = true)
        private String clusterName;

        @Schema(name = "ClusterType", title = "集群类型", required = true)
        @JsonProperty(value = "ClusterType", required = true)
        private ClusterTypeEnum clusterType;

        @Schema(name = "ClusterState", title = "集群状态", required = true)
        @JsonProperty(value = "ClusterState", required = true)
        private ClusterStateEnum clusterState;

        @Schema(name = "ClusterDesc", title = "集群描述", required = true)
        @JsonProperty(value = "ClusterDesc", required = true)
        private String clusterDesc;

        @Schema(name = "DlcVersion", title = "服务组件包版本", required = true)
        @JsonProperty(value = "DlcVersion", required = true)
        private String dlcVersion;

        @Schema(name = "SnapshotTime", title = "快照生成时间毫秒", required = true)
        @JsonProperty(value = "SnapshotTime", required = true)
        private Long snapshotTime;

        @Schema(name = "NodeList", title = "节点列表", required = true)
        @JsonProperty(value = "NodeList", required = true)
        private List<NodeBriefVo> nodeList;

        @Schema(name = "ServiceList", title = "服务列表", required = true)
        @JsonProperty(value = "ServiceList", required = true)
        private List<ServiceBriefVo> serviceList;

        @Schema(name = "ActiveJobId", title = "当前活跃作业 ID，无则为空", required = true)
        @JsonProperty(value = "ActiveJobId", required = true)
        private Long activeJobId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAgentVo.NodeBriefVo",
            description = "AbstractAgentVo.NodeBriefVo 节点概览"
    )
    public final static class NodeBriefVo implements IVo {

        private static final long serialVersionUID = -6108283128917426811L;

        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @Schema(name = "Hostname", title = "主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

        @Schema(name = "NodeState", title = "节点状态", required = true)
        @JsonProperty(value = "NodeState", required = true)
        private NodeStateEnum nodeState;

        @Schema(name = "CpuArch", title = "CPU 架构", required = true)
        @JsonProperty(value = "CpuArch", required = true)
        private String cpuArch;

        @Schema(name = "CpuCores", title = "CPU 核数", required = true)
        @JsonProperty(value = "CpuCores", required = true)
        private Long cpuCores;

        @Schema(name = "Ram", title = "内存字节数", required = true)
        @JsonProperty(value = "Ram", required = true)
        private Long ram;

        @Schema(name = "Disk", title = "磁盘字节数", required = true)
        @JsonProperty(value = "Disk", required = true)
        private Long disk;

        @Schema(name = "OsVersion", title = "操作系统版本", required = true)
        @JsonProperty(value = "OsVersion", required = true)
        private String osVersion;

        @Schema(name = "ComponentCount", title = "该节点上的组件数量", required = true)
        @JsonProperty(value = "ComponentCount", required = true)
        private Integer componentCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAgentVo.ServiceBriefVo",
            description = "AbstractAgentVo.ServiceBriefVo 服务概览，含其下组件分布"
    )
    public final static class ServiceBriefVo implements IVo {

        private static final long serialVersionUID = 8875002174392330621L;

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        private String serviceName;

        @Schema(name = "ServiceState", title = "服务状态", required = true)
        @JsonProperty(value = "ServiceState", required = true)
        private SCStateEnum serviceState;

        @Schema(name = "Priority", title = "部署优先级", required = true)
        @JsonProperty(value = "Priority", required = true)
        private Long priority;

        @Schema(name = "ComponentList", title = "组件实例列表", required = true)
        @JsonProperty(value = "ComponentList", required = true)
        private List<ComponentBriefVo> componentList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAgentVo.ComponentBriefVo",
            description = "AbstractAgentVo.ComponentBriefVo 组件实例概览"
    )
    public final static class ComponentBriefVo implements IVo {

        private static final long serialVersionUID = -1132285419277312388L;

        @Schema(name = "ComponentId", title = "组件 ID", required = true)
        @JsonProperty(value = "ComponentId", required = true)
        private Long componentId;

        @Schema(name = "ComponentName", title = "组件名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        private String componentName;

        @Schema(name = "NodeId", title = "所在节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @Schema(name = "Hostname", title = "所在节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

        @Schema(name = "ComponentState", title = "组件状态", required = true)
        @JsonProperty(value = "ComponentState", required = true)
        private SCStateEnum componentState;

        @Schema(name = "NeedRestart", title = "配置变更后是否待重启", required = true)
        @JsonProperty(value = "NeedRestart", required = true)
        private Boolean needRestart;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAgentVo.HealthSummaryVo",
            description = "AbstractAgentVo.HealthSummaryVo 集群健康摘要，只列出需要关注的项"
    )
    public final static class HealthSummaryVo implements IVo {

        private static final long serialVersionUID = 2201884471129386640L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @Schema(name = "Healthy", title = "是否一切正常", required = true)
        @JsonProperty(value = "Healthy", required = true)
        private Boolean healthy;

        @Schema(name = "SnapshotTime", title = "快照生成时间毫秒", required = true)
        @JsonProperty(value = "SnapshotTime", required = true)
        private Long snapshotTime;

        @Schema(name = "AbnormalNodeList", title = "状态异常的节点", required = true)
        @JsonProperty(value = "AbnormalNodeList", required = true)
        private List<NodeBriefVo> abnormalNodeList;

        @Schema(name = "AbnormalComponentList", title = "状态异常的组件", required = true)
        @JsonProperty(value = "AbnormalComponentList", required = true)
        private List<ComponentBriefVo> abnormalComponentList;

        @Schema(name = "NeedRestartComponentList", title = "配置已变更待重启的组件", required = true)
        @JsonProperty(value = "NeedRestartComponentList", required = true)
        private List<ComponentBriefVo> needRestartComponentList;

        @Schema(name = "IssueList", title = "问题描述列表，人可直接读", required = true)
        @JsonProperty(value = "IssueList", required = true)
        private List<String> issueList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAgentVo.JobHistoryVo",
            description = "AbstractAgentVo.JobHistoryVo 作业历史列表"
    )
    public final static class JobHistoryVo implements IVo {

        private static final long serialVersionUID = -3320119220137418805L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @Schema(name = "JobList", title = "作业列表，按开始时间倒序", required = true)
        @JsonProperty(value = "JobList", required = true)
        private List<JobBriefVo> jobList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAgentVo.JobBriefVo",
            description = "AbstractAgentVo.JobBriefVo 作业概览"
    )
    public final static class JobBriefVo implements IVo {

        private static final long serialVersionUID = 5510031882274139330L;

        @Schema(name = "JobId", title = "作业 ID", required = true)
        @JsonProperty(value = "JobId", required = true)
        private Long jobId;

        @Schema(name = "JobName", title = "作业名称", required = true)
        @JsonProperty(value = "JobName", required = true)
        private String jobName;

        @Schema(name = "JobActionType", title = "操作类型", required = true)
        @JsonProperty(value = "JobActionType", required = true)
        private ActionTypeEnum jobActionType;

        @Schema(name = "JobState", title = "执行状态", required = true)
        @JsonProperty(value = "JobState", required = true)
        private ExecStateEnum jobState;

        @Schema(name = "StartTime", title = "开始时间毫秒", required = true)
        @JsonProperty(value = "StartTime", required = true)
        private Long startTime;

        @Schema(name = "EndTime", title = "结束时间毫秒", required = true)
        @JsonProperty(value = "EndTime", required = true)
        private Long endTime;

        @Schema(name = "Duration", title = "耗时毫秒", required = true)
        @JsonProperty(value = "Duration", required = true)
        private Long duration;

        @Schema(name = "Tag", title = "作业标签", required = true)
        @JsonProperty(value = "Tag", required = true)
        private String tag;
    }
}
