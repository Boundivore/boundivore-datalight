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
package cn.boundivore.dl.cloud.swagger;


import cn.boundivore.dl.base.result.ResultEnum;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;

/**
 * Description: 接口文档基础配置，由 Master 与 Worker 各自继承后提供自身的文档元信息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description: 由 springfox 迁移到 springdoc，适配 Spring Boot 3
 * Modified by: Boundivore
 * Modification time: 2026/9/1
 * Version: V2.0
 */
@Slf4j
public abstract class AbsBaseSwaggerConfig {

    /**
     * 扫描接口定义与实现所在的根包
     */
    private static final String BASE_PACKAGE = "cn.boundivore.dl";

    /**
     * Description: 构建接口文档的全局元信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return OpenAPI 接口文档元信息
     */
    @Bean
    public OpenAPI createOpenApi() {
        SwaggerProperties swaggerProperties = this.printSwaggerInfo();

        log.info(
                "接口文档地址：http://{}:{}/swagger-ui/index.html",
                swaggerProperties.getHostname(),
                swaggerProperties.getPort()
        );

        return new OpenAPI()
                .info(
                        new Info()
                                .title("DataLight")
                                .description("DataLight 接口文档")
                                .termsOfService("http://www.boundivore.cn/")
                                .contact(
                                        new Contact()
                                                .name("boundivore")
                                                .url("http://www.boundivore.cn/")
                                                .email("boundivore@foxmail.com")
                                )
                                .version("V1.8.0")
                )
                .addServersItem(
                        new Server().url(
                                String.format(
                                        "http://%s:%s",
                                        swaggerProperties.getIp(),
                                        swaggerProperties.getPort()
                                )
                        )
                );
    }

    /**
     * Description: 按应用名称对接口分组
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return GroupedOpenApi 接口分组
     */
    @Bean
    public GroupedOpenApi createGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group(this.printSwaggerInfo().getGroupName())
                .packagesToScan(BASE_PACKAGE)
                .build();
    }

    /**
     * Description: 为每个接口补充平台统一的返回码说明。
     * springfox 时代通过 globalResponses 实现，springdoc 需要逐个接口定制。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return OperationCustomizer 接口定制器
     */
    @Bean
    public OperationCustomizer globalResultCodeCustomizer() {
        return (operation, handlerMethod) -> {
            for (ResultEnum resultEnum : ResultEnum.values()) {
                operation.getResponses()
                        .addApiResponse(
                                resultEnum.getCode(),
                                new ApiResponse().description(resultEnum.getMessageCN())
                        );
            }
            return operation;
        };
    }

    public abstract SwaggerProperties printSwaggerInfo();

}
