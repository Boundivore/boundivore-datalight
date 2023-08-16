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
package cn.boundivore.dl.cloud.config.async;

import cn.boundivore.dl.cloud.config.async.executors.CustomThreadPoolTaskExecutor;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description: CustomThreadPoolExecutorConfig
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/3/30
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Configuration
@EnableAsync
@Slf4j
public class CustomThreadPoolExecutorConfig {
    private static final int cpuCores = Runtime.getRuntime().availableProcessors();
    private static final int corePoolSize = Math.max(cpuCores * cpuCores, cpuCores * 5);
    private static final int maxPoolSize = Math.max((int) (cpuCores * cpuCores * (1 + 0.5)), cpuCores * 10);
    private static final int keepAliveTime = 10 * 1000;
    private static final int queueCapacity = 100;
    private static final String ThreadNamePrefix = "datalight-custom";

    @Bean(name = "customExecutor")
    @Scope(value = "singleton")
    public CustomThreadPoolTaskExecutor executor() {
        CustomThreadPoolTaskExecutor executor = new CustomThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(ThreadNamePrefix);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setDaemon(true);
        executor.setRejectedExecutionHandler(new DataLightRejectedExecutionHandler());
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    /**
     * Thread pool and thread queue refuse policy after full load.
     * If the load is full, it will block and wait.
     */
    private static class DataLightRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                log.error(ExceptionUtil.stacktraceToString(e));
            }
        }
    }
}
