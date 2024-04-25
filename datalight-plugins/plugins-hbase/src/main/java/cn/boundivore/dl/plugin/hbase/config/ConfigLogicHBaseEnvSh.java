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
package cn.boundivore.dl.plugin.hbase.config;

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

/**
 * Description: 配置 hbase-env.sh 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/25
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicHBaseEnvSh extends AbstractConfigLogic {

    public ConfigLogicHBaseEnvSh(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        return replacedTemplated
                // HMaster
                .replace(
                        "{{jmxRemotePort_HMaster}}",
                        PortConstants.getRemotePort(
                                ConfigLogicJmxYaml.SERVICE_NAME,
                                "JournalNode"
                        )
                )
                .replace(
                        "{{jmxExporterPort_HMaster}}",
                        PortConstants.getExporterPort(
                                ConfigLogicJmxYaml.SERVICE_NAME,
                                "JournalNode"
                        )
                )
                // HRegionServer
                .replace(
                        "{{jmxRemotePort_HRegionServer}}",
                        PortConstants.getRemotePort(
                                ConfigLogicJmxYaml.SERVICE_NAME,
                                "HRegionServer"
                        )
                )
                .replace(
                        "{{jmxExporterPort_HRegionServer}}",
                        PortConstants.getExporterPort(
                                ConfigLogicJmxYaml.SERVICE_NAME,
                                "HRegionServer"
                        )
                )
                ;
    }

}
