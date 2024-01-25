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

import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractClusterRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractClusterVo;
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
 * Description: 集群管理的相关接口定义
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterClusterAPI", tags = {"Master 接口：集群相关"})
@FeignClient(
        name = "IMasterClusterAPI",
        contextId = "IMasterClusterAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterClusterAPI {

    @PostMapping(value = "/cluster/new")
    @ApiOperation(notes = "新增集群", value = "新增集群")
    Result<AbstractClusterVo.ClusterVo> clusterNew(
            @RequestBody
            @Valid
            AbstractClusterRequest.NewClusterRequest request
    ) throws Exception;

    @GetMapping(value = "/cluster/getById")
    @ApiOperation(notes = "查询集群信息", value = "查询集群信息")
    Result<AbstractClusterVo.ClusterVo> getClusterById(
            @ApiParam(name = "ClusterId", value = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @GetMapping(value = "/cluster/getClusterListByClusterType")
    @ApiOperation(notes = "查询指定类型的集群信息列表", value = "查询指定类型的集群信息列表")
    Result<AbstractClusterVo.ClusterListVo> getClusterListByClusterType(
            @ApiParam(name = "ClusterType", value = "集群 ID")
            @RequestParam(value = "ClusterType", required = true)
            ClusterTypeEnum clusterTypeEnum
    ) throws Exception;

    @GetMapping(value = "/cluster/getClusterList")
    @ApiOperation(notes = "查询所有集群信息列表", value = "查询所有集群信息列表")
    Result<AbstractClusterVo.ClusterListVo> getClusterList() throws Exception;

    @GetMapping(value = "/cluster/getComputeClusterListByRelativeClusterId")
    @ApiOperation(notes = "查询依赖了指定集群的计算集群信息列表", value = "查询依赖了指定集群的计算集群信息列表")
    Result<AbstractClusterVo.ClusterListVo> getComputeClusterListByRelativeClusterId(
            @ApiParam(name = "ClusterId", value = "被依赖集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @GetMapping(value = "/cluster/getClusterRelative")
    @ApiOperation(notes = "查询当前集群依赖的集群信息", value = "查询当前集群依赖的集群信息")
    Result<AbstractClusterVo.ClusterVo> getClusterRelative(
            @ApiParam(name = "ClusterId", value = "当前集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId
    ) throws Exception;

    @PostMapping(value = "/cluster/updateCurrentView")
    @ApiOperation(notes = "更新集群视图标记", value = "更新集群视图标记")
    Result<AbstractClusterVo.ClusterVo> updateClusterCurrentView(
            @RequestBody
            @Valid
            AbstractClusterRequest.ClusterIdRequest request
    ) throws Exception;

    @PostMapping(value = "/cluster/remove")
    @ApiOperation(notes = "移除集群", value = "移除集群")
    Result<AbstractClusterVo.ClusterVo> removeCluster(
            @RequestBody
            @Valid
            AbstractClusterRequest.ClusterIdRequest request
    ) throws Exception;

}
