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
package cn.boundivore.dl.plugin.minio.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Description: 配置 start-minio.sh 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/12/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicMinIOStartMinIOSh extends AbstractConfigLogic {

    public ConfigLogicMinIOStartMinIOSh(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        //{{STORAGE_PATH}}
        String storagePath = this.storagePath();

        return replacedTemplated
                .replace(
                        "{{LOG_DIR}}",
                        String.format(
                                "%s/%s",
                                super.logDir(),
                                ConfigMINIO.SERVICE_NAME
                        )
                )
                .replace(
                        "{{STORAGE_PATH}}",
                        storagePath
                )
                ;
    }

    /**
     * Description: 拼装分布式存储目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     * <p>
     * EXAMPLE:
     * http://node01/data/datalight/data/MINIO \
     * http://node02/data/datalight/data/MINIO \
     * http://node03/data/datalight/data/MINIO \
     *
     * @return String 分布式存储目录
     */
    private String storagePath() {
        List<String> urls =super.currentMetaService.getMetaComponentMap()
                .keySet()
                .stream()
                .filter(k -> k.contains("MinIOServer"))
                .map(k -> super.currentMetaService.getMetaComponentMap().get(k))
                .sorted(Comparator.comparing(PluginConfig.MetaComponent::getHostname))
                .map(metaComponent -> String.format(
                        "http://%s/%s/%s",
                        metaComponent.getHostname(),
                        super.dataDir(),
                        ConfigMINIO.SERVICE_NAME
                ))
                .collect(Collectors.toList());

        return urls.subList(0, urls.size() - 1).stream()
                .map(url -> url + " \\")
                .collect(Collectors.joining("\n"))
                + "\n"
                + urls.get(urls.size() - 1);
    }
}
