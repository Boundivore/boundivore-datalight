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

import javax.validation.constraints.NotEmpty;
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
            name = "AbstractServiceComponentRequest.ComponentIdListRequest",
            description = "AbstractServiceComponentRequest.ComponentIdListRequest: 组件 ID 列表请求体"
    )
    public final static class ComponentIdListRequest implements IRequest{

        private static final long serialVersionUID = -3758052660074676120L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull(message = "集群 ID 不能为空")
        private Long clusterId;

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        @NotNull(message = "服务名称不能为空")
        private String serviceName;

        @Schema(name = "ComponentIdList", title = "组件 ID 列表", required = true)
        @JsonProperty(value = "ComponentIdList", required = true)
        @NotEmpty(message = "组件 ID 列表不能为空")
        private List<ComponentIdRequest> componentIdList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractServiceComponentRequest.ComponentIdRequest",
            description = "AbstractServiceComponentRequest.ComponentIdRequest: 组件 ID 请求体"
    )
    public final static class ComponentIdRequest implements IRequest {

        private static final long serialVersionUID = 6871733675492103531L;

        @Schema(name = "ComponentId", title = "组件 ID", required = true)
        @JsonProperty(value = "ComponentId", required = true)
        @NotNull(message = "组件 ID不能为空")
        private Long componentId;
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractServiceComponentRequest.ServiceSelectRequest",
            description = "AbstractServiceComponentRequest.ServiceSelectRequest 部署前，选择服务(需传递全部服务名称，以及服务选中或未选中状态) 请求体"
    )
    public static class ServiceSelectRequest implements IRequest {

        private static final long serialVersionUID = 4338825806579322642L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull(message = "集群 ID 不能为空")
        private Long clusterId;

        @Schema(name = "ServiceList", title = "服务列表", required = true)
        @JsonProperty(value = "ServiceList", required = true)
        @NotEmpty(message = "服务列表不能为空")
        private List<ServiceRequest> serviceList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractServiceComponentRequest.ServiceRequest",
            description = "AbstractServiceComponentRequest.ServiceRequest 服务信息 请求体"
    )
    public static class ServiceRequest implements IRequest {

        private static final long serialVersionUID = -2841145628841691164L;

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        @NotNull(message = "服务名称不能为空")
        private String serviceName;

        @Schema(name = "SCStateEnum", title = "服务状态", required = true)
        @JsonProperty(value = "SCStateEnum", required = true)
        @NotNull(message = "服务状态不能为空")
        private SCStateEnum scStateEnum;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractServiceComponentRequest.ComponentSelectRequest",
            description = "AbstractProcedureRequest.ComponentSelectRequest 部署前，选择组件请求体"
    )
    public static class ComponentSelectRequest implements IRequest {

        private static final long serialVersionUID = -5566602790833948797L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull(message = "集群 ID 不能为空")
        private Long clusterId;

        @Schema(name = "ComponentList", title = "组件分布列表", required = true)
        @JsonProperty(value = "ComponentList", required = true)
        @NotEmpty(message = "组件分布列表不能为空")
        private List<ComponentRequest> componentList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractServiceComponentRequest.ComponentRequest",
            description = "AbstractServiceComponentRequest.ComponentRequest组件信息 请求体"
    )
    public static class ComponentRequest implements IRequest {

        private static final long serialVersionUID = -4647990920130441597L;

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        @NotNull(message = "服务名称不能为空")
        private String serviceName;

        @Schema(name = "ComponentName", title = "组件名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        @NotNull(message = "组件名称不能为空")
        private String componentName;

        @Schema(name = "SCStateEnum", title = "组件状态", required = true)
        @JsonProperty(value = "SCStateEnum", required = true)
        @NotNull(message = "组件状态不能为空")
        private SCStateEnum scStateEnum;

        @Schema(name = "NodeIdList", title = "组件分布在哪些节点", required = true)
        @JsonProperty(value = "NodeIdList", required = true)
        @NotEmpty(message = "组件分布在哪些节点不能为空")
        private List<Long> nodeIdList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractServiceComponentRequest.UpdateNeedRestartRequest",
            description = "AbstractServiceComponentRequest.更新组件重启标记 请求体"
    )
    public static class UpdateNeedRestartRequest implements IRequest {

        private static final long serialVersionUID = 321314736276175417L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull(message = "集群 ID 不能为空")
        private Long clusterId;

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        @NotNull(message = "服务名称不能为空")
        private String serviceName;

        @Schema(name = "NodeIdList", title = "节点 ID 列表", required = true)
        @JsonProperty(value = "NodeIdList", required = true)
        @NotNull(message = "节点 ID 列表不能为空")
        private List<Long> nodeIdList;

        @Schema(name = "NeedRestart", title = "是否需要重启标记", required = true)
        @JsonProperty(value = "NeedRestart", required = true)
        @NotNull(message = "是否需要重启标记不能为空")
        private Boolean needRestart;

    }



}
