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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
            name = "AbstractClusterRequest.ClusterNewRequest",
            description = "AbstractClusterRequest.ClusterNewRequest: 新建集群 请求体"
    )
    public static class ClusterNewRequest implements IRequest {

        @Schema(name = "DlcVersion", title = "DataLight 服务组件包版本", required = true)
        @JsonProperty(value = "DlcVersion", required = true)
        @NotNull
        private String dlcVersion;

        @Schema(name = "ClusterName", title = "集群名称", required = true)
        @JsonProperty(value = "ClusterName", required = true)
        @NotNull
        private String clusterName;

        @Schema(name = "ClusterType", title = "集群类型", required = true)
        @JsonProperty(value = "ClusterType", required = true)
        @NotNull
        private ClusterTypeEnum clusterTypeEnum;

//        @Schema(name = "ClusterState", title = "集群状态", required = true)
//        @JsonProperty(value = "ClusterState", required = true)
//        @NotNull
//        private ClusterStateEnum clusterStateEnum;

        @Schema(name = "ClusterDesc", title = "集群描述", required = true)
        @JsonProperty(value = "ClusterDesc", required = true)
        private String clusterDesc;

        @Schema(name = "RelativeClusterId", title = "关联集群 ID", required = true)
        @JsonProperty(value = "RelativeClusterId", required = true)
        private Long relativeClusterId;
    }
}
