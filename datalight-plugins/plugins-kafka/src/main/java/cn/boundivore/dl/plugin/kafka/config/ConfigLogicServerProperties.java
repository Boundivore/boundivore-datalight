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
package cn.boundivore.dl.plugin.kafka.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.Comparator;

import static cn.boundivore.dl.plugin.kafka.config.ConfigLogicJmxYaml.SERVICE_NAME;

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
public class ConfigLogicServerProperties extends AbstractConfigLogic {

    public ConfigLogicServerProperties(PluginConfig pluginConfig) {
        super(pluginConfig);
    }


    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );


        // {{broker.id}}
        String brokerId = this.brokerId();

        // {{listeners.host.name}}
        String listenersHostName = this.listenersHostName();

        // {{kafka.log.dirs}}
        String kafkaLogDirs = this.kafkaLogDirs();

        // {{zookeeper.connect}}
        String zookeeperConnect = this.zookeeperConnect();

        return replacedTemplated
                .replace(
                        "{{broker.id}}",
                        brokerId
                )
                .replace(
                        "{{listeners.host.name}}",
                        listenersHostName
                )
                .replace(
                        "{{kafka.log.dirs}}",
                        kafkaLogDirs
                )
                .replace(
                        "{{zookeeper.connect}}",
                        zookeeperConnect
                )
                ;
    }

    /**
     * Description: 获取 Kafka BrokerId
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Kafka BrokerId
     */
    private String brokerId() {
        return super.pluginConfig.getCurrentNodeSerialNum().toString();
    }

    /**
     * Description: 获取 Broker 监听地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Broker 监听地址
     */
    private String listenersHostName() {
        return super.currentNodeHostname;
    }

    /**
     * Description: 获取 Kafka 日志存储目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String kafkaLogDirs() {
        return String.format(
                "%s/%s",
                super.logDir(),
                SERVICE_NAME
        );
    }

    /**
     * Description: 获取 Zookeeper 集群地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String zookeeperConnect() {
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
