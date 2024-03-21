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
package cn.boundivore.dl.base.request.impl.worker;

import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: Service 当前服役状态
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/1
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ServiceMetaRequest", description = "ServiceMetaRequest: Service 服务元数据信息")
public class ServiceMetaRequest implements IRequest {

    private static final long serialVersionUID = 765365166477094549L;

    @Schema(name = "NodeId", title = "Worker 当前节点 ID", required = true)
    @JsonProperty("NodeId")
    @NotNull
    private Long nodeId;

    @Schema(name = "Hostname", title = "Worker 当前节点主机名", required = true)
    @JsonProperty("Hostname")
    @NotNull
    private String hostname;

    @Schema(name = "Ip", title = "Worker 当前节点 IP", required = true)
    @JsonProperty("Ip")
    @NotNull
    private String ip;

    @Schema(name = "MetaServiceList", title = "服务元信息列表", required = true)
    @JsonProperty("MetaServiceList")
    @NotNull
    private List<MetaServiceRequest> metaServiceList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "ServiceMetaRequest.ServiceRequest",
            description = "ServiceMetaRequest.ServiceRequest: 服务信息"
    )
    public static class MetaServiceRequest implements IRequest {

        private static final long serialVersionUID = -5797825661179835513L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        @NotNull
        private String serviceName;

        @Schema(name = "Priority", title = "服务部署时优先级", required = true)
        @JsonProperty(value = "Priority", required = true)
        @NotNull
        private Long priority;

        @Schema(name = "SCStateEnum", title = "服务状态", required = true)
        @JsonProperty(value = "SCStateEnum", required = true)
        @NotNull
        private SCStateEnum scStateEnum;

        @Schema(name = "MetaComponentList", title = "服务名称", required = true)
        @JsonProperty(value = "MetaComponentList", required = true)
        @NotNull
        private List<MetaComponentRequest> metaComponentList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "ServiceMetaRequest.MetaComponentRequest",
            description = "ServiceMetaRequest.MetaComponentRequest: 组件信息"
    )
    public static class MetaComponentRequest implements IRequest {

        private static final long serialVersionUID = -2837314074750994802L;

        @Schema(name = "ComponentName", title = "组件名称", required = true)
        @JsonProperty("ComponentName")
        @NotNull
        private String componentName;

        @Schema(name = "Priority", title = "组件部署时优先级", required = true)
        @JsonProperty(value = "Priority", required = true)
        @NotNull
        private Long priority;

        @Schema(name = "SCStateEnum", title = "组件状态", required = true)
        @JsonProperty("SCStateEnum")
        @NotNull
        private SCStateEnum scStateEnum;

        @Schema(name = "CheckAndStartShell", title = "可执行脚本：检查并启动组件", required = true)
        @JsonProperty("CheckAndStartShell")
        @NotNull
        private String checkAndStartShell;

        @Schema(name = "StopShell", title = "可执行脚本：组件停止", required = true)
        @JsonProperty("StopShell")
        @NotNull
        private String stopShell;

    }
}
