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

import cn.boundivore.dl.base.request.impl.master.AbstractServiceComponentRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractServiceComponentVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 关于组件管理的接口定义
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterComponentAPI", tags = {"Master 接口：组件相关"})
@FeignClient(
        name = "IMasterComponentAPI",
        contextId = "IMasterComponentAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterComponentAPI {

    @GetMapping(value = "/component/listByServiceName")
    @ApiOperation(notes = "获取指定服务下的组件分布信息", value = "获取指定服务下的组件分布信息")
    Result<AbstractServiceComponentVo.ComponentVo> getComponentList(
            @ApiParam(name = "ClusterId", value = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId,

            @ApiParam(name = "ServiceName", value = "服务名称")
            @RequestParam(value = "ServiceName", required = true)
            String serviceName
    ) throws Exception;

    @GetMapping(value = "/component/list")
    @ApiOperation(notes = "获取服务下的组件信息列表并附带其状态信息", value = "获取服务下的组件信息列表并附带其状态信息")
    Result<AbstractServiceComponentVo.ComponentVo> getComponentList(
            @ApiParam(name = "ClusterId", value = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;


    @PostMapping(value = "/component/select")
    @ApiOperation(notes = "选择准备部署的组件", value = "选择准备部署的组件")
    Result<String> saveComponentSelected(
            @RequestBody
            @Valid
            AbstractServiceComponentRequest.ComponentSelectRequest request
    ) throws Exception;

    @PostMapping(value = "/component/removeBatchByIds")
    @ApiOperation(notes = "批量移除组件", value = "批量移除组件")
    Result<AbstractServiceComponentVo.RemoveComponentBatchVo> removeComponentBatchByIds(
            @RequestBody
            @Valid
            AbstractServiceComponentRequest.ComponentIdListRequest request
    ) throws Exception;

    @PostMapping(value = "/component/updateComponentRestartMark")
    @ApiOperation(notes = "更新组件重启标记", value = "更新组件重启标记")
    Result<String> updateComponentRestartMark(
            @RequestBody
            @Valid
            AbstractServiceComponentRequest.UpdateNeedRestartRequest request
    ) throws Exception;

}
