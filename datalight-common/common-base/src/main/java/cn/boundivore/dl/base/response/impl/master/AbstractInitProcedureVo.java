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

public abstract class AbstractInitProcedureVo {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "JobIdVo",
            description = "JobIdVo: JobId 信息"
    )
    public static class InitProcedureVo implements IVo {

        @Schema(name = "ProcedureId", title = "步骤 ID", required = true)
        @JsonProperty(value = "ProcedureId", required = true)
        @NotNull
        private Long procedureId;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "Tag", title = "初始化步骤唯一标识", required = true)
        @JsonProperty(value = "Tag", required = true)
        @NotNull
        private String tag;

        @Schema(name = "ProcedureName", title = "初始化步骤名称", required = true)
        @JsonProperty(value = "ProcedureName", required = true)
        @NotNull
        private String procedureName;

        @Schema(name = "ProcedureState", title = "初始化步骤状态", required = true)
        @JsonProperty(value = "ProcedureState", required = true)
        @NotNull
        private ProcedureStateEnum procedureState;

    }
}
