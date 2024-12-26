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
package cn.boundivore.dl.base.request.impl.worker;

import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: 读取节点本地配置文件请求体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/12/25
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ConfigDiffRequest",
        description = "ConfigDiffRequest: 读取节点本地配置文件 请求体"
)
public class ConfigDiffRequest implements IRequest {

    private static final long serialVersionUID = -5675053746054474265L;

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull(message = "集群 ID 不能为空")
    private Long clusterId;

    @Schema(name = "NodeId", title = "节点 ID", required = true)
    @JsonProperty(value = "NodeId", required = true)
    @NotNull(message = "节点 ID 不能为空")
    private Long nodeId;

    @Schema(name = "ServiceName", title = "服务名称", required = true)
    @JsonProperty(value = "ServiceName", required = true)
    @NotNull(message = "服务名称不能为空")
    private String serviceName;

    @Schema(name = "ConfigInfoList", title = "配置文件信息列表", required = true)
    @JsonProperty(value = "ConfigInfoList", required = true)
    @NotEmpty(message = "配置文件信息列表不能为空")
    private List<ConfigInfoRequest> configInfoList;

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ConfigDiffRequest.ConfigInfoRequest",
            description = "ConfigDiffRequest.ConfigInfoRequest: 待读取的配置文件信息 请求体"
    )
    public static class ConfigInfoRequest implements IRequest {

        private static final long serialVersionUID = -5675053746054474265L;

        @Schema(name = "Filename", title = "配置文件名称", required = true)
        @JsonProperty(value = "Filename", required = true)
        @NotNull(message = "配置文件名称不能为空")
        private String filename;

        @Schema(name = "Sha256", title = "配置文件内容信息摘要", required = true)
        @JsonProperty(value = "Sha256", required = true)
        @NotNull(message = "配置文件内容信息摘要不能为空")
        private String sha256;

        @Schema(name = "ConfigPath", title = "配置文件路径", required = true)
        @JsonProperty(value = "ConfigPath", required = true)
        @NotNull(message = "配置文件路径不能为空")
        private String configPath;
    }
}
