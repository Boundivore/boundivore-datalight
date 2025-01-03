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
package cn.boundivore.dl.service.master.config;

import cn.boundivore.dl.cloud.swagger.AbsBaseSwaggerConfig;
import cn.boundivore.dl.cloud.swagger.SwaggerProperties;
import cn.hutool.core.net.NetUtil;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: SwaggerConfig
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Configuration
@EnableOpenApi
@EnableKnife4j
@EnableSwagger2
@Slf4j
public class SwaggerConfig extends AbsBaseSwaggerConfig {

    @Value("${server.port}")
    private String port;

    @Value("${spring.application.name}")
    private String appName;

    @Override
    @SneakyThrows
    public SwaggerProperties printSwaggerInfo() {
        InetAddress inetAddress = InetAddress.getLocalHost();
        String hostName = inetAddress.getHostName();
        String ip = NetUtil.getIpByHost("localhost");

        return SwaggerProperties.builder()
                .hostname(hostName)
                .ip(ip)
                .port(port)
                .groupName(appName)
                .build();
    }

    @Bean
    public BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return handlerProviderBeanPostProcessor();
    }
}
