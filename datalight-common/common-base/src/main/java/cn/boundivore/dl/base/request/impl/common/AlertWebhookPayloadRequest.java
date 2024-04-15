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
package cn.boundivore.dl.base.request.impl.common;

import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Description: 告警回调请求体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/15
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "AlertWebhookPayloadRequest",
        description = "AlertWebhookPayloadRequest AlertManager 告警回调请求体"
)
public class AlertWebhookPayloadRequest implements IRequest {

    private static final long serialVersionUID = 6039125380395960156L;

    @Schema(name = "version", title = "版本号", required = true)
    @JsonProperty(value = "version", required = true)
    private String version;

    @Schema(name = "groupKey", title = "组键", required = true)
    @JsonProperty(value = "groupKey", required = true)
    private String groupKey;

    @Schema(name = "groupLabels", title = "组标签", required = true)
    @JsonProperty(value = "groupLabels", required = true)
    private Map<String, String> groupLabels;

    @Schema(name = "commonLabels", title = "通用标签", required = true)
    @JsonProperty(value = "commonLabels", required = true)
    private Map<String, String> commonLabels;

    @Schema(name = "alerts", title = "告警列表", required = true)
    @JsonProperty(value = "alerts", required = true)
    private List<Alert> alerts;

    @Schema(name = "resolvedAlerts", title = "已解决告警列表", required = true)
    @JsonProperty(value = "resolvedAlerts", required = true)
    private List<Alert> resolvedAlerts;

    @Schema(name = "receiver", title = "接收器名称", required = true)
    @JsonProperty(value = "receiver", required = true)
    private String receiver;

    @Schema(name = "status", title = "状态", required = true)
    @JsonProperty(value = "status", required = true)
    private String status;

    @Schema(name = "externalURL", title = "外部 URL", required = true)
    @JsonProperty(value = "externalURL", required = true)
    private String externalURL;

    @Schema(name = "generatorURL", title = "生成器 URL", required = true)
    @JsonProperty(value = "generatorURL", required = true)
    private String generatorURL;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AlertWebhookPayloadRequest.Alert",
            description = "AlertWebhookPayloadRequest.Alert AlertManager 告警回调请求体"
    )

    public static class Alert implements IRequest {
        private static final long serialVersionUID = 8633461993773312932L;

        @Schema(name = "status", title = "状态", required = true)
        @JsonProperty(value = "status", required = true)
        private String status;

        @Schema(name = "labels", title = "标签", required = true)
        @JsonProperty(value = "labels", required = true)
        private Map<String, String> labels;

        @Schema(name = "annotations", title = "注解", required = true)
        @JsonProperty(value = "annotations", required = true)
        private Map<String, String> annotations;

        @Schema(name = "startsAt", title = "开始时间", required = true)
        @JsonProperty(value = "startsAt", required = true)
        private String startsAt;

        @Schema(name = "endsAt", title = "结束时间", required = true)
        @JsonProperty(value = "endsAt", required = true)
        private String endsAt;

        @Schema(name = "generatorURL", title = "生成器 URL", required = true)
        @JsonProperty(value = "generatorURL", required = true)
        private String generatorURL;

        @Schema(name = "fingerprint", title = "指纹", required = true)
        @JsonProperty(value = "fingerprint", required = true)
        private String fingerprint;

    }
}
