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

import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.NONE_PREFIX;


/**
 * Description: 告警外部接口调用客户端
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IThirdHandlerInterfaceAPI", tags = {"告警通知接口：调用告警外部通知接口"})
@FeignClient(
        name = "IThirdHandlerInterfaceAPI",
        contextId = "IThirdHandlerInterfaceAPI",
        path = NONE_PREFIX
)
public interface IThirdHandlerInterfaceAPI {

    @PostMapping(value = "")
    @ApiOperation(notes = "向任意 URI 发送 POST 请求", value = "向任意 URI 发送 POST 请求")
    ResponseEntity<String> sendPostRequest(
            @RequestBody
            Object requestBody
    );
}
