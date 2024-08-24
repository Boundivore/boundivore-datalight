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
package cn.boundivore.dl.plugin.kerberos.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.NoSuchElementException;


/**
 * Description: 配置 krb5.conf 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/8/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicKrb5Conf extends AbstractConfigLogic {

    public ConfigLogicKrb5Conf(PluginConfig pluginConfig) {
        super(pluginConfig);
    }


    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{kdc}}
        String kdc = this.kdc();

        // {{admin_server}}
        String adminServer = this.adminServer();

        // {{LOG_DIR}}
        String logDir = super.logDir();

        return replacedTemplated
                .replace(
                        "{{kdc}}",
                        kdc
                )
                .replace(
                        "{{admin_server}}",
                        adminServer
                )
                .replace(
                        "{{LOG_DIR}}",
                        logDir
                )
                ;
    }

    /**
     * Description: 返回 KerberosServer 组件所在节点主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String KerberosServer 组件所在节点主机名
     */
    private String adminServer() {
        return super.currentMetaService.getMetaComponentMap()
                .values()
                .stream()
                .filter(v -> "KerberosServer".equals(v.getComponentName()))
                .map(PluginConfig.MetaComponent::getHostname)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("KerberosServer 未找到"));
    }

    /**
     * Description: 返回 KerberosServer 组件所在节点主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String KerberosServer 组件所在节点主机名
     */
    private String kdc() {
        return super.currentMetaService.getMetaComponentMap()
                .values()
                .stream()
                .filter(v -> "KerberosServer".equals(v.getComponentName()))
                .map(PluginConfig.MetaComponent::getHostname)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("KerberosServer 未找到"));
    }

}
