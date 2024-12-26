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
package cn.boundivore.dl.base.response.impl.worker;

import cn.boundivore.dl.base.response.IVo;
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
 * Description: 配置文件差异 响应体
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
@Schema(name = "ConfigDifferVo",
        description = "ConfigDifferVo: 配置文件差异 响应体"
)
public class ConfigDifferVo implements IVo {

    private static final long serialVersionUID = 5689403292574643729L;

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    private Long clusterId;

    @Schema(name = "NodeId", title = "节点 ID", required = true)
    @JsonProperty(value = "NodeId", required = true)
    private Long nodeId;

    @Schema(name = "ServiceName", title = "服务名称", required = true)
    @JsonProperty(value = "ServiceName", required = true)
    private String serviceName;

    @Schema(name = "ConfigDetailList", title = "配置文件详情列表", required = true)
    @JsonProperty(value = "ConfigDetailList", required = true)
    private List<ConfigDetailVo> configDetailList;

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "ConfigDifferVo.ConfigDetailVo",
            description = "ConfigDifferVo.ConfigDetailVo: 配置文件详情 响应体"
    )
    public static class ConfigDetailVo implements IVo {

        private static final long serialVersionUID = -5675053746054474265L;

        @Schema(name = "FileName", title = "配置文件名称", required = true)
        @JsonProperty(value = "FileName", required = true)
        private String filename;

        @Schema(name = "Sha256", title = "配置文件内容信息摘要", required = true)
        @JsonProperty(value = "Sha256", required = true)
        private String sha256;

        @Schema(name = "ConfigData", title = "配置文件内容(Base64)", required = true)
        @JsonProperty(value = "ConfigData", required = true)
        private String configData;

        @Schema(name = "ConfigPath", title = "配置文件路径", required = true)
        @JsonProperty(value = "ConfigPath", required = true)
        private String configPath;
    }
}
