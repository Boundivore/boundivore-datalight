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
import cn.boundivore.dl.service.master.logs.LogsIgnore;
import cn.boundivore.dl.service.master.service.MasterAiAgentStreamService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;

/**
 * Description: AI 智能体对话入口。
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

    /**
     * Description: 智能体流式对话。
     * <p>
     * 事件序列：meta / turn_start / delta / tool_call / tool_result / done / error，
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
}
