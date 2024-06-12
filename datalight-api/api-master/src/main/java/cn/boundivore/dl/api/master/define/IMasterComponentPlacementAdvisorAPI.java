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

import cn.boundivore.dl.base.response.impl.master.AbstractComponentPlacementVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 自动推荐组件分布情况接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterComponentPlacementAdvisorAPI", tags = {"Master 接口：自动推荐组件分布情况接口"})
@FeignClient(
        name = "IMasterComponentPlacementAdvisorAPI",
        contextId = "IMasterComponentPlacementAdvisorAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterComponentPlacementAdvisorAPI {

    @GetMapping(value = "/advisor/getComponentPlacementRecommendation")
    @ApiOperation(notes = "获取组件分布推荐", value = "获取组件分布推荐")
    Result<AbstractComponentPlacementVo.PlacementAdvisorVo> getComponentPlacementRecommendation(
            @ApiParam(name = "ClusterId", value = "集群 ID")
            @RequestParam(value = "ClusterId", required = true)
            Long clusterId,

            @ApiParam(name = "ServiceNames", value = "多个服务名称(英文逗号拼接)")
            @RequestParam(value = "ServiceNames", required = true)
            String serviceNames
    ) throws Exception;
}
