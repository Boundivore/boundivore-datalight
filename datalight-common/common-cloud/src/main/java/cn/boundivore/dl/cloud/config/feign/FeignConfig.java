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
package cn.boundivore.dl.cloud.config.feign;

import feign.Client;
import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * Description: 配置 FeignClient
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/3
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@Configuration
@Slf4j
@ConditionalOnClass(HttpServletRequest.class)
public class FeignConfig {

    @Value("${feign.client.config.default.connectTimeout}")
    private long connectTimeout;

    @Value("${feign.client.config.default.readTimeout}")
    private long readTimeout;

    @Bean
    public Feign.Builder feignBuilder(Encoder encoder, Decoder decoder) {
        return Feign.builder()
                .client(feignClient())
                .contract(new SpringMvcContract())
                .encoder(encoder)
                .decoder(decoder)
                .retryer(Retryer.NEVER_RETRY)
                .options(new Request.Options(
                        connectTimeout,
                        TimeUnit.MILLISECONDS,
                        readTimeout,
                        TimeUnit.MILLISECONDS,
                        true)
                );
    }

    @Bean
    @Primary
    public Client feignClient() {
        return new OkHttpClient();
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(customMessageConverters());
    }

    @Bean
    public ObjectFactory<HttpMessageConverters> customMessageConverters() {
        return () -> new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
    }

}
