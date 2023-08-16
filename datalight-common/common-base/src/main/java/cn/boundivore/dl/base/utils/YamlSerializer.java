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
package cn.boundivore.dl.base.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

/**
 * Description: Yaml 序列化器
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class YamlSerializer {
    private final static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    /**
     * Description: 将 YAML 字符串解析为指定的 JavaBean 实体
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: JsonProcessingException
     *
     * @param yamlStr 字符串
     * @param valueType 要转换的目标类型
     * @return 转换后的对象
     */
    public static <T> T toObject(String yamlStr, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(yamlStr, valueType);
    }

    /**
     * Description: 将 YAML 字符串解析为指定的 JavaBean 实体
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: JsonProcessingException
     *
     * @param yamlFile 文件
     * @param valueType 要转换的目标类型
     * @return 转换后的对象
     */
    public static <T> T toObject(File yamlFile, Class<T> valueType) throws IOException {
        return objectMapper.readValue(yamlFile, valueType);
    }
}
