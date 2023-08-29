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
package cn.boundivore.dl.plugin.monitor.config.event;

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.base.utils.ComponentUtil;
import cn.boundivore.dl.base.utils.YamlDeserializer;
import cn.boundivore.dl.base.utils.YamlSerializer;
import cn.boundivore.dl.plugin.base.bean.PluginConfigEvent;
import cn.boundivore.dl.plugin.base.bean.PluginConfigResult;
import cn.boundivore.dl.plugin.base.bean.PluginConfigSelf;
import cn.boundivore.dl.plugin.base.bean.config.YamlPrometheusConfig;
import cn.boundivore.dl.plugin.base.config.event.AbstractConfigEventHandler;
import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: PROMETHEUS 关联的服务发生变动时，检查是否需要联动修改自身的配置文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class ConfigEventHandler extends AbstractConfigEventHandler {

    private static final String PROMETHEUS_CONFIG_PATH = "MONITOR/prometheus/prometheus.yml";

    @Override
    public List<String> getRelativeConfigPathList(List<String> configPathList) {
        // 根据服务变动的内容返回需要修改的文件文件绝对路径
        List<String> relativeConfigPathList = super.getRelativeConfigPathList(configPathList);
        relativeConfigPathList.addAll(
                configPathList.stream()
                        .filter(i -> i.contains(PROMETHEUS_CONFIG_PATH))
                        .collect(Collectors.toList())
        );

        // EASY_TO_FIX: 如果有其他配置文件需要联动（例如除了 PROMETHEUS_CONFIG_PATH )，则在此进行逻辑判断

        return relativeConfigPathList;
    }

    @Override
    public PluginConfigResult configByEvent(PluginConfigSelf pluginConfigSelf) {
        PluginConfigResult pluginConfigResult = super.configByEvent(pluginConfigSelf);

        final LinkedHashMap<PluginConfigResult.ConfigKey, PluginConfigResult.ConfigValue> resultMap = pluginConfigResult.getConfigMap();
        pluginConfigSelf.getConfigSelfDataList()
                .forEach(i -> {
                            if (i.getConfigPath().contains(PROMETHEUS_CONFIG_PATH)) {
                                resultMap.putAll(
                                        this.getPrometheusYmlConfigKeyValue(
                                                super.pluginConfigEvent,
                                                pluginConfigSelf
                                        )
                                );
                            } else {
                                log.info("配置文件变动被主动忽略的文件: {}", i.getConfigPath());
                            }
                        }
                );

        return pluginConfigResult;
    }

    /**
     * Description: 修改配置文件，如果当前自己的配置无变更，则返回空 configMap 集合
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/11
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param pluginConfigEvent 配置发生变更的时间（包括服务下的所有配置文件信息）
     * @param pluginConfigSelf  当前服务的 getRelativeConfigPathList 指定的配置文件
     * @return 修改后的最终结果（如无变更，则 configMap 依然为空）
     */
    private LinkedHashMap<PluginConfigResult.ConfigKey, PluginConfigResult.ConfigValue> getPrometheusYmlConfigKeyValue(
            final PluginConfigEvent pluginConfigEvent,
            final PluginConfigSelf pluginConfigSelf) {

        LinkedHashMap<PluginConfigResult.ConfigKey, PluginConfigResult.ConfigValue> configKeyValueMap = new LinkedHashMap<>();

        // 获取 Map<ServiceName-ComponentName, List<Hostname:ExporterPort>>，其中 List 按主机名排序
        Map<String, List<String>> prometheusComponentTargetsMap = this.getPrometheusComponentTargetsMap(pluginConfigEvent);

        // 遍历当前服务的所有配置文件，查找并处理 Prometheus 配置文件
        pluginConfigSelf.getConfigSelfDataList()
                .stream()
                .filter(i -> i.getConfigPath().contains(PROMETHEUS_CONFIG_PATH))
                .forEach(i -> {
                    try {
                        YamlPrometheusConfig yamlPrometheusConfig = YamlSerializer.toObject(
                                super.deBase64(i.getConfigData()),
                                YamlPrometheusConfig.class
                        );

                        // 获取并修改发生变更的 ScrapeConfigList
                        List<YamlPrometheusConfig.ScrapeConfig> newScrapeConfigList = yamlPrometheusConfig
                                .getScrapeConfigs()
                                .stream()
                                .filter(m -> {
                                    List<String> hostPortList = prometheusComponentTargetsMap.get(m.getJobName());
                                    if (hostPortList == null) {
                                        return false;
                                    }

                                    List<String> targets = m.getStaticConfigs()
                                            .get(0)
                                            .getTargets()
                                            .stream()
                                            .sorted(Comparator.comparing(s -> s.split(":")[0]))
                                            .collect(Collectors.toList());

                                    // 如果 targets 和 hostPortList 不相等，则需要更新 targets
                                    if (!targets.equals(hostPortList)) {
                                        // 更新 targets
                                        m.getStaticConfigs().get(0).setTargets(hostPortList);
                                        return true;
                                    } else {
                                        return false;
                                    }
                                })
                                .collect(Collectors.toList());

                        // 如果自身配置发生变更，则输出变更后的 ConfigKeyValueMap
                        if (!newScrapeConfigList.isEmpty()) {
                            String newConfigData = super.base64(YamlDeserializer.toString(yamlPrometheusConfig));
                            String sha256 = SecureUtil.sha256(newConfigData);
                            i.setConfigData(newConfigData);
                            i.setSha256(sha256);

                            i.getConfigSelfNodeList().forEach(m ->
                                    configKeyValueMap.put(
                                            new PluginConfigResult.ConfigKey(
                                                    m.getNodeId(),
                                                    i.getConfigPath()
                                            ),
                                            new PluginConfigResult.ConfigValue(
                                                    i.getFilename(),
                                                    newConfigData,
                                                    sha256
                                            )
                                    )
                            );
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        return configKeyValueMap;
    }

    /**
     * Description: 获取 Map<ServiceName-ComponentName, List<Hostname:ExporterPort>>，其中 List 按主机名排序
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param pluginConfigEvent 发生变动的服务下的配置文件信息
     * @return Map<ServiceName-ComponentName, List < Hostname:ExporterPort>>
     */
    private Map<String, List<String>> getPrometheusComponentTargetsMap(final PluginConfigEvent pluginConfigEvent) {

        String serviceName = pluginConfigEvent.getServiceName();

        // Map<ServiceName-ComponentName, List < Hostname:ExporterPort>>
        Map<String, List<String>> prometheusComponentTargetsMap = new LinkedHashMap<>();

        pluginConfigEvent.getConfigEventComponentMap()
                .forEach((k, v) -> {
                            // 获取当前组件的 Exporter 端口号
                            String curComponentExporterPort = PortConstants.getMonitorExporterPort(
                                    pluginConfigEvent.getServiceName(),
                                    k
                            );

                            // 过滤 *Client 这样的组件（无常驻进程）
                            if (curComponentExporterPort != null && !curComponentExporterPort.isEmpty()) {
                                // List <Hostname:ExporterPort>
                                List<String> hostnameExporterPortList = v.stream()
                                        .map(i -> String.format(
                                                        "%s:%s",
                                                        i.getHostname(),
                                                        curComponentExporterPort
                                                )
                                        )
                                        .collect(Collectors.toList());

                                String jobName = String.format(
                                        "%s-%s",
                                        serviceName,
                                        ComponentUtil.clipComponentName(k) // 去除 ComponentName 末尾的数字
                                );

                                List<String> targetList = prometheusComponentTargetsMap.get(jobName);
                                if (targetList == null) {
                                    targetList = hostnameExporterPortList;
                                } else {
                                    targetList.addAll(hostnameExporterPortList);
                                }

                                targetList.sort(Comparator.comparing(o -> o.split(":")[0]));

                                prometheusComponentTargetsMap.put(jobName, targetList);
                            }
                        }
                );


        return prometheusComponentTargetsMap;
    }
}
