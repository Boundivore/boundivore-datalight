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

import cn.boundivore.dl.api.third.define.IThirdPrometheusAPI;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.master.InvokePrometheusRequest;
import cn.boundivore.dl.base.result.ErrorMessage;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.result.ResultEnum;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.service.master.service.MasterComponentService;
import cn.boundivore.dl.service.master.service.MasterNodeService;
import cn.boundivore.dl.service.master.service.RemoteInvokePrometheusService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
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
     * Description: 根据集群 ID 获取 Prometheus 所在节点主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Prometheus 部署所在节点的主机名
     */
    private String getPrometheusHostname(Long clusterId) {
        List<TDlComponent> prometheusTDlComponentList = this.masterComponentService
                .getTDlComponentListByServiceName(
                        clusterId,
                        "MONITOR"
                )
                .stream()
                .filter(i -> i.getComponentName().equals("Prometheus"))
                .collect(Collectors.toList());

        Assert.notEmpty(
                prometheusTDlComponentList,
                () -> new BException("指定集群下未找到 Prometheus 实例")
        );

        TDlComponent prometheusServerTDlComponent = prometheusTDlComponentList.get(0);


        if (prometheusServerTDlComponent != null && prometheusServerTDlComponent.getComponentState() == SCStateEnum.STARTED) {
            TDlNode tDlNodePrometheus = this.masterNodeService.getNodeListInNodeIds(
                            clusterId,
                            CollUtil.newArrayList(prometheusServerTDlComponent.getNodeId())
                    )
                    .get(0);

            return tDlNodePrometheus.getHostname();
        }

        throw new BException("未找到可用的 Prometheus 实例");
    }


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
            String prometheusHostname = this.getPrometheusHostname(clusterId);
            this.remoteInvokePrometheusService.iThirdPrometheusAPI(
                            prometheusHostname,
                            "9090"
                    )
                    .reloadPrometheus();
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }
    }

    /**
     * Description: 通用代理调用 Prometheus 接口
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 请求 Prometheus 时的相关参数
     * @return Prometheus 相关参数
     */
    public Result<String> invokePrometheus(InvokePrometheusRequest request) {
        // 检查请求规范
        this.checkRequest(request);

        Result<String> result;

        try {
            String prometheusHostname = this.getPrometheusHostname(request.getClusterId());

            IThirdPrometheusAPI iThirdPrometheusAPI = this.remoteInvokePrometheusService.iThirdPrometheusAPI(
                    prometheusHostname,
                    "9090"
            );

            switch (request.getRequestMethod()) {
                case GET:
                    result = iThirdPrometheusAPI.getPrometheus(request.getPath(), request.getQueryParamsMap());
                    break;
                case POST:
                    result = iThirdPrometheusAPI.postPrometheus(request.getPath(), request.getBody());
                    break;
                default:
                    result = Result.success();
            }

        } catch (Exception e) {
            String errorMsg = String.format(
                    "调用 Prometheus 失败: \n%s",
                    ExceptionUtil.stacktraceToString(e)
            );
            log.error(errorMsg);

            result = Result.fail(
                    ResultEnum.FAIL_REMOTE_INVOKE_EXCEPTION,
                    new ErrorMessage(errorMsg)
            );
        }

        return result;
    }

    /**
     * Description: 检查请求体规范
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 请求 Prometheus 的相关参数
     */
    private void checkRequest(InvokePrometheusRequest request) {
        switch (request.getRequestMethod()) {
            case GET:
            case POST:
                return;
            case PUT:
            case HEAD:
            case PATCH:
            case TRACE:
            case DELETE:
            case CONNECT:
            case OPTIONS:
            default:
                throw new IllegalArgumentException(
                        String.format(
                                "代理 Prometheus 时暂不支持的 Http Method: %s",
                                request.getRequestMethod()
                        )
                );

        }
    }
}
