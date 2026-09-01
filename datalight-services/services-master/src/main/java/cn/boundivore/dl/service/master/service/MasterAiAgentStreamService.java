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
package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.base.request.impl.master.AbstractAiAgentRequest;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: AI 智能体流式对话的中转。
 * <p>
 * 链路是 前端 → Master → AIAgent。前端不直连 AIAgent，
 * 这样鉴权、审计、限流都收在 Master 一处，Python 侧只认内部调用密钥，
 * 不需要理解平台的用户体系。
 * <p>
 * 本类做的是 SSE 字节流的透传，不解析事件内容。解析放在前端，
 * 中间多一次解析既无必要，又会在协议演进时多一个要同步改的地方。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/1
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
public class MasterAiAgentStreamService {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * AIAgent 的流式对话路径
     */
    private static final String AI_CONVERSE_PATH = "/api/v1/ai/py/agent/converse/stream";

    /**
     * 与 AIAgent 约定的内部调用密钥请求头
     */
    private static final String INTERNAL_HEADER = "X-DataLight-Internal";

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);

    /**
     * 流式读超时。一次对话可能连续调多轮工具，中间几十秒没有正文输出属正常，
     * 这里要给足，否则会在模型还在干活时把连接掐掉
     */
    private static final Duration STREAM_READ_TIMEOUT = Duration.ofMinutes(10);

    private final OkHttpClient streamClient;

    private final ObjectMapper objectMapper;

    /**
     * AIAgent 服务地址
     */
    @Value("${datalight.ai.base-url:http://127.0.0.1:8010}")
    private String aiBaseUrl;

    /**
     * 内部调用密钥，与 AIAgent 的 DATALIGHT_AI_INTERNAL_TOKEN 同值
     */
    @Value("${datalight.ai.internal-token:}")
    private String internalToken;

    /**
     * 是否启用 AIAgent。关闭时直接回一条 error 事件，不去连上游
     */
    @Value("${datalight.ai.enabled:false}")
    private boolean aiEnabled;

    public MasterAiAgentStreamService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // 流式专用客户端：长读超时，且不走系统代理
        this.streamClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(STREAM_READ_TIMEOUT)
                .writeTimeout(Duration.ofSeconds(30))
                .proxy(Proxy.NO_PROXY)
                .build();
    }

    /**
     * Description: 转发一次流式对话。
     * <p>
     * SSE 的响应头一旦写出，HTTP 状态码就定死了，后续任何异常都必须转成 error 事件收尾，
     * 不能向上抛。抛出去客户端只会看到连接静默断开，表现为「回答说到一半戛然而止」，
     * 极难排查。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request  对话请求
     * @param response SSE 输出
     */
    public void streamConverse(AbstractAiAgentRequest.ConverseRequest request,
                               HttpServletResponse response) {

        this.applySseHeaders(response);

        if (!this.aiEnabled) {
            this.writeSseError(response, "AIAgent 未启用，请在 directory.yaml 中打开 datalight.ai.enabled");
            return;
        }

        final String userId = String.valueOf(StpUtil.getLoginIdAsLong());

        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("SessionId", request.getSessionId() == null ? "" : request.getSessionId());
        payload.put("UserId", userId);
        payload.put("Message", request.getMessage());
        payload.put("AutonomyLevel", request.getAutonomyLevel() == null ? "" : request.getAutonomyLevel());
        payload.put("ConfirmToken", request.getConfirmToken() == null ? "" : request.getConfirmToken());
        payload.put("History", request.getHistory() == null ? java.util.List.of() : request.getHistory());

        final Request httpRequest;
        try {
            final String body = this.objectMapper.writeValueAsString(payload);
            httpRequest = new Request.Builder()
                    .url(this.aiBaseUrl.replaceAll("/+$", "") + AI_CONVERSE_PATH)
                    .post(RequestBody.create(body, JSON_MEDIA_TYPE))
                    .header(INTERNAL_HEADER, this.internalToken == null ? "" : this.internalToken)
                    .header("Accept", "text/event-stream")
                    .build();
        } catch (Exception e) {
            log.error("智能体请求组装失败, sessionId={}", request.getSessionId(), e);
            this.writeSseError(response, "请求组装失败");
            return;
        }

        log.info(
                "智能体流式对话开始: UserId: {}, SessionId: {}, HistorySize: {}",
                userId,
                request.getSessionId(),
                request.getHistory() == null ? 0 : request.getHistory().size()
        );

        try (Response upstream = this.streamClient.newCall(httpRequest).execute()) {
            final ResponseBody upstreamBody = upstream.body();
            if (!upstream.isSuccessful() || upstreamBody == null) {
                log.warn("智能体上游响应异常: Code: {}, SessionId: {}", upstream.code(), request.getSessionId());
                this.writeSseError(
                        response,
                        String.format("AIAgent 响应异常（HTTP %d）", upstream.code())
                );
                return;
            }
            this.pipe(upstreamBody.byteStream(), response.getOutputStream());
        } catch (IOException e) {
            // 客户端主动断开或上游断流，属正常情况，记一条即可
            log.info("智能体流式对话中断: SessionId: {}, 原因: {}", request.getSessionId(), e.getMessage());
        } catch (Exception e) {
            log.error("智能体流式对话失败, sessionId={}", request.getSessionId(), e);
            this.writeSseError(response, "AIAgent 暂时不可用，请稍后重试");
        }
    }

    /**
     * Description: 逐块透传上游字节流。每块都要 flush，否则会被攒在缓冲里，
     * 流式就退化成一次性返回。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException
     *
     * @param in  上游输入流
     * @param out 下游输出流
     */
    private void pipe(InputStream in, OutputStream out) throws IOException {
        final byte[] buffer = new byte[4096];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
            out.flush();
        }
    }

    /**
     * Description: 写 SSE 响应头
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param response HTTP 响应
     */
    private void applySseHeaders(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/event-stream;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Cache-Control", "no-cache");
        // 提示反向代理不要缓冲，否则流式会被攒成一整块再吐出来
        response.setHeader("X-Accel-Buffering", "no");
        response.setHeader("Connection", "keep-alive");
    }

    /**
     * Description: 以 SSE error 事件收尾。
     * 头已写出的情况下这是唯一能让前端知道出了什么事的方式。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param response HTTP 响应
     * @param message  给人看的错误原因
     */
    private void writeSseError(HttpServletResponse response, String message) {
        final String safe = message == null
                ? "未知错误"
                : message.replace("\n", " ").replace("\r", " ");

        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("Message", safe);

        try {
            final String data = this.objectMapper.writeValueAsString(payload);
            final OutputStream out = response.getOutputStream();
            out.write(("event: error\ndata: " + data + "\n\n").getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (Exception e) {
            log.warn("写出 SSE 错误事件失败: {}", e.getMessage());
        }
    }
}
