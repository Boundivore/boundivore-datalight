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


import cn.boundivore.dl.base.enumeration.impl.ProcedureStateEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

public abstract class AbstractInitProcedureVo {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "InitProcedureVo",
            description = "InitProcedureVo 初始化步骤信息"
    )
    public static class InitProcedureVo implements IVo {

        private static final long serialVersionUID = -5492093532192483710L;

        @Schema(name = "ProcedureId", title = "步骤 ID", required = true)
        @JsonProperty(value = "ProcedureId", required = true)
        private Long procedureId;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @Schema(name = "ProcedureName", title = "初始化步骤名称", required = true)
        @JsonProperty(value = "ProcedureName", required = true)
        private String procedureName;

        @Schema(name = "ProcedureState", title = "初始化步骤状态", required = true)
        @JsonProperty(value = "ProcedureState", required = true)
        private ProcedureStateEnum procedureState;

        @Schema(name = "NodeJobId", title = "NodeJob Id", required = true)
        @JsonProperty(value = "NodeJobId", required = true)
        private Long nodeJobId;

        @Schema(name = "NodeInfoList", title = "节点信息列表", required = true)
        @JsonProperty(value = "NodeInfoList", required = true)
        private List<NodeInfoListVo> nodeInfoList;

        @Schema(name = "JobId", title = "JobId", required = true)
        @JsonProperty(value = "JobId", required = true)
        private Long jobId;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "NodeInfoListVo",
            description = "NodeInfoListVo 节点信息列表响应体"
    )
    public static class NodeInfoListVo implements IVo {
        private static final long serialVersionUID = 578368111091866818L;

        @Schema(name = "NodeId", title = "节点 Id", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @Schema(name = "Hostname", title = "主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;
    }
}
