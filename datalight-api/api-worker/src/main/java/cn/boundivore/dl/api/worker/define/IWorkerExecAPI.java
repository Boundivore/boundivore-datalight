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

import cn.boundivore.dl.base.request.impl.common.TestRequest;
import cn.boundivore.dl.base.request.impl.worker.ExecRequest;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.WORKER_URL_PREFIX;


/**
 * Description: Worker 节点执行指定脚本代码
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IWorkerExecAPI", tags = {"Worker 接口：调用相关"})
@FeignClient(
        name = "IWorkerExecAPI",
        contextId = "IWorkerExecAPI",
        path = WORKER_URL_PREFIX
)
public interface IWorkerExecAPI {

    @PostMapping(value = "/exec")
    @ApiOperation(notes = "执行相关指令", value = "执行相关指令")
    Result<String> exec(
            @RequestBody
            ExecRequest request
    );

    @PostMapping(value = "/test")
    @ApiOperation(notes = "测试", value = "测试")
    Result<String> test(
            @RequestBody
            TestRequest request
    );

}
