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

import cn.boundivore.dl.base.response.impl.common.AbstractLogFileVo;
import cn.boundivore.dl.base.response.impl.master.AbstractAgentVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;

/**
 * Description: 面向 AIAgent 的数据接口。
 * AIAgent 不具备直连数据库的能力，集群元数据与节点日志一律从这里取。
 * 这些接口对人同样开放，不是给 Agent 单开的后门，前端需要同样的聚合视图时可直接复用。
 * 设计原则：一次调用返回一个语义完整的快照，减少调用方的往返次数。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/1
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：智能体数据服务", description = "IMasterAgentAPI")
@FeignClient(
        name = "IMasterAgentAPI",
        contextId = "IMasterAgentAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterAgentAPI {

    @GetMapping(value = "/agent/cluster/snapshot")
    @Operation(summary = "获取集群全景快照", description = "一次返回集群、节点、服务、组件状态与当前活跃作业，供智能体建立完整上下文")
    Result<AbstractAgentVo.ClusterSnapshotVo> getClusterSnapshot(
            @Parameter(name = "ClusterId", description = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @GetMapping(value = "/agent/health/summary")
    @Operation(summary = "获取集群健康摘要", description = "只返回需要关注的异常项：状态异常的节点与组件、配置变更后待重启的组件")
    Result<AbstractAgentVo.HealthSummaryVo> getHealthSummary(
            @Parameter(name = "ClusterId", description = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @GetMapping(value = "/agent/job/history")
    @Operation(summary = "获取作业历史", description = "按开始时间倒序返回集群的历史作业，用于回溯最近做过什么操作")
    Result<AbstractAgentVo.JobHistoryVo> getJobHistory(
            @Parameter(name = "ClusterId", description = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId,

            @Parameter(name = "Limit", description = "返回条数，默认 20，上限 200")
            @RequestParam(value = "Limit", required = false)
            Integer limit
    ) throws Exception;

    @GetMapping(value = "/agent/log/tail")
    @Operation(summary = "读取组件日志尾部", description = "按节点与组件读取日志尾部。日志路径由服务端根据组件元信息拼装，调用方不传路径")
    Result<AbstractLogFileVo.LogFileContentVo> tailComponentLog(
            @Parameter(name = "ClusterId", description = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId,

            @Parameter(name = "NodeId", description = "节点 ID")
            @RequestParam(value = "NodeId", required = true)
            Long nodeId,

            @Parameter(name = "ServiceName", description = "服务名称，全大写")
            @RequestParam(value = "ServiceName", required = true)
            String serviceName,

            @Parameter(name = "ComponentName", description = "组件名称，帕斯卡命名")
            @RequestParam(value = "ComponentName", required = true)
            String componentName,

            @Parameter(name = "Lines", description = "读取行数，默认 200，上限 2000")
            @RequestParam(value = "Lines", required = false)
            Integer lines
    ) throws Exception;
}
