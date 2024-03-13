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

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

import static cn.boundivore.dl.plugin.hive.config.ConfigLogicJmxYaml.SERVICE_NAME;

/**
 * Description: 配置 catalina.sh 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicCatalinaSh extends AbstractConfigLogic {


    public ConfigLogicCatalinaSh(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{CATALINA_PID}}
        String catalinaPid = this.catalinaPid();

        // {{CATALINA_OUT}}
        String catalinaOut = this.catalinaOut();

        return replacedTemplated
                .replace(
                        "{{CATALINA_PID}}",
                        catalinaPid
                )
                .replace(
                        "{{CATALINA_OUT}}",
                        catalinaOut
                )

                // TezUI
                .replace(
                        "{{jmxRemotePort_TezUI}}",
                        PortConstants.getRemotePort(
                                SERVICE_NAME,
                                "TezUI"
                        )
                )
                .replace(
                        "{{jmxExporterPort_TezUI}}",
                        PortConstants.getExporterPort(
                                SERVICE_NAME,
                                "TezUI"
                        )
                )
                ;
    }

    /**
     * Description: 获取 TezUI 所在容器（Tomcat）的进程 ID 文件所在目录
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
    private String catalinaPid() {
        return String.format(
                "%s/HIVE",
                super.pidDir()
        );
    }

    /**
     * Description: 获取 Catalina 标准输出路径
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
    private String catalinaOut() {
        return String.format(
                "%s/HIVE",
                super.logDir()
        );
    }

}
