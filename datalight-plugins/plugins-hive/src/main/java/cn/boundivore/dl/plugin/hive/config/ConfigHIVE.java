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
package cn.boundivore.dl.plugin.hive.config;

import cn.boundivore.dl.plugin.base.config.AbstractConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Description: 参考父类中的注释
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023-04-25
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class ConfigHIVE extends AbstractConfig {
    /**
     * Description: 根据配置文件执行不同的配置修改逻辑
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param file             当前某个配置文件
     * @param replacedTemplate 当前配置文件内容
     * @return String 修改后的最终配置文件内容
     */
    @Override
    public String configLogic(File file, String replacedTemplate) {
        switch (file.getName()) {
            case "hive-log4j2.properties":
                return new ConfigLogicHiveLog4j2Properties(super.pluginConfig).config(file, replacedTemplate);
            case "hive-exec-log4j2.properties":
                return new ConfigLogicHiveExecLog4jProperties(super.pluginConfig).config(file, replacedTemplate);
            case "hive-site.xml":
                return new ConfigLogicHiveSite(super.pluginConfig).config(file, replacedTemplate);
            case "hive-env.sh":
                return new ConfigLogicHiveEnv(super.pluginConfig).config(file, replacedTemplate);
            case "catalina.sh":
                return new ConfigLogicCatalinaSh(super.pluginConfig).config(file, replacedTemplate);
            case "configs.env":
                return new ConfigLogicConfigsEnv(super.pluginConfig).config(file, replacedTemplate);
            case "server.xml":
                return new ConfigLogicServerXml(super.pluginConfig).config(file, replacedTemplate);
            case "logging.properties":
                return new ConfigLogicLoggingProperties(super.pluginConfig).config(file, replacedTemplate);
            case ConfigLogicJmxYaml.JMX_CONFIG_FILE_MetaStore:
            case ConfigLogicJmxYaml.JMX_CONFIG_FILE_HiveServer2:
            case ConfigLogicJmxYaml.JMX_CONFIG_FILE_TezUI:
                return new ConfigLogicJmxYaml(super.pluginConfig).config(file, replacedTemplate);
            default:
                if (log.isDebugEnabled()) {
                    log.debug("无处理文件: {}", file.getName());
                }
                return replacedTemplate;
        }
    }
}
