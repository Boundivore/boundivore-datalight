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

import cn.boundivore.dl.api.master.define.IMasterInitProcedureAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ProcedureStateEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractProcedureRequest;
import cn.boundivore.dl.base.request.impl.master.RemoveProcedureRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractInitProcedureVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterInitProcedureService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterInitProcedureController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/1/4
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = true)
public class MasterInitProcedureController implements IMasterInitProcedureAPI {

    private final MasterInitProcedureService masterInitProcedureService;


    @Override
    public Result<AbstractInitProcedureVo.InitProcedureVo> persistInitStatus(AbstractProcedureRequest.PersistProcedureRequest request) throws Exception {
        return this.masterInitProcedureService.persistInitStatus(request);
    }

    @Override
    public Result<AbstractInitProcedureVo.InitProcedureVo> getInitProcedure(Long clusterId) throws Exception {
        return this.masterInitProcedureService.getInitProcedure(clusterId);
    }

    @Override
    public Result<Boolean> isExistInitProcedure(Long clusterId) throws Exception {
        return this.masterInitProcedureService.isExistInitProcedure(clusterId);
    }

    @Override
    public Result<String> removeInitProcedure(RemoveProcedureRequest request) throws Exception {
        return this.masterInitProcedureService.removeInitProcedure(request);
    }

    @Override
    public Result<Boolean> checkOperationIllegal(Long clusterId, ProcedureStateEnum procedureStateEnum) throws Exception {
        return this.masterInitProcedureService.checkOperationIllegal(clusterId, procedureStateEnum);
    }
}
