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
 * Description: JavaBean 用于解析 Prometheus 规则配置文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/16
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Data
public class YamlPrometheusRules {

    @JsonProperty("groups")
    private List<Group> groups;

    /**
     * Description: 表示 Prometheus 规则组
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Data
    public static class Group {
        @JsonProperty("name")
        private String name;

        @JsonProperty("rules")
        private List<Rule> rules;
    }

    /**
     * Description: 表示单条 Prometheus 规则
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Data
    public static class Rule {
        @JsonProperty("alert")
        private String alert;

        @JsonProperty("expr")
        private String expr;

        @JsonProperty("for")
        private String duration;

        @JsonProperty("labels")
        private Labels labels;

        @JsonProperty("annotations")
        private Annotations annotations;
    }

    /**
     * Description: 表示 Prometheus 规则的标签
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Data
    public static class Labels {
        @JsonProperty("severity")
        private String severity;
    }

    /**
     * Description: 表示 Prometheus 规则的注释
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Data
    public static class Annotations {
        @JsonProperty("summary")
        private String summary;

        @JsonProperty("description")
        private String description;
    }
}
