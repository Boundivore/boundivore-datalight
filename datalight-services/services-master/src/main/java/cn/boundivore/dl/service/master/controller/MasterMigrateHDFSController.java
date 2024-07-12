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

import cn.boundivore.dl.api.master.define.IMasterMigrateHDFSAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.request.impl.master.JobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractJobVo;
import cn.boundivore.dl.base.response.impl.master.ConfigListByGroupVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterMigrateHDFSService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterMigrateHDFSController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/17
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = true)
public class MasterMigrateHDFSController implements IMasterMigrateHDFSAPI {

    private final MasterMigrateHDFSService masterMigrateHDFSService;

    @Override
    public Result<AbstractJobVo.JobIdVo> migrate(JobRequest request) throws Exception {
        return this.masterMigrateHDFSService.migrate(request);
    }

    @Override
    public Result<ConfigListByGroupVo> getNewHdfsSiteConfigListByGroup(Long clusterId) throws Exception {
        return this.masterMigrateHDFSService.getNewHdfsSiteConfigListByGroup(clusterId);
    }
}
