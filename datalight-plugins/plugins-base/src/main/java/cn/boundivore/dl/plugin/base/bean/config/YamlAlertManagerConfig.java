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

/**
 * Description: alertmanager.yml 对应的 JavaBean 实体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
public class YamlAlertManagerConfig {

    @JsonProperty("global")
    private GlobalConfig global;

    @JsonProperty("route")
    private RouteConfig route;

    @JsonProperty("receivers")
    private List<ReceiverConfig> receivers;

    @JsonProperty("inhibit_rules")
    private List<InhibitRuleConfig> inhibitRules;

    @Data
    public static class GlobalConfig {
        @JsonProperty("resolve_timeout")
        private String resolveTimeout;
    }

    @Data
    public static class RouteConfig {
        @JsonProperty("group_by")
        private List<String> groupBy;

        @JsonProperty("group_wait")
        private String groupWait;

        @JsonProperty("group_interval")
        private String groupInterval;

        @JsonProperty("repeat_interval")
        private String repeatInterval;

        @JsonProperty("receiver")
        private String receiver;
    }

    @Data
    public static class ReceiverConfig {
        @JsonProperty("name")
        private String name;

        @JsonProperty("webhook_configs")
        private List<WebhookConfig> webhookConfigs;

        @Data
        public static class WebhookConfig {
            @JsonProperty("url")
            private String url;

            @JsonProperty("send_resolved")
            private Boolean sendResolved;
        }
    }

    @Data
    public static class InhibitRuleConfig {
        @JsonProperty("source_match")
        private MatchConfig sourceMatch;

        @JsonProperty("target_match")
        private MatchConfig targetMatch;

        @JsonProperty("equal")
        private List<String> equal;

        @Data
        public static class MatchConfig {
            @JsonProperty("severity")
            private String severity;
        }
    }
}
