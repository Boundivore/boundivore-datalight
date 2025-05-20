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

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

/**
 * Description: 配置 application-mysql.yml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2025/05/19
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicDinkyApplicationYml extends AbstractConfigLogic {

    public ConfigLogicDinkyApplicationYml(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        String mysqlHost = this.mysqlHost();
        String mysqlPort = this.mysqlPort();
        String mysqlUsername = this.mysqlUserName();
        String mysqlPassword = this.mysqlPassword();


        return replacedTemplated
                .replace(
                        "{{mysql.host}}",
                        mysqlHost
                )
                .replace(
                        "{{mysql.port}}",
                        mysqlPort
                )
                .replace(
                        "{{mysql.username}}",
                        mysqlUsername
                )
                .replace(
                        "{{mysql.password}}",
                        mysqlPassword
                )
                ;
    }

    /**
     * Description: 获取 Dinky 元数据存储库的主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2025/05/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String mysqlHost() {
        return super.pluginConfig.getMysqlEnv().getDbHost();
    }


    /**
     * Description: 获取 Dinky 元数据存储库的端口号
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2025/05/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String mysqlPort() {
        return super.pluginConfig.getMysqlEnv().getDbPort();
    }


    /**
     * Description: 获取 Dinky 元数据存储库的用户名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2025/05/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String mysqlUserName() {
        return super.pluginConfig.getMysqlEnv().getDbUser();
    }


    /**
     * Description: 获取 Dinky 元数据存储库的密码
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2025/05/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String mysqlPassword() {
        return super.pluginConfig.getMysqlEnv().getDbPassword();
    }

}
