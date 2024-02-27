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

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: JobDetailRequest
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/1/17
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(
        name = "JobDetailRequest",
        description = "JobDetailRequest: Job 详细执行意图请求体"
)
public class JobDetailRequest implements IRequest {

    private static final long serialVersionUID = -6920520866491139326L;

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull
    private Long clusterId;

    @Schema(name = "ActionTypeEnum", title = "执行操作的类型", required = true)
    @JsonProperty(value = "ActionTypeEnum", required = true)
    @NotNull
    private ActionTypeEnum actionTypeEnum;

    @Schema(name = "JobDetailServiceList", title = "待执行操作的服务列表", required = true)
    @JsonProperty(value = "JobDetailServiceList", required = true)
    @NotEmpty
    private List<JobDetailServiceRequest> jobDetailServiceList;


    @Schema(name = "IsOneByOne", title = "是否为滚动操作依次执行", required = true)
    @JsonProperty(value = "IsOneByOne", required = true)
    private Boolean isOneByOne = false;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "JobDetailRequest.JobDetailServiceRequest",
            description = "JobDetailRequest.JobDetailServiceRequest: Job 详细执行服务请求体"
    )
    public static class JobDetailServiceRequest implements IRequest {

        private static final long serialVersionUID = 8024606218863558979L;

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        @NotNull
        private String serviceName;

        @Schema(name = "JobDetailComponentList", title = "待操作组件列表", required = true)
        @JsonProperty(value = "JobDetailComponentList", required = true)
        @NotEmpty
        private List<JobDetailComponentRequest> jobDetailComponentList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "JobDetailRequest.JobDetailComponentRequest",
            description = "JobDetailRequest.JobDetailComponentRequest: Job 详细执行组件请求体"
    )
    public static class JobDetailComponentRequest implements IRequest {
        private static final long serialVersionUID = -1031797646399714400L;

        @Schema(name = "ComponentName", title = "组件名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        @NotNull
        private String componentName;

        @Schema(name = "JobDetailNodeList", title = "涉及到的节点列表", required = true)
        @JsonProperty(value = "JobDetailNodeList", required = true)
        @NotEmpty
        private List<JobDetailNodeRequest> jobDetailNodeList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "JobDetailRequest.JobDetailNodeRequest",
            description = "JobDetailRequest.JobDetailNodeRequest: Job 详细执行节点请求体"
    )
    public static class JobDetailNodeRequest implements IRequest {
        private static final long serialVersionUID = 1067282774088299896L;

        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        @NotNull
        private Long nodeId;

        @Schema(name = "NodeIp", title = "节点 IP", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        @NotNull
        private String nodeIp;

        @Schema(name = "Hostname", title = "节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        @NotNull
        private String hostname;
    }

}
