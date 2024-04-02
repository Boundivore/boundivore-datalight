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
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Component
@Slf4j
public class SpringContextUtil implements ApplicationContextAware {

    public static final String USER_DATALIGHT = "datalight";
    public static final String USER_GROUP = "datalight";

    public static final String PRIVATE_KEY_PATH = "/root/.ssh/id_rsa";

    public static ApplicationHome APP_HOME;
    public static String APP_DIR;

    public static String APP_PARENT_DIR;

    public static String ASSISTANT_DIR;

    public static String BIN_DIR;

    public static String CONF_DIR;

    public static String CONF_ENV_DIR;

    public static String CONF_SERVICE_DIR;

    public static String CONF_WEB_DIR;

    public static String DOCS_DIR;

    public static String NODE_DIR;

    public static String NODE_CONF_DIR;

    public static String NODE_SCRIPTS_DIR;

    public static String ORM_DIR;

    public static String PLUGINS_DIR;

    public static String SCRIPTS_DIR;


    static {
        init();
    }

    public static void init() {
        APP_HOME = new ApplicationHome(SpringContextUtil.class);
        if (APP_HOME.getSource() == null) {
            APP_DIR = "/opt/datalight/app/X";
            log.error("当前无法定位当前进程家目录，将使用默认目录: {}", APP_DIR);
        } else {
            APP_DIR = APP_HOME.getSource().getAbsolutePath();
            log.info("当前 APP_DIR: {}", APP_DIR);
        }

        APP_PARENT_DIR = FileUtil.getParent(APP_DIR, 2);
        log.info("APP_PARENT_DIR :{}", APP_PARENT_DIR);

        ASSISTANT_DIR = APP_PARENT_DIR + File.separator + "assistant";
        BIN_DIR = APP_PARENT_DIR + File.separator + "bin";
        CONF_DIR = APP_PARENT_DIR + File.separator + "conf";
        CONF_ENV_DIR = CONF_DIR + File.separator + "env";

        CONF_SERVICE_DIR = CONF_DIR + File.separator + "service";
        CONF_WEB_DIR = CONF_DIR + File.separator + "web";
        DOCS_DIR = APP_PARENT_DIR + File.separator + "docs";
        NODE_DIR = APP_PARENT_DIR + File.separator + "node";
        NODE_CONF_DIR = NODE_DIR + File.separator + "conf";
        NODE_SCRIPTS_DIR = NODE_DIR + File.separator + "scripts";
        ORM_DIR = APP_PARENT_DIR + File.separator + "orm";
        PLUGINS_DIR = APP_PARENT_DIR + File.separator + "plugins";
        SCRIPTS_DIR = APP_PARENT_DIR + File.separator + "scripts";
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
