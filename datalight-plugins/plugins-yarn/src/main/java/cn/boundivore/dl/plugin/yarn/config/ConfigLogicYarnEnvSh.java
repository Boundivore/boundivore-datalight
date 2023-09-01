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
 * Description: 配置 yarn-env.sh 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicYarnEnvSh extends AbstractConfigLogic {

    private static final String SERVICE_NAME_YARN = "YARN";

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
                // ResourceManager
                .replace(
                        "{{jmxRemotePort_ResourceManager}}",
                        PortConstants.getMonitorRemotePort(
                                SERVICE_NAME_YARN,
                                "ResourceManager"
                        )
                )
                .replace(
                        "{{jmxExporterPort_ResourceManager}}",
                        PortConstants.getMonitorExporterPort(
                                SERVICE_NAME_YARN,
                                "ResourceManager"
                        )
                )
                // NodeManager
                .replace(
                        "{{jmxRemotePort_NodeManager}}",
                        PortConstants.getMonitorRemotePort(
                                SERVICE_NAME_YARN,
                                "NodeManager"
                        )
                )
                .replace(
                        "{{jmxExporterPort_NodeManager}}",
                        PortConstants.getMonitorExporterPort(
                                SERVICE_NAME_YARN,
                                "NodeManager"
                        )
                )
                // TimelineServer
                .replace(
                        "{{jmxRemotePort_TimelineServer}}",
                        PortConstants.getMonitorRemotePort(
                                SERVICE_NAME_YARN,
                                "TimelineServer"
                        )
                )
                .replace(
                        "{{jmxExporterPort_TimelineServer}}",
                        PortConstants.getMonitorExporterPort(
                                SERVICE_NAME_YARN,
                                "TimelineServer"
                        )
                )
                ;
    }

}
