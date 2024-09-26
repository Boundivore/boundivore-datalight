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
package cn.boundivore.dl.plugin.kafka.config.event;

import cn.boundivore.dl.plugin.base.bean.PluginConfigEvent;
import cn.boundivore.dl.plugin.base.bean.PluginConfigResult;
import cn.boundivore.dl.plugin.base.bean.PluginConfigSelf;
import cn.boundivore.dl.plugin.base.config.event.AbstractConfigEventHandler;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: KAFKA 关联的服务发生变动时，检查是否需要联动修改自身的配置文件
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

    private static final String INSTALL_PROPERTIES_PATH = "KAFKA/ranger-kafka-plugin/install.properties";

    @Override
    public List<String> getRelativeConfigPathList(List<String> configPathList) {
        // 根据服务变动的内容返回需要修改的文件文件绝对路径
        List<String> relativeConfigPathList = super.getRelativeConfigPathList(configPathList);
        relativeConfigPathList.addAll(
                configPathList.stream()
                        .filter(i -> i.contains(INSTALL_PROPERTIES_PATH))
                        .collect(Collectors.toList())
        );

        return relativeConfigPathList;
    }

    @Override
    public PluginConfigResult configByEvent(PluginConfigSelf pluginConfigSelf) {
        PluginConfigResult pluginConfigResult = super.configByEvent(pluginConfigSelf);

        final LinkedHashMap<PluginConfigResult.ConfigKey, PluginConfigResult.ConfigValue> resultMap = pluginConfigResult.getConfigMap();

        pluginConfigSelf.getConfigSelfDataList()
                .forEach(i -> {
                            if (i.getConfigPath().contains(INSTALL_PROPERTIES_PATH)) {
                                resultMap.putAll(
                                        this.getInstallPropertiesConfigKeyValue(
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
     * Creation time: 2024/9/24
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param pluginConfigEvent 配置发生变更的时间（包括服务下的所有配置文件信息）
     * @param pluginConfigSelf  当前服务的 getRelativeConfigPathList 指定的配置文件
     * @return 修改后的最终结果（如无变更，则 configMap 依然为空）
     */
    private LinkedHashMap<PluginConfigResult.ConfigKey, PluginConfigResult.ConfigValue> getInstallPropertiesConfigKeyValue(
            final PluginConfigEvent pluginConfigEvent,
            final PluginConfigSelf pluginConfigSelf) {

        LinkedHashMap<PluginConfigResult.ConfigKey, PluginConfigResult.ConfigValue> configKeyValueMap = new LinkedHashMap<>();

        // 遍历当前服务的所有配置文件，查找并处理 INSTALL_PROPERTIES_PATH 配置文件
        pluginConfigSelf.getConfigSelfDataList()
                .stream()
                .filter(i -> i.getConfigPath().contains(INSTALL_PROPERTIES_PATH))
                .forEach(i -> {
                    try {
                        // 解码 Base64 编码的配置数据
                        String deBase64ConfigData = super.deBase64(i.getConfigData());

                        // 将配置数据按行拆分，并保留 Properties 中的注释
                        List<String> lines = new ArrayList<>(Arrays.asList(deBase64ConfigData.split("\\r?\\n")));

                        // 读取当前的 POLICY_MGR_URL 值
                        String policyMgrUrl = null;
                        for (String line : lines) {
                            String trimLine = line.trim();

                            // 跳过注释和空行
                            if (trimLine.startsWith("#") || trimLine.startsWith("!") || trimLine.isEmpty()) {
                                continue;
                            }

                            int equalPos = trimLine.indexOf('=');
                            if (equalPos > 0) {
                                String key = trimLine.substring(0, equalPos).trim();
                                String value = trimLine.substring(equalPos + 1).trim();
                                if ("POLICY_MGR_URL".equals(key)) {
                                    policyMgrUrl = value;
                                    break;
                                }
                            }
                        }

                        // 获取新的 Ranger Admin 主机名
                        String rangerAdminNewHostname = this.getRangerAdminHostname(pluginConfigEvent);
                        if (StrUtil.isBlank(rangerAdminNewHostname)) {
                            rangerAdminNewHostname = "localhost";
                        }

                        String policyMgrUrlNew = String.format("http://%s:6080", rangerAdminNewHostname);

                        if (!Objects.equals(policyMgrUrl, policyMgrUrlNew)) {
                            // 修改 POLICY_MGR_URL 的值
                            boolean keyFound = false;
                            for (int n = 0; n < lines.size(); n++) {
                                String line = lines.get(n);
                                String trimLine = line.trim();

                                // 跳过注释和空行
                                if (trimLine.startsWith("#") || trimLine.startsWith("!") || trimLine.isEmpty()) {
                                    continue;
                                }

                                int equalPos = trimLine.indexOf('=');
                                if (equalPos > 0) {
                                    String key = line.substring(0, equalPos).trim();
                                    if ("POLICY_MGR_URL".equals(key)) {
                                        // 替换值
                                        String newLine = key + "=" + policyMgrUrlNew;
                                        lines.set(n, newLine);
                                        keyFound = true;
                                        break;
                                    }
                                }
                            }

                            // 如果未找到 POLICY_MGR_URL，则在文件末尾添加
                            if (!keyFound) {
                                lines.add("POLICY_MGR_URL=" + policyMgrUrlNew);
                            }

                            // 将修改后的配置序列化为 String，同时保留注释
                            String newConfigDataString = String.join("\n", lines);

                            // 对新的配置数据进行 Base64 编码
                            String newConfigData = super.base64(newConfigDataString);
                            String sha256 =  super.sha256(newConfigData);

                            // 更新配置数据和校验和
                            i.setConfigData(newConfigData);
                            i.setSha256(sha256);

                            // 更新结果集合
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

                    } catch (Exception e) {
                        throw new RuntimeException("解析 Properties 文件出错：" + e.getMessage(), e);
                    }
                });

        return configKeyValueMap;

    }

    /**
     * Description: 获取 Ranger Admin 节点主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param pluginConfigEvent 发生变动的服务下的配置文件信息
     * @return Ranger Admin 节点主机名
     */
    private String getRangerAdminHostname(final PluginConfigEvent pluginConfigEvent) {
        List<PluginConfigEvent.ConfigEventComponentNode> rangerAdmin = pluginConfigEvent
                .getConfigEventComponentMap()
                .get("RangerAdmin");

        if (rangerAdmin == null || rangerAdmin.isEmpty()) return "";

        return rangerAdmin.get(0).getHostname();
    }
}
