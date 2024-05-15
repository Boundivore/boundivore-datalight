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
package cn.boundivore.dl.boot.config;

import cn.boundivore.dl.exception.BException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;
import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.WORKER_URL_PREFIX;

/**
 * Description: WebMvcConfig
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${spring.application.name}")
    private String appName;


    /**
     * Description: 添加视图控制器，将 "/notFound" 请求路径转发到 "index.html" 页面。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param registry ViewControllerRegistry 对象，用于注册视图控制器。
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/notFound").setViewName("forward:/index.html");
    }

    /**
     * Description: 创建并配置 WebServerFactoryCustomizer 实例，设置自定义的404错误页面。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> 配置后的Web服务器工厂自定义器。
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return container -> {
            container.addErrorPages(
                    new ErrorPage(
                            HttpStatus.NOT_FOUND,
                            "/notFound")
            );
        };
    }

    /**
     * Description: 创建并配置 TomcatServletWebServerFactory 实例，允许在查询字符串中使用 `[]` 和 `{}` 字符。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return TomcatServletWebServerFactory 配置后的 Tomcat 嵌入式 Web 服务器工厂。
     */
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        // 创建 TomcatServletWebServerFactory 实例
        TomcatServletWebServerFactory tomcatServlet = new TomcatServletWebServerFactory();

        // 添加自定义的 TomcatConnectorCustomizer
        tomcatServlet.addConnectorCustomizers(
                (TomcatConnectorCustomizer) connector ->
                        // 配置连接器属性，允许在查询字符串中使用 `[]` 和 `{}` 字符
                        connector.setProperty("relaxedQueryChars", "[]{}")
        );

        // 返回配置后的 TomcatServletWebServerFactory 实例
        return tomcatServlet;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }

    /**
     * Description: 配置静态资源处理，包括Swagger UI、Webjars和Druid监控页面的资源映射。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param registry ResourceHandlerRegistry 对象，用于配置资源处理器。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        // Druid 监控页面资源配置
        registry.addResourceHandler("/druid/**")
                .addResourceLocations("classpath:/META-INF/resources/druid/");
    }

    /**
     * Description: 配置异步请求支持，包括设置默认超时时间和注册超时拦截器。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param configurer AsyncSupportConfigurer 对象，用于配置异步支持选项。
     */
    @Override
    public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {

        configurer.setDefaultTimeout(30 * 1000);

        configurer.registerCallableInterceptors(timeoutInterceptor());

    }

    /**
     * Description: 创建并配置 TimeoutCallableProcessingInterceptor，用于处理异步请求的超时。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return TimeoutCallableProcessingInterceptor 用于处理异步请求的超时拦截器。
     */
    @Bean
    public TimeoutCallableProcessingInterceptor timeoutInterceptor() {

        return new TimeoutCallableProcessingInterceptor();

    }

    /**
     * Description: 配置全局跨域映射，允许所有来源、方法和头部的跨域请求，并允许凭证。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param registry CorsRegistry 对象，用于配置跨域请求映射。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许所有来源的请求
                .allowedOriginPatterns("*")
                // 允许所有HTTP方法（GET, POST, PUT, DELETE等）
                .allowedMethods("*")
                // 允许所有请求头
                .allowedHeaders("*")
                // 允许发送凭证（如Cookie）
                .allowCredentials(true);
    }

    /**
     * Description: 根据应用类型为所有控制器添加路径前缀，但排除了特定包名下的控制器。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException 当应用类型未知时抛出异常。
     *
     * @param configurer PathMatchConfigurer 对象，用于配置路径匹配选项。
     */
    @Override
    public void configurePathMatch(@NotNull PathMatchConfigurer configurer) {
        // 对所有控制器生效
        switch (appName) {
            case "datalight-master":
                // 为 datalight-master 应用类型的所有控制器添加 /master 前缀，
                // 但排除包名以 springfox.documentation 开头的控制器。
                configurer.addPathPrefix(
                        MASTER_URL_PREFIX,
                        c -> !c.getPackage().getName().startsWith("springfox.documentation")
                                && !c.getPackage().getName().startsWith("com.alibaba.druid")
                );
                break;
            case "datalight-worker":
                // 为 datalight-worker 应用类型的所有控制器添加 /worker 前缀，
                // 但排除包名以 springfox.documentation 开头的控制器。
                configurer.addPathPrefix(
                        WORKER_URL_PREFIX,
                        c -> !c.getPackage().getName().startsWith("springfox.documentation")
                );
                break;
            default:
                // 如果 appName 不匹配已知的应用类型，抛出异常。
                throw new BException("未知的应用类型");
        }
    }
}
