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
import cn.boundivore.dl.base.request.impl.master.AbstractAlertHandlerRequest;
import cn.boundivore.dl.base.request.impl.master.AbstractAlertRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractAlertHandlerVo;
import cn.boundivore.dl.base.response.impl.master.AbstractAlertVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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


    @PostMapping(value = "/testAlertInterface")
    @ApiOperation(notes = "测试告警调用外部通知接口", value = "测试告警调用外部通知接口")
    Object testAlertInterface(
            @RequestBody
            String body
    );

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

    @PostMapping(value = "/alert/removeAlertRule")
    @ApiOperation(notes = "移除告警规则", value = "移除告警规则")
    Result<String> removeAlertRule(
            @RequestBody
            @Valid
            AbstractAlertRequest.AlertIdListRequest request
    ) throws Exception;


    @GetMapping(value = "/alert/getAlertSimpleList")
    @ApiOperation(notes = "获取告警信息概览列表", value = "获取告警信息概览列表")
    Result<AbstractAlertVo.AlertSimpleListVo> getAlertSimpleList(
            @ApiParam(name = "ClusterId", value = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @GetMapping(value = "/alert/getAlertDetailById")
    @ApiOperation(notes = "根据 ID 获取告警详细信息", value = "根据 ID 获取告警详细信息")
    Result<AbstractAlertVo.AlertRuleVo> getAlertDetailById(
            @ApiParam(name = "AlertId", value = "告警信息 ID")
            @RequestParam(value = "AlertId", required = true)
            Long alertId
    ) throws Exception;


    @PostMapping(value = "/alert/switchAlertEnabled")
    @ApiOperation(notes = "启用、停用告警", value = "启用、停用告警")
    Result<String> switchAlertEnabled(
            @RequestBody
            @Valid
            AbstractAlertRequest.AlertSwitchEnabledListRequest request
    ) throws Exception;

    @PostMapping(value = "/alert/updateAlertRule")
    @ApiOperation(notes = "更新告警配置信息", value = "更新告警配置信息")
    Result<AbstractAlertVo.AlertRuleVo> updateAlertRule(
            @RequestBody
            @Valid
            AbstractAlertRequest.UpdateAlertRuleRequest request
    ) throws Exception;

    @PostMapping(value = "/alert/newAlertHandlerInterface")
    @ApiOperation(notes = "新增接口告警处理方式", value = "新增接口告警处理方式")
    Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> newAlertHandlerInterface(
            @RequestBody
            @Valid
            AbstractAlertHandlerRequest.NewAlertHandlerInterfaceRequest request
    ) throws Exception;

    @GetMapping(value = "/alert/getAlertHandlerInterfaceDetailsById")
    @ApiOperation(notes = "根据 ID 获取告警接口处理方式详情", value = "根据 ID 获取告警接口处理方式详情")
    Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> getAlertHandlerInterfaceDetailsById(
            @ApiParam(name = "HandlerId", value = "告警处理方式 ID")
            @RequestParam(value = "HandlerId", required = true)
            Long handlerId
    ) throws Exception;

    @PostMapping(value = "/alert/updateAlertHandlerInterface")
    @ApiOperation(notes = "更新告警接口处理方式", value = "更新告警接口处理方式")
    Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> updateAlertHandlerInterface(
            @RequestBody
            @Valid
            AbstractAlertHandlerRequest.UpdateAlertHandlerInterfaceRequest request
    ) throws Exception;

    @GetMapping(value = "/alert/getAlertHandlerInterfaceList")
    @ApiOperation(notes = "获取告警接口处理方式列表", value = "获取告警接口处理方式列表")
    Result<AbstractAlertHandlerVo.AlertHandlerInterfaceListVo> getAlertHandlerInterfaceList(
    ) throws Exception;


    @PostMapping(value = "/alert/newAlertHandlerMail")
    @ApiOperation(notes = "新增邮件告警处理方式", value = "新增邮件告警处理方式")
    Result<AbstractAlertHandlerVo.AlertHandlerMailVo> newAlertHandlerMail(
            @RequestBody
            @Valid
            AbstractAlertHandlerRequest.NewAlertHandlerMailRequest request
    ) throws Exception;

    @GetMapping(value = "/alert/getAlertHandlerMailDetailsById")
    @ApiOperation(notes = "根据 ID 获取告警邮件处理方式详情", value = "根据 ID 获取告警邮件处理方式详情")
    Result<AbstractAlertHandlerVo.AlertHandlerMailVo> getAlertHandlerMailDetailsById(
            @ApiParam(name = "HandlerId", value = "告警处理方式 ID")
            @RequestParam(value = "HandlerId", required = true)
            Long handlerId
    ) throws Exception;

    @PostMapping(value = "/alert/updateAlertHandlerMail")
    @ApiOperation(notes = "更新告警邮件处理方式", value = "更新告警邮件处理方式")
    Result<AbstractAlertHandlerVo.AlertHandlerMailVo> updateAlertHandlerMail(
            @RequestBody
            @Valid
            AbstractAlertHandlerRequest.UpdateAlertHandlerMailRequest request
    ) throws Exception;

    @GetMapping(value = "/alert/getAlertHandlerMailList")
    @ApiOperation(notes = "获取告警邮箱处理方式列表", value = "获取告警邮箱处理方式列表")
    Result<AbstractAlertHandlerVo.AlertHandlerMailListVo> getAlertHandlerMailList(
    ) throws Exception;


    @PostMapping(value = "/alert/bindAlertAndAlertHandler")
    @ApiOperation(notes = "绑定或解绑告警与告警处理方式", value = "绑定或解绑告警与告警处理方式")
    Result<String> bindAlertAndAlertHandler(
            @RequestBody
            @Valid
            AbstractAlertHandlerRequest.AlertHandlerRelationListRequest request
    )throws Exception;
}
