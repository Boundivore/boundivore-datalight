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

import cn.boundivore.dl.api.third.define.IThirdGrafanaAPI;
import cn.boundivore.dl.api.third.define.IThirdPrometheusAPI;
import cn.boundivore.dl.base.constants.IUrlPrefixConstants;
import cn.boundivore.dl.cloud.feign.RequestOptionsGenerator;
import feign.Feign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: 通过 Feign 远程调用指定节点上的 IThirdGrafanaAPI 接口的一系列封装
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
public class RemoteInvokeGrafanaService {

    private final Feign.Builder feignBuilder;

    /**
     * Description: Feign 远程调用指定节点的 IThirdGrafanaAPI 的接口
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param grafanaIp   Grafana IP 地址
     * @param grafanaPort Grafana 端口号
     * @return IWorkerExecAPI 可调用 API 实例
     */
    public IThirdGrafanaAPI iThirdGrafanaAPI(String grafanaIp, String grafanaPort) {
        return feignBuilder
                .options(RequestOptionsGenerator.getRequestOptions(
                                2 * 1000L,
                                5 * 1000L
                        )
                )
                .target(
                        IThirdGrafanaAPI.class,
                        String.format(
                                "http://%s:%s%s",
                                grafanaIp,
                                grafanaPort,
                                IUrlPrefixConstants.NONE_PREFIX
                        )
                );
    }
}
