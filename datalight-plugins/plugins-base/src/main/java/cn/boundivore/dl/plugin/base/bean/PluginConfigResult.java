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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Description: 配置文件修改后的结果集
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class PluginConfigResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long clusterId;

    @NotNull
    private String serviceName;


    private LinkedHashMap<ConfigKey, ConfigValue> configMap;

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class ConfigKey implements Serializable {
        private static final long serialVersionUID = 1L;

        @NotNull
        private Long nodeId;

        @NotNull
        private String configPath;
    }

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class ConfigValue implements Serializable{
        private static final long serialVersionUID = 1L;
        @NotNull
        private String filename;

        @NotNull
        private String configData;

        @NotNull
        private String sha256;
    }
}
