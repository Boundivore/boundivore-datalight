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

import cn.boundivore.dl.base.response.impl.common.AbstractDataLightDirVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.WORKER_URL_PREFIX;


/**
 * Description: 按照指定规则推送文件相关 API
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IWorkerDispatchAPI", tags = {"Worker 接口：重分发文件相关"})
@FeignClient(
        name = "IWorkerDispatchAPI",
        contextId = "IWorkerDispatchAPI",
        path = WORKER_URL_PREFIX
)
public interface IWorkerDispatchAPI {

    @GetMapping(value = "/dispatch/getDataLightDirTree")
    @ApiOperation(notes = "获取当前节点 DataLight 部署目录树状集合", value = "获取当前节点 DataLight 部署目录树状集合")
    Result<AbstractDataLightDirVo.DataLightDirCollectionVo> getDataLightDirTree(
            @ApiParam(name = "DataLightDirectory", value = "DataLight 根目录")
            @RequestParam(value = "DataLightDirectory", required = true)
            String dataLightDirectory
    ) throws Exception;


}
