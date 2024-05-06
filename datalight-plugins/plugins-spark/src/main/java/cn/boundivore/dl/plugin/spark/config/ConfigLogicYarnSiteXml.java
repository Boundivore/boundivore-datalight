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
import java.util.Comparator;
import java.util.Map;

/**
 * Description: 配置 yarn-site.xml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicYarnSiteXml extends AbstractConfigLogic {


    public ConfigLogicYarnSiteXml(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );


        // 获取 {{yarn.resourcemanager.cluster-id}}
        String rmClusterId = this.rmClusterId();

        // 获取 {{rm1.hostname}}
        String rm1Hostname = this.rm1Hostname();

        // 获取 {{rm2.hostname}}
        String rm2Hostname = this.rm2Hostname();

        // 获取 {{yarn.resourcemanager.zk-address}}
        String rmZkAddress = this.rmZkAddress();

        // 获取 {{yarn.nodemanager.remote-app-log-dir}}
        String nmRemoteAppLogDir = this.nmRemoteAppLogDir();

        // 获取 {{yarn.log.server.url}}
        String logServerUrl = this.logServerUrl();

        // 获取 {{yarn.timeline-service.hostname}}
        String timelineServiceHostname = this.timelineServiceHostname();

        // 获取 {{yarn.timeline-service.address}}
        String timelineServiceAddress = this.timelineServiceAddress();

        // 获取 {{yarn.timeline-service.webapp.address}}
        String timelineServiceWebappAddress = this.timelineServiceWebappAddress();

        // 获取 {{yarn.timeline-service.webapp.https.address}}
        String timelineServiceWebappHttpsAddress = this.timelineServiceWebappHttpsAddress();

        // 获取 {{yarn.nodemanager.local-dirs}}
        String nmLocalDirs = this.nmLocalDirs();

        return replacedTemplated
                .replace(
                        "{{yarn.resourcemanager.cluster-id}}",
                        rmClusterId
                )
                .replace(
                        "{{rm1.hostname}}",
                        rm1Hostname
                )
                .replace(
                        "{{rm2.hostname}}",
                        rm2Hostname
                )
                .replace(
                        "{{yarn.resourcemanager.zk-address}}",
                        rmZkAddress
                )
                .replace(
                        "{{yarn.nodemanager.remote-app-log-dir}}",
                        nmRemoteAppLogDir
                )
                .replace(
                        "{{yarn.log.server.url}}",
                        logServerUrl
                )
                .replace(
                        "{{yarn.timeline-service.hostname}}",
                        timelineServiceHostname
                )
                .replace(
                        "{{yarn.timeline-service.address}}",
                        timelineServiceAddress
                )
                .replace(
                        "{{yarn.timeline-service.webapp.address}}",
                        timelineServiceWebappAddress
                )
                .replace(
                        "{{yarn.timeline-service.webapp.https.address}}",
                        timelineServiceWebappHttpsAddress
                )
                .replace(
                        "{{yarn.nodemanager.local-dirs}}",
                        nmLocalDirs
                )
                ;
    }

    /**
     * Description: 获取 {{yarn.resourcemanager.cluster-id}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{yarn.resourcemanager.cluster-id}} 真实值
     */
    private String rmClusterId() {
        PluginConfig.MetaService hdfsMetaService = super.pluginConfig.getMetaServiceMap().get("HDFS");
        return hdfsMetaService.getPluginClusterMeta().getClusterName();
    }

    /**
     * Description: 获取 {{rm1.hostname}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{rm1.hostname}} 真实值
     */
    private String rm1Hostname() {
        Map<String, PluginConfig.MetaComponent> yarnMetaComponentMap = super.pluginConfig
                .getMetaServiceMap()
                .get("YARN")
                .getMetaComponentMap();

        String resourceManager1AndNodeId = yarnMetaComponentMap.keySet()
                .stream()
                .filter(i -> i.contains("ResourceManager1"))
                .findFirst()
                .orElse(null);

        Assert.notNull(
                resourceManager1AndNodeId,
                () -> new RuntimeException("设置配置文件时，无法找到 ResourceManager1 的部署位置")
        );

        return yarnMetaComponentMap
                .get(resourceManager1AndNodeId)
                .getHostname();
    }

    /**
     * Description: 获取 {{rm2.hostname}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{rm2.hostname}} 真实值
     */
    private String rm2Hostname() {
        Map<String, PluginConfig.MetaComponent> yarnMetaComponentMap = super.pluginConfig
                .getMetaServiceMap()
                .get("YARN")
                .getMetaComponentMap();

        String resourceManager2AndNodeId = yarnMetaComponentMap.keySet()
                .stream()
                .filter(i -> i.contains("ResourceManager2"))
                .findFirst()
                .orElse(null);

        Assert.notNull(
                resourceManager2AndNodeId,
                () -> new RuntimeException("设置配置文件时，无法找到 ResourceManager2 的部署位置")
        );

        return yarnMetaComponentMap
                .get(resourceManager2AndNodeId)
                .getHostname();
    }

    /**
     * Description: 获取 {{yarn.resourcemanager.zk-address}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{yarn.resourcemanager.zk-address}} 真实值
     */
    private String rmZkAddress() {
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
     * Description: 当前 YARN 服务配置中，需获取 HDFS 服务的 {{fs.defaultFS}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{fs.defaultFS}} 真实值
     */
    private String fsDefaultFS() {
        PluginConfig.MetaService hdfsMetaService = super.pluginConfig.getMetaServiceMap().get("HDFS");
        return hdfsMetaService.getPluginClusterMeta().getClusterName();
    }

    /**
     * Description: 获取 {{yarn.nodemanager.remote-app-log-dir}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{yarn.nodemanager.remote-app-log-dir}} 真实值
     */
    private String nmRemoteAppLogDir() {
        String fsDefault = this.fsDefaultFS();
        return String.format(
                "hdfs://%s/%s/tmp",
                fsDefault,
                fsDefault
        );
    }

    /**
     * Description: 获取 {{yarn.log.server.url}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{yarn.log.server.url}} 真实值
     */
    private String logServerUrl() {
        PluginConfig.MetaComponent metaComponent = super.pluginConfig
                .getMetaServiceMap()
                .get("YARN")
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("HistoryServer"))
                .findFirst()
                .orElse(null);

        return String.format(
                "http://%s:19888/jobhistory/logs/",
                metaComponent == null ? "localhost" : metaComponent.getHostname()
        );
    }

    /**
     * Description: 获取 {{yarn.timeline-service.hostname}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{yarn.timeline-service.hostname}} 真实值
     */
    private String timelineServiceHostname() {
        PluginConfig.MetaComponent metaComponent = super.pluginConfig
                .getMetaServiceMap()
                .get("YARN")
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("TimelineServer"))
                .findFirst()
                .orElse(null);

        return metaComponent == null ? "localhost" : metaComponent.getHostname();
    }

    /**
     * Description: 获取 {{yarn.timeline-service.address}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{yarn.timeline-service.address}} 真实值
     */
    private String timelineServiceAddress() {
        return String.format(
                "%s:%s",
                this.timelineServiceHostname(),
                "10201"
        );
    }

    /**
     * Description: 获取 {{yarn.timeline-service.webapp.address}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{yarn.timeline-service.webapp.address}} 真实值
     */
    private String timelineServiceWebappAddress() {
        return String.format(
                "%s:%s",
                this.timelineServiceHostname(),
                "8188"
        );
    }

    /**
     * Description: 获取 {{yarn.timeline-service.webapp.https.address}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{yarn.timeline-service.webapp.https.address}} 真实值
     */
    private String timelineServiceWebappHttpsAddress() {
        return String.format(
                "%s:%s",
                this.timelineServiceHostname(),
                "2191"
        );
    }

    /**
     * Description: 获取 {{yarn.nodemanager.local-dirs}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{yarn.nodemanager.local-dirs}} 真实值
     */
    private String nmLocalDirs() {
        // EXAMPLE: /data/datalight
        String dataDir = super.dataDir();
        return String.format(
                "%s/YARN/tmp/nm-local-dir",
                dataDir
        );
    }


}
