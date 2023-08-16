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
package cn.boundivore.dl.api.worker.define;

import cn.boundivore.dl.base.request.impl.worker.ConfigFileRequest;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.WORKER_URL_PREFIX;

/**
 * Description: 对 Worker 所在节点的配置文件的相关操作
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/15
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IWorkerConfigAPI", tags = {"Worker 接口：配置文件相关"})
@FeignClient(
        name = "IWorkerConfigAPI",
        contextId = "IWorkerConfigAPI",
        path = WORKER_URL_PREFIX
)
public interface IWorkerConfigAPI {

    @PostMapping(value = "/conf/modify")
    @ApiOperation(notes = "修改配置文件", value = "修改配置文件")
    Result<String> config(
            @RequestBody
            ConfigFileRequest request
    );
}
