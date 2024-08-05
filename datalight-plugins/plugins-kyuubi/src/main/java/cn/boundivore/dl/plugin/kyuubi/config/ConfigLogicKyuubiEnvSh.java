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

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

import static cn.boundivore.dl.plugin.kyuubi.config.ConfigLogicJmxYaml.SERVICE_NAME;


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
public class ConfigLogicKyuubiEnvSh extends AbstractConfigLogic {

    public ConfigLogicKyuubiEnvSh(PluginConfig pluginConfig) {
        super(pluginConfig);
    }


    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{SPARK_HOME}}
        String sparkHome = this.sparkHome();

        // {{FLINK_HOME}}
        String flinkHome = this.flinkHome();

        // {{HIVE_HOME}}
        String hiveHome = this.hiveHome();

        // {{KYUUBI_HOME}}
        String kyuubiHome = this.kyuubiHome();

        // {{YARN_HOME}}
        String yarnHome = this.yarnHome();

        // {{KYUUBI_LOG_DIR}}
        String kyuubiLogDir = this.kyuubiLogDir();

        // {{KYUUBI_PID_DIR}}
        String kyuubiPidDir = this.kyuubiPidDir();

        return replacedTemplated
                .replace(
                        "{{jmxRemotePort_KyuubiServer}}",
                        PortConstants.getRemotePort(
                                SERVICE_NAME,
                                "KyuubiServer"
                        )
                )
                .replace(
                        "{{jmxExporterPort_KyuubiServer}}",
                        PortConstants.getExporterPort(
                                SERVICE_NAME,
                                "KyuubiServer"
                        )
                )
                .replace(
                        "{{SPARK_HOME}}",
                        sparkHome
                )
                .replace(
                        "{{FLINK_HOME}}",
                        flinkHome
                )
                .replace(
                        "{{HIVE_HOME}}",
                        hiveHome
                )
                .replace(
                        "{{KYUUBI_HOME}}",
                        kyuubiHome
                )
                .replace(
                        "{{YARN_HOME}}",
                        yarnHome
                )
                .replace(
                        "{{KYUUBI_LOG_DIR}}",
                        kyuubiLogDir
                )
                .replace(
                        "{{KYUUBI_PID_DIR}}",
                        kyuubiPidDir
                )
                ;
    }

    /**
     * Description: 获取 Kyuubi Pid 目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Kyuubi Pid 目录
     */
    private String kyuubiPidDir() {
        // EXAMPLE: /data/datalight/logs/KYUUBI
        return String.format(
                "%s/KYUUBI",
                super.pidDir()
        );
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

    /**
     * Description: 获取 Yarn 部署目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Yarn Home
     */
    private String yarnHome() {
        return String.format(
                "%s/YARN",
                super.serviceDir()
        );
    }

    /**
     * Description: 获取 Kyuubi 部署目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Kyuubi Home
     */
    private String kyuubiHome() {
        return String.format(
                "%s/KYUUBI",
                super.serviceDir()
        );
    }

    /**
     * Description: 获取 Hive 部署目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Hive Home
     */
    private String hiveHome() {
        return String.format(
                "%s/HIVE",
                super.serviceDir()
        );
    }

    /**
     * Description: 获取 Flink 部署目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Flink Home
     */
    private String flinkHome() {
        return String.format(
                "%s/FLINK",
                super.serviceDir()
        );
    }

    /**
     * Description: 获取 Spark 部署目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Spark Home
     */
    private String sparkHome() {
        return String.format(
                "%s/SPARK",
                super.serviceDir()
        );
    }


}
