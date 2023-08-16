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

import cn.hutool.core.collection.CollUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Description: Caffeine 缓存配置
 * Created by: Boundivore
 * E-mail: boundivore@formail.com
 * Creation time: 2023/7/18
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean(name = "caffeine")
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 设置缓存名称和缓存过期时间

        cacheManager.setCacheNames(CollUtil.newArrayList("DataLightCache1")); // 设置缓存名称，可以设置多个缓存
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .initialCapacity(10)
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .maximumSize(10000)
        );

        return cacheManager;
    }
}
