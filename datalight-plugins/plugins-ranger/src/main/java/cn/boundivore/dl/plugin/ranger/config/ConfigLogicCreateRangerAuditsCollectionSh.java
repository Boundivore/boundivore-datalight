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
package cn.boundivore.dl.plugin.ranger.config;

import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.Comparator;
import java.util.Map;

/**
 * Description: 配置 create_ranger_audits_collection.sh 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/9/20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicCreateRangerAuditsCollectionSh extends AbstractConfigLogic {

    public ConfigLogicCreateRangerAuditsCollectionSh(PluginConfig pluginConfig) {
        super(pluginConfig);
    }


    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{SOLR_HOST}}
        String solrHost = this.solrHost();

        // {{SOLR_ZK}}
        String solrZk = this.solrZk();

        return replacedTemplated
                .replace(
                        "{{SOLR_HOST}}",
                        solrHost
                )
                .replace(
                        "{{SOLR_ZK}}",
                        solrZk
                )
                ;
    }

    /**
     * Description: 获取 Solr url
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Solr url
     */
    private String solrHost() {
        PluginConfig.MetaService solrMetaService = super.pluginConfig
                .getMetaServiceMap()
                .get("SOLR");

        return solrMetaService.getMetaComponentMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().contains("SolrServer"))
                .map(Map.Entry::getValue)
                .filter(metaComponent -> metaComponent.getComponentState() == SCStateEnum.STARTED)
                .findFirst()
                .map(PluginConfig.MetaComponent::getHostname)
                .orElseThrow(() -> new RuntimeException("未找到处于 STARTED 状态的 SolrServer"));
    }


    /**
     * Description: 获取 Zookeeper 集群地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Zookeeper 集群地址
     */
    private String solrZk() {
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
}
