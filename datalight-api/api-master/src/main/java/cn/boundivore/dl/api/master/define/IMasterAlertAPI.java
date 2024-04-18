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
package cn.boundivore.dl.api.master.define;

import cn.boundivore.dl.base.request.impl.common.AlertWebhookPayloadRequest;
import cn.boundivore.dl.base.request.impl.master.AbstractAlertRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractAlertVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 告警接收器接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterAlertAPI", tags = {"Master 接口：告警相关"})
@FeignClient(
        name = "IMasterAlertAPI",
        contextId = "IMasterAlertAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterAlertAPI {

    @PostMapping(value = "/alert/alertHook")
    @ApiOperation(notes = "接收原始告警信息", value = "接收原始告警信息")
    Result<String> alertHook(
            @RequestBody
            @Valid
            AlertWebhookPayloadRequest request
    ) throws Exception;

    @PostMapping(value = "/alert/newAlertRule")
    @ApiOperation(notes = "新建告警规则", value = "新建告警规则")
    Result<AbstractAlertVo.AlertRuleVo> newAlertRule(
            @RequestBody
            @Valid
            AbstractAlertRequest.NewAlertRuleRequest request
    ) throws Exception;
}
