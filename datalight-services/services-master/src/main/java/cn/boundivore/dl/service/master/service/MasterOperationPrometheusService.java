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

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.master.ConfigSaveByGroupRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractServiceComponentVo;
import cn.boundivore.dl.base.response.impl.master.ConfigListByGroupVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.utils.ComponentUtil;
import cn.boundivore.dl.base.utils.YamlDeserializer;
import cn.boundivore.dl.base.utils.YamlSerializer;
import cn.boundivore.dl.boot.lock.LocalLock;
import cn.boundivore.dl.plugin.base.bean.config.YamlPrometheusConfig;
import cn.boundivore.dl.service.master.handler.RemoteInvokePrometheusHandler;
import cn.boundivore.dl.service.master.resolver.ResolverYamlDirectory;
import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 检查、重置普罗米修斯配置
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
public class MasterOperationPrometheusService {

    private static final String PROMETHEUS_CONFIG_PATH = "MONITOR/prometheus/prometheus.yml";

    private final MasterConfigService masterConfigService;

    private final MasterConfigSyncService masterConfigSyncService;

    private final MasterComponentService masterComponentService;

    private final RemoteInvokePrometheusHandler remoteInvokePrometheusHandler;


    /**
     * Description: 修改 Prometheus 配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return 修改后的内容
     */
    @LocalLock
    public Result<String> resetPrometheusConfig(Long clusterId) throws Exception {

        // Map<ServiceName-ComponentName, List < Hostname:ExporterPort>>
        final Map<String, List<String>> prometheusComponentTargetsMap = new LinkedHashMap<>();

        // 获取指定集群下的服务组件信息
        List<AbstractServiceComponentVo.ServiceComponentSummaryVo> serviceComponentSummaryList = this.masterComponentService
                .getComponentList(clusterId)
                .getData()
                .getServiceComponentSummaryList();

        serviceComponentSummaryList
                .stream()
                .filter(service -> {
                    // 容错：过滤不合法的服务状态
                    SCStateEnum serviceState = service.getServiceSummaryVo().getScStateEnum();
                    return serviceState != SCStateEnum.REMOVED
                            && serviceState != SCStateEnum.UNSELECTED;
                })
                // 遍历每一个服务
                .forEach(service -> {
                    service.getComponentSummaryList()
                            .stream()
                            // 过滤包含 Client 的组件
                            .filter(component -> !component.getComponentName().contains("Client"))
                            // 遍历每一个组件
                            .forEach(component -> {
                                final String serviceName = service.getServiceSummaryVo().getServiceName();
                                final String componentName = component.getComponentName();

                                // 去除 ComponentName 末尾的数字
                                String clipComponentName = ComponentUtil.clipComponentName(componentName);
                                String jobName = String.format(
                                        "%s-%s",
                                        serviceName,
                                        clipComponentName
                                );

                                // List <Hostname:ExporterPort>
                                final List<String> hostnameExporterPortList = prometheusComponentTargetsMap.computeIfAbsent(
                                        jobName,
                                        k -> new ArrayList<>()
                                );

                                component.getComponentNodeList()
                                        .stream()
                                        .filter(componentNode -> {
                                            // 容错：过滤某些节点上不合法的组件状态
                                            SCStateEnum componentState = componentNode.getScStateEnum();
                                            return componentState != SCStateEnum.REMOVED
                                                    && componentState != SCStateEnum.UNSELECTED;
                                        })
                                        // 遍历每一个组件在节点上的信息
                                        .forEach(componentNodeVo -> {
                                            // 获取当前组件的 Exporter 端口号
                                            String curComponentExporterPort = PortConstants.getExporterPort(
                                                    serviceName,
                                                    clipComponentName
                                            );

                                            // 如果存在 Exporter 配置，则开始修改
                                            if (curComponentExporterPort != null) {
                                                // List <Hostname:ExporterPort>
                                                hostnameExporterPortList.add(
                                                        String.format(
                                                                "%s:%s",
                                                                componentNodeVo.getHostname(),
                                                                curComponentExporterPort
                                                        )
                                                );

                                            }
                                        });

                                hostnameExporterPortList.sort(Comparator.comparing(o -> o.split(":")[0]));
                            });
                });

        this.getYamlPrometheusConfig(
                clusterId,
                prometheusComponentTargetsMap
        );

        return Result.success();
    }


    /**
     * Description: 获取并更新 Prometheus 配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: Exception
     *
     * @param clusterId                     集群 ID
     * @param prometheusComponentTargetsMap Prometheus 组件与 Targets 信息映射
     */
    private void getYamlPrometheusConfig(Long clusterId, Map<String, List<String>> prometheusComponentTargetsMap) throws Exception {

        String prometheusYmlPath = String.format("%s/%s",
                ResolverYamlDirectory.DIRECTORY_YAML.getDatalight().getServiceDir(),
                PROMETHEUS_CONFIG_PATH
        );

        ConfigListByGroupVo configListByGroup = this.masterConfigService.getConfigListByGroup(
                clusterId,
                "MONITOR",
                prometheusYmlPath
        ).getData();

        for (ConfigListByGroupVo.ConfigGroupVo configGroup : configListByGroup.getConfigGroupList()) {
            YamlPrometheusConfig yamlPrometheusConfig = YamlSerializer.toObject(
                    Base64.decodeStr(
                            configGroup.getConfigData(),
                            StandardCharsets.UTF_8
                    ),
                    YamlPrometheusConfig.class
            );

            // 获取并修改发生变更的 ScrapeConfigList
            yamlPrometheusConfig
                    .getScrapeConfigs()
                    .forEach(m -> {
                                List<String> hostPortList = prometheusComponentTargetsMap.get(m.getJobName());
                                if (hostPortList != null) {
                                    // 更新 targets
                                    m.getStaticConfigs().get(0).setTargets(hostPortList);
                                }
                            }
                    );

            String newConfigData = Base64.encode(
                    YamlDeserializer.toString(
                            yamlPrometheusConfig
                    ),
                    StandardCharsets.UTF_8
            );

            String sha256 = SecureUtil.sha256(newConfigData);
            configGroup.setConfigData(newConfigData);
            configGroup.setSha256(sha256);

        }

        ConfigSaveByGroupRequest configSaveByGroupRequest = new ConfigSaveByGroupRequest();
        configSaveByGroupRequest.setClusterId(configListByGroup.getClusterId());
        configSaveByGroupRequest.setServiceName(configListByGroup.getServiceName());
        configSaveByGroupRequest.setConfigGroupList(
                configListByGroup.getConfigGroupList()
                        .stream()
                        .map(configGroupVo -> new ConfigSaveByGroupRequest.ConfigGroupRequest(
                                configGroupVo.getSha256(),
                                configGroupVo.getFilename(),
                                configGroupVo.getConfigPath(),
                                configGroupVo.getConfigData(),
                                configGroupVo.getConfigNodeList()
                                        .stream()
                                        .map(configNodeVo -> new ConfigSaveByGroupRequest.ConfigNodeRequest(
                                                configNodeVo.getNodeId(),
                                                configNodeVo.getHostname(),
                                                configNodeVo.getNodeIp(),
                                                configNodeVo.getConfigVersion() + 1
                                        ))
                                        .collect(Collectors.toList())

                        ))
                        .collect(Collectors.toList())
        );


        // 同步阻塞修改配置
        this.masterConfigSyncService.saveConfigByGroupSync(
                configSaveByGroupRequest
        );

        // 重载 Prometheus 配置
        this.remoteInvokePrometheusHandler.invokePrometheusReload(clusterId);
    }
}
