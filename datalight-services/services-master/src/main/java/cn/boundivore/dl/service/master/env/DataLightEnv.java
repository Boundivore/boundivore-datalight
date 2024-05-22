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
package cn.boundivore.dl.service.master.env;

import cn.boundivore.dl.boot.utils.ReactiveAddressUtil;
import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.cloud.utils.SpringContextUtilTest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Description: DataLight 常用环境变量
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/12/26
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Component
@Data
@Slf4j
public class DataLightEnv {
    public static boolean IS_DEBUG;

    public static String MASTER_IP;
    public static String MASTER_REAL_IP;
    public static String MASTER_HOSTNAME;

    public static String PRIVATE_KEY_PATH;

    public static String APP_PARENT_ROOT_DIR_LOCAL;
    public static String APP_PARENT_ROOT_DIR_REMOTE;

    public static String CONF_ENV_DIR;
    public static String CONF_SERVICE_DIR;
    public static String CONF_WEB_DIR;
    public static String CONF_PERMISSION_DIR;
    public static String PLUGINS_DIR_LOCAL;
    public static String PLUGINS_DIR_REMOTE;
    public static String NODE_CONF_DIR;
    public static String NODE_SCRIPTS_DIR_REMOTE;
    public static String NODE_DIR_LOCAL;
    public static String NODE_DIR_REMOTE;
    public static String CONF_ENV_DIR_LOCAL;
    public static String CONF_ENV_DIR_REMOTE;
    public static String SCRIPTS_PATH_DIR_REMOTE;
    public static String BIN_PATH_DIR_REMOTE;

    @Value("${server.datalight.version}")
    private String version;

    @Value("${server.datalight.is-debug}")
    private boolean isDebug;

    @Value("${server.datalight.super-user}")
    private String superUser;
    @Value("${server.datalight.super-user-default-password}")
    private String superUserDefaultPassword;

    @Value("${server.datalight.url.master}")
    private String masterRootPath;
    @Value("${server.datalight.url.master-port}")
    private String masterPort;
    @Value("${server.datalight.url.worker}")
    private String workerRootPath;
    @Value("${server.datalight.url.worker-port}")
    private String workerPort;

    @Value("${aigc.qianfan.access-key}")
    private String qianfanAccessKey;
    @Value("${aigc.qianfan.secret-key}")
    private String qianfanSecretKey;
    @Value("${aigc.qianfan.model}")
    private String qianfanModel;

    @PostConstruct
    public void init() {
        DataLightEnv.IS_DEBUG = this.isDebug;
        DataLightEnv.initEnv();
    }

    public static void initEnv(){
        if (DataLightEnv.IS_DEBUG) {
            MASTER_IP = SpringContextUtilTest.MASTER_IP_TEST;
            MASTER_REAL_IP = SpringContextUtilTest.MASTER_IP_GATEWAY_TEST;
            MASTER_HOSTNAME = SpringContextUtilTest.MASTER_HOSTNAME_TEST;

            PRIVATE_KEY_PATH = SpringContextUtilTest.PRIVATE_KEY_PATH;

            APP_PARENT_ROOT_DIR_LOCAL = SpringContextUtilTest.APP_PARENT_DIR_LOCAL_LOCAL;
            APP_PARENT_ROOT_DIR_REMOTE = SpringContextUtilTest.APP_PARENT_DIR_REMOTE_LOCAL;

            CONF_ENV_DIR = SpringContextUtilTest.CONF_ENV_DIR_LOCAL;
            CONF_SERVICE_DIR = SpringContextUtilTest.CONF_SERVICE_DIR;
            CONF_WEB_DIR = SpringContextUtilTest.CONF_WEB_DIR;
            CONF_PERMISSION_DIR = SpringContextUtilTest.CONF_PERMISSION_DIR;
            PLUGINS_DIR_LOCAL = SpringContextUtilTest.PLUGINS_DIR_LOCAL;
            PLUGINS_DIR_REMOTE = SpringContextUtilTest.PLUGINS_DIR_REMOTE;
            NODE_CONF_DIR = SpringContextUtilTest.NODE_CONF_DIR_LOCAL;
            NODE_SCRIPTS_DIR_REMOTE = SpringContextUtilTest.NODE_SCRIPTS_DIR_REMOTE;
            NODE_DIR_LOCAL = SpringContextUtilTest.NODE_DIR_LOCAL;
            NODE_DIR_REMOTE = SpringContextUtilTest.NODE_DIR_REMOTE;
            CONF_ENV_DIR_LOCAL = SpringContextUtilTest.CONF_ENV_DIR_LOCAL;
            CONF_ENV_DIR_REMOTE = SpringContextUtilTest.CONF_ENV_DIR_REMOTE;
            SCRIPTS_PATH_DIR_REMOTE = SpringContextUtilTest.SCRIPTS_DIR_REMOTE;
            BIN_PATH_DIR_REMOTE = SpringContextUtilTest.BIN_DIR_REMOTE;
        } else {
            MASTER_IP = ReactiveAddressUtil.getInternalIPAddress();
            MASTER_REAL_IP = ReactiveAddressUtil.getInternalIPAddress();
            MASTER_HOSTNAME = ReactiveAddressUtil.getLocalHostName();
            log.info("MASTER_IP: {}, MASTER_REAL_IP: {}, MASTER_HOSTNAME: {}", MASTER_IP, MASTER_REAL_IP, MASTER_HOSTNAME);

            PRIVATE_KEY_PATH = SpringContextUtil.PRIVATE_KEY_PATH;

            APP_PARENT_ROOT_DIR_LOCAL = SpringContextUtil.APP_PARENT_DIR;
            APP_PARENT_ROOT_DIR_REMOTE = SpringContextUtil.APP_PARENT_DIR;

            CONF_ENV_DIR = SpringContextUtil.CONF_ENV_DIR;
            CONF_SERVICE_DIR = SpringContextUtil.CONF_SERVICE_DIR;
            CONF_WEB_DIR = SpringContextUtil.CONF_WEB_DIR;
            CONF_PERMISSION_DIR = SpringContextUtil.CONF_PERMISSION_DIR;
            PLUGINS_DIR_LOCAL = SpringContextUtil.PLUGINS_DIR;
            PLUGINS_DIR_REMOTE = SpringContextUtil.PLUGINS_DIR;
            NODE_CONF_DIR = SpringContextUtil.NODE_CONF_DIR;
            NODE_SCRIPTS_DIR_REMOTE = SpringContextUtil.NODE_SCRIPTS_DIR;
            NODE_DIR_LOCAL = SpringContextUtil.NODE_DIR;
            NODE_DIR_REMOTE = SpringContextUtil.NODE_DIR;
            CONF_ENV_DIR_LOCAL = SpringContextUtil.CONF_ENV_DIR;
            CONF_ENV_DIR_REMOTE = SpringContextUtil.CONF_ENV_DIR;
            SCRIPTS_PATH_DIR_REMOTE = SpringContextUtil.SCRIPTS_DIR;
            BIN_PATH_DIR_REMOTE = SpringContextUtil.BIN_DIR;
        }
    }
}
