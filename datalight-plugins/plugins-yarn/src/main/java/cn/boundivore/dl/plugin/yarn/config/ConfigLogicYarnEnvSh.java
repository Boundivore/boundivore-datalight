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
package cn.boundivore.dl.plugin.yarn.config;

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

/**
 * Description: 配置 hadoop-env.sh 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicYarnEnvSh extends AbstractConfigLogic {

    private static final String SERVICE_NAME_HDFS = "HDFS";

    public ConfigLogicYarnEnvSh(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        return replacedTemplated
                // HDFS_DATANODE_SECURE_USER
                .replace(
                        "{{HDFS_DATANODE_SECURE_USER}}",
                        "datalight"
                )
                // JournalNode
                .replace(
                        "{{jmxRemotePort_JournalNode}}",
                        PortConstants.getMonitorRemotePort(
                                SERVICE_NAME_HDFS,
                                "JournalNode"
                        )
                )
                .replace(
                        "{{jmxExporterPort_JournalNode}}",
                        PortConstants.getMonitorExporterPort(
                                SERVICE_NAME_HDFS,
                                "JournalNode"
                        )
                )
                // NameNode
                .replace(
                        "{{jmxRemotePort_NameNode}}",
                        PortConstants.getMonitorRemotePort(
                                SERVICE_NAME_HDFS,
                                "NameNode"
                        )
                )
                .replace(
                        "{{jmxExporterPort_NameNode}}",
                        PortConstants.getMonitorExporterPort(
                                SERVICE_NAME_HDFS,
                                "NameNode"
                        )
                )
                // ZKFailoverController
                .replace(
                        "{{jmxRemotePort_ZKFailoverController}}",
                        PortConstants.getMonitorRemotePort(
                                SERVICE_NAME_HDFS,
                                "ZKFailoverController"
                        )
                )
                .replace(
                        "{{jmxExporterPort_ZKFailoverController}}",
                        PortConstants.getMonitorExporterPort(
                                SERVICE_NAME_HDFS,
                                "ZKFailoverController"
                        )
                )
                // DataNode
                .replace(
                        "{{jmxRemotePort_DataNode}}",
                        PortConstants.getMonitorRemotePort(
                                SERVICE_NAME_HDFS,
                                "DataNode"
                        )
                )
                .replace(
                        "{{jmxExporterPort_DataNode}}",
                        PortConstants.getMonitorExporterPort(
                                SERVICE_NAME_HDFS,
                                "DataNode"
                        )
                )
                // HttpFS
                .replace(
                        "{{jmxRemotePort_HttpFS}}",
                        PortConstants.getMonitorRemotePort(
                                SERVICE_NAME_HDFS,
                                "HttpFS"
                        )
                )
                .replace(
                        "{{jmxExporterPort_HttpFS}}",
                        PortConstants.getMonitorExporterPort(
                                SERVICE_NAME_HDFS,
                                "HttpFS"
                        )
                )
                ;
    }

}
