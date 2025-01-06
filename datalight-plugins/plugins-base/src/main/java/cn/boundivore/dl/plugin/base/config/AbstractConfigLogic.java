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
package cn.boundivore.dl.plugin.base.config;

import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Description: 通用逻辑处理父类，保存通用变量或通用逻辑等
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public abstract class AbstractConfigLogic {
    protected PluginConfig pluginConfig;

    protected PluginConfig.MetaService currentMetaService;
    protected PluginConfig.MetaComponent currentMetaComponent;
    protected Long currentNodeId;
    protected String currentNodeIp;
    protected String currentNodeHostname;

    public AbstractConfigLogic(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;

        this.currentMetaService = this.pluginConfig.getCurrentMetaService();
        this.currentMetaComponent = this.pluginConfig.getCurrentMetaComponent();

        this.currentNodeId = this.pluginConfig.getCurrentNodeId();
        this.currentNodeIp = this.pluginConfig.getCurrentNodeIp();
        this.currentNodeHostname = this.pluginConfig.getCurrentNodeHostname();
    }

    public abstract String config(File file, String replacedTemplated);

    /**
     * Description: 打印当前处理的配置文件名称
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/29
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param file     配置文件
     * @param hostname 所在节点
     */
    protected void printFilename(String hostname, File file) {
        log.info(
                String.format(
                        "准备处理文件[%s]: %s",
                        hostname,
                        file.getName()
                )
        );
    }

    /**
     * Description: 获取 {{JAVA_HOME}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{JAVA_HOME}} 真实值
     */
    protected String javaHomeDir() {
        String javaHome = this.pluginConfig.getUnixEnv().getJAVA_HOME();
        Assert.notNull(
                javaHome,
                () -> new RuntimeException("无法读取环境变量 JAVA_HOME")
        );
        return javaHome;
    }

    /**
     * Description: 获取 {{DATALIGHT_DIR}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{DATALIGHT_DIR}} 真实值
     */
    protected String datalightDir() {
        String datalightDir = this.pluginConfig.getUnixEnv().getDATALIGHT_DIR();
        Assert.notNull(
                datalightDir,
                () -> new RuntimeException("无法读取环境变量 DATALIGHT_DIR")
        );
        return datalightDir;
    }

    /**
     * Description: 获取 {{SERVICE_DIR}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{SERVICE_DIR}} 真实值
     */
    protected String serviceDir() {
        String serviceDir = this.pluginConfig.getUnixEnv().getSERVICE_DIR();
        Assert.notNull(
                serviceDir,
                () -> new RuntimeException("无法读取环境变量 SERVICE_DIR")
        );
        return serviceDir;
    }

    /**
     * Description: 获取 {{LOG_DIR}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{LOG_DIR}} 真实值
     */
    protected String logDir() {
        String logDir = this.pluginConfig.getUnixEnv().getLOG_DIR();
        Assert.notNull(
                logDir,
                () -> new RuntimeException("无法读取环境变量 LOG_DIR")
        );
        return logDir;
    }

    /**
     * Description: 获取 {{PID_DIR}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{PID_DIR}} 真实值
     */
    protected String pidDir() {
        String pidDir = this.pluginConfig.getUnixEnv().getPID_DIR();
        Assert.notNull(
                pidDir,
                () -> new RuntimeException("无法读取环境变量 PID_DIR")
        );
        return pidDir;
    }

    /**
     * Description: 获取 {{DATA_DIR}}
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return {{DATA_DIR}} 真实值
     */
    protected String dataDir() {
        String dataDir = this.pluginConfig.getUnixEnv().getDATA_DIR();
        Assert.notNull(
                dataDir,
                () -> new RuntimeException("无法读取环境变量 DATA_DIR")
        );
        return dataDir;
    }

}
