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
package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: AI 会话式部署计划相关 Request 集合
 * <p>
 * 这份计划由 AIAgent 起草、用户在页面上确认后回传。它经过浏览器，
 * 因此服务端必须重新校验一遍，不能因为 AIAgent 已经校验过就放行。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAiPlanRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAiPlanRequest.PlanNodeRequest",
            description = "AbstractAiPlanRequest.PlanNodeRequest 计划中的节点"
    )
    public static class PlanNodeRequest implements IRequest {

        private static final long serialVersionUID = 5471302884174471322L;

        @Schema(name = "Hostname", title = "主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        @NotEmpty(message = "主机名不能为空")
        private String hostname;

        @Schema(name = "NodeIp", title = "内网 IPv4 地址", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        @NotEmpty(message = "节点 IP 不能为空")
        private String nodeIp;

        @Schema(name = "SshPort", title = "SSH 端口，默认 22", required = false)
        @JsonProperty(value = "SshPort", required = false)
        private Long sshPort;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAiPlanRequest.PlanComponentRequest",
            description = "AbstractAiPlanRequest.PlanComponentRequest 计划中的组件放置"
    )
    public static class PlanComponentRequest implements IRequest {

        private static final long serialVersionUID = 5471302884174471323L;

        @Schema(name = "ServiceName", title = "服务名，全大写", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        @NotEmpty(message = "服务名不能为空")
        private String serviceName;

        @Schema(name = "ComponentName", title = "组件名，帕斯卡命名", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        @NotEmpty(message = "组件名不能为空")
        private String componentName;

        @Schema(name = "Hostnames", title = "部署到哪些主机", required = true)
        @JsonProperty(value = "Hostnames", required = true)
        @NotEmpty(message = "组件部署主机不能为空")
        private List<String> hostnames = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAiPlanRequest.PlanSubmitRequest",
            description = "AbstractAiPlanRequest.PlanSubmitRequest 提交部署计划"
    )
    public static class PlanSubmitRequest implements IRequest {

        private static final long serialVersionUID = 5471302884174471324L;

        @Schema(name = "SessionId", title = "产出这份计划的会话 ID，用于回溯", required = false)
        @JsonProperty(value = "SessionId", required = false)
        private String sessionId;

        @Schema(name = "ClusterName", title = "集群名称", required = true)
        @JsonProperty(value = "ClusterName", required = true)
        @NotEmpty(message = "集群名称不能为空")
        private String clusterName;

        @Schema(name = "ClusterType", title = "集群类型 COMPUTE / MIXED", required = true)
        @JsonProperty(value = "ClusterType", required = true)
        @NotEmpty(message = "集群类型不能为空")
        private String clusterType;

        @Schema(name = "DlcVersion", title = "DLC 包版本", required = false)
        @JsonProperty(value = "DlcVersion", required = false)
        private String dlcVersion;

        @Schema(name = "Description", title = "计划摘要", required = false)
        @JsonProperty(value = "Description", required = false)
        private String description;

        @Schema(name = "NodeList", title = "节点清单", required = true)
        @JsonProperty(value = "NodeList", required = true)
        @NotNull(message = "节点清单不能为空")
        @NotEmpty(message = "节点清单不能为空")
        @Valid
        private List<PlanNodeRequest> nodeList = new ArrayList<>();

        @Schema(name = "ComponentList", title = "组件放置", required = true)
        @JsonProperty(value = "ComponentList", required = true)
        @NotNull(message = "组件放置不能为空")
        @NotEmpty(message = "组件放置不能为空")
        @Valid
        private List<PlanComponentRequest> componentList = new ArrayList<>();
    }
}
