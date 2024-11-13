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

import java.sql.*;

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
        String sql = String.format(
                "ALTER SYSTEM ADD FOLLOWER \"%s:%s\"",
                ip,
                port
        );
        return execSQL(connection, sql);
    }

    @Override
    public String addFeObserver(Connection connection, String ip, String port) {
        String sql = String.format(
                "ALTER SYSTEM ADD OBSERVER \"%s:%s\"",
                ip,
                port
        );
        return execSQL(connection, sql);
    }

    @Override
    public String addBe(Connection connection, String ip, String port) {
        String sql = String.format(
                "ALTER SYSTEM ADD BACKEND \"%s:%s\"",
                ip,
                port
        );
        return execSQL(connection, sql);
    }


    /**
     * Description: 执行 SQL
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param connection JDBC 连接器
     * @param sql 待执行的 SQL 语句
     * @return String 执行结果
     */
    private String execSQL(Connection connection, String sql) {
        log.info("Exec sql: {}", sql);

        try (Statement stmt = connection.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            log.info("Rows affected: {}", rowsAffected);
            return "Rows affected: " + rowsAffected;
        } catch (SQLException e) {
            log.error("Error executing SQL: {}", e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Description: For Test
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     */
    public static void main(String[] args) {
        DorisClusterOperator dorisClusterOperator = new DorisClusterOperator();

        // 创建连接
        try (Connection connection = dorisClusterOperator.initConnector(
                "root",
                "",
                "192.168.137.10",
                "7030",
                ""
        )) {
//            dorisClusterOperator.addFeFollower(connection, "192.168.137.10", "7010");
            String execResult = dorisClusterOperator.addBe(connection, "192.168.137.11", "7050");
            System.out.println("EXECResult: " + execResult);

            // 执行 SQL
            try (Statement stmt = connection.createStatement();
                 ResultSet result = stmt.executeQuery("show backends")) {
                ResultSetMetaData metaData = result.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (result.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.println(result.getObject(i));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
