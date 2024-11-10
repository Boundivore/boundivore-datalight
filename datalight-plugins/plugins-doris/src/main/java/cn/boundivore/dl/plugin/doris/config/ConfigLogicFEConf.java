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
package cn.boundivore.dl.plugin.doris.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

import static cn.boundivore.dl.plugin.doris.config.ConfigDORIS.SERVICE_NAME;

/**
 * Description: 配置 fe.conf 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/11/08
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicFEConf extends AbstractConfigLogic {

    public ConfigLogicFEConf(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{LOG_DIR}}
        String logDir = String.format(
                "%s/%s",
                super.logDir(),
                SERVICE_NAME
        );

        // {{metaDir}}
        String metaDir = this.metaDir();

        // {{priority_networks}}
        String priorityNetworks = this.priorityNetworks();

        // {{JAVA_HOME}}
        String javaHome = this.javaHome();


        return replacedTemplated
                .replace(
                        "{{LOG_DIR}}",
                        logDir
                )
                .replace(
                        "{{meta_dir}}",
                        metaDir
                )
                .replace(
                        "{{priority_networks}}",
                        priorityNetworks
                )
                .replace(
                        "{{JAVA_HOME}}",
                        javaHome
                )
                ;
    }

    /**
     * Description: 获取 Doris 内置的 JavaHome
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Doris 内置的 JavaHome
     */
    private String javaHome() {
        return "/srv/datalight/DORIS/jdk-17";
    }

    /**
     * Description: 获取 Doris 元数据存储目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/8
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Doris 元数据存储目录
     */
    private String metaDir() {
        return String.format(
                "%s/DORIS/doris-meta",
                super.dataDir()
        );
    }

    /**
     * Description: 获取 FE 可用网段
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/8
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return FE 可用网段
     */
    private String priorityNetworks() {
        return super.currentNodeIp;
    }

}
