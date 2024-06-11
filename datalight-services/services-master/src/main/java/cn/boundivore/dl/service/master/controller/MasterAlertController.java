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

import cn.boundivore.dl.api.master.define.IMasterAlertAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.request.impl.common.AlertWebhookPayloadRequest;
import cn.boundivore.dl.base.request.impl.master.AbstractAlertHandlerRequest;
import cn.boundivore.dl.base.request.impl.master.AbstractAlertRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractAlertHandlerVo;
import cn.boundivore.dl.base.response.impl.master.AbstractAlertVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterAlertHandlerInterfaceService;
import cn.boundivore.dl.service.master.service.MasterAlertHandlerMailService;
import cn.boundivore.dl.service.master.service.MasterAlertHandlerService;
import cn.boundivore.dl.service.master.service.MasterAlertService;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterAlertController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/3/30
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = true)
public class MasterAlertController implements IMasterAlertAPI {

    private final MasterAlertService masterAlertService;

    private final MasterAlertHandlerInterfaceService masterAlertHandlerInterfaceService;
    private final MasterAlertHandlerMailService masterAlertHandlerMailService;
    private final MasterAlertHandlerService masterAlertHandlerService;

    @SaIgnore
    @Override
    public Result<String> testAlertInterface(String body) {
        log.info("告警外部接口调用: {}", body);
        return Result.success();
    }

    @SaIgnore
    @Override
    public Result<String> alertHook(AlertWebhookPayloadRequest request) throws Exception {
        return this.masterAlertService.alertHook(request);
    }

    @Override
    public Result<AbstractAlertVo.AlertRuleVo> newAlertRule(AbstractAlertRequest.NewAlertRuleRequest request) throws Exception {
        return this.masterAlertService.newAlertRule(request);
    }

    @Override
    public Result<String> removeAlertRule(AbstractAlertRequest.AlertIdListRequest request) throws Exception {
        return this.masterAlertService.removeAlertRule(request);
    }

    @Override
    public Result<AbstractAlertVo.AlertSimpleListVo> getAlertSimpleList(Long clusterId) throws Exception {
        return this.masterAlertService.getAlertSimpleList(clusterId);
    }

    @Override
    public Result<AbstractAlertVo.AlertRuleVo> getAlertDetailById(Long alertId) throws Exception {
        return this.masterAlertService.getAlertDetailById(alertId);
    }

    @Override
    public Result<String> switchAlertEnabled(AbstractAlertRequest.AlertSwitchEnabledListRequest request) throws Exception {
        return this.masterAlertService.switchAlertEnabled(request);
    }

    @Override
    public Result<AbstractAlertVo.AlertRuleVo> updateAlertRule(AbstractAlertRequest.UpdateAlertRuleRequest request) throws Exception {
        return this.masterAlertService.updateAlertRule(request);
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> newAlertHandlerInterface(AbstractAlertHandlerRequest.NewAlertHandlerInterfaceRequest request) throws Exception {
        return this.masterAlertHandlerInterfaceService.newAlertHandlerInterface(request);
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> getAlertHandlerInterfaceDetailsById(Long handlerId) throws Exception {
        return this.masterAlertHandlerInterfaceService.getAlertHandlerInterfaceDetailsById(handlerId);
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> updateAlertHandlerInterface(AbstractAlertHandlerRequest.UpdateAlertHandlerInterfaceRequest request) throws Exception {
        return this.masterAlertHandlerInterfaceService.updateAlertHandlerInterface(request);
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerInterfaceListVo> getAlertHandlerInterfaceList() throws Exception {
        return this.masterAlertHandlerInterfaceService.getAlertHandlerInterfaceList();
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerInterfaceListVo> getAlertHandlerInterfaceListByIdList(AbstractAlertHandlerRequest.AlertHandlerIdListRequest request) throws Exception {
        return this.masterAlertHandlerInterfaceService.getAlertHandlerInterfaceListByIdList(request);
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerMailVo> newAlertHandlerMail(AbstractAlertHandlerRequest.NewAlertHandlerMailRequest request) throws Exception {
        return this.masterAlertHandlerMailService.newAlertHandlerMail(request);
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerMailVo> getAlertHandlerMailDetailsById(Long handlerId) throws Exception {
        return this.masterAlertHandlerMailService.getAlertHandlerMailDetailsById(handlerId);
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerMailVo> updateAlertHandlerMail(AbstractAlertHandlerRequest.UpdateAlertHandlerMailRequest request) throws Exception {
        return this.masterAlertHandlerMailService.updateAlertHandlerMail(request);
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerMailListVo> getAlertHandlerMailList() throws Exception {
        return this.masterAlertHandlerMailService.getAlertHandlerMailList();
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerMailListVo> getAlertHandlerMailListIdList(AbstractAlertHandlerRequest.AlertHandlerIdListRequest request) throws Exception {
        return this.masterAlertHandlerMailService.getAlertHandlerMailListIdList(request);
    }

    @Override
    public Result<String> bindAlertAndAlertHandler(AbstractAlertHandlerRequest.AlertHandlerRelationListRequest request) throws Exception {
        return this.masterAlertHandlerService.bindAlertAndAlertHandler(request);
    }

    @Override
    public Result<String> removeBatchAlertHandler(AbstractAlertHandlerRequest.AlertHandlerIdTypeListRequest request) throws Exception {
        return this.masterAlertHandlerService.removeBatchAlertHandler(request);
    }

    @Override
    public Result<AbstractAlertHandlerVo.AlertHandlerListVo> getBindingAlertHandlerByAlertId(Long alertId) throws Exception {
        return this.masterAlertHandlerService.getBindingAlertHandlerByAlertId(alertId);
    }

    @Override
    public Result<AbstractAlertHandlerVo.HandlerAndAlertIdListVo> getBindingAlertHandlerByHandlerId(Long handlerId) throws Exception {
        return this.masterAlertHandlerService.getBindingAlertHandlerByHandlerId(handlerId);
    }

}
