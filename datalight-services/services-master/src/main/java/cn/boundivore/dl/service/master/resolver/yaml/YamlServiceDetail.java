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

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.enumeration.impl.ServiceTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.StepTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Description: 用于解析 ${SERVICE-NAME}.yaml 的实体 Bean
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@Data
public class YamlServiceDetail {

    @JsonProperty("datalight")
    private DataLight dataLight;

    @Data
    public static class DataLight {
        @JsonProperty(value = "service", required = true)
        private Service service;
    }

    @Data
    public static class Service {
        /**
         * 以下信息需要从 MANIFEST YAML 中填充
         */
        @JsonProperty(value = "dlc-version", required = false)
        private String dlcVersion;

        @JsonProperty(value = "type", required = false)
        private ServiceTypeEnum type;

        @JsonProperty(value = "desc", required = false)
        private String desc;

        @JsonProperty(value = "priority", required = false)
        private long priority;

        @JsonProperty(value = "dependencies", required = false)
        private List<String> dependencies;

        @JsonProperty(value = "relatives", required = false)
        private List<String> relatives;

        /**
         * 以下信息来自于个服务自己的配置文件
         */
        @JsonProperty(value = "name", required = true)
        private String name;

        @JsonProperty(value = "tgz", required = true)
        private String tgz;

        @JsonProperty(value = "conf-dirs", required = true)
        private List<ConfDir> confDirs;

        @JsonProperty(value = "version", required = true)
        private String version;

        @JsonProperty(value = "config-event-handler-jar", required = true)
        private String configEventHandlerJar;

        @JsonProperty(value = "config-event-handler-clazz", required = true)
        private String configEventHandlerClazz;

        /**
         * 服务操作，即组件通用操作
         */
        @JsonProperty(value = "initialize", required = true)
        private Initialize initialize;

        /**
         * 组件操作
         */
        @JsonProperty(value = "components", required = true)
        private List<Component> components;
    }

    @Data
    public static class ConfDir {
        @JsonProperty(value = "service-conf-dir", required = true)
        private String serviceConfDir;

        @JsonProperty(value = "templated-dir", required = true)
        private String templatedDir;

    }

    @Data
    public static class Initialize {
        @JsonProperty(value = "steps", required = true)
        private List<Step> steps;
    }

    @Data
    public static class Component {
        @JsonProperty(value = "name", required = true)
        private String name;

        @JsonProperty(value = "priority", required = true)
        private long priority;

        @JsonProperty(value = "max", required = true)
        private long max;

        @JsonProperty(value = "min", required = true)
        private long min;

        @JsonProperty("mutexes")
        private List<String> mutexes;

        @JsonProperty("dependencies")
        private List<String> dependencies;

        @JsonProperty("actions")
        private List<Action> actions;
    }

    @Data
    public static class Action {
        @JsonProperty(value = "type", required = true)
        private ActionTypeEnum type;

        @JsonProperty(value = "start-state", required = true)
        private SCStateEnum startState;

        @JsonProperty(value = "fail-state", required = true)
        private SCStateEnum failState;

        @JsonProperty(value = "success-state", required = true)
        private SCStateEnum successState;

        @JsonProperty("steps")
        private List<Step> steps;
    }

    @Data
    public static class Step {

        @JsonProperty(value = "type", required = true)
        private StepTypeEnum type;

        @JsonProperty(value = "name", required = true)
        private String name;

        @JsonProperty("jar")
        private String jar;

        @JsonProperty("clazz")
        private String clazz;

        @JsonProperty("shell")
        private String shell;

        @JsonProperty("args")
        private List<String> args;

        @JsonProperty("interactions")
        private List<String> interactions;

        @JsonProperty("exits")
        private int exits;

        @JsonProperty("timeout")
        private long timeout;

        @JsonProperty("sleep")
        private long sleep;
    }

}

