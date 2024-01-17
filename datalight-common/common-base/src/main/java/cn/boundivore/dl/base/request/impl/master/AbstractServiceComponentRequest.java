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

import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: 服务组件 Request 集合
 * Created by: Boundivore
 * E-mail: boundivore@formail.com
 * Creation time: 2023/7/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractServiceComponentRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractServiceComponentRequest.ServiceSelectRequest",
            description = "AbstractServiceComponentRequest.ServiceSelectRequest: " +
                    "部署前，选择服务(需传递全部服务名称，以及服务选中或未选中状态) 请求体"
    )
    public static class ServiceSelectRequest implements IRequest {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "ServiceList", title = "服务列表", required = true)
        @JsonProperty(value = "ServiceList", required = true)
        @NotNull
        private List<ServiceRequest> serviceList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractServiceComponentRequest.ServiceRequest",
            description = "服务信息 请求体"
    )
    public static class ServiceRequest implements IRequest {

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        @NotNull
        private String serviceName;

        @Schema(name = "SCStateEnum", title = "服务状态", required = true)
        @JsonProperty(value = "SCStateEnum", required = true)
        @NotNull
        private SCStateEnum scStateEnum;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractServiceComponentRequest.ComponentSelectRequest",
            description = "部署前，选择组件请求体"
    )
    public static class ComponentSelectRequest implements IRequest {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "ComponentList", title = "组件分布列表", required = true)
        @JsonProperty(value = "ComponentList", required = true)
        @NotNull
        private List<ComponentRequest> componentList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractServiceComponentRequest.ComponentRequest",
            description = "AbstractServiceComponentRequest.ComponentRequest: " +
                    "组件信息 请求体"
    )
    public static class ComponentRequest implements IRequest {

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        @NotNull
        private String serviceName;

        @Schema(name = "ComponentName", title = "组件名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        @NotNull
        private String componentName;

        @Schema(name = "SCStateEnum", title = "组件状态", required = true)
        @JsonProperty(value = "SCStateEnum", required = true)
        @NotNull
        private SCStateEnum scStateEnum;

        @Schema(name = "NodeIdList", title = "组件分布在哪些节点", required = true)
        @JsonProperty(value = "NodeIdList", required = true)
        @NotNull
        private List<Long> nodeIdList;

    }



}
