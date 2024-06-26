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
package cn.boundivore.dl.service.master.grafana;

import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.handler.RemoteInvokeGrafanaHandler;
import cn.boundivore.dl.service.master.service.RemoteInvokeGrafanaService;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * Description: 测试 Grafana API
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/16
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.yaml")
@Slf4j
public class GrafanaTest {

    @Autowired
    private RemoteInvokeGrafanaService remoteInvokeGrafanaService;

    @Autowired
    private RemoteInvokeGrafanaHandler remoteInvokeGrafanaHandler;
    private final static String GRAFANA_HOST = "node01";
    private final static String GRAFANA_PORT = "3000";


    @PostConstruct
    public void init() {
        this.remoteInvokeGrafanaService.init(
                GRAFANA_HOST,
                GRAFANA_PORT
        );
    }

    @Test
    public void createOrg() {
        Result<String> result = this.remoteInvokeGrafanaService.createOrg(
                RemoteInvokeGrafanaHandler.ADMIN_NEW_TOKEN,
                RemoteInvokeGrafanaHandler.GRAFANA_BASE_ORG_NAME
        );

        log.info(result.toString());
    }


    @Test
    public void createUsers() {
        Result<String> result = this.remoteInvokeGrafanaService.createUsers(
                RemoteInvokeGrafanaHandler.ADMIN_NEW_TOKEN,
                "datalight",
                "datalight",
                "datalight"
        );
        log.info(result.toString());
    }

    @Test
    public void getOrgByName() {
        Result<String> result = this.remoteInvokeGrafanaService.getOrgByName(
                RemoteInvokeGrafanaHandler.ADMIN_NEW_TOKEN,
                "datalight"
        );
        log.info(result.toString());
    }

    @Test
    public void getStats() {
        Result<String> result = this.remoteInvokeGrafanaService.getStats(RemoteInvokeGrafanaHandler.ADMIN_NEW_TOKEN);
        log.info(result.toString());
    }

    @Test
    public void searchAllOrgs() {
        Result<String> result = this.remoteInvokeGrafanaService.searchAllOrgs(RemoteInvokeGrafanaHandler.ADMIN_NEW_TOKEN);
        log.info(result.toString());
    }

    @Test
    public void getDatasourceByName() {
        Result<String> result = this.remoteInvokeGrafanaService.getDatasourceByName(
                RemoteInvokeGrafanaHandler.ADMIN_NEW_TOKEN,
                "MONITOR-Prometheus"
        );
        log.info(result.toString());
    }

    @Test
    public void createDataSources() {
        Result<String> result = this.remoteInvokeGrafanaService.createDataSources(
                RemoteInvokeGrafanaHandler.ADMIN_DATALIGHT_NEW_TOKEN,
                "2",
                "MONITOR-Prometheus",
                "node01",
                "9090",
                "admin-datalight",
                "admin-datalight"
        );
        log.info(result.toString());
    }

    @Test
    public void createOrUpdateDashboard() {
        String dashboardJson1 = FileUtil.readString(
                "D:\\workspace\\boundivore_workspace\\boundivore-datalight\\.documents\\plugins\\MONITOR\\dashboard\\DATALIGHT.json",
                StandardCharsets.UTF_8
        );
        String dashboardJson2 = FileUtil.readString(
                "D:\\workspace\\boundivore_workspace\\boundivore-datalight\\.documents\\plugins\\MONITOR\\dashboard\\MONITOR-Alertmanager.json",
                StandardCharsets.UTF_8
        );
        String dashboardJson3 = FileUtil.readString(
                "D:\\workspace\\boundivore_workspace\\boundivore-datalight\\.documents\\plugins\\MONITOR\\dashboard\\MONITOR-Grafana.json",
                StandardCharsets.UTF_8
        );
        String dashboardJson4 = FileUtil.readString(
                "D:\\workspace\\boundivore_workspace\\boundivore-datalight\\.documents\\plugins\\MONITOR\\dashboard\\MONITOR-MySQLExporter.json",
                StandardCharsets.UTF_8
        );
        String dashboardJson5 = FileUtil.readString(
                "D:\\workspace\\boundivore_workspace\\boundivore-datalight\\.documents\\plugins\\MONITOR\\dashboard\\MONITOR-NodeExporter.json",
                StandardCharsets.UTF_8
        );
        String dashboardJson6 = FileUtil.readString(
                "D:\\workspace\\boundivore_workspace\\boundivore-datalight\\.documents\\plugins\\MONITOR\\dashboard\\MONITOR-Prometheus.json",
                StandardCharsets.UTF_8
        );
        String dashboardJson7 = FileUtil.readString(
                "D:\\workspace\\boundivore_workspace\\boundivore-datalight\\.documents\\plugins\\MONITOR\\dashboard\\ZOOKEEPER-QuarumPeermain.json",
                StandardCharsets.UTF_8
        );

        Result<String> result1 = this.remoteInvokeGrafanaService.createOrUpdateDashboard(
                RemoteInvokeGrafanaHandler.ADMIN_DATALIGHT_NEW_TOKEN,
                dashboardJson1
        );
        Result<String> result2 = this.remoteInvokeGrafanaService.createOrUpdateDashboard(
                RemoteInvokeGrafanaHandler.ADMIN_DATALIGHT_NEW_TOKEN,
                dashboardJson2
        );
        Result<String> result3 = this.remoteInvokeGrafanaService.createOrUpdateDashboard(
                RemoteInvokeGrafanaHandler.ADMIN_DATALIGHT_NEW_TOKEN,
                dashboardJson3
        );
        Result<String> result4 = this.remoteInvokeGrafanaService.createOrUpdateDashboard(
                RemoteInvokeGrafanaHandler.ADMIN_DATALIGHT_NEW_TOKEN,
                dashboardJson4);
        Result<String> result5 = this.remoteInvokeGrafanaService.createOrUpdateDashboard(
                RemoteInvokeGrafanaHandler.ADMIN_DATALIGHT_NEW_TOKEN,
                dashboardJson5
        );
        Result<String> result6 = this.remoteInvokeGrafanaService.createOrUpdateDashboard(
                RemoteInvokeGrafanaHandler.ADMIN_DATALIGHT_NEW_TOKEN,
                dashboardJson6
        );
        Result<String> result7 = this.remoteInvokeGrafanaService.createOrUpdateDashboard(
                RemoteInvokeGrafanaHandler.ADMIN_DATALIGHT_NEW_TOKEN,
                dashboardJson7
        );

        log.info(result1.toString());
        log.info(result2.toString());
        log.info(result3.toString());
        log.info(result4.toString());
        log.info(result5.toString());
        log.info(result6.toString());
        log.info(result7.toString());
    }

    @Test
    public void grafanaAllTest() {
        this.remoteInvokeGrafanaHandler.initGrafanaSettings(
                1L
        );

        this.createOrUpdateDashboard();
    }

    @Test
    public void initAllDashboards() {
        this.remoteInvokeGrafanaHandler.initAllDashboard();
    }
}
