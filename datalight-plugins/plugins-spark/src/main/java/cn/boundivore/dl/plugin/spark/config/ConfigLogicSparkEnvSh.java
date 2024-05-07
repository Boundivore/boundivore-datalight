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
package cn.boundivore.dl.plugin.spark.config;

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

import static cn.boundivore.dl.plugin.spark.config.ConfigLogicJmxYaml.SERVICE_NAME;

/**
 * Description: 配置 spark-env.sh 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicSparkEnvSh extends AbstractConfigLogic {

    private final String DEPENDENCY_SERVICE_NAME = "YARN";


    public ConfigLogicSparkEnvSh(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{SPARK_LOG_DIR}}
        String sparkLogDir = this.sparkLogDir();

        // {{SPARK_CONF_DIR}}
        String sparkConfDir = this.sparkConfDir();

        // {{HADOOP_CONF_DIR}}
        String hadoopConfDir = this.hadoopConfDir();

        // {{SPARK_PID_DIR}}
        String sparkPidDir = this.sparkPidDir();

        // {{SPARK_LOCAL_DIRS}}
        String sparkLocalDirs = this.sparkLocalDirs();

        return replacedTemplated
                .replace(
                        "{{SPARK_LOG_DIR}}",
                        sparkLogDir
                )
                .replace(
                        "{{SPARK_CONF_DIR}}",
                        sparkConfDir
                )
                .replace(
                        "{{HADOOP_CONF_DIR}}",
                        hadoopConfDir
                )
                .replace(
                        "{{SPARK_PID_DIR}}",
                        sparkPidDir
                )
                .replace(
                        "{{SPARK_LOCAL_DIRS}}",
                        sparkLocalDirs
                )
                .replace(
                        "{{jmxRemotePort_SparkHistoryServer}}",
                        PortConstants.getRemotePort(
                                SERVICE_NAME,
                                "SparkHistoryServer"
                        )
                )
                .replace(
                        "{{jmxExporterPort_SparkHistoryServer}}",
                        PortConstants.getExporterPort(
                                SERVICE_NAME,
                                "SparkHistoryServer"
                        )
                )
                ;
    }

    /**
     * Description: Spark 日志目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String sparkLogDir() {
        return String.format(
                "%s/%s",
                super.logDir(),
                SERVICE_NAME
        );
    }

    /**
     * Description: Spark 配置文件目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String sparkConfDir() {
        return String.format(
                "%s/%s/conf",
                super.serviceDir(),
                SERVICE_NAME
        );
    }

    /**
     * Description: Hadoop 配置文件目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String hadoopConfDir() {
        return String.format(
                "%s/%s/etc/hadoop",
                super.serviceDir(),
                this.DEPENDENCY_SERVICE_NAME
        );
    }

    /**
     * Description: Spark 进程文件目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String sparkPidDir() {
        return String.format(
                "%s/%s",
                super.pidDir(),
                SERVICE_NAME
        );
    }

    /**
     * Description: Spark 本地目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String sparkLocalDirs() {
        return String.format(
                "%s/%s",
                super.dataDir(),
                SERVICE_NAME
        );
    }


}
