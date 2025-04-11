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
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${spring.application.name}")
    private String appName;

    private final ObjectMapper objectMapper;

    public WebMvcConfig(@Qualifier("stringFormatObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        log.info("WebMvcConfig初始化，使用stringFormatObjectMapper, 数字转字符串设置: {}",
                objectMapper.isEnabled(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS.mappedFeature()));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展MessageConverters，当前converter数量: {}", converters.size());

        // 先打印所有converter类型
        converters.forEach(converter ->
                log.info("当前converter类型: {}", converter.getClass().getName())
        );

        // 移除所有Jackson converter
        converters.removeIf(converter ->
                converter instanceof MappingJackson2HttpMessageConverter);

        // 添加我们的自定义converter到末尾
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(objectMapper);
        converters.add(converter);

        log.info("重新配置Jackson converters后的数量: {}", converters.size());

        // 确认最终的converter配置
        converters.forEach(conv -> {
            if (conv instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jacksonConverter =
                        (MappingJackson2HttpMessageConverter) conv;
                log.info("最终的MappingJackson2HttpMessageConverter:");
                log.info("- 类名: {}", jacksonConverter.getClass().getName());
                log.info("- ObjectMapper实例: {}", jacksonConverter.getObjectMapper());
                log.info("- 数字转字符串设置: {}",
                        jacksonConverter.getObjectMapper().isEnabled(
                                JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS.mappedFeature()));
            }
        });
    }


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

        configurer.setDefaultTimeout(120 * 1000L);

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

    /**
     * Description: 创建并配置缓存控制过滤器，确保 index.html 每次请求时从服务器获取最新版本。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Filter 配置后的缓存控制过滤器。
     */
    @Bean
    public Filter cacheControlFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;

                String requestURI = httpRequest.getRequestURI();

                if (this.isHtmlResource(requestURI)) {
                    httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    httpResponse.setHeader("Pragma", "no-cache");
                    httpResponse.setHeader("Expires", "0");
                } else if (this.isStaticResource(requestURI)) {
                    httpResponse.setHeader("Cache-Control", "public, max-age=31536000");
                }

                chain.doFilter(request, response);
            }

            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void destroy() {
            }

            private boolean isHtmlResource(String requestURI) {
                return requestURI.equals("/") || requestURI.equals("/index.html") || requestURI.endsWith(".html");
            }

            private boolean isStaticResource(String requestURI) {
//                return requestURI.startsWith("/assets/") ||
//                        requestURI.startsWith("/service_logo/") ||
//                        requestURI.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg)$");

                return requestURI.startsWith("/assets/");
            }
        };
    }


}
