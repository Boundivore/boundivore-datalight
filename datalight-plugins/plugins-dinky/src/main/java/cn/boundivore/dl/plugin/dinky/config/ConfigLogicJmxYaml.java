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
package cn.boundivore.dl.plugin.dinky.config;

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Description: 配置 jmx_config_*.yaml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2025/05/19
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class ConfigLogicJmxYaml extends AbstractConfigLogic {

    public static final String JMX_CONFIG_FILE_FlinkHistoryServer = "jmx_config_DinkyServer.yaml";

    public static final String SERVICE_NAME = "DINKY";


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
            // DinkyServer
            case JMX_CONFIG_FILE_FlinkHistoryServer:
                jmxRemotePort = PortConstants.getRemotePort(
                        SERVICE_NAME,
                        "DinkyServer"
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
