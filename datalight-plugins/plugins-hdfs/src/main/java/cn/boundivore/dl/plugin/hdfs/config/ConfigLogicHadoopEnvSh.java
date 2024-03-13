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

import java.io.File;

import static cn.boundivore.dl.plugin.hdfs.config.ConfigLogicJmxYaml.SERVICE_NAME;

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
public class ConfigLogicHadoopEnvSh extends AbstractConfigLogic {

    public ConfigLogicHadoopEnvSh(PluginConfig pluginConfig) {
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
                        PortConstants.getRemotePort(
                                SERVICE_NAME,
                                "JournalNode"
                        )
                )
                .replace(
                        "{{jmxExporterPort_JournalNode}}",
                        PortConstants.getExporterPort(
                                SERVICE_NAME,
                                "JournalNode"
                        )
                )
                // NameNode
                .replace(
                        "{{jmxRemotePort_NameNode}}",
                        PortConstants.getRemotePort(
                                SERVICE_NAME,
                                "NameNode"
                        )
                )
                .replace(
                        "{{jmxExporterPort_NameNode}}",
                        PortConstants.getExporterPort(
                                SERVICE_NAME,
                                "NameNode"
                        )
                )
                // ZKFailoverController
                .replace(
                        "{{jmxRemotePort_ZKFailoverController}}",
                        PortConstants.getRemotePort(
                                SERVICE_NAME,
                                "ZKFailoverController"
                        )
                )
                .replace(
                        "{{jmxExporterPort_ZKFailoverController}}",
                        PortConstants.getExporterPort(
                                SERVICE_NAME,
                                "ZKFailoverController"
                        )
                )
                // DataNode
                .replace(
                        "{{jmxRemotePort_DataNode}}",
                        PortConstants.getRemotePort(
                                SERVICE_NAME,
                                "DataNode"
                        )
                )
                .replace(
                        "{{jmxExporterPort_DataNode}}",
                        PortConstants.getExporterPort(
                                SERVICE_NAME,
                                "DataNode"
                        )
                )
                // HttpFS
                .replace(
                        "{{jmxRemotePort_HttpFS}}",
                        PortConstants.getRemotePort(
                                SERVICE_NAME,
                                "HttpFS"
                        )
                )
                .replace(
                        "{{jmxExporterPort_HttpFS}}",
                        PortConstants.getExporterPort(
                                SERVICE_NAME,
                                "HttpFS"
                        )
                )
                ;
    }

}
