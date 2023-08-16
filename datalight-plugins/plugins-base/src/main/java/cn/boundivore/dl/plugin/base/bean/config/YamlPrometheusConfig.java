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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;

import java.util.List;

/**
 * Description: prometheus.yml 对应的 JavaBean 实体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/10
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
public class YamlPrometheusConfig {

    @JsonProperty("global")
    private GlobalConfig global;

    @JsonProperty("alerting")
    private AlertingConfig alerting;

    @JsonProperty("rule_files")
    private List<String> ruleFiles;

    @JsonProperty("scrape_configs")
    private List<ScrapeConfig> scrapeConfigs;

    @Data
    public static class GlobalConfig {
        @JsonProperty("scrape_interval")
        private String scrapeInterval;

        @JsonProperty("evaluation_interval")
        private String evaluationInterval;

        @JsonProperty("scrape_timeout")
        private String scrapeTimeout;
    }

    @Data
    public static class AlertingConfig {
        @JsonProperty("alertmanagers")
        private List<AlertmanagerConfig> alertmanagers;
    }

    @Data
    public static class AlertmanagerConfig {
        @JsonProperty("static_configs")
        private List<StaticConfig> staticConfigs;
    }

    @Data
    public static class ScrapeConfig {
        @JsonProperty("job_name")
        private String jobName;

        @JsonProperty("metrics_path")
        private String metricsPath;

        @JsonProperty("static_configs")
        private List<StaticConfig> staticConfigs;
    }

    @Data
    public static class StaticConfig {
        @JsonProperty("targets")
        private List<String> targets;
    }
}
