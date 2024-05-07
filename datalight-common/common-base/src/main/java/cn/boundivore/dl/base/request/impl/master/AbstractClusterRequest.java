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

import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * Description: 集群相关 Request 集合
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

public abstract class AbstractClusterRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractClusterRequest.ClusterIdRequest",
            description = "AbstractClusterRequest.ClusterIdRequest 集群 ID 请求体"
    )
    public static class ClusterIdRequest implements IRequest {
        private static final long serialVersionUID = 1536975563357226084L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull(message = "集群 ID 不能为空")
        private Long clusterId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractClusterRequest.NewClusterRequest",
            description = "AbstractClusterRequest.NewClusterRequest 新建集群 请求体"
    )
    public static class NewClusterRequest implements IRequest {

        private static final long serialVersionUID = -1574151757754502450L;

        @Schema(name = "DlcVersion", title = "DataLight 服务组件包版本", required = true)
        @JsonProperty(value = "DlcVersion", required = true)
        @NotNull(message = "DataLight 服务组件包版本不能为空")
        private String dlcVersion;

        @Schema(name = "ClusterName", title = "集群名称", required = true)
        @JsonProperty(value = "ClusterName", required = true)
        @NotNull(message = "集群名称不能为空")
        private String clusterName;

        @Schema(name = "ClusterType", title = "集群类型", required = true)
        @JsonProperty(value = "ClusterType", required = true)
        @NotNull(message = "集群类型不能为空")
        private ClusterTypeEnum clusterTypeEnum;

//        @Schema(name = "ClusterState", title = "集群状态", required = true)
//        @JsonProperty(value = "ClusterState", required = true)
//        @NotNull(message = "集群状态不能为空")
//        private ClusterStateEnum clusterStateEnum;

        @Schema(name = "ClusterDesc", title = "集群描述", required = true)
        @JsonProperty(value = "ClusterDesc", required = true)
        private String clusterDesc;

        @Schema(name = "RelativeClusterId", title = "关联集群 ID", required = true)
        @JsonProperty(value = "RelativeClusterId", required = true)
        private Long relativeClusterId;
    }
}
