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

/**
 * Description: 配置 be.conf 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/11/08
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicBEConf extends AbstractConfigLogic {

    public ConfigLogicBEConf(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{LOG_DIR}}
        String logDir = super.logDir();

        // {{priority_networks}}
        String priorityNetworks = this.priorityNetworks();

        // {{storage_root_path}}
        String storageRootPath = this.storageRootPath();


        return replacedTemplated
                .replace(
                        "{{LOG_DIR}}",
                        logDir
                )
                .replace(
                        "{{priority_networks}}",
                        priorityNetworks
                )
                .replace(
                        "{{storage_root_path}}",
                        storageRootPath
                )
                ;
    }

    /**
     * Description: 获取 Doris 数据存储目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/8
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Doris 数据存储目录
     */
    private String storageRootPath() {
        return String.format(
                "%s/doris",
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
