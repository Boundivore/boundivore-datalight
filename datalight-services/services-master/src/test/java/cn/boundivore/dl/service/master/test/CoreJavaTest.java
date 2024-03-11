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
package cn.boundivore.dl.service.master.test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: 临时 CoreJava 测试
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class CoreJavaTest {

    @Test
    public void javaPerformanceTest() {
        List<Integer> initList1 = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            initList1.add(i);
        }

        List<Integer> initList2 = new ArrayList<>();
        for (int i = 11; i <= 20; i++) {
            initList2.add(i);
        }

        List<Integer> resultList = new LinkedList<>(initList1);
        resultList.addAll(0, initList2);

        System.out.println(resultList);

    }

    @SneakyThrows
    @Test
    public void testMetaStoreDB() {
        String dbUrl = "jdbc:mysql://node01:3306/db_hive_metastore?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8&useSSL=false";
        String user = "root";
        String password = "1qaz!QAZ";
        Class.forName("com.mysql.jdbc.Driver");


        log.info(String.format("MetaStore DB URL: %s", dbUrl));
        log.info(String.format("MetaStore DB User: %s", user));

        Connection conn = DriverManager.getConnection(dbUrl, user, password);
        DatabaseMetaData meta = conn.getMetaData();

        ResultSet tables = meta.getTables(null, null, null, null);

        log.info(String.format("MaxTablesInSelect: %s", tables.next()));
    }
}
