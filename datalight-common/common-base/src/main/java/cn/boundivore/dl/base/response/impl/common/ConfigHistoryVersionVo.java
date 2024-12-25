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
package cn.boundivore.dl.base.response.impl.common;

import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Description: 配置文件历史版本列表
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/12/24
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
        name = "ConfigHistoryVersionVo",
        description = "ConfigHistoryVersionVo 配置文件历史信息"
)
public class ConfigHistoryVersionVo implements IVo {

    private static final long serialVersionUID = -533504461326191029L;

    @Schema(name = "CurrentConfigId", title = "当前生效的配置文件 ID", required = true)
    @JsonProperty(value = "CurrentConfigId", required = true)
    private Long currentConfigId;

    @Schema(name = "ConfigHistoryVersionSummaryList", title = "历史配置文件概览列表", required = true)
    @JsonProperty(value = "ConfigHistoryVersionSummaryList", required = true)
    private List<ConfigHistoryVersionSummaryVo> configHistoryVersionSummaryVoList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    @Schema(
            name = "ConfigVersionVo.ConfigHistoryVersionSummaryVo",
            description = "ConfigVersionVo.ConfigHistoryVersionSummaryVo 历史版本文件概览信息"
    )
    public static class ConfigHistoryVersionSummaryVo implements IVo {

        private static final long serialVersionUID = -7069614664638343064L;

        @Schema(name = "HistoryConfigVersion", title = "历史配置文件版本", required = true)
        @JsonProperty(value = "HistoryConfigVersion", required = true)
        private Long historyConfigVersion;

        @Schema(name = "ConfigVersionFileName", title = "历史配置文件文件名", required = true)
        @JsonProperty(value = "ConfigVersionFileName", required = true)
        private String configVersionFileName;

        @Schema(name = "ConfigVersionTimestamp", title = "历史配置文件创建时间戳", required = true)
        @JsonProperty(value = "ConfigVersionTimestamp", required = true)
        private Long configVersionTimestamp;

        @Schema(name = "ConfigVersionDateTimeFormat", title = "历史配置文件创建时间格式化", required = true)
        @JsonProperty(value = "ConfigVersionDateTimeFormat", required = true)
        private String configVersionDateTimeFormat;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    @Schema(
            name = "ConfigVersionVo.ConfigVersionDetailVo",
            description = "ConfigVersionVo.ConfigVersionDetailVo 指定版本的配置文件详情"
    )
    public static class ConfigVersionDetailVo implements IVo {
        private static final long serialVersionUID = -5037217242203888265L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        private Long clusterId;

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        private String serviceName;

        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        private Long nodeId;

        @Schema(name = "Hostname", title = "节点主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        private String hostname;

        @Schema(name = "NodeIp", title = "节点 IP 地址", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        private String nodeIp;

        @Schema(name = "ConfigFileName", title = "配置文件名称", required = true)
        @JsonProperty(value = "ConfigFileName", required = true)
        private String configFileName;

        @Schema(name = "ConfigFilePath", title = "配置文件路径", required = true)
        @JsonProperty(value = "ConfigFilePath", required = true)
        private String configFilePath;

        @Schema(name = "CurrentConfigVersion", title = "当前生效的配置文件版本", required = true)
        @JsonProperty(value = "CurrentConfigVersion", required = true)
        private Long currentConfigVersion;

        @Schema(name = "HistoryConfigVersion", title = "本次查看的历史配置文件版本", required = true)
        @JsonProperty(value = "HistoryConfigVersion", required = true)
        private Long historyConfigVersion;

        @Schema(name = "Sha256", title = "配置文件唯一信息摘要", required = true)
        @JsonProperty(value = "Sha256", required = true)
        private String sha256;

        @Schema(name = "ConfigData", title = "配置文件内容(Base64)", required = true)
        @JsonProperty(value = "ConfigData", required = true)
        private String configData;
    }
}
