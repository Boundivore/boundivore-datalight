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
package cn.boundivore.dl.plugin.monitor.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

/**
 * Description: 配置 mysql-datalight.cnf 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicMySQLCnf extends AbstractConfigLogic {


    public ConfigLogicMySQLCnf(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(file);

        return replacedTemplated
                .replace(
                        "{{mysql.host}}",
                        super.pluginConfig.getMysqlEnv().getDbHost()
                )
                .replace(
                        "{{mysql.port}}",
                        super.pluginConfig.getMysqlEnv().getDbPort()
                )
                .replace(
                        "{{mysql.user}}",
                        super.pluginConfig.getMysqlEnv().getDbUser()
                )
                .replace(
                        "{{mysql.password}}",
                        super.pluginConfig.getMysqlEnv().getDbPassword()
                )
                ;
    }

}
