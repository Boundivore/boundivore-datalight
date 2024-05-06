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

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;
import cn.hutool.core.lang.Assert;

import java.io.File;
import java.util.Map;

/**
 * Description: 配置 spark-defaults.conf 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicSparkDefaults extends AbstractConfigLogic {

    private final String HISTORY_SERVER_UI_PORT = "4000";


    public ConfigLogicSparkDefaults(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{spark.yarn.historyServer.address}}
        String historyServerAddress = this.historyServerAddress();

        // {{spark.history.ui.port}}
        String sparkHistoryUiPort = this.sparkHistoryUiPort();

        // {{spark.eventLog.dir}}
        String sparkEventLogDir = this.sparkEventLogDir();

        // {{spark.history.fs.logDirectory}}
        String sparkHistoryFsLogDirectory = this.sparkHistoryFsLogDirectory();

        // {{spark.yarn.stagingDir}}
        String sparkYarnStagingDir = this.sparkYarnStagingDir();

        return replacedTemplated
                .replace(
                        "{{spark.yarn.historyServer.address}}",
                        historyServerAddress
                )
                .replace(
                        "{{spark.history.ui.port}}",
                        sparkHistoryUiPort
                )
                .replace(
                        "{{spark.eventLog.dir}}",
                        sparkEventLogDir
                )
                .replace(
                        "{{spark.history.fs.logDirectory}}",
                        sparkHistoryFsLogDirectory
                )
                .replace(
                        "{{spark.yarn.stagingDir}}",
                        sparkYarnStagingDir
                )
                ;
    }


    /**
     * Description: 设定 YARN 应用可以访问 Spark History Server 的网络地址。此配置确保 YARN 应用能够获取其执行历史记录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String SparkHistoryServer 网络地址
     */
    private String historyServerAddress() {

        Map<String, PluginConfig.MetaComponent> metaComponentMap = super.currentMetaService
                .getMetaComponentMap();

        String sparkHistoryServerAndNodeId = super.currentMetaService
                .getMetaComponentMap()
                .keySet()
                .stream()
                .filter(i -> i.contains("SparkHistoryServer"))
                .findFirst()
                .orElse(null);

        Assert.notNull(
                sparkHistoryServerAndNodeId,
                () -> new RuntimeException("设置配置文件时，无法找到 SparkHistoryServer 的部署位置")
        );

        return String.format(
                "%s:%s",
                metaComponentMap
                        .get(sparkHistoryServerAndNodeId)
                        .getHostname(),
                this.HISTORY_SERVER_UI_PORT
        );
    }

    /**
     * Description: 确定 Spark History Server 用户界面服务的端口号
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 用户界面服务的端口号
     */
    private String sparkHistoryUiPort() {
        return this.HISTORY_SERVER_UI_PORT;
    }

    /**
     * Description: 指定存储 Spark 事件日志的目录，这些日志用于应用程序的监控和故障排查
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 存储 Spark 事件日志的目录
     */
    private String sparkEventLogDir() {
        return this.commonDir();
    }


    /**
     * Description: 指定 Spark History Server 用于读取应用程序日志的文件系统目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 用于读取应用程序日志的文件系统目录
     */
    private String sparkHistoryFsLogDirectory() {
        return this.commonDir();
    }

    /**
     * Description: 指定 YARN 在提交应用程序过程中用于存储临时文件的目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 用于存储临时文件的目录
     */
    private String sparkYarnStagingDir() {
        return this.commonDir();
    }


    /**
     * Description: Spark 通用日志目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Spark 通用日志目录
     */
    private String commonDir() {
        PluginConfig.MetaService hdfsMetaService = super.pluginConfig.getMetaServiceMap().get("HDFS");
        String hdfsClusterName = hdfsMetaService.getPluginClusterMeta().getClusterName();

        // 在存储计算分离的场景中，不同计算集群的计算日志应该有不同的存储路径，因此以计算集群的名称来作为 HDFS 根目录作为分割
        String sparkClusterName = super.currentMetaService.getPluginClusterMeta().getClusterName();

        return String.format(
                "hdfs://%s/%s/spark-logs",
                hdfsClusterName,
                sparkClusterName
        );
    }


}
