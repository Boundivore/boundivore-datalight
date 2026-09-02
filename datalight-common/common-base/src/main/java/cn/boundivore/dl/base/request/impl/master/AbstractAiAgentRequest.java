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
package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: AI 智能体相关 Request 集合
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/1
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAiAgentRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAiAgentRequest.ConverseRequest",
            description = "AbstractAiAgentRequest.ConverseRequest 智能体流式对话请求体"
    )
    public static class ConverseRequest implements IRequest {

        private static final long serialVersionUID = -6413810288074471322L;

        @Schema(name = "SessionId", title = "会话 ID，首次对话可为空", required = false)
        @JsonProperty(value = "SessionId", required = false)
        private String sessionId;

        @Schema(name = "Message", title = "用户输入", required = true)
        @JsonProperty(value = "Message", required = true)
        @NotBlank(message = "对话内容不能为空")
        private String message;

        @Schema(name = "AutonomyLevel", title = "本次使用的自治级别，留空取服务端配置", required = false)
        @JsonProperty(value = "AutonomyLevel", required = false)
        private String autonomyLevel;

        @Schema(name = "ConfirmToken", title = "确认令牌，执行需确认的动作时携带", required = false)
        @JsonProperty(value = "ConfirmToken", required = false)
        private String confirmToken;

        /**
         * 历史消息。由前端带入，服务端不落库。
         * 会话历史留在前端可以避免 Master 侧再引一套会话存储，
         * 需要跨设备续聊时再考虑落到 AIAgent 的 Redis 里。
         */
        @Schema(name = "History", title = "历史消息", required = false)
        @JsonProperty(value = "History", required = false)
        private List<Map<String, Object>> history = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAiAgentRequest.RegisterRequest",
            description = "AbstractAiAgentRequest.RegisterRequest AIAgent 实例注册与心跳请求体"
    )
    public static class RegisterRequest implements IRequest {

        private static final long serialVersionUID = 4821553092148174539L;

        /**
         * 实例自己上报的可达地址。
         * 由 AIAgent 侧决定而不是 Master 从请求里取远端 IP，
         * 因为跨网段、走网关或做了端口映射时，Master 看到的来源地址回连不了。
         */
        @Schema(name = "BaseUrl", title = "实例地址，形如 http://192.168.1.10:8010", required = true)
        @JsonProperty(value = "BaseUrl", required = true)
        @NotBlank(message = "实例地址不能为空")
        private String baseUrl;

        @Schema(name = "Hostname", title = "实例所在主机名", required = false)
        @JsonProperty(value = "Hostname", required = false)
        private String hostname;

        @Schema(name = "Version", title = "实例版本", required = false)
        @JsonProperty(value = "Version", required = false)
        private String version;
    }
}
