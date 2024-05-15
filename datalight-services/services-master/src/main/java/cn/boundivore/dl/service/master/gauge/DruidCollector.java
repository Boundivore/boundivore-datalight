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
package cn.boundivore.dl.service.master.gauge;

import com.alibaba.druid.pool.DruidDataSource;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * Description: DruidCollector 负责收集并将 DruidDataSource 实例的指标注册到 MeterRegistry 中。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/15
 * Modification description:
 * Modified by:
 * Modification time:
 */
public class DruidCollector {

    private static final String LABEL_NAME = "pool";

    private final List<DruidDataSource> dataSources;

    private final MeterRegistry registry;

    /**
     * Description: 构造一个 DruidCollector，包含 DruidDataSource 实例列表和 MeterRegistry。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param dataSources DruidDataSource 实例列表
     * @param registry    注册指标的 MeterRegistry
     */
    DruidCollector(List<DruidDataSource> dataSources, MeterRegistry registry) {
        this.registry = registry;
        this.dataSources = dataSources;
    }

    /**
     * Description: 将每个 DruidDataSource 实例的指标注册到 MeterRegistry 中。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    void register() {
        this.dataSources.forEach((druidDataSource) -> {
            // basic configurations
            createGauge(druidDataSource, "druid_initial_size", "Initial size", (datasource) -> (double) druidDataSource.getInitialSize());
            createGauge(druidDataSource, "druid_min_idle", "Min idle", datasource -> (double) druidDataSource.getMinIdle());
            createGauge(druidDataSource, "druid_max_active", "Max active", datasource -> (double) druidDataSource.getMaxActive());

            // connection pool core metrics
            createGauge(druidDataSource, "druid_active_count", "Active count", datasource -> (double) druidDataSource.getActiveCount());
            createGauge(druidDataSource, "druid_active_peak", "Active peak", datasource -> (double) druidDataSource.getActivePeak());
            createGauge(druidDataSource, "druid_pooling_peak", "Pooling peak", datasource -> (double) druidDataSource.getPoolingPeak());
            createGauge(druidDataSource, "druid_pooling_count", "Pooling count", datasource -> (double) druidDataSource.getPoolingCount());
            createGauge(druidDataSource, "druid_wait_thread_count", "Wait thread count", datasource -> (double) druidDataSource.getWaitThreadCount());

            // connection pool detail metrics
            createGauge(druidDataSource, "druid_not_empty_wait_count", "Not empty wait count", datasource -> (double) druidDataSource.getNotEmptyWaitCount());
            createGauge(druidDataSource, "druid_not_empty_wait_millis", "Not empty wait millis", datasource -> (double) druidDataSource.getNotEmptyWaitMillis());
            createGauge(druidDataSource, "druid_not_empty_thread_count", "Not empty thread count", datasource -> (double) druidDataSource.getNotEmptyWaitThreadCount());

            createGauge(druidDataSource, "druid_logic_connect_count", "Logic connect count", datasource -> (double) druidDataSource.getConnectCount());
            createGauge(druidDataSource, "druid_logic_close_count", "Logic close count", datasource -> (double) druidDataSource.getCloseCount());
            createGauge(druidDataSource, "druid_logic_connect_error_count", "Logic connect error count", datasource -> (double) druidDataSource.getConnectErrorCount());
            createGauge(druidDataSource, "druid_physical_connect_count", "Physical connect count", datasource -> (double) druidDataSource.getCreateCount());
            createGauge(druidDataSource, "druid_physical_close_count", "Physical close count", datasource -> (double) druidDataSource.getDestroyCount());
            createGauge(druidDataSource, "druid_physical_connect_error_count", "Physical connect error count", datasource -> (double) druidDataSource.getCreateErrorCount());

            // sql execution core metrics
            createGauge(druidDataSource, "druid_error_count", "Error count", datasource -> (double) druidDataSource.getErrorCount());
            createGauge(druidDataSource, "druid_execute_count", "Execute count", datasource -> (double) druidDataSource.getExecuteCount());
            // transaction metrics
            createGauge(druidDataSource, "druid_start_transaction_count", "Start transaction count", datasource -> (double) druidDataSource.getStartTransactionCount());
            createGauge(druidDataSource, "druid_commit_count", "Commit count", datasource -> (double) druidDataSource.getCommitCount());
            createGauge(druidDataSource, "druid_rollback_count", "Rollback count", datasource -> (double) druidDataSource.getRollbackCount());

            // sql execution detail
            createGauge(druidDataSource, "druid_prepared_statement_open_count", "Prepared statement open count", datasource -> (double) druidDataSource.getPreparedStatementCount());
            createGauge(druidDataSource, "druid_prepared_statement_closed_count", "Prepared statement closed count", datasource -> (double) druidDataSource.getClosedPreparedStatementCount());
            createGauge(druidDataSource, "druid_ps_cache_access_count", "PS cache access count", datasource -> (double) druidDataSource.getCachedPreparedStatementAccessCount());
            createGauge(druidDataSource, "druid_ps_cache_hit_count", "PS cache hit count", datasource -> (double) druidDataSource.getCachedPreparedStatementHitCount());
            createGauge(druidDataSource, "druid_ps_cache_miss_count", "PS cache miss count", datasource -> (double) druidDataSource.getCachedPreparedStatementMissCount());
            createGauge(druidDataSource, "druid_execute_query_count", "Execute query count", datasource -> (double) druidDataSource.getExecuteQueryCount());
            createGauge(druidDataSource, "druid_execute_update_count", "Execute update count", datasource -> (double) druidDataSource.getExecuteUpdateCount());
            createGauge(druidDataSource, "druid_execute_batch_count", "Execute batch count", datasource -> (double) druidDataSource.getExecuteBatchCount());

            // none core metrics, some are static configurations
            createGauge(druidDataSource, "druid_max_wait", "Max wait", datasource -> (double) druidDataSource.getMaxWait());
            createGauge(druidDataSource, "druid_max_wait_thread_count", "Max wait thread count", datasource -> (double) druidDataSource.getMaxWaitThreadCount());
            createGauge(druidDataSource, "druid_login_timeout", "Login timeout", datasource -> (double) druidDataSource.getLoginTimeout());
            createGauge(druidDataSource, "druid_query_timeout", "Query timeout", datasource -> (double) druidDataSource.getQueryTimeout());
            createGauge(druidDataSource, "druid_transaction_query_timeout", "Transaction query timeout", datasource -> (double) druidDataSource.getTransactionQueryTimeout());
        });
    }

    /**
     * Description: 为指定的 DruidDataSource 创建一个 Gauge 指标。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param weakRef DruidDataSource 实例
     * @param metric  指标名称
     * @param help    指标描述
     * @param measure 从 DruidDataSource 提取指标值的函数
     */
    private void createGauge(DruidDataSource weakRef, String metric, String help, ToDoubleFunction<DruidDataSource> measure) {
        Gauge.builder(metric, weakRef, measure)
                .description(help)
                .tag(LABEL_NAME, weakRef.getName())
                .register(this.registry);
    }
}


