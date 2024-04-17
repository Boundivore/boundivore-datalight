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
package cn.boundivore.dl.service.master.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 告警回传参数 Bean
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/16
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
public class AlertSummaryBean {

    private String hostname;
    private String port;
    private String serviceName;
    private String componentName;

    public static AlertSummaryBean parseAndPrintComponents(String alertJob, String alertInstance) {

        // 解析服务名与组件名
        String[] serviceComponentNameArr = alertJob.split("-");
        String serviceName = serviceComponentNameArr[0];
        String componentName = serviceComponentNameArr[1];

        // 解析主机名与端口号
        String[] hostnamePortArr = alertInstance.split(":");
        String hostname = hostnamePortArr[0];
        String port = hostnamePortArr[1];

        return new AlertSummaryBean(
                hostname,
                port,
                serviceName,
                componentName
        );
    }

    @Override
    public String toString() {
        return String.format(
                "\n Hostname: %s\n Port: %s\n ServiceName: %s\n ComponentName: %s",
                hostname,
                port,
                serviceName,
                componentName
        );
    }
}
