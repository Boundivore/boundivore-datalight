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
package cn.boundivore.dl.plugin.solr.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;

import static cn.boundivore.dl.plugin.solr.config.ConfigLogicJmxYaml.SERVICE_NAME;


/**
 * Description: 配置 install.properties 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/9/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicInstallProperties extends AbstractConfigLogic {

    public ConfigLogicInstallProperties(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{ranger_admin_hostname}}
        String rangerAdminHostname = this.rangerAdminHostname();

        // {{REPOSITORY_NAME}} ranger-{服务名称小写}-repository
        String repositoryName = this.repositoryName();

        // {{COMPONENT_INSTALL_DIR_NAME}}
        String componentInstallDirName = this.componentInstallDirName();

        // {{XAAUDIT.SOLR.FILE_SPOOL_DIR}}
        String xaAuditSolrFileSpoolDir = this.xaAuditSolrFileSpoolDir();


        return replacedTemplated
                .replace(
                        "{{ranger_admin_hostname}}",
                        rangerAdminHostname
                )
                .replace(
                        "{{REPOSITORY_NAME}}",
                        repositoryName
                )
                .replace(
                        "{{COMPONENT_INSTALL_DIR_NAME}}",
                        componentInstallDirName
                )
                .replace(
                        "{{FILE_SPOOL_DIR}}",
                        xaAuditSolrFileSpoolDir
                )
                ;
    }

    /**
     * Description: 获取审计日志临时存储目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 审计日志临时存储目录
     */
    private String xaAuditSolrFileSpoolDir() {
        return String.format(
                "%s/%s",
                super.logDir(),
                SERVICE_NAME
        );
    }

    /**
     * Description: 获取插件安装目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 插件安装目录
     */
    private String componentInstallDirName() {
        return String.format(
                "%s/%s",
                super.serviceDir(),
                SERVICE_NAME
        );
    }

    /**
     * Description: 获取 repository name，等价于 Ranger Admin 中的 Service 名称
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String repositoryName() {
        return String.format(
                "ranger-%s-repository",
                SERVICE_NAME.toLowerCase()
        );
    }

    /**
     * Description: 获取 Ranger Admin Hostname
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Ranger Admin Hostname
     */
    private String rangerAdminHostname() {
        return "localhost";
    }

}
