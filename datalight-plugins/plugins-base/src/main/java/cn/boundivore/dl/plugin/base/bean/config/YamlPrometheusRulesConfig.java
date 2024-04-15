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
package cn.boundivore.dl.plugin.base.bean.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Description: rules.yml 对应的 JavaBean 实体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/15
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
public class YamlPrometheusRulesConfig {

    @JsonProperty("groups")
    private List<RuleGroup> groups;

    @Data
    public static class RuleGroup {
        @JsonProperty("name")
        private String name;

        @JsonProperty("rules")
        private List<Rule> rules;
    }

    @Data
    public static class Rule {
        @JsonProperty("alert")
        private String alert;

        @JsonProperty("expr")
        private String expr;

        @JsonProperty("for")
        private String duration;

        @JsonProperty("labels")
        private Map<String, String> labels;

        @JsonProperty("annotations")
        private Map<String, String> annotations;
    }
}
