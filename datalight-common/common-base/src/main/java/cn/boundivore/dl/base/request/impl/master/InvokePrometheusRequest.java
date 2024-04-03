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

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.request.IRequest;
import cn.hutool.http.Method;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: JobRequest
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(
        name = "InvokePrometheusRequest",
        description = "InvokePrometheusRequest: 代理调用 Prometheus 执行请求体"
)
public class InvokePrometheusRequest implements IRequest {

    private static final long serialVersionUID = 7295785643301326121L;

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull
    private Long clusterId;

    @Schema(name = "RequestMethod", title = "Http 请求 Prometheus 方式", required = true)
    @JsonProperty(value = "RequestMethod", required = true)
    @NotNull
    private Method requestMethod;

    @Schema(name = "Path", title = "请求 Prometheus 的路径", required = true)
    @JsonProperty(value = "Path", required = true)
    @NotNull
    private String path;

    @Schema(name = "Body", title = "请求 Prometheus 的请求体", required = true)
    @JsonProperty(value = "Body", required = true)
    @NotNull
    private String body;

}
