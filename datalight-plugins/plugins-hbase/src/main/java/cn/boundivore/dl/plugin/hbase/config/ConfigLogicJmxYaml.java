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
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Description: 配置 jmx_config_*.yaml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/25
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class ConfigLogicJmxYaml extends AbstractConfigLogic {

    public static final String JMX_CONFIG_FILE_HMaster = "jmx_config_HMaster.yaml";
    public static final String JMX_CONFIG_FILE_HRegionServer = "jmx_config_HRegionServer.yaml";
    public static final String JMX_CONFIG_FILE_HThriftServer = "jmx_config_HThriftServer.yaml";
    public static final String SERVICE_NAME = "HBASE";


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
            case JMX_CONFIG_FILE_HMaster:
                jmxRemotePort = PortConstants.getRemotePort(
                        SERVICE_NAME,
                        "HMaster"
                );
                break;
            case JMX_CONFIG_FILE_HRegionServer:
                jmxRemotePort = PortConstants.getRemotePort(
                        SERVICE_NAME,
                        "HRegionServer"
                );
                break;
            case JMX_CONFIG_FILE_HThriftServer:
                jmxRemotePort = PortConstants.getRemotePort(
                        SERVICE_NAME,
                        "HThriftServer2"
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
