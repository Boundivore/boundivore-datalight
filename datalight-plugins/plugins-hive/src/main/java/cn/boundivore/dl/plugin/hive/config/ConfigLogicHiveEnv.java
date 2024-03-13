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

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

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

        // {{jmxExporterPort_MetaStore}}
        String jmxExporterPortMetaStore = this.jmxExporterPortMetaStore();

        // {{jmxExporterPort_HiveServer2}}
        String jmxExporterPortHiveServer2 = this.jmxExporterPortHiveServer2();

        return replacedTemplated
                .replace(
                        "{{HADOOP_HOME}}",
                        ""
                )
                .replace(
                        "{{HIVE_CONF_DIR}}",
                        ""
                )
                .replace(
                        "{{HIVE_PID_DIR}}",
                        ""
                )
                .replace(
                        "{{jmxExporterPort_MetaStore}}",
                        ""
                )
                .replace(
                        "{{jmxExporterPort_HiveServer2}}",
                        ""
                )
                ;
    }

    /**
     * Description: TODO
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
        return null;
    }

    /**
     * Description: TODO
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
        return null;
    }

    /**
     * Description: TODO
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
        return null;
    }

    /**
     * Description: TODO
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
    private String jmxExporterPortMetaStore() {
        return null;
    }

    /**
     * Description: TODO
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
    private String jmxExporterPortHiveServer2() {
        return null;
    }

}
