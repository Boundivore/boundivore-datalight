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
package cn.boundivore.dl.service.master.controller;

import cn.boundivore.dl.base.request.impl.master.AbstractAiAgentRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractAgentVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.logs.LogsIgnore;
import cn.boundivore.dl.service.master.service.MasterAiAgentRegistry;
import cn.boundivore.dl.service.master.service.MasterAiAgentStreamService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;

/**
 * Description: AI 智能体对话入口与实例注册。
 * <p>
 * 前端只跟 Master 打交道，由 Master 转发到 AIAgent。
 * 这样鉴权、审计、限流收在一处，Python 侧不需要理解平台的用户体系，
 * 也不必对浏览器直接暴露端口。
 * <p>
 * 流式端点返回 text/event-stream，不能走 Feign 接口定义，
 * 因此直接声明在本控制器上，不放进 datalight-api。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/1
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Master 接口：AI 智能体", description = "MasterAiAgentController")
public class MasterAiAgentController {

    private final MasterAiAgentStreamService masterAiAgentStreamService;

    private final MasterAiAgentRegistry masterAiAgentRegistry;

    /**
     * 内部调用密钥，与 AIAgent 的 DATALIGHT_AI_INTERNAL_TOKEN 同值
     */
    @Value("${datalight.ai.internal-token:}")
    private String internalToken;

    /**
     * Description: 智能体流式对话。
     * <p>
     * 事件序列：meta / turn_start / delta / tool_call / tool_result / plan / done / error，
     * data 为 JSON，字段用 PascalCase，与平台其他接口一致。
     * <p>
     * 返回体是持续的字节流，日志切面不要打印，否则日志文件会被撑爆。
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
    @PostMapping(
            value = MASTER_URL_PREFIX + "/ai/agent/converse",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    @Operation(
            summary = "AI 智能体流式对话",
            description = "多轮工具循环，模型可连续查集群状态、读日志、看配置；事件流式透传"
    )
    @SaCheckLogin
    @LogsIgnore
    public void converse(
            @RequestBody
            @Valid
            AbstractAiAgentRequest.ConverseRequest request,

            HttpServletResponse response
    ) {
        this.masterAiAgentStreamService.streamConverse(request, response);
    }

    /**
     * Description: AIAgent 实例注册与心跳。
     * <p>
     * 平台不引入 Nacos，改由 AIAgent 主动上报自己的地址。方向是反的，
     * 因为 Master 是最先起来、地址最固定的角色，而 AIAgent 可能被部署到任意节点；
     * 更关键的是问答式部署发生在集群还不存在时，那会儿查库定位根本无从谈起。
     * <p>
     * 本接口由 AIAgent 进程调用，不是浏览器，因此不走 satoken 登录态，
     * 改用内部密钥校验。密钥未配置时拒绝注册，避免开源默认部署裸奔。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 注册请求
     * @param token   内部调用密钥
     * @return 当前存活实例数
     */
    /**
     * Description: 断线后续接事件流。
     * <p>
     * 浏览器刷新、切页面、网络抖动之后调这里。生成任务在 AIAgent 侧独立于连接运行，
     * 断开不会中止它，所以能拿到断开期间产生的全部事件，包括最终结论。
     * <p>
     * cursor 传上次收到的最后一个事件序号加一。事件序号在 SSE 的 id 字段里。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param sessionId 会话 ID
     * @param cursor    起始事件序号
     * @param response  SSE 输出
     */
    @GetMapping(
            value = MASTER_URL_PREFIX + "/ai/agent/attach",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    @Operation(summary = "断线续接智能体事件流", description = "从指定事件序号继续接收，生成任务不受连接断开影响")
    @SaCheckLogin
    @LogsIgnore
    public void attach(
            @RequestParam(value = "SessionId")
            String sessionId,

            @RequestParam(value = "Cursor", required = false, defaultValue = "0")
            Long cursor,

            HttpServletResponse response
    ) {
        this.masterAiAgentStreamService.attachStream(
                sessionId,
                cursor == null ? 0L : cursor,
                response
        );
    }

    /**
     * Description: 取消正在进行的生成。
     * <p>
     * 引擎在轮之间检查取消标志，已经在跑的那一轮工具调用会执行完再停，
     * 不会把远端调用中途掐断。已产出的内容照常写入历史。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param sessionId 会话 ID
     * @return Result<Boolean> 是否确实取消了某个正在运行的任务
     */
    @PostMapping(value = MASTER_URL_PREFIX + "/ai/agent/cancel")
    @Operation(summary = "取消智能体生成", description = "在轮之间中止，已产出内容照常保留")
    @SaCheckLogin
    public Result<Boolean> cancel(
            @RequestParam(value = "SessionId")
            String sessionId
    ) {
        return Result.success(this.masterAiAgentStreamService.cancel(sessionId));
    }

    /**
     * Description: 读取会话历史。
     * <p>
     * 前端重新打开页面时调这里回放对话。返回的是 AIAgent 存的富格式，
     * 含工具调用轨迹，前端据此还原每条回答当时查了哪些工具。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param sessionId 会话 ID
     * @return Result<List<Map<String, Object>>> 历史消息
     */
    @GetMapping(value = MASTER_URL_PREFIX + "/ai/agent/history")
    @Operation(summary = "读取智能体会话历史", description = "供前端重新打开页面时回放，含工具调用轨迹")
    @SaCheckLogin
    @LogsIgnore
    public Result<List<Map<String, Object>>> history(
            @RequestParam(value = "SessionId")
            String sessionId
    ) {
        return Result.success(this.masterAiAgentStreamService.sessionHistory(sessionId));
    }

    /**
     * Description: 清空会话历史。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param sessionId 会话 ID
     * @return Result<String> 操作结果
     */
    @PostMapping(value = MASTER_URL_PREFIX + "/ai/agent/history/clear")
    @Operation(summary = "清空智能体会话历史", description = "用户点清空时调用")
    @SaCheckLogin
    public Result<String> clearHistory(
            @RequestParam(value = "SessionId")
            String sessionId
    ) {
        this.masterAiAgentStreamService.clearSessionHistory(sessionId);
        return Result.success("SUCCESS");
    }

    @PostMapping(value = MASTER_URL_PREFIX + "/ai/agent/register")
    @Operation(summary = "AIAgent 注册与心跳", description = "由 AIAgent 进程调用，非浏览器接口")
    @SaIgnore
    @LogsIgnore
    public Result<String> register(
            @RequestBody
            @Valid
            AbstractAiAgentRequest.RegisterRequest request,

            @RequestHeader(value = "X-DataLight-Internal", required = false)
            String token
    ) {
        this.checkInternalToken(token);

        final int alive = this.masterAiAgentRegistry.register(
                request.getBaseUrl(),
                request.getHostname(),
                request.getVersion()
        );

        return Result.success(String.valueOf(alive));
    }

    /**
     * Description: AIAgent 主动下线。
     * <p>
     * 正常停止时调用，让路由立刻切走，不用等心跳超时。
     * 进程被强杀时收不到这个调用，届时由 TTL 兜底。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 下线请求，只用其中的地址
     * @param token   内部调用密钥
     * @return 操作结果
     */
    @PostMapping(value = MASTER_URL_PREFIX + "/ai/agent/unregister")
    @Operation(summary = "AIAgent 主动下线", description = "由 AIAgent 进程调用，非浏览器接口")
    @SaIgnore
    @LogsIgnore
    public Result<String> unregister(
            @RequestBody
            @Valid
            AbstractAiAgentRequest.RegisterRequest request,

            @RequestHeader(value = "X-DataLight-Internal", required = false)
            String token
    ) {
        this.checkInternalToken(token);
        this.masterAiAgentRegistry.unregister(request.getBaseUrl());
        return Result.success("OK");
    }

    /**
     * Description: 查看当前存活的 AIAgent 实例。
     * <p>
     * 前端用它决定是否展示智能体入口：没有存活实例时按钮置灰并说明原因，
     * 好过让用户点开抽屉再看到一条报错。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 存活实例列表
     */
    @GetMapping(value = MASTER_URL_PREFIX + "/ai/agent/instances")
    @Operation(summary = "查看存活的 AIAgent 实例", description = "用于前端判断入口是否可用")
    @SaCheckLogin
    public Result<AbstractAgentVo.AgentInstanceListVo> instances() {
        final List<AbstractAgentVo.AgentInstanceVo> list = new ArrayList<>();

        for (MasterAiAgentRegistry.AgentInstance instance : this.masterAiAgentRegistry.aliveInstances()) {
            list.add(
                    new AbstractAgentVo.AgentInstanceVo()
                            .setBaseUrl(instance.getBaseUrl())
                            .setHostname(instance.getHostname())
                            .setVersion(instance.getVersion())
                            .setLastHeartbeatTime(instance.getLastHeartbeatTime())
            );
        }

        return Result.success(
                new AbstractAgentVo.AgentInstanceListVo()
                        .setInstanceList(list)
        );
    }

    /**
     * 校验内部调用密钥。密钥没配置时一律拒绝，
     * 不能因为「没配就放行」而让开源默认部署裸奔
     */
    private void checkInternalToken(String token) {
        if (this.internalToken == null || this.internalToken.isEmpty()) {
            throw new IllegalStateException(
                    "未配置 datalight.ai.internal-token，拒绝 AIAgent 注册。" +
                            "请在 directory.yaml 中设置该密钥，并与 AIAgent 侧保持一致"
            );
        }
        if (!this.internalToken.equals(token)) {
            throw new IllegalStateException("内部调用密钥不匹配，拒绝 AIAgent 注册");
        }
    }
}
