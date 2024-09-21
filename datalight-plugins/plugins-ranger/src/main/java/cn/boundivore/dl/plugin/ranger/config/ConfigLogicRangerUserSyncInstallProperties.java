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

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.Map;

/**
 * Description: 配置 Ranger UserSync install.properties 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/9/20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicRangerUserSyncInstallProperties extends AbstractConfigLogic {

    public ConfigLogicRangerUserSyncInstallProperties(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{ranger_admin_hostname}}
        String rangerAdminHostname = this.rangerAdminHostname();

        // {{rangerUsersync_password}}
        String rangerUsersyncPassword = this.rangerUsersyncPassword();

        // {{SERVICE_DIR}}
        String serviceDir = super.serviceDir();

        // {{PID_DIR}}
        String pidDir = super.pidDir();

        // {{LOG_DIR}}
        String logDir = super.logDir();


        return replacedTemplated
                .replace(
                        "{{ranger_admin_hostname}}",
                        rangerAdminHostname
                )
                .replace(
                        "{{rangerUsersync_password}}",
                        rangerUsersyncPassword
                )
                .replace(
                        "{{SERVICE_DIR}}",
                        serviceDir
                )
                .replace(
                        "{{PID_DIR}}",
                        pidDir
                )
                .replace(
                        "{{LOG_DIR}}",
                        logDir
                )
                ;
    }

    /**
     * Description: Ranger UserSync Password
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Ranger UserSync Password
     */
    private String rangerUsersyncPassword() {
        return "Datalight123!";
    }

    /**
     * Description: Ranger Admin 所在节点主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Ranger Admin 所在节点主机名
     */
    private String rangerAdminHostname() {
        return super.currentMetaService
                .getMetaComponentMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().contains("RangerAdmin"))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(PluginConfig.MetaComponent::getHostname)
                .orElseThrow(() -> new RuntimeException("未找到处于 STARTED 状态的 RangerAdmin"));
    }
}
