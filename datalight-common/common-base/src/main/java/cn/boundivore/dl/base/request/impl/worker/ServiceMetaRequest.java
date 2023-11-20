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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "ServiceMetaRequest", description = "MasterMetaRequest: Service 服务元数据信息")
public class ServiceMetaRequest implements IRequest {

    @ApiModelProperty(name = "NodeId", value = "Worker 当前节点 ID", required = true)
    @JsonProperty("NodeId")
    @NotNull
    private Long nodeId;

    @ApiModelProperty(name = "Hostname", value = "Worker 当前节点主机名", required = true)
    @JsonProperty("Hostname")
    @NotNull
    private String hostname;

    @ApiModelProperty(name = "Ip", value = "Worker 当前节点 IP", required = true)
    @JsonProperty("Ip")
    @NotNull
    private String ip;

    @ApiModelProperty(name = "ServiceList", value = "服务元信息列表", required = true)
    @JsonProperty("ServiceList")
    @NotNull
    private List<ServiceRequest> serviceList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @ApiModel(
            value = "ServiceMetaRequest#ServiceRequest",
            description = "ServiceMetaRequest#ServiceRequest: 服务信息"
    )
    public static class ServiceRequest implements IRequest {

        @ApiModelProperty(name = "ClusterId", value = "集群 ID", required = true)
        @JsonProperty("ClusterId")
        @NotNull
        private Long clusterId;

        @ApiModelProperty(name = "ServiceName", value = "服务名称", required = true)
        @JsonProperty("ServiceName")
        @NotNull
        private String serviceName;

        @ApiModelProperty(name = "SCStateEnum", value = "服务状态", required = true)
        @JsonProperty("SCStateEnum")
        @NotNull
        private SCStateEnum scStateEnum;

        @ApiModelProperty(name = "ServiceName", value = "服务名称", required = true)
        @JsonProperty("ServiceName")
        @NotNull
        private List<ComponentRequest> componentList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @ApiModel(
            value = "ServiceMetaRequest.ComponentRequest",
            description = "ServiceMetaRequest.ComponentRequest: 组件信息"
    )
    public static class ComponentRequest implements IRequest {

        @ApiModelProperty(name = "ComponentName", value = "组件名称", required = true)
        @JsonProperty("ComponentName")
        @NotNull
        private String componentName;

        @ApiModelProperty(name = "SCStateEnum", value = "组件状态", required = true)
        @JsonProperty("SCStateEnum")
        @NotNull
        private SCStateEnum scStateEnum;

        @ApiModelProperty(name = "StartShell", value = "可执行脚本：组件启动", required = true)
        @JsonProperty("StartShell")
        @NotNull
        private String startShell;

        @ApiModelProperty(name = "StopShell", value = "可执行脚本：组件停止", required = true)
        @JsonProperty("StopShell")
        @NotNull
        private String stopShell;

    }
}
