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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * Description: ConfigSaveRequest
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
@Schema(name = "ConfigSaveRequest",
        description = "ConfigSaveRequest: 保存服务组件配置 请求体"
)
public class ConfigSaveRequest implements IRequest {

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull
    private Long clusterId;

    @Schema(name = "ServiceName", title = "当前服务", required = true)
    @JsonProperty(value = "ServiceName", required = true)
    @NotNull
    private String serviceName;

    @Schema(name = "ConfigList", title = "服务组件配置信息列表", required = true)
    @JsonProperty(value = "ConfigList", required = true)
    @NotEmpty
    private List<ConfigRequest> configList;


    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
//    @Schema(
//            name = "ConfigSaveRequest#ConfigRequest",
//            description = "ConfigSaveRequest#ConfigRequest: 单个配置 请求体"
//    )
    @Schema(name = "ConfigSaveRequest#ConfigRequest", description = "ConfigSaveRequest#ConfigRequest: 单个配置 请求体")
    public static class ConfigRequest implements IRequest {

        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        @NotNull
        private Long nodeId;

        @Schema(name = "Filename", title = "配置文件名称", required = true)
        @JsonProperty(value = "Filename", required = true)
        @NotNull
        private String filename;

        @Schema(name = "ConfigData", title = "配置文件内容(Base64)", required = true)
        @JsonProperty(value = "ConfigData", required = true)
        @NotNull
        private String configData;

        @Schema(name = "Sha256", title = "配置文件内容信息摘要", required = true)
        @JsonProperty(value = "Sha256", required = true)
        @NotNull
        private String sha256;

        @Schema(name = "ConfigPath", title = "配置文件内容信息摘要", required = true)
        @JsonProperty(value = "ConfigPath", required = true)
        @NotNull
        private String configPath;

    }


}
