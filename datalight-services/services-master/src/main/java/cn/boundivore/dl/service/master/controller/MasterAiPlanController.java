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

import cn.boundivore.dl.base.request.impl.master.AbstractAiPlanRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractAiPlanVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterAiPlanService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;

/**
 * Description: AI 会话式部署计划入口。
 * <p>
 * 计划由 AIAgent 起草、在页面上预览，用户确认后调这里落库。
 * 落库只是记下打算怎么部署，不会动机器；真正的部署仍由用户在部署页面上发起。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Master 接口：AI 部署计划", description = "MasterAiPlanController")
public class MasterAiPlanController {

    private final MasterAiPlanService masterAiPlanService;

    /**
     * Description: 提交部署计划。
     * <p>
     * 计划经过浏览器回传，服务端会完整重做一遍拓扑校验，不因 AIAgent 校验过就放行。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 部署计划
     * @return Result<AbstractAiPlanVo.PlanSubmitVo> 集群 ID 与计划 ID
     */
    @PostMapping(value = MASTER_URL_PREFIX + "/ai/plan/submit")
    @Operation(summary = "提交 AI 部署计划", description = "校验拓扑、创建集群并保存计划，不触发部署")
    @SaCheckLogin
    public Result<AbstractAiPlanVo.PlanSubmitVo> submitPlan(
            @RequestBody
            @Valid
            AbstractAiPlanRequest.PlanSubmitRequest request) {

        return this.masterAiPlanService.submitPlan(request);
    }
}
