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
package cn.boundivore.dl.plugin.yarn.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;
import cn.hutool.core.lang.Assert;

import java.io.File;
import java.util.Comparator;
import java.util.Map;

/**
 * Description: 配置 hdfs-site.xml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicHdfsSite extends AbstractConfigLogic {


    public ConfigLogicHdfsSite(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // 获取 {{dfs.nameservices}}
        String dfsNameservices = this.dfsNameservices();

        // 获取 {{nn1.hostname}}
        String nn1Hostname = this.nn1Hostname();

        // 获取 {{nn2.hostname}}
        String nn2Hostname = this.nn2Hostname();

        // 获取 {{journal.node.url}}
        String journalNodeUrl = this.journalNodeUrl();

        // 获取 {{dfs.journalnode.edits.dir}}
        String dfsJournalNodeEditsDir = this.dfsJournalNodeEditsDir();

        // 获取 {{SERVICE_DIR}}
        String serviceDir = this.serviceDir();

        // 获取 {{dfs.datanode.data.dir}}
        String dfsDataNodeDataDir = this.dfsDataNodeDataDir();

        return replacedTemplated
                .replace(
                        "{{dfs.nameservices}}",
                        dfsNameservices
                )
                .replace(
                        "{{nn1.hostname}}",
                        nn1Hostname
                )
                .replace(
                        "{{nn2.hostname}}",
                        nn2Hostname
                )
                .replace(
                        "{{journal.node.url}}",
                        journalNodeUrl
                )
                .replace(
                        "{{dfs.journalnode.edits.dir}}",
                        dfsJournalNodeEditsDir
                )
                .replace(
                        "{{SERVICE_DIR}}",
                        serviceDir
                )
                .replace(
                        "{{dfs.datanode.data.dir}}",
                        dfsDataNodeDataDir
                )
                ;
    }


    /**
     * Description: 获取 {{dfs.nameservices}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{dfs.nameservices}} 真实值
     */
    private String dfsNameservices() {
        PluginConfig.MetaService hdfsMetaService = super.pluginConfig.getMetaServiceMap().get("HDFS");
        return hdfsMetaService.getPluginClusterMeta().getClusterName();
    }

    /**
     * Description: 获取 {{nn1.hostname}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{nn1.hostname}} 真实值
     */
    private String nn1Hostname() {
        PluginConfig.MetaService hdfsMetaService = super.pluginConfig.getMetaServiceMap().get("HDFS");

        Map<String, PluginConfig.MetaComponent> hdfsMetaComponentMap = hdfsMetaService.getMetaComponentMap();

        String nameNode1AndNodeId = hdfsMetaComponentMap.keySet()
                .stream()
                .filter(i -> i.contains("NameNode1"))
                .findFirst()
                .orElse(null);

        Assert.notNull(
                nameNode1AndNodeId,
                () -> new RuntimeException("设置配置文件时，无法找到 NameNode1 的部署位置")
        );

        return hdfsMetaComponentMap
                .get(nameNode1AndNodeId)
                .getHostname();
    }

    /**
     * Description: 获取 {{nn2.hostname}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{nn2.hostname}} 真实值
     */
    private String nn2Hostname() {
        PluginConfig.MetaService hdfsMetaService = super.pluginConfig.getMetaServiceMap().get("HDFS");

        Map<String, PluginConfig.MetaComponent> hdfsMetaComponentMap = hdfsMetaService.getMetaComponentMap();

        String nameNode1AndNodeId = hdfsMetaComponentMap.keySet()
                .stream()
                .filter(i -> i.contains("NameNode2"))
                .findFirst()
                .orElse(null);

        Assert.notNull(
                nameNode1AndNodeId,
                () -> new RuntimeException("设置配置文件时，无法找到 NameNode2 的部署位置")
        );

        return hdfsMetaComponentMap
                .get(nameNode1AndNodeId)
                .getHostname();
    }

    /**
     * Description: 获取 {{journal.node.url}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{journal.node.url}} 真实值
     */
    private String journalNodeUrl() {
        PluginConfig.MetaService hdfsMetaService = super.pluginConfig.getMetaServiceMap().get("HDFS");

        StringBuilder sb = new StringBuilder();

        hdfsMetaService.getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("JournalNode"))
                .sorted(Comparator.comparing(PluginConfig.MetaComponent::getHostname))
                .forEach(c ->  sb.append(c.getHostname()).append(":8485,"));

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * Description: 获取 {{dfs.journalnode.edits.dir}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{dfs.journalnode.edits.dir}} 真实值
     */
    private String dfsJournalNodeEditsDir() {
        // EXAMPLE: /data/datalight
        String dataDir = super.pluginConfig.getUnixEnv().getDATA_DIR();
        Assert.notNull(
                dataDir,
                () -> new RuntimeException("无法读取环境变量 DATA_DIR")
        );
        return String.format(
                "%s/HDFS/jnData",
                dataDir
        );
    }

    /**
     * Description: 获取 {{SERVICE_DIR}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{SERVICE_DIR}} 真实值
     */
    private String serviceDir() {
        String serviceDir = super.pluginConfig.getUnixEnv().getSERVICE_DIR();
        Assert.notNull(
                serviceDir,
                () -> new RuntimeException("无法读取环境变量 SERVICE_DIR")
        );
        return serviceDir;
    }

    /**
     * Description: 获取 {{dfs.datanode.data.dir}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{dfs.datanode.data.dir}} 真实值
     */
    private String dfsDataNodeDataDir() {
        return "/data/datalight/data/HDFS";
    }
}
