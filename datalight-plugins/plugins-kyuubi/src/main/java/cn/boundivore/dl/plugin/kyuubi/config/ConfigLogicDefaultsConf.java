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
package cn.boundivore.dl.plugin.kyuubi.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.Comparator;


/**
 * Description: 配置 server.properties 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicDefaultsConf extends AbstractConfigLogic {

    public ConfigLogicDefaultsConf(PluginConfig pluginConfig) {
        super(pluginConfig);
    }


    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{kyuubi.frontend.bind.host}}
        String bindHost = this.bindHost();

        // {{kyuubi.ha.addresses}}
        String haAddresses = this.haAddresses();

        return replacedTemplated
                .replace(
                        "{{kyuubi.frontend.bind.host}}",
                        bindHost
                )
                .replace(
                        "{{kyuubi.ha.addresses}}",
                        haAddresses
                )
                ;
    }

    /**
     * Description: 获取 KyuubiServer HA Zookeeper 集群地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Zookeeper 集群地址
     */
    private String haAddresses() {
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
     * Description: 获取 KyuubiServer bind host
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String KyuubiServer bind host
     */
    private String bindHost() {
        return "0.0.0.0";
    }


}
