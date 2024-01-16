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

import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 当前服务相关的服务和组件信息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/19
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "ServiceDependenciesVo",
        description = "ServiceDependenciesVo: 当前服务相关的服务和组件信息"
)
public class ServiceDependenciesVo implements IVo {

    @Schema(name = "CurrentServiceName", title = "当前服务主体的名称", required = true)
    @JsonProperty(value = "CurrentServiceName", required = true)
    private String currentServiceName;


    @Schema(name = "ServiceDetailList", title = "被当前服务依赖的服务及组件，包括自己", required = true)
    @JsonProperty(value = "ServiceDetailList", required = true)
    private List<ServiceDetailVo> serviceDetailList;

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "ServiceDependenciesVo.RelativeDetailVo",
            description = "ServiceDependenciesVo.RelativeDetailVo: 当前服务"
    )
    public static class ServiceDetailVo implements IVo {
        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @Schema(name = "ClusterName", title = "集群名称", required = true)
        @JsonProperty(value = "ClusterName", required = true)
        private String clusterName;

        @Schema(name = "ClusterType", title = "集群类型", required = true)
        @JsonProperty(value = "ClusterType", required = true)
        private ClusterTypeEnum clusterType;

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        private String serviceName;

        @Schema(name = "ServiceState", title = "当前服务状态", required = true)
        @JsonProperty(value = "ServiceState", required = true)
        private SCStateEnum serviceState;

        @Schema(name = "ComponentDetailList", title = "当前服务下的组件", required = true)
        @JsonProperty(value = "ComponentDetailList", required = true)
        private List<ComponentDetailVo> componentDetailList;

        @Schema(name = "ConfDirList", title = "服务与模板目录信息列表", required = true)
        @JsonProperty(value = "ConfDirList", required = true)
        private List<ConfDirVo> confDirList;

        @Schema(name = "PropertyList", title = "预配置属性信息列表", required = true)
        @JsonProperty(value = "PropertyList", required = true)
        private List<PropertyVo> propertyList;
    }

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "ServiceDependenciesVo.ComponentDetailVo",
            description = "ServiceDependenciesVo.ComponentDetailVo: 当前组件"
    )
    public static class ComponentDetailVo implements IVo {

        @Schema(name = "ComponentName", title = "组件名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        private String componentName;

        @Schema(name = "ComponentState", title = "当前组件状态", required = true)
        @JsonProperty(value = "ComponentState", required = true)
        private SCStateEnum componentState;

        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @Schema(name = "NodeIp", title = "节点 IP", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        private String nodeIp;

        @Schema(name = "Hostname", title = "节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

        @Schema(name = "Ram", title = "内存字节数 单位 MB", required = true)
        @JsonProperty(value = "Ram", required = true)
        private Long ram;

        @Schema(name = "CpuCores", title = "CPU 核心数", required = true)
        @JsonProperty(value = "CpuCores", required = true)
        private Long cpuCores;
    }

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(
            name = "ServiceDependenciesVo.ConfDirVo",
            description = "ServiceDependenciesVo.ConfDirVo: 服务配置目录与模板目录信息"
    )
    public static class ConfDirVo implements Serializable {
        @Schema(name = "ServiceConfDir", title = "服务配置文件目录", required = true)
        @JsonProperty(value = "ServiceConfDir", required = true)
        private String serviceConfDir;

        @Schema(name = "TemplatedDir", title = "配置文件模板目录", required = true)
        @JsonProperty(value = "TemplatedDir", required = true)
        private String templatedDir;
    }


    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(
            name = "ServiceDependenciesVo.PropertyVo",
            description = "ServiceDependenciesVo.PropertyVo: 预配置属性信息"
    )
    public static class PropertyVo implements Serializable {

        @Schema(name = "TemplatedFilePath", title = "配置文件模板路径", required = true)
        @JsonProperty(value = "TemplatedFilePath", required = true)
        private String templatedFilePath;

        @Schema(name = "Placeholder", title = "占位符", required = true)
        @JsonProperty(value = "Placeholder", required = true)
        private String placeholder;

        @Schema(name = "Value", title = "占位符替代值", required = true)
        @JsonProperty(value = "Value", required = true)
        private String value;
    }

}
