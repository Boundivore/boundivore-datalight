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


import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description: 推荐组件分布响应体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractComponentPlacementVo {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractComponentPlacementVo.PlacementAdvisorVo",
            description = "AbstractComponentPlacementVo.PlacementAdvisorVo 组件推荐分布封装信息响应体"
    )
    public final static class PlacementAdvisorVo implements IVo {

        private static final long serialVersionUID = -4209390641234761844L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @Schema(name = "ServicePlacementList", title = "服务封装信息列表", required = true)
        @JsonProperty(value = "ServicePlacementList", required = true)
        private List<ServicePlacementVo> servicePlacementList;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractComponentPlacementVo.ServicePlacementVo",
            description = "AbstractComponentPlacementVo.ServicePlacementVo 组件推荐分布之服务封装信息响应体"
    )
    public final static class ServicePlacementVo implements IVo {

        private static final long serialVersionUID = -4209390641234761844L;

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        private String serviceName;

        @Schema(name = "ComponentPlacementList", title = "组件封装信息列表", required = true)
        @JsonProperty(value = "ComponentPlacementList", required = true)
        private List<ComponentPlacementVo> componentPlacementList;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractComponentPlacementVo.ComponentPlacementVo",
            description = "AbstractComponentPlacementVo.ComponentPlacementVo 组件推荐分布之组件封装信息响应体"
    )
    public final static class ComponentPlacementVo implements IVo {

        private static final long serialVersionUID = -4209390641234761844L;

        @Schema(name = "ComponentName", title = "服务名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        private String componentName;

        @Schema(name = "ComponentState", title = "组件状态", required = true)
        @JsonProperty(value = "ComponentState", required = true)
        private SCStateEnum componentState;

        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @Schema(name = "NodeState", title = "节点状态", required = true)
        @JsonProperty(value = "NodeState", required = true)
        private NodeStateEnum nodeState;

        @Schema(name = "NodeIp", title = "节点 IP", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        private String nodeIp;

        @Schema(name = "Hostname", title = "节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

    }

}
