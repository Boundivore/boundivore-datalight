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
package cn.boundivore.dl.plugin.flink.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

import static cn.boundivore.dl.plugin.flink.config.ConfigLogicJmxYaml.SERVICE_NAME;

/**
 * Description: 配置 config.sh 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicFlinkConfigSh extends AbstractConfigLogic {

    public ConfigLogicFlinkConfigSh(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        String defaultHadoopConfDir = this.defaultHadoopConfDir();

        String defaultHBaseConfDir = this.defaultHBaseConfDir();

        String hadoopHome = this.hadoopHome();

        String hbaseHome = this.hbaseHome();

        String defaultFlinkLogDir = this.defaultFlinkLogDir();


        return replacedTemplated
                .replace(
                        "{{DEFAULT_HADOOP_CONF_DIR}}",
                        defaultHadoopConfDir
                )
                .replace(
                        "{{DEFAULT_HBASE_CONF_DIR}}",
                        defaultHBaseConfDir
                )
                .replace(
                        "{{HADOOP_HOME}}",
                        hadoopHome
                )
                .replace(
                        "{{HBASE_HOME}}",
                        hbaseHome
                )
                .replace(
                        "{{DEFAULT_FLINK_LOG_DIR}}",
                        defaultFlinkLogDir
                )
                ;
    }

    /**
     * Description: 获取 Hadoop 配置文件目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Hadoop 配置文件目录
     */
    private String defaultHadoopConfDir() {
        return String.format(
                "%s/YARN/etc/hadoop",
                super.serviceDir()
        );
    }

    /**
     * Description: 获取 HBase 配置文件目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String HBase 配置文件目录
     */
    private String defaultHBaseConfDir() {
        return String.format(
                "%s/HBASE/etc/hadoop",
                super.serviceDir()
        );
    }

    /**
     * Description: 获取 Hadoop 根目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Hadoop 根目录
     */
    private String hadoopHome() {
        return String.format(
                "%s/YARN",
                super.serviceDir()
        );
    }

    /**
     * Description: 获取 HBase 配置文件目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String HBase 配置文件目录
     */
    private String hbaseHome() {
        return String.format(
                "%s/HBASE",
                super.serviceDir()
        );
    }

    /**
     * Description: 获取 Flink 默认日志目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Flink 默认日志目录
     */
    private String defaultFlinkLogDir() {
        return String.format(
                "%s/%s",
                super.logDir(),
                SERVICE_NAME
        );
    }

}
