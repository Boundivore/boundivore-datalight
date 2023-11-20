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
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Description: 获取指定配置文件的详细信息（按照配置文件内容和节点进行分组）
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/19
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
@Schema(
        name = "ConfigListByGroupVo",
        description = "ConfigListByGroupVo: 指定配置文件的详细信息（包含分组）"
)
public class ConfigListByGroupVo implements IVo {

    @Schema(name = "ClusterId", title = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    private Long clusterId;

    @Schema(name = "ServiceName", title = "服务名称", required = true)
    @JsonProperty(value = "ServiceName", required = true)
    private String serviceName;

    @Schema(name = "ConfigGroupList", title = "配置信息分组列表", required = true)
    @JsonProperty(value = "ConfigGroupList", required = true)
    private List<ConfigGroupVo> configGroupList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    @EqualsAndHashCode(
            exclude = {
                    "configNodeList",
                    "filename",
                    "configData"
            }
    )
    @Schema(
            name = "ConfigListByGroupVo.ConfigGroupVo",
            description = "ConfigListByGroupVo.ConfigGroupVo: 当前配置信息"
    )
    public static class ConfigGroupVo implements IVo {

        @Schema(name = "Sha256", title = "配置文件唯一信息摘要", required = true)
        @JsonProperty(value = "Sha256", required = true)
        private String sha256;

        @Schema(name = "Filename", title = "配置文件名称", required = true)
        @JsonProperty(value = "Filename", required = true)
        private String filename;

        @Schema(name = "ConfigPath", title = "配置文件绝对路径", required = true)
        @JsonProperty(value = "ConfigPath", required = true)
        private String configPath;

        @Schema(name = "ConfigData", title = "配置文件内容(Base64)", required = true)
        @JsonProperty(value = "ConfigData", required = true)
        private String configData;

        @Schema(name = "ConfigNodeList", title = "配置文件所在节点", required = true)
        @JsonProperty(value = "ConfigNodeList", required = true)
        private List<ConfigNodeVo> configNodeList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    @Schema(
            name = "ConfigListByGroupVo.ConfigNodeVo",
            description = "ConfigListByGroupVo.ConfigNodeVo: 节点信息"
    )
    public static class ConfigNodeVo implements IVo {

        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @Schema(name = "Hostname", title = "节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

        @Schema(name = "NodeIp", title = "节点 IP 地址", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        private String nodeIp;

        @Schema(name = "ConfigVersion", title = "配置文件当前版本", required = true)
        @JsonProperty(value = "ConfigVersion", required = true)
        private Long configVersion;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    @Schema(
            name = "ConfigListByGroupVo.ComponentNodeVo",
            description = "ConfigListByGroupVo.ComponentNodeVo: 组件所在节点信息"
    )
    public static class ComponentNodeVo implements IVo {

        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @Schema(name = "Hostname", title = "节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

        @Schema(name = "NodeIp", title = "节点 IP 地址", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        private String nodeIp;
    }
}
