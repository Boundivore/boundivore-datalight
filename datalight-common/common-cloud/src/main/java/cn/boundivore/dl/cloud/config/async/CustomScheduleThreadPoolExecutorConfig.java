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

import cn.hutool.core.thread.NamedThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: CustomScheduleThreadPoolExecutorConfig
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Configuration
@EnableScheduling
public class CustomScheduleThreadPoolExecutorConfig implements SchedulingConfigurer {
    private static final int cpuCores = Runtime.getRuntime().availableProcessors();
    private static final int corePoolSize = cpuCores * 2;
    private static final int maxPoolSize = Math.max((int) (cpuCores * cpuCores * (1 + 0.5)), cpuCores * 10);
    private static final int keepAliveTime = 120;
    private static final String ThreadNamePrefix = "datalight-schedule-";

    @Bean(name = "scheduleExecutor", destroyMethod = "shutdown")
    public ScheduledThreadPoolExecutor threadPoolTaskScheduler() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(corePoolSize);
        executor.setKeepAliveTime(keepAliveTime, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
        executor.setMaximumPoolSize(maxPoolSize);
        executor.setThreadFactory(new NamedThreadFactory(ThreadNamePrefix, true));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        return executor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(threadPoolTaskScheduler());
    }
}
