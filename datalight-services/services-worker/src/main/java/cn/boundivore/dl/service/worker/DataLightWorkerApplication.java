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
package cn.boundivore.dl.service.worker;


import io.prometheus.metrics.model.registry.PrometheusRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Description: DataLightWorkerApplication
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
//@EnableDiscoveryClient
//@EnableFeignClients(basePackages = {"cn.boundivore.dl"})
@SpringBootApplication(scanBasePackages = {"cn.boundivore.dl"}, exclude = {DataSourceAutoConfiguration.class})
@Slf4j
public class DataLightWorkerApplication extends SpringBootServletInitializer {
    public static String MASTER_IP_FROM_SHELL = "";
    public static void main(String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        String masterIp = System.getProperty("masterIp");
        if(masterIp != null && !masterIp.isEmpty()) {
            MASTER_IP_FROM_SHELL = masterIp;
            log.info("当前 MasterIp: {}", MASTER_IP_FROM_SHELL);
        }

        SpringApplication.run(DataLightWorkerApplication.class, args);

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
