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
 * Description: 配置 core-site.xml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicYarnSite extends AbstractConfigLogic {


    public ConfigLogicYarnSite(PluginConfig pluginConfig) {
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



    private String rmClusterId() {
        return super.currentMetaService.getPluginClusterMeta().getClusterName();
    }

    private String rm1Hostname() {
        Map<String, PluginConfig.MetaComponent> currentMetaComponentMap = super.pluginConfig
                .getCurrentMetaService()
                .getMetaComponentMap();

        String resourceManager1AndNodeId = currentMetaComponentMap.keySet()
                .stream()
                .filter(i -> i.contains("ResourceManager1"))
                .findFirst()
                .orElse(null);

        Assert.notNull(
                resourceManager1AndNodeId,
                () -> new RuntimeException("设置配置文件时，无法找到 ResourceManager1 的部署位置")
        );

        return currentMetaComponentMap
                .get(resourceManager1AndNodeId)
                .getHostname();
    }

    private String rm2Hostname() {
        Map<String, PluginConfig.MetaComponent> currentMetaComponentMap = super.pluginConfig
                .getCurrentMetaService()
                .getMetaComponentMap();

        String resourceManager2AndNodeId = currentMetaComponentMap.keySet()
                .stream()
                .filter(i -> i.contains("ResourceManager2"))
                .findFirst()
                .orElse(null);

        Assert.notNull(
                resourceManager2AndNodeId,
                () -> new RuntimeException("设置配置文件时，无法找到 ResourceManager2 的部署位置")
        );

        return currentMetaComponentMap
                .get(resourceManager2AndNodeId)
                .getHostname();
    }

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
                .forEach(c ->  sb.append(c.getHostname()).append(":2181,"));

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    private String nmRemoteAppLogDir() {
        return null;
    }

    private String logServerUrl() {
        return null;
    }

    private String timelineServiceHostname() {
        return null;
    }

    private String timelineServiceAddress() {
        return null;
    }

    private String timelineServiceWebappAddress() {
        return null;
    }

    private String timelineServiceWebappHttpsAddress() {
        return null;
    }

    private String nmLocalDirs() {
        return null;
    }
}
