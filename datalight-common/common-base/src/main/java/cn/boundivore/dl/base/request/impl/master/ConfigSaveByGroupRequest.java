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
import lombok.*;
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
@ApiModel(
        value = "ConfigSaveByGroupRequest",
        description = "ConfigSaveByGroupRequest: 保存服务组件配置 请求体"
)
public class ConfigSaveByGroupRequest implements IRequest {


    @ApiModelProperty(name = "ClusterId", value = "集群 ID", required = true)
    @JsonProperty(value = "ClusterId", required = true)
    @NotNull
    private Long clusterId;

    @ApiModelProperty(name = "ServiceName", value = "服务名称", required = true)
    @JsonProperty(value = "ServiceName", required = true)
    @NotNull
    private String serviceName;

    @ApiModelProperty(name = "ConfigGroupList", value = "配置信息分组列表", required = true)
    @JsonProperty(value = "ConfigGroupList", required = true)
    @NotEmpty
    private List<ConfigGroupRequest> configGroupList;

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
            value = "ConfigSaveByGroupRequest#ConfigGroupRequest",
            description = "ConfigSaveByGroupRequest#ConfigGroupRequest: 当前配置信息"
    )
    public static class ConfigGroupRequest implements IRequest {

        @ApiModelProperty(name = "Sha256", value = "配置文件唯一信息摘要", required = true)
        @JsonProperty(value = "Sha256", required = true)
        @NotNull
        private String sha256;

        @ApiModelProperty(name = "Filename", value = "配置文件名称", required = true)
        @JsonProperty(value = "Filename", required = true)
        @NotNull
        private String filename;

        @ApiModelProperty(name = "ConfigPath", value = "配置文件绝对路径", required = true)
        @JsonProperty(value = "ConfigPath", required = true)
        @NotNull
        private String configPath;

        @ApiModelProperty(name = "ConfigData", value = "配置文件内容(Base64)", required = true)
        @JsonProperty(value = "ConfigData", required = true)
        @NotNull
        private String configData;

        @ApiModelProperty(name = "ConfigNodeList", value = "配置文件所在节点", required = true)
        @JsonProperty(value = "ConfigNodeList", required = true)
        @NotEmpty
        private List<ConfigNodeRequest> configNodeList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    @ApiModel(
            value = "ConfigSaveByGroupRequest#ConfigNodeRequest",
            description = "ConfigSaveByGroupRequest#ConfigNodeRequest: 节点信息"
    )
    public static class ConfigNodeRequest implements IRequest {

        @ApiModelProperty(name = "NodeId", value = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        @NotNull
        private Long nodeId;

        @ApiModelProperty(name = "Hostname", value = "节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        @NotNull
        private String hostname;

        @ApiModelProperty(name = "NodeIp", value = "节点 IP 地址", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        @NotNull
        private String nodeIp;

        @ApiModelProperty(name = "ConfigVersion", value = "配置文件当前版本", required = true)
        @JsonProperty(value = "ConfigVersion", required = true)
        @NotNull
        private Long configVersion;
    }


}
