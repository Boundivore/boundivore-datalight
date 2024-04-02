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
package cn.boundivore.dl.cloud.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringContextUtilTest {
    /**
     * TEST IP
     */
    public static final String MASTER_IP_TEST = "192.168.137.10";
    public static final String MASTER_IP_GATEWAY_TEST = "192.168.137.1";
    public static final String MASTER_HOSTNAME_TEST = "WINDOWS";

    /**
     * TEST Link
     */
    private static final String ROOT_PATH = "D:/datalight";

    private static final String APP_PARENT_DIR = "/opt/datalight";
    // TEST private_key
    public static final String PRIVATE_KEY_PATH = ROOT_PATH + "/z.node01/id_rsa";

    // TEST conf/env
    public static final String CONF_ENV_DIR_REMOTE = APP_PARENT_DIR + "/conf/env";

    public static final String CONF_ENV_DIR_LOCAL = ROOT_PATH + "/conf/env";

    // TEST conf/scripts
    public static final String NODE_CONF_DIR_LOCAL = ROOT_PATH + "/node/conf";

    // TEST app parent
    public static final String APP_PARENT_DIR_REMOTE_LOCAL = APP_PARENT_DIR;
    public static final String APP_PARENT_DIR_LOCAL_LOCAL = ROOT_PATH;

    // TEST ./node
    public static final String NODE_DIR_REMOTE = APP_PARENT_DIR + "/node";
    public static final String NODE_DIR_LOCAL = ROOT_PATH + "/node";


    // TEST node/scripts
    public static final String NODE_SCRIPTS_DIR_REMOTE = APP_PARENT_DIR + "/node/scripts";
    public static final String NODE_SCRIPTS_DIR_LOCAL = ROOT_PATH + "/node/scripts";

    // TEST conf/service
    public static final String CONF_SERVICE_DIR = ROOT_PATH + "/conf/service";

    public static final String CONF_WEB_DIR = ROOT_PATH + "/conf/web";

    //TEST plugins
    public static final String PLUGINS_DIR_LOCAL = ROOT_PATH + "/plugins";

    // ./datalight/plugins
    public static final String PLUGINS_DIR_REMOTE = APP_PARENT_DIR + "/plugins";

    // ./datalight/scripts
    public static final String SCRIPTS_DIR_REMOTE = APP_PARENT_DIR + "/scripts";

    public static final String BIN_DIR_REMOTE = APP_PARENT_DIR + "/bin";

}
