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
package cn.boundivore.dl.plugin.zkui.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

import static cn.boundivore.dl.plugin.zkui.config.ConfigLogicJmxYaml.SERVICE_NAME;

/**
 * Description: 配置 log4j.properties 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicLog4jProperties extends AbstractConfigLogic {

    public ConfigLogicLog4jProperties(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{zkui.log.dir}}
        String zkuiLogDir = this.zkuiLogDir();

        return replacedTemplated
                .replace(
                        "{{zkui.log.dir}}",
                        zkuiLogDir
                )
                ;
    }

    /**
     * Description: 获取 ZKUI 的日志存储目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String zkuiLogDir() {
        return String.format(
                "%s/%s",
                super.logDir(),
                SERVICE_NAME
        );
    }


}
