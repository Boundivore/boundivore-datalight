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


import cn.boundivore.dl.base.enumeration.impl.ClusterStateEnum;
import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.response.IVo;
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


public abstract class AbstractClusterVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractClusterVo.ClusterListVo",
            description = "AbstractClusterVo.ClusterListVo 集群信息列表"
    )
    public static class ClusterListVo implements IVo {
        @Schema(name = "ClusterList", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterList", required = true)
        @NotNull
        private List<ClusterVo> clusterList;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractClusterVo.ClusterVo",
            description = "AbstractClusterVo.ClusterVo 集群信息"
    )
    public static class ClusterVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

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

        @Schema(name = "ClusterState", title = "集群状态", required = true)
        @JsonProperty(value = "ClusterState", required = true)
        @NotNull
        private ClusterStateEnum clusterStateEnum;

        @Schema(name = "ClusterDesc", title = "集群描述", required = true)
        @JsonProperty(value = "ClusterDesc", required = true)
        @NotNull
        private String clusterDesc;

        @Schema(name = "RelativeClusterId", title = "关联集群 ID", required = true)
        @JsonProperty(value = "RelativeClusterId", required = true)
        @NotNull
        private Long relativeClusterId;

        @Schema(name = "IsExistInitProcedure", title = "出否存在未完成的步骤信息", required = true)
        @JsonProperty(value = "IsExistInitProcedure", required = true)
        @NotNull
        private Boolean isExistInitProcedure = false;

    }

}
