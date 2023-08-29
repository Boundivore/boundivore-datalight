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
@ApiModel(
        value = "ConfigListByGroupVo",
        description = "ConfigListByGroupVo: 指定配置文件的详细信息（包含分组）"
)
public class ConfigListByGroupVo implements IVo {

    @ApiModelProperty(name = "ClusterId", value = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    private Long clusterId;

    @ApiModelProperty(name = "ServiceName", value = "服务名称", required = true)
    @JsonProperty(value = "ServiceName", required = true)
    private String serviceName;

    @ApiModelProperty(name = "ConfigComponentList", value = "组件分布信息", required = true)
    @JsonProperty(value = "ConfigComponentList", required = true)
    private List<ConfigComponentVo> configComponentList;

    @ApiModelProperty(name = "ConfigGroupList", value = "配置信息分组列表", required = true)
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
    @ApiModel(
            value = "ConfigListByGroupVo.ConfigGroupVo",
            description = "ConfigListByGroupVo.ConfigGroupVo: 当前配置信息"
    )
    public static class ConfigGroupVo implements IVo {

        @ApiModelProperty(name = "Sha256", value = "配置文件唯一信息摘要", required = true)
        @JsonProperty(value = "Sha256", required = true)
        private String sha256;

        @ApiModelProperty(name = "Filename", value = "配置文件名称", required = true)
        @JsonProperty(value = "Filename", required = true)
        private String filename;

        @ApiModelProperty(name = "ConfigPath", value = "配置文件绝对路径", required = true)
        @JsonProperty(value = "ConfigPath", required = true)
        private String configPath;

        @ApiModelProperty(name = "ConfigData", value = "配置文件内容(Base64)", required = true)
        @JsonProperty(value = "ConfigData", required = true)
        private String configData;

        @ApiModelProperty(name = "ConfigNodeList", value = "配置文件所在节点", required = true)
        @JsonProperty(value = "ConfigNodeList", required = true)
        private List<ConfigNodeVo> configNodeList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    @ApiModel(
            value = "ConfigListByGroupVo.ConfigNodeVo",
            description = "ConfigListByGroupVo.ConfigNodeVo: 节点信息"
    )
    public static class ConfigNodeVo implements IVo {

        @ApiModelProperty(name = "NodeId", value = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @ApiModelProperty(name = "Hostname", value = "节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

        @ApiModelProperty(name = "NodeIp", value = "节点 IP 地址", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        private String nodeIp;

        @ApiModelProperty(name = "ConfigVersion", value = "配置文件当前版本", required = true)
        @JsonProperty(value = "ConfigVersion", required = true)
        private Long configVersion;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    @ApiModel(
            value = "ConfigListByGroupVo.ComponentNodeVo",
            description = "ConfigListByGroupVo.ComponentNodeVo: 组件所在节点信息"
    )
    public static class ComponentNodeVo implements IVo {

        @ApiModelProperty(name = "NodeId", value = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @ApiModelProperty(name = "Hostname", value = "节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

        @ApiModelProperty(name = "NodeIp", value = "节点 IP 地址", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        private String nodeIp;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    @ApiModel(
            value = "ConfigListByGroupVo.ConfigComponentVo",
            description = "ConfigListByGroupVo.ConfigComponentVo: 组件分布信息"
    )
    public static class ConfigComponentVo implements IVo {

        @ApiModelProperty(name = "ComponentName", value = "组件名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        private String componentName;

        @ApiModelProperty(name = "ComponentNodeList", value = "组件所在节点", required = true)
        @JsonProperty(value = "ComponentNodeList", required = true)
        private List<ComponentNodeVo> componentNodeList;
    }
}
