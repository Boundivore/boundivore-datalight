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
package cn.boundivore.dl.service.master.resolver.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Description: plugins/templated/${ServiceName}/${ServiceName}-PLACEHOLDER.yaml 的 Java Bean
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/16
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Data
public class YamlServicePlaceholder {

    @JsonProperty("datalight")
    private DataLight dataLight;

    @Data
    public static class DataLight {
        @JsonProperty("service")
        private Service service;
    }

    @Data
    public static class Service {
        @JsonProperty("name")
        private String name;

        @JsonProperty("placeholder-infos")
        private List<PlaceholderInfo> placeholderInfos;
    }

    @Data
    public static class PlaceholderInfo {
        @JsonProperty("templated-file-path")
        private String templatedFilePath;

        @JsonProperty("properties")
        private List<Property> properties;
    }

    @Data
    public static class Property {
        @JsonProperty("placeholder")
        private String placeholder;

        @JsonProperty("describe")
        private String describe;

        @JsonProperty("default")
        private String defaultValue;
    }
}



