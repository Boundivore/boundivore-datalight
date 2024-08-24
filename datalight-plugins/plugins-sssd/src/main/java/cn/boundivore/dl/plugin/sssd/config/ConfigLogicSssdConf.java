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
package cn.boundivore.dl.plugin.sssd.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.NoSuchElementException;


/**
 * Description: 配置 sssd.conf 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/8/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicSssdConf extends AbstractConfigLogic {

    public ConfigLogicSssdConf(PluginConfig pluginConfig) {
        super(pluginConfig);
    }


    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{ldap_uri}}
        String ldapUri = this.ldapUri();

        // {{krb5_server}}
        String krb5Server = this.krb5Server();

        return replacedTemplated
                .replace(
                        "{{ldap_uri}}",
                        ldapUri
                )
                .replace(
                        "{{krb5_server}}",
                        krb5Server
                )
                ;
    }

    /**
     * Description: 获取 krb5Server 所在节点主机名
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String krb5Server 所在节点主机名
     */
    private String krb5Server() {
        return super.pluginConfig
                .getMetaServiceMap()
                .get("KERBEROS")
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(v -> "KerberosServer".equals(v.getComponentName()))
                .map(PluginConfig.MetaComponent::getHostname)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("未找到 KerberosServer 实例"));
    }

    /**
     * Description: 获取 ldapUri
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/8/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String ldapUri
     */
    private String ldapUri() {

        String ldapServerHostname = super.pluginConfig
                .getMetaServiceMap()
                .get("LDAP")
                .getMetaComponentMap()
                .values()
                .stream()
                .filter(v -> "LDAPServer".equals(v.getComponentName()))
                .map(PluginConfig.MetaComponent::getHostname)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("未找到 LDAPServer 实例"));


        return String.format(
                "ldap://%s",
                ldapServerHostname

        );
    }

}
