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
package cn.boundivore.dl.plugin.doris.jdbc;

import cn.boundivore.dl.plugin.base.jdbc.AbstractJDBCOperator;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

/**
 * Description: 操作数据库变更 Doris 集群
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/11/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class DorisClusterOperator extends AbstractJDBCOperator {
    @Override
    public String addFeFollower(Connection connection, String ip, String port) {
        String sql = "ALTER SYSTEM ADD FOLLOWER ip:port";
        return null;
    }

    @Override
    public String addFeObserver(Connection connection, String ip, String port) {
        String sql = "ALTER SYSTEM ADD OBSERVER ip:port";
        return null;
    }

    @Override
    public String addBe(Connection connection, String ip, String port) {
        String sql = "ALTER SYSTEM ADD BACKEND ip:port";
        return null;
    }
    /*
     String user = "user_name";
        String password = "user_password";
        String newUrl = "jdbc:mysql://FE_IP:FE_PORT/demo？useUnicode=true&characterEncoding=utf8&useTimezone=true&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        try {
            Connection myCon = DriverManager.getConnection(newUrl, user, password);
            Statement stmt = myCon.createStatement();
            ResultSet result = stmt.executeQuery("show databases");
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (result.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.println(result.getObject(i));
                }
            }
        } catch (SQLException e) {
            log.error("get JDBC connection exception.", e);
        }
     */
}
