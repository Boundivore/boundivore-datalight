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
package cn.boundivore.dl.service.worker.controller;

import cn.boundivore.dl.api.worker.define.IWorkerDispatchAPI;
import cn.boundivore.dl.base.response.impl.common.AbstractDataLightDirVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.worker.service.WorkerDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: WorkerDispatchController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
public class WorkerDispatchController implements IWorkerDispatchAPI {

    private final WorkerDispatchService workerDispatchService;


    @Override
    public Result<AbstractDataLightDirVo.DataLightDirCollectionVo> getDataLightDirTree(String dataLightDirectory) throws Exception {
        return this.workerDispatchService.getDataLightDirTree(dataLightDirectory);
    }
}
