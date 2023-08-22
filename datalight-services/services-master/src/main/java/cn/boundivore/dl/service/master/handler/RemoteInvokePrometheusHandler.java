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
package cn.boundivore.dl.service.master.handler;

import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.service.master.service.MasterComponentService;
import cn.boundivore.dl.service.master.service.MasterNodeService;
import cn.boundivore.dl.service.master.service.RemoteInvokePrometheusService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Description: Prometheus 综合调用逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteInvokePrometheusHandler {

    private final RemoteInvokePrometheusService remoteInvokePrometheusService;

    private final MasterComponentService masterComponentService;

    private final MasterNodeService masterNodeService;

    /**
     * Description: 重新加载 Prometheus
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     */
    public void invokePrometheusReload(Long clusterId) {
        try {
            TDlComponent prometheusServerTDlComponent = this.masterComponentService
                    .getTDlComponentListByServiceName(
                            clusterId,
                            "MONITOR"
                    )
                    .stream()
                    .filter(i -> i.getComponentName().equals("Prometheus"))
                    .collect(Collectors.toList())
                    .get(0);

            if (prometheusServerTDlComponent != null && prometheusServerTDlComponent.getComponentState() == SCStateEnum.STARTED) {
                TDlNode tDlNodePrometheus = this.masterNodeService.getNodeListInNodeIds(
                                clusterId,
                                CollUtil.newArrayList(prometheusServerTDlComponent.getNodeId())
                        )
                        .get(0);

                this.remoteInvokePrometheusService.iThirdPrometheusAPI(
                                tDlNodePrometheus.getIpv4(),
                                "9090"
                        )
                        .reloadPrometheus();
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }
    }
}
