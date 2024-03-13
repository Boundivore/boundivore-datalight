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

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

import static cn.boundivore.dl.plugin.hive.config.ConfigLogicJmxYaml.SERVICE_NAME;

/**
 * Description: 配置 hive-env.sh 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicHiveEnv extends AbstractConfigLogic {


    public ConfigLogicHiveEnv(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{HADOOP_HOME}}
        String hadoopHome = this.hadoopHome();

        // {{HIVE_CONF_DIR}}
        String hiveConfDir = this.hiveConfDir();

        // {{HIVE_PID_DIR}}
        String hivePidDir = this.hivePidDir();

        return replacedTemplated
                .replace(
                        "{{HADOOP_HOME}}",
                        hadoopHome
                )
                .replace(
                        "{{HIVE_CONF_DIR}}",
                        hiveConfDir
                )
                .replace(
                        "{{HIVE_PID_DIR}}",
                        hivePidDir
                )
                // MetaStore
                .replace(
                        "{{jmxRemotePort_MetaStore}}",
                        PortConstants.getRemotePort(
                                SERVICE_NAME,
                                "MetaStore"
                        )
                )
                .replace(
                        "{{jmxExporterPort_MetaStore}}",
                        PortConstants.getExporterPort(
                                SERVICE_NAME,
                                "MetaStore"
                        )
                )

                // HiveServer2
                .replace(
                        "{{jmxRemotePort_MetaStore}}",
                        PortConstants.getRemotePort(
                                SERVICE_NAME,
                                "HiveServer2"
                        )
                )
                .replace(
                        "{{jmxExporterPort_HiveServer2}}",
                        PortConstants.getExporterPort(
                                SERVICE_NAME,
                                "HiveServer2"
                        )
                )
                ;
    }

    /**
     * Description: 获取 YARN 根目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String hadoopHome() {
        return String.format(
                "%s/YARN",
                super.serviceDir()
        );
    }

    /**
     * Description: 获取 HIVE 配置文件存放路径
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String hiveConfDir() {
        return String.format(
                "%s/HIVE/conf",
                super.serviceDir()
        );
    }

    /**
     * Description: 获取 HIVE 下组件进程 ID 的存放位置
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String hivePidDir() {
        return String.format(
                "%s/HIVE",
                super.pidDir()
        );
    }

}
