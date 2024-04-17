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
package cn.boundivore.dl.service.master.prometheus;

import cn.boundivore.dl.api.third.define.IThirdGrafanaAPI;
import cn.boundivore.dl.api.third.define.IThirdPrometheusAPI;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.RemoteInvokePrometheusService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.PostConstruct;

/**
 * Description: 测试 Prometheus API
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/16
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
@Slf4j
public class PrometheusTest {

    @Autowired
    private RemoteInvokePrometheusService remoteInvokePrometheusService;

    private final static String PROMETHEUS_HOST = "node01";
    private final static String PROMETHEUS_PORT = "9090";

    private IThirdPrometheusAPI iThirdPrometheusAPI;

    @PostConstruct
    public void init() {
        this.iThirdPrometheusAPI = this.remoteInvokePrometheusService.iThirdPrometheusAPI(
                PROMETHEUS_HOST,
                PROMETHEUS_PORT
        );
    }

    @Test
    public void reloadPrometheus() {
        Result<String> result = this.iThirdPrometheusAPI.reloadPrometheus();
        log.info(result.toString());
    }
}
