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
package cn.boundivore.dl.service.master;

import io.prometheus.metrics.model.registry.PrometheusRegistry;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Description: DataLightMasterApplication
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/3/29
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@EnableTransactionManagement
@EnableScheduling
@EnableDiscoveryClient
@MapperScan("cn.boundivore.dl.orm")
@EnableFeignClients(basePackages = {"cn.boundivore.dl"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = {"cn.boundivore.dl"}, exclude = {DataSourceAutoConfiguration.class})
public class DataLightMasterApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(DataLightMasterApplication.class, args);
    }

    /**
     * Description: 暴露 Prometheus 默认注册表。
     * Micrometer 1.13 起改用 Prometheus Java Client 1.x，CollectorRegistry 已由 PrometheusRegistry 取代。
     * 这里显式返回 defaultRegistry，保证直接注册到默认注册表的指标同样能被采集到。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/3/29
     * Modification description: CollectorRegistry 迁移为 PrometheusRegistry
     * Modified by: Boundivore
     * Modification time: 2026/9/1
     * Throws:
     *
     * @return PrometheusRegistry Prometheus 默认注册表
     */
    @Bean
    public PrometheusRegistry prometheusRegistry() {
        return PrometheusRegistry.defaultRegistry;
    }
}
