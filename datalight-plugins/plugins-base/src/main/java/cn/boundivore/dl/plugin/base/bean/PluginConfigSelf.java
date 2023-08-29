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
package cn.boundivore.dl.plugin.base.bean;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description: 所依赖服务配置文件发生变动时，当前服务自己的配置文件信息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class PluginConfigSelf implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long clusterId;

    private String serviceName;

    //<ComponentName, List<ConfigSelfComponentNode>>
    private Map<String, List<ConfigSelfComponentNode>> configSelfComponentMap;

    private List<ConfigSelfData> configSelfDataList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    @EqualsAndHashCode(
            exclude = {
                    "configSelfNodeList",
                    "filename",
                    "configData"
            }
    )
    public static class ConfigSelfData implements Serializable {
        private static final long serialVersionUID = 1L;

        private String sha256;

        private String filename;

        private String configPath;

        private String configData;

        private List<ConfigSelfNode> configSelfNodeList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    public static class ConfigSelfNode implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long nodeId;

        private String hostname;

        private String nodeIp;

        private Long configVersion;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    public static class ConfigSelfComponentNode implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long nodeId;

        private String hostname;

        private String nodeIp;
    }
}
