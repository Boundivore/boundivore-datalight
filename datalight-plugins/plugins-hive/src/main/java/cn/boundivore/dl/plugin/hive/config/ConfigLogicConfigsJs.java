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
package cn.boundivore.dl.plugin.hive.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.stream.Collectors;

/**
 * Description: 配置 config.js 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicConfigsJs extends AbstractConfigLogic {


    public ConfigLogicConfigsJs(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{timeline}}
        String timeline = this.timeline();

        // {{rm}}
        String rm = this.rm();

        // {{timeZone}}
        String timeZone = this.timeZone();


        return replacedTemplated
                .replace(
                        "{{timeline}}",
                        timeline
                )
                .replace(
                        "{{rm}}",
                        rm
                )
                .replace(
                        "{{timeZone}}",
                        timeZone
                )
                ;
    }

    /**
     * Description: 获取 YARN TimelineServer 地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String timeline() {
        String timelineServerHostname = super.pluginConfig
                .getMetaServiceMap()
                .get("YARN")
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("TimelineServer"))
                .collect(Collectors.toList())
                .get(0)
                .getHostname();

        return String.format(
                "http://%s:8188",
                timelineServerHostname
        );
    }

    /**
     * Description: 获取 YARN TimelineServer 地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String rm() {
        String resourceManager1Hostname = super.pluginConfig
                .getMetaServiceMap()
                .get("YARN")
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(i -> i.getComponentName().equals("ResourceManager1"))
                .collect(Collectors.toList())
                .get(0)
                .getHostname();

        return String.format(
                "http://%s:8088",
                resourceManager1Hostname
        );
    }

    /**
     * Description: 获取中国大陆时区
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String timeZone() {
        return "Asia/Shanghai";
    }

}
