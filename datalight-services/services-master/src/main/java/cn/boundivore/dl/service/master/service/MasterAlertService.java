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
package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ExecTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.enumeration.impl.StepTypeEnum;
import cn.boundivore.dl.base.request.impl.common.AlertWebhookPayloadRequest;
import cn.boundivore.dl.base.request.impl.worker.ExecRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.service.master.bean.AlertSummaryBean;
import cn.boundivore.dl.service.master.env.DataLightEnv;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceDetail;
import cn.hutool.core.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 告警相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterAlertService {

    private final MasterComponentService masterComponentService;

    private final MasterNodeService masterNodeService;

    private final RemoteInvokeWorkerService remoteInvokeWorkerService;

    /**
     * Description: 接收 AlertManager 告警钩子函数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request AlertManager 告警封装
     * @return Result<String> 调用成功或失败
     */
    public Result<String> alertHook(AlertWebhookPayloadRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("调用告警钩子接口成功: {}", request);
        }

        // 微信

        // 钉钉

        // 飞书

        // 邮件

        // 短信

        // 根据告警自动拉起服务组件
        this.pullServiceComponent(request.getAlerts());

        return Result.success();
    }

    /**
     * Description: 用户服务组件自动拉起
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alerts 告警列表
     */
    private void pullServiceComponent(List<AlertWebhookPayloadRequest.Alert> alerts) {
        // 服务组件进程自动拉起
        alerts.parallelStream()
                .filter(alert -> {
                    String summary = alert.getAnnotations().get("summary");
                    return summary != null && summary.contains("STATIC");
                })
                .forEach(alert -> {
                            try {
                                String summary = alert.getAnnotations().get("summary");
                                AlertSummaryBean alertSummaryBean = AlertSummaryBean.parseAndPrintComponents(summary);
                                log.info("\n 收到告警: {}\n 描述: {}",
                                        alertSummaryBean,
                                        alert.getAnnotations().get("description")
                                );

                                Assert.notNull(
                                        alertSummaryBean,
                                        () -> new BException("告警信息解析错误")
                                );

                                if (alertSummaryBean != null) {
                                    TDlNode tDlNode = this.masterNodeService.getNodeListByHostname(alertSummaryBean.getHostname());

                                    TDlComponent tDlComponent = this.masterComponentService.getTDlComponentByComponentNameInNode(
                                            tDlNode.getId(),
                                            alertSummaryBean.getServiceName(),
                                            alertSummaryBean.getComponentName()
                                    );

                                    // 判断，如果当前组件意图状态为 STARTED，但监控状态为不活跃，则应自动拉起该组件
                                    if (tDlComponent.getComponentState() == SCStateEnum.STARTED) {
                                        RestartInfo restartInfo = this.getRestartInfo(
                                                alertSummaryBean.getServiceName(),
                                                alertSummaryBean.getComponentName()
                                        );

                                        // 执行远程自动拉起操作
                                        this.executeShell(
                                                tDlNode.getIpv4(),
                                                restartInfo
                                        );
                                    }
                                }

                            } catch (Exception ignored) {
                            }
                        }
                );
    }

    /**
     * Description: 获取拉起组件进程的脚本（绝对路径）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param serviceName   服务名称
     * @param componentName 组件名称
     * @return RestartInfo 重启组件的相关信息
     */
    private RestartInfo getRestartInfo(String serviceName, String componentName) {
        YamlServiceDetail.Component component = ResolverYamlServiceDetail.COMPONENT_MAP.get(componentName);
        if (component == null) {
            throw new BException(
                    String.format(
                            "未找到对应组件: %s",
                            componentName
                    )
            );
        }

        return component.getActions().stream()
                .filter(i -> ActionTypeEnum.RESTART == i.getType())
                .findFirst()
                .map(action -> findRestartInfo(serviceName, action))
                .orElseThrow(
                        () -> new BException(
                                String.format(
                                        "没有找到组件可用的重启操作: %s",
                                        componentName
                                )
                        )
                );
    }

    /**
     * Description: 查找重启信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param serviceName 服务名称
     * @param action      组件的动作
     * @return RestartInfo 重启组件的相关信息
     */
    private RestartInfo findRestartInfo(String serviceName, YamlServiceDetail.Action action) {
        return action.getSteps().stream()
                .filter(i -> StepTypeEnum.COMMON_SCRIPT == i.getType())
                .map(step -> createRestartInfo(serviceName, step))
                .findFirst()
                .orElseThrow(
                        () -> new BException(
                                "没有找到对应的 RESTART Action"
                        )

                );
    }

    /**
     * Description: 创建重启信息对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param serviceName 服务名称
     * @param step        组件步骤
     * @return RestartInfo 重启信息
     */
    private RestartInfo createRestartInfo(String serviceName, YamlServiceDetail.Step step) {
        String restartShell = String.format(
                "%s/%s/scripts/%s",
                DataLightEnv.PLUGINS_DIR_REMOTE,
                serviceName,
                step.getShell()
        );

        return new RestartInfo(
                restartShell,
                step.getArgs(),
                step.getName(),
                step.getExits(),
                step.getTimeout()
        );
    }

    @Data
    @AllArgsConstructor
    private static final class RestartInfo {
        private String restartShell;
        private List<String> args;
        private String name;
        private Integer exit;
        private Long timeout;
    }


    /**
     * Description: 执行远程自动拉起组件操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param ip          节点 IP
     * @param restartInfo 重启组件的相关参数
     */
    private void executeShell(String ip, RestartInfo restartInfo) {
        log.info("准备远程执行命令: {}, {}",
                restartInfo.getRestartShell(),
                restartInfo.getArgs()
        );
        Result<String> result = this.remoteInvokeWorkerService.iWorkerExecAPI(ip)
                .exec(
                        new ExecRequest(
                                ExecTypeEnum.COMMAND,
                                String.format(
                                        "告警自动拉起: %s",
                                        restartInfo.getName()
                                ),
                                restartInfo.getRestartShell(),
                                restartInfo.getExit(),
                                restartInfo.getTimeout(),
                                restartInfo.getArgs().toArray(new String[0]),
                                new String[0],
                                true
                        )
                );

        log.info("自动拉起执行结果, Success: {}, Message: {}, Data: {}",
                result.isSuccess(),
                result.getMessage(),
                result.getData()
        );
    }


}


