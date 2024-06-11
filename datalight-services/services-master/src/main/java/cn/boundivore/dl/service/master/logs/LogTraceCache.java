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
package cn.boundivore.dl.service.master.logs;

import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Description: Log 缓存类，用于缓存 LogTrace 对象，并定时或在达到一定数量时批量写入数据库。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class LogTraceCache {

    // 最大缓存大小
    private static final int MAX_CACHE_SIZE = 10;
    // 预留空间大小
    private static final int RESERVED_SPACE = 10;
    // 最大缓存时间（秒）
    private static final long MAX_CACHE_TIME = 30;

    // 线程安全的阻塞队列，用于存储 LogTrace 对象
    private static final BlockingQueue<LogTrace> logTraceQueue = new LinkedBlockingQueue<>(MAX_CACHE_SIZE + RESERVED_SPACE);

    // 定时任务调度器
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 批量写入执行器
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    static {
        // 每隔 MAX_CACHE_TIME 秒执行一次 flushCache 方法
        scheduler.scheduleAtFixedRate(
                LogTraceCache::flushCache,
                MAX_CACHE_TIME,
                MAX_CACHE_TIME,
                TimeUnit.SECONDS
        );
    }

    /**
     * Description: 添加 LogTrace 对象到缓存队列。当队列达到指定大小时，触发批量写入操作。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param logTrace 要添加的 LogTrace 对象
     */
    public static void addLogTrace(LogTrace logTrace) {
        try {
            // 阻塞直到有空间可用
            logTraceQueue.put(logTrace);
        } catch (InterruptedException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            Thread.currentThread().interrupt(); // 重设中断状态
        }

        if (logTraceQueue.size() >= MAX_CACHE_SIZE) {
            flushCache();
        }
    }

    /**
     * Description: 刷新缓存，将缓存中的 LogTrace 对象批量写入数据库。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     */
    private static void flushCache() {
        List<LogTrace> batch = new ArrayList<>();
        logTraceQueue.drainTo(batch, MAX_CACHE_SIZE); // 一次性取出最多 MAX_CACHE_SIZE 个元素
        if (!batch.isEmpty()) {
            // 使用 CompletableFuture 异步执行批量写入操作，避免阻塞新的缓存入队请求
            CompletableFuture.runAsync(() -> batchWriteToDatabase(batch), executor)
                    .handle((result, ex) -> {
                        if (ex != null) {
                            log.error("写入日志到数据库时出错: {}", ExceptionUtil.stacktraceToString(ex));
                        }
                        return null;
                    });
        }
    }

    /**
     * Description: 批量写入数据库。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param logTraces 要写入的 LogTrace 对象列表
     */
    private static void batchWriteToDatabase(List<LogTrace> logTraces) {
        log.info("批量写入 {} 个 LogTrace 对象到数据库", logTraces.size());
        new LogExporterTask("批量日志导出", logTraces).run();
    }

    /**
     * Description: 关闭定时任务调度器和批量写入执行器。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     */
    public static void shutdown() {
        scheduler.shutdown();
        executor.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            executor.shutdownNow();
            Thread.currentThread().interrupt(); // 重设中断状态
        }
    }
}
