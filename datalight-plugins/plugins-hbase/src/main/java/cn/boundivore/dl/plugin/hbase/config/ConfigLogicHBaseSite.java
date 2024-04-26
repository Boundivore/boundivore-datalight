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
package cn.boundivore.dl.plugin.hbase.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.Comparator;

/**
 * Description: 配置 hbase-site.xml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/25
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicHBaseSite extends AbstractConfigLogic {


    public ConfigLogicHBaseSite(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // 获取 {{hbase.zookeeper.quorum}}
        String hbaseZookeeperQuorum = this.hbaseZookeeperQuorum();

        // 获取 {{fs.defaultFS}}
        String fsDefaultFS = this.fsDefaultFS();

        // 获取 {{hbase.tmp.dir}}
        String hbaseTmpDir = this.hbaseTmpDir();


        return replacedTemplated
                .replace(
                        "{{hbase.zookeeper.quorum}}",
                        hbaseZookeeperQuorum
                )
                .replace(
                        "{{fs.defaultFS}}",
                        fsDefaultFS
                )
                .replace(
                        "{{hbase.tmp.dir}}",
                        hbaseTmpDir
                )
                ;
    }

    /**
     * Description: 获取 Zookeeper 集群地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/26
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Zookeeper 集群地址
     */
    private String hbaseZookeeperQuorum() {
        PluginConfig.MetaService zookeeperMetaService = super.pluginConfig
                .getMetaServiceMap()
                .get("ZOOKEEPER");

        StringBuilder sb = new StringBuilder();

        zookeeperMetaService.getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("QuarumPeermain"))
                .sorted(Comparator.comparing(PluginConfig.MetaComponent::getHostname))
                .forEach(c -> sb.append(c.getHostname()).append(":2181,"));

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * Description: 获取 HDFS 的 fs.defaultFS
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/26
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String HDFS 的 fs.defaultFS
     */
    private String fsDefaultFS() {
        PluginConfig.MetaService hdfsMetaService = super.pluginConfig.getMetaServiceMap().get("HDFS");
        return hdfsMetaService.getPluginClusterMeta().getClusterName();
    }

    /**
     * Description: 获取 HBase 本地临时缓存目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/26
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String HBase 本地临时缓存目录
     */
    private String hbaseTmpDir() {
        return String.format(
                "%s/HBASE/tmp",
                super.dataDir()
        );
    }


}
