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

@Data
public class YamlServiceWebUI {
    @JsonProperty(value = "datalight", required = true)
    private YamlServiceWebUI.DataLight datalight;

    @Data
    public static class DataLight {
        @JsonProperty("components")
        private List<YamlServiceWebUI.Component> services;
    }

    @Data
    public static class Component {
        @JsonProperty("service")
        private String service;

        @JsonProperty("component")
        private String component;

        @JsonProperty("port")
        private String port;

        @JsonProperty("path")
        private String path;

        @JsonProperty("button-name-suffix")
        private String buttonNameSuffix;
    }
}
