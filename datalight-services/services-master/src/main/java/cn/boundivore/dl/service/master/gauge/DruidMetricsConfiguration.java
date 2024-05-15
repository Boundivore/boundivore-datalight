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
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Description: DruidMetricsConfiguration 用于配置 Druid 数据源的指标收集。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/15
 * Modification description:
 * Modified by:
 * Modification time:
 */
@Configuration
@ConditionalOnClass({DruidDataSource.class, MeterRegistry.class})
@Slf4j
public class DruidMetricsConfiguration {

    private final MeterRegistry registry;


    /**
     * Description: 构造一个 DruidMetricsConfiguration，包含 MeterRegistry。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param registry 注册指标的 MeterRegistry
     */
    public DruidMetricsConfiguration(MeterRegistry registry) {
        this.registry = registry;
    }

    /**
     * Description: 将指标注册表绑定到 Druid 数据源。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: SQLException
     *
     * @param dataSources 数据源集合
     */
    @Autowired
    public void bindMetricsRegistryToDruidDataSources(Collection<DataSource> dataSources) throws SQLException {
        List<DruidDataSource> druidDataSources = new ArrayList<>(dataSources.size());
        for (DataSource dataSource : dataSources) {
            DruidDataSource druidDataSource = dataSource.unwrap(DruidDataSource.class);
            if (druidDataSource != null) {
                druidDataSources.add(druidDataSource);
            }
        }
        DruidCollector druidCollector = new DruidCollector(druidDataSources, registry);
        druidCollector.register();
        log.info("DruidMetricsConfiguration 配置完成");
    }
}
