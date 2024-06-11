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

import cn.boundivore.dl.api.master.define.IMasterComponentAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractServiceComponentRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractServiceComponentVo;
import cn.boundivore.dl.base.response.impl.master.ServiceWebUIVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterComponentService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterComponentController
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
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = true)
public class MasterComponentController implements IMasterComponentAPI {

    private final MasterComponentService masterComponentService;

    @Override
    public Result<AbstractServiceComponentVo.ComponentListVo> getComponentListByComponentName(Long clusterId, String serviceName, String componentName) throws Exception {
        return this.masterComponentService.getComponentListByComponentName(clusterId, serviceName, componentName);
    }

    @Override
    public Result<AbstractServiceComponentVo.ComponentVo> getComponentList(Long clusterId, String serviceName) throws Exception {
        return this.masterComponentService.getComponentList(clusterId, serviceName);
    }

    @Override
    public Result<AbstractServiceComponentVo.ComponentVo> getComponentList(Long clusterId) throws Exception {
        return this.masterComponentService.getComponentList(clusterId);
    }

    @Override
    public Result<String> saveComponentSelected(AbstractServiceComponentRequest.ComponentSelectRequest request) throws Exception {
        return this.masterComponentService.saveComponentSelected(request);
    }

    @Override
    public Result<AbstractServiceComponentVo.RemoveComponentBatchVo> removeComponentBatchByIds(AbstractServiceComponentRequest.ComponentIdListRequest request) throws Exception {
        return this.masterComponentService.removeComponentBatchByIds(request);
    }

    @Override
    public Result<String> updateComponentRestartMark(AbstractServiceComponentRequest.UpdateNeedRestartRequest request) throws Exception {
        return this.masterComponentService.updateComponentRestartMark(request);
    }

    @Override
    public Result<ServiceWebUIVo> getComponentWebUIList(Long clusterId, String serviceName) throws Exception {
        return this.masterComponentService.getComponentWebUIList(clusterId, serviceName);
    }
}
