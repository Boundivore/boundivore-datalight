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

import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.config.AbstractConfigLogic;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * Description: 配置 Ranger Admin install.properties 文件
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/9/20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ConfigLogicRangerAdminInstallProperties extends AbstractConfigLogic {

    public ConfigLogicRangerAdminInstallProperties(PluginConfig pluginConfig) {
        super(pluginConfig);
    }

    @Override
    public String config(File file, String replacedTemplated) {
        super.printFilename(
                pluginConfig.getCurrentMetaComponent().getHostname(),
                file
        );

        // {{SERVICE_DIR}}/ranger-admin/db/mysql/mysql-connector-java-5.1.47.jar
        // {{SERVICE_DIR}}
        String serviceDir = super.serviceDir();

        // {{db_root_user}}
        String dbRootUser = this.dbRootUser();

        // {{db_root_password}}
        String dbRootPassword = this.dbRootPassword();

        // {{db_name}}
        String dbName = this.dbName();

        // {{db_host}}
        String dbHost = this.dbHost();

        // {{db_port}}
        String dbPort = this.dbPort();

        // {{rangerAdmin_password}}
        String rangerAdminPassword = this.rangerAdminPassword();

        // {{rangerTagsync_password}}
        String rangerTagSyncPassword = this.rangerTagSyncPassword();

        // {{rangerUsersync_password}}
        String rangerUserSyncPassword = this.rangerUserSyncPassword();

        // {{keyadmin_password}}
        String keyAdminPassword = this.keyAdminPassword();

        // {{solr_host}}
        String solrHost = this.solrHost();

        // {{policymgr_external_url}} http://node01:6080
        String policymgrExternalUrl = this.policymgrExternalUrl();

        // {{unix_user}}
        String unixUser = this.unixUser();

        // {{unix_user_pwd}}
        String unixUserPwd = this.unixUserPwd();

        // {{unix_group}}
        String unixGroup = this.unixGroup();

        // {{hadoop_conf}}
        String hadoopConf = this.hadoopConf();

        // {{RANGER_ADMIN_LOG_DIR}} /data/datalight/logs/RANGER
        String rangerAdminLogDir = this.rangerAdminLogDir();

        // {{RANGER_PID_DIR_PATH}} /data/datalight/pids/RANGER
        String rangerPidDirPath = this.rangerPidDirPath();

        // {{LOGFILE}} /data/datalight/logs/RANGER/logfile
        String logFile = this.logFile();


        return replacedTemplated
                .replace(
                        "{{SERVICE_DIR}}",
                        serviceDir
                )
                .replace(
                        "{{db_root_user}}",
                        dbRootUser
                )
                .replace(
                        "{{db_root_password}}",
                        dbRootPassword
                )
                .replace(
                        "{{db_name}}",
                        dbName
                )
                .replace(
                        "{{db_host}}",
                        dbHost
                )
                .replace(
                        "{{db_port}}",
                        dbPort
                )
                .replace(
                        "{{rangerAdmin_password}}",
                        rangerAdminPassword
                )
                .replace(
                        "{{rangerTagsync_password}}",
                        rangerTagSyncPassword
                )
                .replace(
                        "{{rangerUsersync_password}}",
                        rangerUserSyncPassword
                )
                .replace(
                        "{{keyadmin_password}}",
                        keyAdminPassword
                )
                .replace(
                        "{{solr_host}}",
                        solrHost
                )
                .replace(
                        "{{policymgr_external_url}}",
                        policymgrExternalUrl
                )
                .replace(
                        "{{unix_user}}",
                        unixUser
                )
                .replace(
                        "{{unix_user_pwd}}",
                        unixUserPwd
                )
                .replace(
                        "{{unix_group}}",
                        unixGroup
                )
                .replace(
                        "{{hadoop_conf}}",
                        hadoopConf
                )
                .replace(
                        "{{RANGER_ADMIN_LOG_DIR}}",
                        rangerAdminLogDir
                )
                .replace(
                        "{{RANGER_PID_DIR_PATH}}",
                        rangerPidDirPath
                )
                .replace(
                        "{{LOGFILE}}",
                        logFile
                )
                ;
    }

    /**
     * Description: ews/webapp 日志文件路径
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String ews/webapp 日志文件路径
     */
    private String logFile() {
        return String.format(
                "%s/RANGER/logfile",
                super.logDir()
        );
    }

    /**
     * Description: Ranger Admin 进程号文件目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String  Ranger Admin 进程号文件目录
     */
    private String rangerPidDirPath() {
        return String.format(
                "%s/RANGER",
                super.pidDir()
        );
    }

    /**
     * Description: Ranger Admin 日志目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Ranger Admin 日志目录
     */
    private String rangerAdminLogDir() {
        return String.format(
                "%s/RANGER",
                super.logDir()
        );
    }

    /**
     * Description: Hadoop 配置文件路径(HDFS)，用于 Kerberos
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Hadoop 配置文件路径(HDFS)
     */
    private String hadoopConf() {
        return String.format(
                "%s/HDFS/etc/hadoop",
                super.serviceDir()
        );
    }

    /**
     * Description: Linux DataLight 用户组（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String "datalight"
     */
    private String unixGroup() {
        return "datalight";
    }

    /**
     * Description: Linux DataLight 用户密码（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String "12345678"
     */
    private String unixUserPwd() {
        return "12345678";
    }

    /**
     * Description: Linux DataLight 用户名（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String "datalight"
     */
    private String unixUser() {
        return "datalight";
    }

    /**
     * Description: 拼接 Ranger Admin Web Url 地址
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String Ranger Admin Web Url 地址
     */
    private String policymgrExternalUrl() {
        String rangerAdminHostname = super.currentMetaService.getMetaComponentMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().contains("RangerAdmin"))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(PluginConfig.MetaComponent::getHostname)
                .orElseThrow(() -> new RuntimeException("未找到 RangerAdmin 组件"));

        return String.format(
                "http://%s:6080",
                rangerAdminHostname
        );
    }

    /**
     * Description: Solr 主机名或 IP
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String
     */
    private String solrHost() {
        PluginConfig.MetaService solrMetaService = super.pluginConfig
                .getMetaServiceMap()
                .get("SOLR");

        return solrMetaService.getMetaComponentMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().contains("SolrServer"))
                .map(Map.Entry::getValue)
                .filter(metaComponent -> metaComponent.getComponentState() == SCStateEnum.STARTED)
                .findFirst()
                .map(PluginConfig.MetaComponent::getHostname)
                .orElseThrow(() -> new RuntimeException("未找到处于 STARTED 状态的 SolrServer"));
    }

    /**
     * Description: UserSync 密码 （此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String "Datalight123!"
     */
    private String keyAdminPassword() {
        return "Datalight123!";
    }

    /**
     * Description: UserSync 密码 （此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String "Datalight123!"
     */
    private String rangerUserSyncPassword() {
        return "Datalight123!";
    }

    /**
     * Description: TagSync 密码 （此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String "Datalight123!"
     */
    private String rangerTagSyncPassword() {
        return "Datalight123!";
    }

    /**
     * Description: Ranger 页面登录密码 （此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String "Datalight123!"
     */
    private String rangerAdminPassword() {
        return "Datalight123!";
    }

    /**
     * Description: 数据库端口号（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 3306
     */
    private String dbPort() {
        return "3306";
    }

    /**
     * Description: 数据库主机名或 IP（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String localhost
     */
    private String dbHost() {
        return "localhost";
    }

    /**
     * Description: Ranger 数据库名称 db_ranger
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String db_ranger
     */
    private String dbName() {
        return "db_ranger";
    }

    /**
     * Description: 数据库 root 密码（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 如果没有配置，则默认返回 1qaz!QAZ
     */
    private String dbRootPassword() {
        return "1qaz!QAZ";
    }

    /**
     * Description: 数据库 root 用户名（此密码会在预配置时被替换，如果没有替换，则返回函数中的默认值）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/9/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return String 如果没有配置，则默认返回 root
     */
    private String dbRootUser() {
        return "root";
    }

}
