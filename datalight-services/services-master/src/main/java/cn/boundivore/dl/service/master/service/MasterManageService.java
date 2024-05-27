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

import cn.boundivore.dl.base.constants.AutoPullWorkerState;
import cn.boundivore.dl.base.constants.Constants;
import cn.boundivore.dl.base.enumeration.impl.*;
import cn.boundivore.dl.base.request.impl.common.AlertWebhookPayloadRequest;
import cn.boundivore.dl.base.request.impl.master.HeartBeatRequest;
import cn.boundivore.dl.base.request.impl.worker.ExecRequest;
import cn.boundivore.dl.base.request.impl.worker.MasterMetaRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.service.master.bean.AlertSummaryBean;
import cn.boundivore.dl.service.master.bean.RestartInfo;
import cn.boundivore.dl.service.master.cache.HeartBeatCache;
import cn.boundivore.dl.service.master.env.DataLightEnv;
import cn.boundivore.dl.service.master.manage.node.job.NodeJobService;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceDetail;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description: Master 端管理相关工作
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MasterManageService {

    @Value("${server.datalight.url.worker-port}")
    private String workerPort;

    private final HeartBeatCache heartBeatCache;

    private final MasterNodeService masterNodeService;

    private final MasterComponentService masterComponentService;

    private final RemoteInvokeWorkerService remoteInvokeWorkerService;

    private final NodeJobService nodeJobService;

    // 多线程拉起 Worker 线程池
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(4);

    /**
     * Description: 接收来自 Worker 端的心跳包
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 心跳包请求体
     * @return Result<String>
     */
    public Result<String> heartBeat(HeartBeatRequest request) {
        // 接收并更新心跳包
        this.heartBeatCache.updateHeartBeat(
                request.getIp()
        );

        return Result.success();
    }

    /**
     * Description: 周期性检查心跳包过期的 Worker，准备 SSH 拉起，对于重启的节点，探测是否重启完成
     * EASY TO FIX: 可以通过动态修改 Trigger 动态改变定时任务的周期策略
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Scheduled(
            initialDelay = 10 * 1000,
            fixedDelay = Constants.HEART_BEAT_TIMEOUT
    )
    private void checkAndPull() {
        // 检查重启的节点是否完成，如果完成，节点状态变更为 STARTED 状态
        this.checkAndDetect();

        // 检查心跳包是否过期，如果过期，则 SSH 拉起对应节点上的 Worker 进程
        this.checkAndPullWorker();
    }

    /**
     * Description: 检查重启的节点是否完成，如果完成，节点状态变更为 STARTED 状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    private void checkAndDetect() {
        // 准备检查正在重启的节点是否已经重启完成
        final Map<String, TDlNode> workerMetaMap = this.masterNodeService.getNodeListByState(
                        CollUtil.newArrayList(
                                NodeStateEnum.RESTARTING
                        )
                )
                .stream()
                .collect(Collectors.toMap(
                        TDlNode::getIpv4,
                        i -> i
                ));

        if (!workerMetaMap.isEmpty()) {
            // 获取能够连通的节点 IP
            List<String> isConnectedIpList = workerMetaMap.values()
                    .parallelStream()
                    .map(i -> {
                        boolean isConnected = this.nodeJobService.scan(
                                i.getIpv4(),
                                Integer.parseInt(i.getSshPort().toString()),
                                DataLightEnv.PRIVATE_KEY_PATH
                        );

                        if (isConnected) {
                            return i.getIpv4();
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 将探测成功的节点状态改为 STARTED
            List<TDlNode> newTDlNodeList = isConnectedIpList.stream()
                    .map(ip -> {
                        TDlNode tDlNode = workerMetaMap.get(ip);
                        tDlNode.setNodeState(NodeStateEnum.STARTED);
                        return tDlNode;
                    })
                    .collect(Collectors.toList());

            // 更新节点状态
            this.masterNodeService.updateBatchById(newTDlNodeList);
        }

    }

    /**
     * Description: 检查心跳包是否过期，如果过期，则 SSH 拉起对应节点上的 Worker 进程
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    private void checkAndPullWorker() {
        // <ip, TDlNode> 获取全部状态为 STARTED 的节点
        final Map<String, TDlNode> startedWorkerTDlNodeMap = this.masterNodeService.getNodeListByState(
                        CollUtil.newArrayList(
                                NodeStateEnum.STARTED
                        )
                )
                .stream()
                .collect(Collectors.toMap(
                                TDlNode::getIpv4,
                                i -> i
                        )
                );

        // 整合上述两个集合，准备 SSH 启动 Worker
        final List<TDlNode> allInvalidWorkerTDlNodeList = this.assembleInvalidWorkerTDlNodeList(startedWorkerTDlNodeMap);

        // 拉起 Worker 并推送
        this.pullWorkerAndPublishMaster(allInvalidWorkerTDlNodeList);
    }


    /**
     * Description: 组装好的全部需要拉起的节点
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param startedWorkerTDlNodeMap 当前为 STARTED 状态的全部节点
     * @return 组装好的全部需要拉起的节点
     */
    private List<TDlNode> assembleInvalidWorkerTDlNodeList(Map<String, TDlNode> startedWorkerTDlNodeMap) {
        // 清除无效的心跳包
        this.heartBeatCache.clearInvalidHearBeat(startedWorkerTDlNodeMap);

        // 检查心跳列表是否齐全
        final List<TDlNode> vacantWorkerTDlNodeList = startedWorkerTDlNodeMap
                .values()
                .stream()
                .filter(i -> !this.heartBeatCache.isContains(i.getIpv4()))
                .collect(Collectors.toList());

        // 检查心跳列表是否存在超时
        final List<TDlNode> timeoutWorkerTDlNodeList = this.heartBeatCache
                .getTimeoutWorkerHeartBeatList()
                .stream()
                .map(i -> startedWorkerTDlNodeMap.get(i.getIp()))
                .collect(Collectors.toList());

        // 整合上述两个集合，准备 SSH 启动 Worker
        final List<TDlNode> allInvalidWorkerTDlNodeList = new ArrayList<>(vacantWorkerTDlNodeList);
        allInvalidWorkerTDlNodeList.addAll(timeoutWorkerTDlNodeList);

        return allInvalidWorkerTDlNodeList;
    }

    /**
     * Description: 执行拉起并向 Worker 暴露 Master 服务的操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param allInvalidWorkerTDlNodeList 所有待操作的节点
     */
    private void pullWorkerAndPublishMaster(List<TDlNode> allInvalidWorkerTDlNodeList) {
        try {
            if (allInvalidWorkerTDlNodeList.isEmpty()) {
//                if (log.isDebugEnabled()) {
//                    log.debug("无待唤醒 Worker");
//                }
                log.info("无待唤醒 Worker");
                return;
            }

            log.info("等待拉起 Worker 数: {}", allInvalidWorkerTDlNodeList.size());

            final String masterRealIp = DataLightEnv.MASTER_REAL_IP;
            log.info("当前 Master 真实 IP: {}", masterRealIp);

            final String cmd = String.format(
                    "%s/datalight.sh restart worker %s",
                    DataLightEnv.BIN_PATH_DIR_REMOTE,
                    this.workerPort
            );

            // 用于 跳过自动拉起 Worker 进程的开关处于关闭状态的集群对应的节点 的判断依据
            final List<Long> autoPullTrueClusterIdList = AutoPullWorkerState.AUTO_PULL_WORKER_CACHE
                    .values()
                    .stream()
                    .filter(AutoPullWorkerState.CacheBean::isAutoPullWorker)
                    .map(AutoPullWorkerState.CacheBean::getClusterId)
                    .collect(Collectors.toList());


            this.forkJoinPool.submit(() -> {
                // SSH 启动 Worker，并推送 Master 位置
                allInvalidWorkerTDlNodeList.parallelStream()
                        .filter(i -> CollUtil.isEmpty(autoPullTrueClusterIdList) || autoPullTrueClusterIdList.contains(i.getClusterId()))
                        .forEach(i -> {
                            try {
                                // 首先尝试主动再次推送 Master 元数据信息，获取心跳，如失败，则尝试拉起
                                this.publishMasterMeta(masterRealIp, i.getIpv4());
                                log.info("拉起前最后一次尝试更新 Master({}) 元数据成功: Worker({})", masterRealIp, i.getIpv4());
                            } catch (Exception e) {
                                try {
                                    log.info("尝试推送 Master 元数据到心跳超时节点({})失败，准备拉起",
                                            i.getIpv4()
                                    );

                                    log.info(
                                            "准备拉起 Worker({}:{}) {}",
                                            i.getIpv4(),
                                            this.workerPort,
                                            cmd
                                    );

                                    this.nodeJobService.exec(
                                            i.getIpv4(),
                                            Integer.parseInt(i.getSshPort().toString()),
                                            DataLightEnv.PRIVATE_KEY_PATH,
                                            cmd,
                                            30 * 1000L,
                                            TimeUnit.MILLISECONDS
                                    );

                                    // 拉起后推送 MasterMeta
                                    this.publishMasterMeta(masterRealIp, i.getIpv4());
                                } catch (Exception ex) {
                                    log.error(
                                            "Worker({}) 远程启动失败: {}",
                                            i.getIpv4(),
                                            ExceptionUtil.stacktraceToString(e)
                                    );
                                }
                            }
                        });
            }).get();
        } catch (Exception e) {
            log.error("线程错误: {}", ExceptionUtil.stacktraceToString(e));
        }
    }

    /**
     * Description: 向 Worker 发布自身元数据信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public void updateMasterMeta() {
        List<TDlNode> tDlNodeList = this.masterNodeService.getNodeListByState(
                CollUtil.newArrayList(NodeStateEnum.STARTED)
        );


        final String masterRealIp = DataLightEnv.MASTER_REAL_IP;

        // 向节点中逐个推送 Master 信息，如需更高效率，可以考虑多线程
        tDlNodeList.forEach(i -> {
            try {
                this.publishMasterMeta(masterRealIp, i.getIpv4());
            } catch (Exception e) {
                log.error(
                        "Master 信息发布失败: {}, {}",
                        i.getHostname(),
                        ExceptionUtil.getMessage(e)
                );
            }
        });
    }

    /**
     * Description: 向 Worker 推送 MasterMeta 位置信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param masterIp Master Ip 地址
     * @param workerIp Worker Ip 地址
     */
    public void publishMasterMeta(String masterIp, String workerIp) {
        log.info(
                "推送 Master({}) 元数据信至 Worker({})",
                masterIp,
                workerIp
        );

        Result<String> result = this.remoteInvokeWorkerService
                .iWorkerManageAPI(workerIp)
                .updateMasterMeta(
                        new MasterMetaRequest(
                                masterIp
                        )
                );

        Assert.isTrue(
                result.isSuccess(),
                () -> new BException(
                        String.format(
                                "向 %s 更新 Master 数据失败: %s",
                                workerIp,
                                result.getMessage()
                        )
                )
        );
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
    public void checkAndPullServiceComponent(List<AlertWebhookPayloadRequest.Alert> alerts) {
        // 服务组件进程自动拉起
        alerts.parallelStream()
                .filter(alert -> {
                    String alertType = alert.getAnnotations().get("alert_type");
                    return alertType != null && alertType.equals("STATIC");
                })
                .forEach(alert -> {
                            try {

                                String alertJob = alert.getAnnotations().get("alert_instance");
                                String alertInstance = alert.getAnnotations().get("alert_instance");

                                AlertSummaryBean alertSummaryBean = AlertSummaryBean.parseAndPrintComponents(
                                        alertJob,
                                        alertInstance
                                );

                                String summary = alert.getAnnotations().get("summary");
                                log.info("\n 收到告警: {}\n 描述: {}",
                                        alertSummaryBean,
                                        summary
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
