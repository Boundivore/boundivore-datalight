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
package cn.boundivore.dl.plugin.dolphinscheduler.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.Comparator;

/**
 * Description: 配置 bin/dolphinscheduler_env.sh 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/10/25
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicDolphinSchedulerEnvSh extends AbstractConfigLogic {

    public ConfigLogicDolphinSchedulerEnvSh(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{db_root_user}}
        String dbRootUser = this.dbRootUser();

        // {{db_root_password}}
        String dbRootPassword = this.dbRootPassword();

        // {{db_name}}
        String dbName = this.dbName();

        // {{db_host}}
        String dbHost = this.dbHost();

        // {{db_port}}
        String dbPort = this.dbPort();

        // {{REGISTRY_ZOOKEEPER_CONNECT_STRING}}
        String registryZookeeperConnectString = this.registryZookeeperConnectString();



        return replacedTemplated
                .replace(
                        "{{db_root_user}}",
                        dbRootUser
                )
                .replace(
                        "{{db_root_password}}",
                        dbRootPassword
                )
                .replace(
                        "{{db_name}}",
                        dbName
                )
                .replace(
                        "{{db_host}}",
                        dbHost
                )
                .replace(
                        "{{db_port}}",
                        dbPort
                )
                .replace(
                        "{{REGISTRY_ZOOKEEPER_CONNECT_STRING}}",
                        registryZookeeperConnectString
                )
                ;
    }

    /**
     * Description: 获取 {{REGISTRY_ZOOKEEPER_CONNECT_STRING}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/10/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{REGISTRY_ZOOKEEPER_CONNECT_STRING}} 真实值
     */
    private String registryZookeeperConnectString() {
        PluginConfig.MetaService zookeeperMetaService = super.pluginConfig
                .getMetaServiceMap()
                .get("ZOOKEEPER");

        StringBuilder sb = new StringBuilder();

        zookeeperMetaService.getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("QuarumPeermain"))
                .sorted(Comparator.comparing(PluginConfig.MetaComponent::getHostname))
                .forEach(c -> sb.append(c.getHostname()).append(":2181,"));

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * Description: DolphinScheduler 页面登录密码 （此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time:2024/10/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String "Datalight123!"
     */
    private String rangerAdminPassword() {
        return "Datalight123!";
    }

    /**
     * Description: 数据库端口号（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/10/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 3306
     */
    private String dbPort() {
        return "3306";
    }

    /**
     * Description: 数据库主机名或 IP（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/10/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String localhost
     */
    private String dbHost() {
        return "localhost";
    }

    /**
     * Description: DolphinScheduler 数据库名称 db_ranger
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/10/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String db_ranger
     */
    private String dbName() {
        return "db_dolphinscheduler";
    }

    /**
     * Description: 数据库 root 密码（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/10/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 如果没有配置，则默认返回 1qaz!QAZ
     */
    private String dbRootPassword() {
        return "1qaz!QAZ";
    }

    /**
     * Description: 数据库 root 用户名（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/10/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 如果没有配置，则默认返回 root
     */
    private String dbRootUser() {
        return "root";
    }

}
