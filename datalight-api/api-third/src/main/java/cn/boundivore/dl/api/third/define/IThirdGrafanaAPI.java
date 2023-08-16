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
package cn.boundivore.dl.api.third.define;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.NONE_PREFIX;


/**
 * Description: Grafana 节点执行指定脚本代码
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IThirdGrafanaAPI", tags = {"Grafana 接口：直接调用 Grafana 接口"})
@FeignClient(
        name = "IThirdGrafanaAPI",
        contextId = "IThirdGrafanaAPI",
        path = NONE_PREFIX
)
public interface IThirdGrafanaAPI {
    @GetMapping(value = "/api/admin/users/{id}")
    @ApiOperation(notes = "根据用户 ID 获取用户信息", value = "根据用户 ID 获取用户信息")
    String getUserById(
            @PathVariable("id")
            String id
    );

    @GetMapping(value = "/api/admin/stats")
    @ApiOperation(notes = "获取 Grafana 状态信息", value = "获取 Grafana 状态信息")
    String getStats();
}
