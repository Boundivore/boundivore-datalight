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
package cn.boundivore.dl.plugin.ranger.config;

import cn.boundivore.dl.plugin.base.config.AbstractConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Description: 参考父类中的注释
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024-09-20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class ConfigRANGER extends AbstractConfig {
    /**
     * Description: 根据配置文件执行不同的配置修改逻辑
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024-05-06
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
            case ConfigLogicJmxYaml.JMX_CONFIG_FILE_RangerAdmin:
            case ConfigLogicJmxYaml.JMX_CONFIG_FILE_RangerUserSync:
                return new ConfigLogicJmxYaml(super.pluginConfig).config(file, replacedTemplate);
        }

        if (file.getAbsolutePath().contains("ranger-admin")) {
            switch (file.getName()) {
                case "ranger-admin-services.sh":
                    return new ConfigLogicRangerAdminServicesSh(super.pluginConfig).config(file, replacedTemplate);
                case "install.properties":
                    return new ConfigLogicRangerAdminInstallProperties(super.pluginConfig).config(file, replacedTemplate);
                case "log4j.properties":
                    return new ConfigLogicRangerAdminAuditsLog4jProperties(super.pluginConfig).config(file, replacedTemplate);
                case "add_ranger_audits_conf_to_zk.sh":
                    return new ConfigLogicAddRangerAuditsConfToZkSh(super.pluginConfig).config(file, replacedTemplate);
                case "create_ranger_audits_collection.sh":
                    return new ConfigLogicCreateRangerAuditsCollectionSh(super.pluginConfig).config(file, replacedTemplate);
            }
        }

        if (file.getAbsolutePath().contains("ranger-usersync")) {
            switch (file.getName()) {
                case "ranger-usersync-services.sh":
                    return new ConfigLogicRangerUserSyncServicesSh(super.pluginConfig).config(file, replacedTemplate);
                case "install.properties":
                    return new ConfigLogicRangerUserSyncInstallProperties(super.pluginConfig).config(file, replacedTemplate);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("无处理文件: {}", file.getName());
        }
        return replacedTemplate;

    }
}
