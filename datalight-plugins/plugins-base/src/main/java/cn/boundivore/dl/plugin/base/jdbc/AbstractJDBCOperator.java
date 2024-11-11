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
package cn.boundivore.dl.plugin.base.jdbc;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * Description: JDBC 操作抽象类
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/11/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public abstract class AbstractJDBCOperator implements IJDBCOperator{

    @Override
    public Connection initConnector(String dbUser,
                                    String dbPassword,
                                    String ip,
                                    String port,
                                    String database) throws SQLException{
        if(StrUtil.isBlank(database)){
            database = "";
        }

        String newUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8&useTimezone=true&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true",
                ip,
                port,
                database
        );

        return DriverManager.getConnection(newUrl, dbUser, dbPassword);
    }
}
