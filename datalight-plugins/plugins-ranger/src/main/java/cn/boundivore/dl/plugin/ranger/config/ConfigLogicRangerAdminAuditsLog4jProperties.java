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

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

/**
 * Description: 配置 Ranger Admin log4j.properties 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicRangerAdminAuditsLog4jProperties extends AbstractConfigLogic {

    public ConfigLogicRangerAdminAuditsLog4jProperties(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{solr.log}} /data/datalight/logs/SOLR
        String solrLog = this.solrLog();

        return replacedTemplated
                .replace(
                        "{{solr.log}}",
                        solrLog
                )
                ;
    }

    /**
     * Description: 获取 Solr 日志目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Solr 日志目录
     */
    private String solrLog() {
        return String.format(
                "%s/SOLR",
                super.logDir()
        );
    }
}
