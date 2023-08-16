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

import cn.boundivore.dl.cloud.config.async.executors.CustomThreadPoolTaskExecutor;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Component
@Slf4j
public class SpringContextUtil implements ApplicationContextAware {

    public static final String USER_DATALIGHT = "datalight";
    public static final String USER_GROUP = "datalight";

    // Database
    public static String DB_HOST;
    public static String DB_PORT;
    public static String DB_NAME;
    public static String DB_USER;
    public static String DB_PASSWORD;


    public static final ApplicationHome APP_HOME = new ApplicationHome(SpringContextUtil.class);

    public static final String APP_DIR = APP_HOME.getSource().getAbsolutePath();

    // ./datalight
    public static final String APP_PARENT_DIR = FileUtil.getParent(APP_DIR, 2);

    // ./datalight/assistant
    public static final String ASSISTANT_DIR = APP_PARENT_DIR + File.separator + "assistant";

    // ./datalight/bin
    public static final String BIN_DIR = APP_PARENT_DIR + File.separator + "bin";

    // ./datalight/conf
    public static final String CONF_DIR = APP_PARENT_DIR + File.separator + "conf";

    // ./datalight/conf/env
    public static final String CONF_ENV_DIR = CONF_DIR + File.separator + "env";

    // ./datalight/conf/service
    public static final String CONF_SERVICE_DIR = CONF_DIR + File.separator + "service";

    // ./datalight/docs
    public static final String DOCS_DIR = APP_PARENT_DIR + File.separator + "docs";

    // ./datalight/node
    public static final String NODE_DIR = APP_PARENT_DIR + File.separator + "node";

    // ./datalight/node/conf
    public static final String NODE_CONF_DIR = NODE_DIR + File.separator + "conf";

    // ./datalight/node/scripts
    public static final String NODE_SCRIPTS_DIR = NODE_DIR + File.separator + "scripts";


    // ./datalight/orm
    public static final String ORM_DIR = APP_PARENT_DIR + File.separator + "orm";

    // ./datalight/plugins
    public static final String PLUGINS_DIR = APP_PARENT_DIR + File.separator + "plugins";

    // ./datalight/scripts
    public static final String SCRIPTS_DIR = APP_PARENT_DIR + File.separator + "scripts";

    public static final String PRIVATE_KEY_PATH = "~/.ssh/id_rsa";

    @PostConstruct
    public void init() {
        log.info("APP_DIR: {}", APP_DIR);

        log.info(
                "DB_HOST: {}, DB_PORT: {}, DB_NAME: {}, DB_USER: {}, DB_PASSWORD: {}",
                DB_HOST,
                DB_PORT,
                DB_NAME,
                DB_USER,
                DB_PASSWORD
        );
    }

    @Value("${server.datalight.database.mysql.host}")
    public void setDbHost(String dbHost) {
        SpringContextUtil.DB_HOST = dbHost;
    }

    @Value("${server.datalight.database.mysql.port}")
    public void setDbPort(String dbPort) {
        SpringContextUtil.DB_PORT = dbPort;
    }

    @Value("${server.datalight.database.mysql.dbName}")
    public void setDbName(String dbName) {
        SpringContextUtil.DB_NAME = dbName;
    }

    @Value("${server.datalight.database.mysql.user}")
    public void setDbUser(String dbUser) {
        SpringContextUtil.DB_USER = dbUser;
    }

    @Value("${server.datalight.database.mysql.password}")
    public void setDbPassword(String dbPassword) {
        SpringContextUtil.DB_PASSWORD = dbPassword;
    }


    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) applicationContext.getBean(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<?> clz) throws BeansException {
        return (T) applicationContext.getBean(clz);
    }


    public static CustomThreadPoolTaskExecutor getCustomExecutor() {
        return getBean("customExecutor");
    }

    public static ThreadPoolTaskExecutor getCommonExecutor() {
        return getBean("commonExecutor");
    }

    public static ScheduledThreadPoolExecutor getScheduleExecutor() {
        return getBean("ServerScheduler");
    }

    public static RestTemplate getRestTemplate() {
        return getBean(RestTemplate.class);
    }
}
