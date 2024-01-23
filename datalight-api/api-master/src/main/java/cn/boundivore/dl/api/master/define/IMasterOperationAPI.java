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

import cn.boundivore.dl.base.request.impl.master.JobDetailRequest;
import cn.boundivore.dl.base.request.impl.master.JobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractJobVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 操作服务或组件（启动、停止、重启等）
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/1/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterOperationAPI", tags = {"Master 接口：服务操作相关"})
@FeignClient(
        name = "IMasterOperationAPI",
        contextId = "IMasterOperationAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterOperationAPI {
    @PostMapping(value = "/operate/jobDetail")
    @ApiOperation(notes = "服务详细操作", value = "服务详细操作")
    Result<AbstractJobVo.JobIdVo> operate(
            @RequestBody
            @Valid
            JobDetailRequest request
    ) throws Exception;

    @PostMapping(value = "/operate/job")
    @ApiOperation(notes = "服务整体操作", value = "服务整体操作")
    Result<AbstractJobVo.JobIdVo> operate(
            @RequestBody
            @Valid
            JobRequest request
    ) throws Exception;

}
