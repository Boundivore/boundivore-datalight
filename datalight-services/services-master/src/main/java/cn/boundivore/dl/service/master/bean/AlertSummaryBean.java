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

    public static AlertSummaryBean parseAndPrintComponents(String input) {
        String[] parts = input.split("[:@]");
        if (parts.length != 3) {
            log.info("AlertSummary 格式错误");
            return null;
        }

        String hostname = parts[0];
        String port = parts[1];
        String[] serviceComponent = parts[2].split("-");
        if (serviceComponent.length != 2) {
            log.info("服务和组件名格式错误");
            return null;
        }
        String serviceName = serviceComponent[0];
        String componentName = serviceComponent[1];

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
                "\nHostname: %s\n Port: %s\n ServiceName: %s\n ComponentName: %s\n",
                hostname,
                port,
                serviceName,
                componentName
        );
    }
}
