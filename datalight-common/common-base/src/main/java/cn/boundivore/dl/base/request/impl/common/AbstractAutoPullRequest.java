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
package cn.boundivore.dl.base.request.impl.common;

import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Description: 进程自动拉起开关相关请求体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/27
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAutoPullRequest {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAutoPullRequest.AutoPullProcessRequest",
            description = "AbstractAutoPullRequest.AutoPullProcessRequest 自动拉起 Worker 开关请求体"
    )
    public static class AutoPullWorkerRequest implements IRequest {

        private static final long serialVersionUID = -7773673617799232078L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "AutoPullWorker", title = "自动拉起 Worker 开关状态", required = true)
        @JsonProperty(value = "AutoPullWorker", required = true)
        @NotNull
        private Boolean autoPullWorker = true;

        @Schema(name = "CloseDuration", title = "关闭时长", required = true)
        @JsonProperty(value = "CloseDuration", required = true)
        @NotNull
        private Long closeDuration;

    }
}
