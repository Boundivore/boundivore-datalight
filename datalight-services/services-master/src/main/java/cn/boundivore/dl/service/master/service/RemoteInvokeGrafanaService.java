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
import cn.boundivore.dl.base.constants.IUrlPrefixConstants;
import cn.boundivore.dl.cloud.feign.RequestOptionsGenerator;
import cn.hutool.core.codec.Base64;
import feign.Feign;
import feign.optionals.OptionalDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
     * add("Accept", "application/json");
     * add("Content-Type", "application/json");
     * add("Authorization", basicAuthValue(user, password));
     *
     * @param grafanaIp       Grafana IP 地址
     * @param grafanaPort     Grafana 端口号
     * @param grafanaUser     Grafana 用户名
     * @param grafanaPassword Grafana 密码
     * @return IWorkerExecAPI 可调用 API 实例
     */
    public IThirdGrafanaAPI iThirdGrafanaAPI(String grafanaIp,
                                             String grafanaPort,
                                             String grafanaUser,
                                             String grafanaPassword) {
        return feignBuilder
                .options(RequestOptionsGenerator.getRequestOptions(
                                2 * 1000L,
                                5 * 1000L
                        )
                )
                .requestInterceptor(
                        template -> template
                                .header(
                                        "Accept",
                                        "application/json"
                                )
                                .header(
                                        "Content-Type",
                                        "application/json"
                                )
                                .header(
                                        "Authorization",
                                        basicAuthValue(grafanaUser, grafanaPassword)
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

    private String basicAuthValue(String user, String password) {
        return String.format("Basic %s", Base64.encode(user + ":" + password));
    }
}
