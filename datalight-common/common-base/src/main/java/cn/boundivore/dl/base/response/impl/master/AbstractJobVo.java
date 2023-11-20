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

public abstract class AbstractJobVo {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "JobIdVo",
            description = "JobIdVo: JobId 信息"
    )
    public static class JobIdVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "JobId", title = "异步 Job Id", required = true)
        @JsonProperty(value = "JobId", required = true)
        @NotNull
        private Long jobId;

    }
}
