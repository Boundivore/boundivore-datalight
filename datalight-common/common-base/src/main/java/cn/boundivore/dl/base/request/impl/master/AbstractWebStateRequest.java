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

import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: 前端状态缓存相关 Request 集合
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

public abstract class AbstractWebStateRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractWebStateRequest.SaveStateRequest",
            description = "AbstractWebStateRequest.SaveStateRequest 保存状态信息 请求体"
    )
    public static class SaveStateRequest implements IRequest {
        private static final long serialVersionUID = 3961737266654424833L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "UserId", title = "用户 ID", required = true)
        @JsonProperty(value = "UserId", required = true)
        @NotNull
        private Long userId;

        @Schema(name = "WebKey", title = "缓存键", required = true)
        @JsonProperty(value = "WebKey", required = true)
        @NotNull
        private String webKey;

        @Schema(name = "WebValue", title = "缓存值", required = true)
        @JsonProperty(value = "WebValue", required = true)
        private String webValue;
    }
}
