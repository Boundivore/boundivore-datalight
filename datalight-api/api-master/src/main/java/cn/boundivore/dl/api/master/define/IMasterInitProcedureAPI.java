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

import cn.boundivore.dl.base.enumeration.impl.ProcedureStateEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractProcedureRequest;
import cn.boundivore.dl.base.request.impl.master.RemoveProcedureRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractInitProcedureVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 初始化步骤记录、查询等功能 API
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：初始化步骤记录相关", description = "IMasterInitProcedureAPI")
@FeignClient(
        name = "IMasterInitProcedureAPI",
        contextId = "IMasterInitProcedureAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterInitProcedureAPI {

    @PostMapping(value = "/init/procedure/persist")
    @Operation(summary = "记录初始化步骤", description = "记录初始化步骤")
    Result<AbstractInitProcedureVo.InitProcedureVo> persistInitStatus(
            @RequestBody
            @Valid
            AbstractProcedureRequest.PersistProcedureRequest request
    ) throws Exception;

    @GetMapping(value = "/init/procedure/get")
    @Operation(summary = "获取当前初始化步骤信息", description = "获取当前初始化步骤信息")
    Result<AbstractInitProcedureVo.InitProcedureVo> getInitProcedure(
            @Parameter(name = "ClusterId", description = "ClusterId")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;


    @GetMapping(value = "/init/procedure/exists")
    @Operation(summary = "查询是否存在记录的步骤信息", description = "查询是否存在记录的步骤信息")
    Result<Boolean> isExistInitProcedure(
            @Parameter(name = "ClusterId", description = "ClusterId")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;


    @PostMapping(value = "/init/procedure/remove")
    @Operation(summary = "清除指定集群的初始化步骤信息记录", description = "清除指定集群的初始化步骤信息记录")
    Result<String> removeInitProcedure(
            @RequestBody
            @Valid
            RemoveProcedureRequest request
    ) throws Exception;

    @GetMapping(value = "/init/procedure/check")
    @Operation(summary = "检查当前操作是否合法", description = "检查当前操作是否合法")
    Result<Boolean> checkOperationIllegal(
            @Parameter(name = "ClusterId", description = "ClusterId")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId,

            @Parameter(name = "ProcedureStateEnum", description = "ProcedureStateEnum")
            @RequestParam(value = "ProcedureStateEnum", required = true)
            ProcedureStateEnum procedureStateEnum
    ) throws Exception;

}
