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
package cn.boundivore.dl.plugin.kyuubi.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;


/**
 * Description: 配置 server.properties 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicLog4j2Xml extends AbstractConfigLogic {

    public ConfigLogicLog4j2Xml(PluginConfig pluginConfig) {
        super(pluginConfig);
    }


    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{logDir}}
        String kyuubiLogDir = this.kyuubiLogDir();

        return replacedTemplated
                .replace(
                        "{{logDir}}",
                        kyuubiLogDir
                )
                ;
    }

    /**
     * Description: 获取 Kyuubi 日志目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Kyuubi 日志目录
     */
    private String kyuubiLogDir() {
        return String.format(
                "%s/KYUUBI",
                super.logDir()
        );
    }


}
