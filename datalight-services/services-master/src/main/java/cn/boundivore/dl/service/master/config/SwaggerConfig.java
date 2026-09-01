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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;


/**
 * Description: SwaggerConfig
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description: 由 springfox 迁移到 springdoc，适配 Spring Boot 3
 * Modified by: Boundivore
 * Modification time: 2026/9/1
 * Version: V2.0
 */
@Configuration
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
}
