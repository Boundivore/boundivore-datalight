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
package cn.boundivore.dl.service.master.cache;

import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Description: TODO
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/16
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Component
@Slf4j
public class MySQLCache {
    // Database
    public static String DB_HOST;
    public static String DB_PORT;
    public static String DB_NAME;
    public static String DB_USER;
    public static String DB_PASSWORD;

    @Value("${server.datalight.database.mysql.host}")
    public void setDbHost(String dbHost) {
        MySQLCache.DB_HOST = dbHost;
    }

    @Value("${server.datalight.database.mysql.port}")
    public void setDbPort(String dbPort) {
        MySQLCache.DB_PORT = dbPort;
    }

    @Value("${server.datalight.database.mysql.dbName}")
    public void setDbName(String dbName) {
        MySQLCache.DB_NAME = dbName;
    }

    @Value("${server.datalight.database.mysql.user}")
    public void setDbUser(String dbUser) {
        MySQLCache.DB_USER = dbUser;
    }

    @Value("${server.datalight.database.mysql.password}")
    public void setDbPassword(String dbPassword) {
        MySQLCache.DB_PASSWORD = dbPassword;
    }

    @PostConstruct
    public void init() {
        log.info(
                "DB_HOST: {}, DB_PORT: {}, DB_NAME: {}, DB_USER: {}, DB_PASSWORD: {}",
                DB_HOST,
                DB_PORT,
                DB_NAME,
                DB_USER,
                DB_PASSWORD
        );
    }

}
