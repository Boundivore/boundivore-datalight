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


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Map;

public class JsonUtil {

    public JsonUtil() {
    }

    /**
     * 使用JSON工具把数据转换成json对象
     *
     * @param value 是对解析的集合的类型
     */
    public static String createJsonString(Object value) {
        return JSON.toJSON(value).toString();
    }

    /**
     * 对单个javabean进行解析
     *
     * @param <T>
     * @param json 要解析的json字符串
     * @param cls  实体bean的字节码对象
     * @return
     */
    public static <T> T getObject(String json, Class<T> cls) {
        return JSON.parseObject(json, cls);
    }

    /**
     * 对list类型进行解析
     *
     * @param <T>
     * @param json 要解析的json字符串
     * @param cls  实体bean的字节码对象
     * @return
     */
    public static <T> List<T> getListObject(String json, Class<T> cls) {
        return JSON.parseArray(json, cls);
    }

    /**
     * 对MapString类型数据进行解析
     *
     * @param json 要解析的json字符串
     * @return
     */
    public static Map<String, String> getMapStr(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, String>>() {
        });
    }

    /**
     * 对MapObject类型数据进行解析
     *
     * @param json 要解析的json字符串
     * @return
     */
    public static Map<String, Object> getMapObj(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 对 getMapList 类型数据进行解析
     *
     * @param json 要解析的json字符串
     * @return
     */
    public static Map<String, List<String>> getMapList(String json) {
        return JSON.parseObject(json, new TypeReference<Map<String, List<String>>>() {});
    }

    /**
     * 对listmap类型进行解析
     *
     * @param json 要解析的json字符串
     * @return
     */
    public static List<Map<String, Object>> getListMapObj(String json) {
        return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
        });
    }

    /**
     * 对listmap类型进行解析
     *
     * @param json 要解析的json字符串
     * @return
     */
    public static List<Map<String, String>> getListMapStr(String json) {
        return JSON.parseObject(json, new TypeReference<List<Map<String, String>>>() {
        });
    }

    /**
     * 对array类型进行解析
     *
     * @param json 要解析的json字符串
     * @return
     */
    public static List<String> getListStr(String json) {
        return JSON.parseObject(json, new TypeReference<List<String>>() {
        });
    }
}
