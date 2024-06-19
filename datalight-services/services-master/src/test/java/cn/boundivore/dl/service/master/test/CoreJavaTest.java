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

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;

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

    @SneakyThrows
    @Test
    public void testSwaggerAPI() {
        // Swagger JSON文件的URL
        String swaggerJsonUrl = "http://node01:8001/v3/api-docs?group=datalight";

        // 创建URL对象
        URL url = new URL(swaggerJsonUrl);
        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 设置请求方法
        connection.setRequestMethod("GET");
        // 连接
        connection.connect();

        // 检查响应码
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed to get swagger JSON, HTTP response code: " + responseCode);
        }

        // 读取响应
        InputStream responseStream = connection.getInputStream();

        // 使用Jackson解析JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseStream);

        // 提取paths节点，包含所有API路径
        JsonNode pathsNode = rootNode.path("paths");

        // 遍历所有接口路径
        Iterator<Map.Entry<String, JsonNode>> fields = pathsNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String apiPath = field.getKey();
            JsonNode pathNode = field.getValue();

            // 遍历该路径下的所有HTTP方法
            Iterator<Map.Entry<String, JsonNode>> methods = pathNode.fields();
            while (methods.hasNext()) {
                Map.Entry<String, JsonNode> method = methods.next();
                String httpMethod = method.getKey();
                JsonNode methodDetails = method.getValue();

                // 获取接口描述
                String apiDescription = methodDetails.has("summary") ?
                        methodDetails.get("summary").asText() : "No description";

                // 打印接口路径、HTTP方法和描述
                System.out.println("API Path: " + apiPath + ", Method: " + httpMethod + ", Description: " + apiDescription);
            }
        }

        // 关闭连接
        connection.disconnect();
    }

    @SneakyThrows
    @Test
    public void testQianfanAPI() {
        String accessKey = "";
        String secretKey = "";

        Qianfan qianfan = new Qianfan(
                accessKey,
                secretKey
        );

        ChatResponse response = qianfan.chatCompletion()
                .model("ERNIE-Speed-128K") // 使用model指定预置模型
                // .endpoint("completions_pro") // 也可以使用endpoint指定任意模型 (二选一)
                .addMessage("user", "你好") // 添加用户消息 (此方法可以调用多次，以实现多轮对话的消息传递)
                .temperature(0.7) // 自定义超参数
                .execute(); // 发起请求
        System.out.println(response);

    }
}
