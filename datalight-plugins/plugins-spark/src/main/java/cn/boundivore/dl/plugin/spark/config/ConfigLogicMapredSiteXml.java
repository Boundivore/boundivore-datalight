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

import java.io.File;

/**
 * Description: 配置 mapred-site.xml 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicMapredSiteXml extends AbstractConfigLogic {


    public ConfigLogicMapredSiteXml(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // 获取 {{yarn.app.mapreduce.am.staging-dir}}
        String appMapReduceAmStagingDir = this.appMapReduceAmStagingDir();

        // 获取 {{history.server.hostname}}
        String historyServerHostname = this.historyServerHostname();

        // 获取 {{SERVICE_DIR}}
        String serviceDir = super.serviceDir();


        return replacedTemplated
                .replace(
                        "{{yarn.app.mapreduce.am.staging-dir}}",
                        appMapReduceAmStagingDir
                )
                .replace(
                        "{{history.server.hostname}}",
                        historyServerHostname
                )
                .replace(
                        "{{SERVICE_DIR}}",
                        serviceDir
                )
                ;
    }

    /**
     * Description: 获取 {{history.server.hostname}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{history.server.hostname}} 真实值
     */
    private String historyServerHostname() {
        PluginConfig.MetaComponent metaComponent = super.pluginConfig
                .getMetaServiceMap()
                .get("YARN")
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("HistoryServer"))
                .findFirst()
                .orElse(null);

        return metaComponent == null ? "localhost" : metaComponent.getHostname();
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
     * Description: 获取 {{yarn.app.mapreduce.am.staging-dir}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/1
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{yarn.app.mapreduce.am.staging-dir}} 真实值
     */
    private String appMapReduceAmStagingDir() {
        String fsDefault = this.fsDefaultFS();

        return String.format(
                "hdfs://%s/%s/tmp/hadoop-yarn/staging",
                fsDefault,
                fsDefault
        );
    }


}
