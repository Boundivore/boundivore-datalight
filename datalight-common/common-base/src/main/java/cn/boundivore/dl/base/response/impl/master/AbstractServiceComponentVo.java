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
import cn.boundivore.dl.base.enumeration.impl.ServiceTypeEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;


public abstract class AbstractServiceComponentVo {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(
            value = "AbstractServiceComponentVo.ServiceVo",
            description = "AbstractServiceComponentVo.ServiceVo: 集群中的服务列表"
    )
    public final static class ServiceVo implements IVo {

        @ApiModelProperty(name = "ClusterId", value = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @ApiModelProperty(name = "DLCVersion", value = "服务组件包版本", required = true)
        @JsonProperty(value = "DLCVersion", required = true)
        private String dlcVersion;

        @ApiModelProperty(name = "ServiceSummaryList", value = "服务概览列表", required = true)
        @JsonProperty(value = "ServiceSummaryList", required = true)
        private List<ServiceSummaryVo> serviceSummaryList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @ApiModel(
            value = "AbstractServiceComponentVo.ServiceSummaryVo",
            description = "AbstractServiceComponentVo.ServiceSummaryVo: 服务信息"
    )
    public final static class ServiceSummaryVo implements IVo {

        @ApiModelProperty(name = "ServiceName", value = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        private String serviceName;

        @ApiModelProperty(name = "ServiceType", value = "服务类型", required = true)
        @JsonProperty(value = "ServiceType", required = true)
        private ServiceTypeEnum serviceTypeEnum;

        @ApiModelProperty(name = "SCStateEnum", value = "服务当前在集群中的状态", required = true)
        @JsonProperty(value = "SCStateEnum", required = true)
        private SCStateEnum scStateEnum;

        @ApiModelProperty(name = "Priority", value = "服务部署优先级", required = true)
        @JsonProperty(value = "Priority", required = true)
        private Long priority;

        @ApiModelProperty(name = "Desc", value = "服务描述", required = true)
        @JsonProperty(value = "Desc", required = true)
        private String desc;

        @ApiModelProperty(name = "Tgz", value = "服务安装包名", required = true)
        @JsonProperty(value = "Tgz", required = true)
        private String tgz;

        @ApiModelProperty(name = "Version", value = "服务版本", required = true)
        @JsonProperty(value = "Version", required = true)
        private String version;

        @ApiModelProperty(name = "DependencyList", value = "需要依赖的服务列表", required = true)
        @JsonProperty(value = "DependencyList", required = true)
        private List<String> dependencyList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(
            value = "AbstractServiceComponentVo.ComponentVo",
            description = "AbstractServiceComponentVo.ComponentVo: 集群中服务与组件详细信息列表"
    )
    public final static class ComponentVo implements IVo {

        @ApiModelProperty(name = "ClusterId", value = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @ApiModelProperty(name = "DLCVersion", value = "服务组件包版本", required = true)
        @JsonProperty(value = "DLCVersion", required = true)
        private String dlcVersion;

        @ApiModelProperty(name = "ServiceComponentSummaryVo", value = "服务与组件信息列表", required = true)
        @JsonProperty(value = "ServiceComponentSummaryVo", required = true)
        private List<ServiceComponentSummaryVo> serviceComponentSummaryList;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(
            value = "AbstractServiceComponentVo.ServiceComponentSummaryVo",
            description = "AbstractServiceComponentVo.ServiceComponentSummaryVo: 服务组件信息"
    )
    public final static class ServiceComponentSummaryVo implements IVo {

        @ApiModelProperty(name = "ServiceSummary", value = "服务名称", required = true)
        @JsonProperty(value = "ServiceSummary", required = true)
        private ServiceSummaryVo serviceSummaryVo;

        @ApiModelProperty(name = "ComponentSummaryList", value = "组件概览列表", required = true)
        @JsonProperty(value = "ComponentSummaryList", required = true)
        private List<ComponentSummaryVo> componentSummaryList;


    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @ApiModel(
            value = "AbstractServiceComponentVo.ComponentSummaryVo",
            description = "AbstractServiceComponentVo.ComponentSummaryVo: 组件信息"
    )
    public final static class ComponentSummaryVo implements IVo {

        @ApiModelProperty(name = "ComponentName", value = "组件名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        private String componentName;

        @ApiModelProperty(name = "Priority", value = "优先级", required = true)
        @JsonProperty(value = "Priority", required = true)
        private long priority;

        @ApiModelProperty(name = "Max", value = "最大安装数量,-1为不限制", required = true)
        @JsonProperty(value = "Max", required = true)
        private long max;

        @ApiModelProperty(name = "Min", value = "最小安装数量", required = true)
        @JsonProperty(value = "Min", required = true)
        private long min;

        @ApiModelProperty(name = "MutexesList", value = "与当前组件互斥的组件名列表（即不允许出现在同一节点）", required = true)
        @JsonProperty("MutexesList")
        private List<String> mutexesList;

        @ApiModelProperty(name = "ComponentNodeList", value = "当前组件在节点的分布信息列表", required = true)
        @JsonProperty("ComponentNodeList")
        private List<ComponentNodeVo> componentNodeList;


    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(
            value = "AbstractServiceComponentVo.ComponentNodeVo",
            description = "AbstractServiceComponentVo.ComponentNodeVo: 组件在节点的分布信息"
    )
    public final static class ComponentNodeVo implements IVo {

        @ApiModelProperty(name = "NodeId", value = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @ApiModelProperty(name = "Hostname", value = "节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

        @ApiModelProperty(name = "NodeIp", value = "节点 IP 地址", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        private String nodeIp;

        @ApiModelProperty(name = "NodeState", value = "节点当前状态", required = true)
        @JsonProperty(value = "NodeState", required = true)
        private NodeStateEnum nodeStateEnum;

        @ApiModelProperty(name = "SCStateEnum", value = "组件在当前节点的状态", required = true)
        @JsonProperty(value = "SCStateEnum", required = true)
        private SCStateEnum scStateEnum;


    }

}
