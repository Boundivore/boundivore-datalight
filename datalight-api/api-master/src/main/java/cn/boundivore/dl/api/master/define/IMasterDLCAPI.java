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

import cn.boundivore.dl.base.response.impl.master.AbstractDlcVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 关于 DCL 包关接口定义
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterDLCAPI", tags = {"Master 接口：DCL 包相关"})
@FeignClient(
        name = "IMasterDLCAPI",
        contextId = "IMasterDLCAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterDLCAPI {

    @GetMapping(value = "/dlc/service/list")
    @ApiOperation(notes = "获取服务组件包中的服务列表", value = "获取服务组件包中的服务列表")
    Result<AbstractDlcVo.DlcServiceVo> dlcServiceList() throws Exception;

    @GetMapping(value = "/dlc/component/list")
    @ApiOperation(notes = "获取服务组件包中的组件列表", value = "获取服务组件包中的组件列表")
    Result<AbstractDlcVo.DlcComponentVo> dlcComponentList() throws Exception;

}
