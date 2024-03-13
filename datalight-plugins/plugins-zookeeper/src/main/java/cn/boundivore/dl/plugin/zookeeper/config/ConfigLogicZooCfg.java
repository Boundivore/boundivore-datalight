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
package cn.boundivore.dl.plugin.zookeeper.config;

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Comparator;

/**
 * Description: 配置 zoo.cfg 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class ConfigLogicZooCfg extends AbstractConfigLogic {


    public ConfigLogicZooCfg(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        //获取当前服务组件其他节点的元数据信息
        StringBuilder serversSb = new StringBuilder();
        super.currentMetaService.getMetaComponentMap()
                .keySet()
                .stream()
                .filter(k -> k.contains("QuarumPeermain"))
                .map(k -> super.currentMetaService.getMetaComponentMap().get(k))
                .sorted(Comparator.comparing(PluginConfig.MetaComponent::getHostname))
                .forEach(metaComponent -> {
                            //EXAMPLE: server.1=172.18.29.152:2888:3888
                            serversSb.append(
                                    String.format(
                                            "server.%s=%s:2888:3888",
                                            metaComponent.getNodeId(),
                                            metaComponent.getHostname()
                                    )
                            );
                            serversSb.append("\n");
                        }
                );


        return replacedTemplated
                .replace(
                        "{{ZOOKEEPER_SERVERS}}",
                        serversSb.toString()
                )
                .replace(
                        "{{DATA_DIR}}",
                        String.format(
                                "%s/%s/zkData",
                                super.dataDir(),
                                super.currentMetaService.getServiceName()
                        )
                )
                .replace(
                        "{{exporterPort}}",
                        PortConstants.EXPORTER_PORT_MAP.get("ZOOKEEPER-QuarumPeermain")

                );
    }
}
