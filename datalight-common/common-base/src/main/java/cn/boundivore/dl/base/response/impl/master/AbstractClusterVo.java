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
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
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
        private static final long serialVersionUID = -2237685706202922155L;

        @Schema(name = "ClusterList", title = "集群信息列表", required = true)
        @JsonProperty(value = "ClusterList", required = true)
        @NotEmpty(message = "集群信息列表不能为空")
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

        private static final long serialVersionUID = 2391231942097797211L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @Schema(name = "DlcVersion", title = "DataLight 服务组件包版本", required = true)
        @JsonProperty(value = "DlcVersion", required = true)
        private String dlcVersion;

        @Schema(name = "ClusterName", title = "集群名称", required = true)
        @JsonProperty(value = "ClusterName", required = true)
        private String clusterName;

        @Schema(name = "ClusterType", title = "集群类型", required = true)
        @JsonProperty(value = "ClusterType", required = true)
        private ClusterTypeEnum clusterTypeEnum;

        @Schema(name = "ClusterState", title = "集群状态", required = true)
        @JsonProperty(value = "ClusterState", required = true)
        private ClusterStateEnum clusterStateEnum;

        @Schema(name = "ClusterDesc", title = "集群描述", required = true)
        @JsonProperty(value = "ClusterDesc", required = true)
        private String clusterDesc;

        @Schema(name = "RelativeClusterId", title = "关联集群 ID", required = true)
        @JsonProperty(value = "RelativeClusterId", required = true)
        private Long relativeClusterId;

        @Schema(name = "IsExistInitProcedure", title = "出否存在未完成的步骤信息", required = true)
        @JsonProperty(value = "IsExistInitProcedure", required = true)
        private Boolean isExistInitProcedure = false;

        @Schema(name = "HasAlreadyNode", title = "集群是否存在已服役节点", required = true)
        @JsonProperty(value = "HasAlreadyNode", required = true)
        private Boolean hasAlreadyNode = false;

        @Schema(name = "IsCurrentView", title = "集群是否在首页正在被预览", required = true)
        @JsonProperty(value = "IsCurrentView", required = true)
        private Boolean isCurrentView = false;

    }

}
