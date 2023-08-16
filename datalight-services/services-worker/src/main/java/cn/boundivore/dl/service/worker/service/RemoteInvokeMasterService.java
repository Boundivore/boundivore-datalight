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
package cn.boundivore.dl.service.worker.service;

import cn.boundivore.dl.api.master.define.IMasterManageAPI;
import cn.boundivore.dl.base.constants.IUrlPrefixConstants;
import cn.boundivore.dl.cloud.feign.RequestOptionsGenerator;
import feign.Feign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Description: 通过 Feign 远程调用指定节点上的 Master 接口的一系列封装
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RemoteInvokeMasterService {

    @Value("${server.datalight.url.master-port}")
    private String masterPort;

    private final Feign.Builder feignBuilder;

    /**
     * Description: Feign 远程调用指定节点的 IMasterManageAPI 的接口
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param ip 对端 IP 地址
     * @return IMasterManageAPI 可调用 API 实例
     */
    public IMasterManageAPI iMasterManageAPI(String ip) {
        return feignBuilder
                .options(RequestOptionsGenerator.getRequestOptions(
                                2 * 1000L,
                                5 * 1000L
                        )
                )
                .target(
                        IMasterManageAPI.class,
                        String.format(
                                "http://%s:%s%s",
                                ip,
                                masterPort,
                                IUrlPrefixConstants.MASTER_URL_PREFIX
                        )
                );
    }
}
