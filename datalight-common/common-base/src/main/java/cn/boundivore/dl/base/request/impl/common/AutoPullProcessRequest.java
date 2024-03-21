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
 * Description: AutoPullProcessRequest
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/21
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "AutoPullProcessRequest",
        description = "AutoPullProcessRequest 自动拉起开关状态 请求体"
)
public class AutoPullProcessRequest implements IRequest {

    private static final long serialVersionUID = -7773673617799232078L;

    @Schema(name = "AutoPullWorker", title = "自动拉起 Worker 开关状态", required = true)
    @JsonProperty(value = "AutoPullWorker", required = true)
    @NotNull
    private Boolean autoPullWorker = true;

    @Schema(name = "AutoPullComponent", title = "自动拉起 Component 开关状态", required = true)
    @JsonProperty(value = "AutoPullComponent", required = true)
    @NotNull
    private Boolean autoPullComponent = true;

    @Schema(name = "CloseDuration", title = "关闭时长", required = true)
    @JsonProperty(value = "CloseDuration", required = true)
    private Long closeDuration = 10 * 60 * 1000L;

}
