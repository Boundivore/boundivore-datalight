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
package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.api.third.define.IThirdHandlerInterfaceAPI;
import cn.boundivore.dl.cloud.feign.RequestOptionsGenerator;
import feign.Feign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: 通过 Feign 远程调用指定节点上的 IThirdHandlerInterfaceAPI 接口的一系列封装
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RemoteInvokeHandlerInterfaceService {

    private final Feign.Builder feignBuilder;

    /**
     * Description: Feign 远程调用指定节点的 IThirdHandlerInterfaceAPI 的接口
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param uri 外部接口绝对路径
     * @return IWorkerExecAPI 可调用 API 实例
     */
    public IThirdHandlerInterfaceAPI iThirdHandlerInterfaceAPI(String uri) {
        return feignBuilder
                .options(RequestOptionsGenerator.getRequestOptions(
                                10 * 1000L,
                                10 * 1000L
                        )
                )
                .target(
                        IThirdHandlerInterfaceAPI.class,
                        uri
                );
    }
}
