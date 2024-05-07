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
import cn.hutool.core.lang.Assert;

import java.io.File;
import java.util.Map;

/**
 * Description: 配置 config.yaml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicFlinkConfigYaml extends AbstractConfigLogic {

    public ConfigLogicFlinkConfigYaml(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{historyserver.web.address}}
        String historyServerWebAddress = this.historyServerWebAddress();

        // {{historyserver.archive.fs.dir}}
        String historyServerArchiveFsDir = this.historyServerArchiveFsDir();


        return replacedTemplated
                .replace(
                        "{{historyserver.web.address}}",
                        historyServerWebAddress
                )
                .replace(
                        "{{historyserver.archive.fs.dir}}",
                        historyServerArchiveFsDir
                )
                ;
    }

    /**
     * Description: 获取 FlinkHistoryServer 地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String FlinkHistoryServer 地址
     */
    private String historyServerWebAddress() {
        Map<String, PluginConfig.MetaComponent> metaComponentMap = super.currentMetaService
                .getMetaComponentMap();

        String flinkHistoryServerAndNodeId = super.currentMetaService
                .getMetaComponentMap()
                .keySet()
                .stream()
                .filter(i -> i.contains("FlinkHistoryServer"))
                .findFirst()
                .orElse(null);

        Assert.notNull(
                flinkHistoryServerAndNodeId,
                () -> new RuntimeException("设置配置文件时，无法找到 FlinkHistoryServer 的部署位置")
        );

        PluginConfig.MetaComponent metaComponent = metaComponentMap.get(flinkHistoryServerAndNodeId);

        return  metaComponent.getHostname();
    }

    /**
     * Description: 获取 FlinkHistoryServer 已完成作业信息的存放目录（HDFS）, 例: hdfs:///completed-jobs/
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String FlinkHistoryServer 已完成作业信息的存放目录（HDFS）
     */
    private String historyServerArchiveFsDir() {
        PluginConfig.MetaService hdfsMetaService = super.pluginConfig.getMetaServiceMap().get("HDFS");
        String hdfsClusterName = hdfsMetaService.getPluginClusterMeta().getClusterName();

        // 在存储计算分离的场景中，不同计算集群的计算日志应该有不同的存储路径，因此以计算集群的名称来作为 HDFS 根目录作为分割
        String flinkClusterName = super.currentMetaService.getPluginClusterMeta().getClusterName();

        return String.format(
                "hdfs://%s/%s/completed-jobs",
                hdfsClusterName,
                flinkClusterName
        );
    }

}
