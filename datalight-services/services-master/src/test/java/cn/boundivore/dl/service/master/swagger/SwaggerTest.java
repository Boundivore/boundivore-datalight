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
package cn.boundivore.dl.service.master.swagger;

import cn.boundivore.dl.api.third.define.IThirdPrometheusAPI;
import cn.boundivore.dl.api.third.define.IThirdSwaggerAPI;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.RemoteInvokePrometheusService;
import cn.boundivore.dl.service.master.service.RemoteInvokeSwaggerService;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Description: 测试 Prometheus API
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/16
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.yaml")
@Slf4j
public class SwaggerTest {

    @Autowired
    private RemoteInvokeSwaggerService remoteInvokeSwaggerService;

    private final static String SWAGGER_HOST = "node01";
    private final static String SWAGGER_PORT = "8001";

    private IThirdSwaggerAPI iThirdSwaggerAPI;

    @PostConstruct
    public void init() {
        this.iThirdSwaggerAPI = this.remoteInvokeSwaggerService.iThirdSwaggerAPI(
                SWAGGER_HOST,
                SWAGGER_PORT
        );
    }

    @Test
    @SneakyThrows
    public void getSwaggerApiDesc() {

        Result<String> result = null;
        try {
            result = this.iThirdSwaggerAPI.getSwaggerApiInfo(new HashMap<String, String>(){
                private static final long serialVersionUID = 4055118102147838742L;

                {
                    put("group", "datalight");
                }
            });
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }

        if(result != null){
            log.info(result.toString());

            // 使用Jackson解析JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(result.getData());

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
        }
    }
}
