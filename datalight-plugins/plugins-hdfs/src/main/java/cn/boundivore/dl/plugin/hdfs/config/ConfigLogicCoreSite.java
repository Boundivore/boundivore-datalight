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
package cn.boundivore.dl.plugin.hdfs.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;
import cn.hutool.core.lang.Assert;

import java.io.File;

/**
 * Description: 配置 core-site.xml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicCoreSite extends AbstractConfigLogic {


    public ConfigLogicCoreSite(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // 获取 {{fs.defaultFS}}
        String fsDefaultFS = this.fsDefaultFS();

        // 获取 {{hadoop.tmp.dir}}
        String hadoopTempDir = this.hadoopTempDir();

        // 获取 {{ha.zookeeper.quorum}}
        String haZookeeperQuorum = this.haZookeeperQuorum();

        // 获取 {{ipc.client.connect.max.retries}}
        String ipcClientConnectMaxRetries = this.ipcClientConnectMaxRetries();

        // 获取 {{ipc.client.connect.retry.interval}}
        String ipcClientConnectRetryInterval = this.ipcClientConnectRetryInterval();

        return replacedTemplated
                .replace(
                        "{{fs.defaultFS}}",
                        fsDefaultFS
                )
                .replace(
                        "{{hadoop.tmp.dir}}",
                        hadoopTempDir
                )
                .replace(
                        "{{ha.zookeeper.quorum}}",
                        haZookeeperQuorum
                )
                .replace(
                        "{{ipc.client.connect.max.retries}}",
                        ipcClientConnectMaxRetries
                )
                .replace(
                        "{{ipc.client.connect.retry.interval}}",
                        ipcClientConnectRetryInterval
                )
                ;
    }

    /**
     * Description: 获取 {{fs.defaultFS}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{fs.defaultFS}} 真实值
     */
    private String fsDefaultFS() {
        return super.currentMetaService.getPluginClusterMeta().getClusterName();
    }

    /**
     * Description: 获取 {{hadoop.tmp.dir}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{hadoop.tmp.dir}} 真实值
     */
    private String hadoopTempDir() {
        // EXAMPLE: /data/datalight
        String dataDir = super.pluginConfig.getUnixEnv().getDATA_DIR();
        Assert.notNull(
                dataDir,
                () -> new RuntimeException("无法读取环境变量 DATA_DIR")
        );
        return String.format(
                "%s/HDFS/tmp/hadoop",
                dataDir
        );
    }

    /**
     * Description: 获取 {{ha.zookeeper.quorum}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{ha.zookeeper.quorum}} 真实值
     */
    private String haZookeeperQuorum() {
        PluginConfig.MetaService zookeeperMetaService = super.pluginConfig
                .getMetaServiceMap()
                .get("ZOOKEEPER");

        StringBuilder sb = new StringBuilder();
        zookeeperMetaService.getMetaComponentMap()
                .forEach((k, v) -> {
                            if (k.contains("QuarumPeermain")) {
                                sb.append(v.getHostname())
                                        .append(":2181,");
                            }
                        }
                );
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * Description: 获取 {{ipc.client.connect.max.retries}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{ipc.client.connect.max.retries}} 真实值
     */
    private String ipcClientConnectMaxRetries() {
        return "100";
    }

    /**
     * Description: 获取 {{ipc.client.connect.retry.interval}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{ipc.client.connect.retry.interval}} 真实值
     */
    private String ipcClientConnectRetryInterval() {
        return "5000";
    }
}
