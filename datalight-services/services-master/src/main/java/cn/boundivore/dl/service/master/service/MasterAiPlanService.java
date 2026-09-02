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

import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractAiPlanRequest;
import cn.boundivore.dl.base.request.impl.master.AbstractClusterRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractAiPlanVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.hutool.core.lang.Assert;
import cn.boundivore.dl.orm.po.single.TDlAiDeployPlan;
import cn.boundivore.dl.orm.service.single.ITDlAiDeployPlanService;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: AI 会话式部署计划的落库逻辑。
 * <p>
 * 计划由 AIAgent 起草，用户在页面上确认后回传到这里。AIAgent 侧已经校验过一轮，
 * 这里仍然要完整重做一遍：请求经过浏览器，中间可以被改。信任 AIAgent 的校验结果，
 * 等于把拓扑校验放在了客户端。
 * <p>
 * 本服务只负责把计划落库并建出集群，不触发部署。部署仍然由用户在部署页面上点击开始，
 * 走原有的 Job/Stage/Task/Step 流程。把「确认计划」和「开始部署」分开，
 * 是因为前者只是记下打算怎么做，后者才会真的动机器。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MasterAiPlanService {

    private final ITDlAiDeployPlanService tDlAiDeployPlanService;
    private final MasterClusterService masterClusterService;

    /**
     * 需要奇数个实例才能选出主的组件。偶数个不是不推荐，是选不出多数派
     */
    private static final Map<String, String> QUORUM_COMPONENTS = new HashMap<>();

    /**
     * 最多两个实例的组件，多了没有意义
     */
    private static final Map<String, String> AT_MOST_TWO = new HashMap<>();

    /**
     * HDFS 默认三副本，DataNode 少于这个数写入会失败
     */
    private static final int DEFAULT_HDFS_REPLICATION = 3;

    /**
     * JournalNode 组成 HA 编辑日志的最小法定人数
     */
    private static final int MIN_JOURNAL_NODE = 3;

    static {
        QUORUM_COMPONENTS.put("QuorumPeerMain", "ZooKeeper");
        QUORUM_COMPONENTS.put("JournalNode", "HDFS");

        AT_MOST_TWO.put("NameNode", "HDFS");
        AT_MOST_TWO.put("ResourceManager", "YARN");
    }

    /**
     * Description: 提交部署计划。校验通过后建集群并落库，返回集群 ID 供页面跳转。
     * <p>
     * 整个过程放在一个事务里：集群建出来了但计划没存下，用户回到页面会看到一个
     * 空集群却不知道原本打算部署什么。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 部署计划
     * @return Result<AbstractAiPlanVo.PlanSubmitVo> 集群 ID 与计划 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<AbstractAiPlanVo.PlanSubmitVo> submitPlan(
            AbstractAiPlanRequest.PlanSubmitRequest request) {

        List<String> errors = this.validatePlan(request);
        if (CollUtil.isNotEmpty(errors)) {
            // 校验信息直接给用户看，所以要说清楚哪里不对、为什么不行
            throw new BException(
                    String.format(
                            "部署计划校验不通过：%s",
                            String.join("；", errors)
                    )
            );
        }

        // 复用页面上创建集群走的同一个接口。计划确认不该有一条独立的建集群路径，
        // 否则两边的校验和状态流转迟早会走偏
        AbstractClusterRequest.NewClusterRequest newClusterRequest =
                new AbstractClusterRequest.NewClusterRequest()
                        .setClusterName(request.getClusterName())
                        .setClusterTypeEnum(ClusterTypeEnum.valueOf(request.getClusterType()))
                        .setDlcVersion(request.getDlcVersion())
                        .setClusterDesc(request.getDescription());

        Long clusterId = this.masterClusterService
                .newCluster(newClusterRequest)
                .getData()
                .getClusterId();

        TDlAiDeployPlan plan = new TDlAiDeployPlan()
                .setClusterId(clusterId)
                .setClusterName(request.getClusterName())
                .setSessionId(request.getSessionId())
                .setUserId(StpUtil.getLoginIdAsLong())
                .setPlanState("CONFIRMED")
                // 原样存 AI 产出的完整计划。日后复盘「当初为什么这么排」要靠它
                .setPlanContent(JSONUtil.toJsonStr(request))
                .setDescription(request.getDescription());

        boolean saved = this.tDlAiDeployPlanService.save(plan);
        Assert.isTrue(saved, () -> new BException("保存部署计划失败"));

        log.info(
                "部署计划已确认, clusterId: {}, planId: {}, 节点数: {}, 组件数: {}",
                clusterId,
                plan.getId(),
                request.getNodeList().size(),
                request.getComponentList().size()
        );

        return Result.success(
                new AbstractAiPlanVo.PlanSubmitVo(
                        clusterId,
                        plan.getId()
                )
        );
    }

    /**
     * Description: 校验计划。与 AIAgent 侧同一套规则，这里是权威的一份。
     * <p>
     * 拦的是「格式对但跑不起来」的拓扑：ZooKeeper 给偶数个选不出主、
     * NameNode 配了 HA 却没有 JournalNode、组件挂在计划里不存在的主机上。
     * 这类问题部署到一半才暴露，代价是整个集群回滚重来。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2026/9/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 部署计划
     * @return List<String> 全部问题。一次性返回，不要让用户改一个报一个
     */
    private List<String> validatePlan(AbstractAiPlanRequest.PlanSubmitRequest request) {
        List<String> errors = new ArrayList<>();

        Set<String> hostnames = this.validateNodes(request.getNodeList(), errors);
        Map<String, List<String>> placement = this.validateComponents(
                request.getComponentList(),
                hostnames,
                errors
        );
        this.validateTopology(placement, errors);

        return errors;
    }

    /**
     * Description: 节点清单自身的一致性。主机名与 IP 都不能重复
     */
    private Set<String> validateNodes(List<AbstractAiPlanRequest.PlanNodeRequest> nodes,
                                      List<String> errors) {
        Set<String> hostnames = new HashSet<>();
        Set<String> ips = new HashSet<>();

        for (AbstractAiPlanRequest.PlanNodeRequest node : nodes) {
            String hostname = StrUtil.trimToEmpty(node.getHostname());
            String ip = StrUtil.trimToEmpty(node.getNodeIp());

            if (StrUtil.isBlank(hostname)) {
                errors.add("存在缺少主机名的节点");
                continue;
            }
            if (StrUtil.isBlank(ip)) {
                errors.add(String.format("节点 %s 缺少 IP", hostname));
            }

            if (!hostnames.add(hostname)) {
                errors.add(String.format("主机名重复: %s", hostname));
            }
            if (StrUtil.isNotBlank(ip) && !ips.add(ip)) {
                errors.add(String.format("IP 重复: %s", ip));
            }

            Long sshPort = node.getSshPort();
            if (sshPort != null && (sshPort < 1 || sshPort > 65535)) {
                errors.add(String.format("节点 %s 的 SSH 端口超出范围: %d", hostname, sshPort));
            }
        }

        return hostnames;
    }

    /**
     * Description: 组件放置。挂到不存在的主机上是最常见的错误
     */
    private Map<String, List<String>> validateComponents(
            List<AbstractAiPlanRequest.PlanComponentRequest> components,
            Set<String> hostnames,
            List<String> errors) {

        Map<String, List<String>> placement = new HashMap<>();

        for (AbstractAiPlanRequest.PlanComponentRequest component : components) {
            String serviceName = StrUtil.trimToEmpty(component.getServiceName()).toUpperCase();
            String componentName = StrUtil.trimToEmpty(component.getComponentName());

            if (StrUtil.isBlank(serviceName) || StrUtil.isBlank(componentName)) {
                errors.add("存在缺少服务名或组件名的组件");
                continue;
            }

            List<String> cleaned = new ArrayList<>();
            for (String host : component.getHostnames()) {
                String hostname = StrUtil.trimToEmpty(host);
                if (!hostnames.contains(hostname)) {
                    errors.add(String.format(
                            "%s/%s 指定的主机 %s 不在节点清单里",
                            serviceName,
                            componentName,
                            hostname
                    ));
                    continue;
                }
                if (cleaned.contains(hostname)) {
                    errors.add(String.format(
                            "%s/%s 在主机 %s 上重复部署",
                            serviceName,
                            componentName,
                            hostname
                    ));
                    continue;
                }
                cleaned.add(hostname);
            }

            if (placement.containsKey(componentName)) {
                errors.add(String.format("组件 %s 出现了多次，请合并到一条记录里", componentName));
            }
            placement.put(componentName, cleaned);
        }

        return placement;
    }

    /**
     * Description: 拓扑约束。格式对但拓扑错的计划能一路跑到部署中途才失败
     */
    private void validateTopology(Map<String, List<String>> placement, List<String> errors) {
        QUORUM_COMPONENTS.forEach((componentName, serviceName) -> {
            List<String> hosts = placement.get(componentName);
            if (CollUtil.isEmpty(hosts)) {
                return;
            }
            if (hosts.size() % 2 == 0) {
                errors.add(String.format(
                        "%s 的 %s 配了 %d 个实例。这类组件靠多数派选主，偶数个选不出来，请用奇数个",
                        serviceName,
                        componentName,
                        hosts.size()
                ));
            }
        });

        AT_MOST_TWO.forEach((componentName, serviceName) -> {
            List<String> hosts = placement.get(componentName);
            if (CollUtil.isNotEmpty(hosts) && hosts.size() > 2) {
                errors.add(String.format(
                        "%s 的 %s 配了 %d 个，最多支持 2 个（主备）",
                        serviceName,
                        componentName,
                        hosts.size()
                ));
            }
        });

        List<String> nameNodes = placement.getOrDefault("NameNode", new ArrayList<>());
        List<String> journalNodes = placement.getOrDefault("JournalNode", new ArrayList<>());
        List<String> zookeepers = placement.getOrDefault("QuorumPeerMain", new ArrayList<>());

        if (nameNodes.size() >= 2) {
            if (CollUtil.isEmpty(journalNodes)) {
                errors.add("配了 2 个 NameNode 但没有 JournalNode。HDFS 的 HA 靠 JournalNode 同步编辑日志，缺了起不来");
            } else if (journalNodes.size() < MIN_JOURNAL_NODE) {
                errors.add(String.format(
                        "NameNode 做了 HA，但 JournalNode 只有 %d 个，至少要 %d 个",
                        journalNodes.size(),
                        MIN_JOURNAL_NODE
                ));
            }

            if (CollUtil.isEmpty(zookeepers)) {
                errors.add("NameNode 做了 HA 但没有部署 ZooKeeper。自动故障转移依赖 ZooKeeper 做选主");
            }
        }

        List<String> nodeManagers = placement.getOrDefault("NodeManager", new ArrayList<>());
        List<String> resourceManagers = placement.getOrDefault("ResourceManager", new ArrayList<>());
        if (CollUtil.isNotEmpty(nodeManagers) && CollUtil.isEmpty(resourceManagers)) {
            errors.add("部署了 NodeManager 但没有 ResourceManager，YARN 无法调度");
        }

        List<String> dataNodes = placement.getOrDefault("DataNode", new ArrayList<>());
        if (CollUtil.isNotEmpty(dataNodes) && dataNodes.size() < DEFAULT_HDFS_REPLICATION) {
            // 这条是提醒不是错误，副本数可以调，所以不进 errors
            log.warn(
                    "计划中只有 {} 个 DataNode，少于默认副本数 {}，需要同时调整 dfs.replication",
                    dataNodes.size(),
                    DEFAULT_HDFS_REPLICATION
            );
        }
    }
}
