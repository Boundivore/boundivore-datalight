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
package cn.boundivore.dl.plugin.hdfs.config;

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Description: 配置 jmx_config_*.yaml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class ConfigLogicJmxYaml extends AbstractConfigLogic {

    private static final String JMX_CONFIG_FILE_JournalNode = "jmx_config_JournalNode.yaml";
    private static final String JMX_CONFIG_FILE_NameNode = "jmx_config_NameNode.yaml";
    private static final String JMX_CONFIG_FILE_ZKFailoverController = "jmx_config_ZKFailoverController.yaml";
    private static final String JMX_CONFIG_FILE_DataNode = "jmx_config_DataNode.yaml";
    private static final String JMX_CONFIG_FILE_HttpFS = "jmx_config_HttpFS.yaml";
    private static final String SERVICE_NAME_HDFS = "HDFS";


    public ConfigLogicJmxYaml(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        String jmxRemotePort = "{{jmxRemotePort}}";
        switch (file.getName()) {
            case JMX_CONFIG_FILE_JournalNode:
                jmxRemotePort = PortConstants.getMonitorRemotePort(
                        SERVICE_NAME_HDFS,
                        "JournalNode"
                );
                break;
            case JMX_CONFIG_FILE_NameNode:
                jmxRemotePort = PortConstants.getMonitorRemotePort(
                        SERVICE_NAME_HDFS,
                        "NameNode"
                );
                break;
            case JMX_CONFIG_FILE_ZKFailoverController:
                jmxRemotePort = PortConstants.getMonitorRemotePort(
                        SERVICE_NAME_HDFS,
                        "ZKFailoverController"
                );
                break;
            case JMX_CONFIG_FILE_DataNode:
                jmxRemotePort = PortConstants.getMonitorRemotePort(
                        SERVICE_NAME_HDFS,
                        "DataNode"
                );
                break;
            case JMX_CONFIG_FILE_HttpFS:
                jmxRemotePort = PortConstants.getMonitorRemotePort(
                        SERVICE_NAME_HDFS,
                        "HttpFS"
                );
                break;
            default:
                log.info("");
                break;

        }


        return replacedTemplated
                .replace(
                        "{{jmxRemotePort}}",
                        jmxRemotePort
                );
    }

}
