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
package cn.boundivore.dl.base.result;


import java.util.HashMap;
import java.util.Map;

/**
 * Description: the ResultEnum of response
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public enum ResultEnum implements BaseResultEnum {
    /**
     * 0 成功
     */
    SUCCESS(ResultPrefix.SUCCESS_PREFIX + "0000", "成功", "Success"),

    /**
     * A 网关类异常
     */
    FAIL_GATEWAY(ResultPrefix.GATEWAY_PREFIX + "1000", "网关异常", "Gateway exception"),

    FAIL_AUTH(ResultPrefix.GATEWAY_PREFIX + "1001", "认证异常", "Auth exception"),

    /**
     * B 业务类通用异常
     */
    FAIL_UNKNOWN(ResultPrefix.BUSINESS_PREFIX + "1000", "未知异常", "Error"),

    FAIL_BUSINESS_EXCEPTION(ResultPrefix.BUSINESS_PREFIX + "1001", "业务异常", "Business exception"),

    FAIL_REQUEST_PARAM_MISSING(ResultPrefix.BUSINESS_PREFIX + "1002", "请求参数丢失异常", "The request parameters are incomplete"),

    FAIL_JSON(ResultPrefix.BUSINESS_PREFIX + "1003", "Json 格式异常", "Parse json error"),

    FAIL_REQUEST_TYPE_MISMATCH(ResultPrefix.BUSINESS_PREFIX + "1004", "请求类型不匹配异常", "Wrong type of request parameter"),

    FAIL_DATA_FORMAT(ResultPrefix.BUSINESS_PREFIX + "1005", "数据格式化异常", "Incorrect format of data"),

    FAIL_ILLEGAL_ARGUMENT(ResultPrefix.BUSINESS_PREFIX + "1006", "非法的参数异常", "Illegal arguments"),

    FAIL_REQUEST_METHOD(ResultPrefix.BUSINESS_PREFIX + "1007", "非法的请求方式异常", "Illegal request method"),

    FAIL_404(ResultPrefix.BUSINESS_PREFIX + "1008", "404 未找到地址异常", "404 not found"),

    FAIL_AUTH_EXCEPTION(ResultPrefix.BUSINESS_PREFIX + "1009", "未登录或无权限异常", "Not logged in or without permission"),

    FAIL_USER_PWD_NOT_MATCH_EXCEPTION(ResultPrefix.BUSINESS_PREFIX + "1010", "用户名或密码错误异常", "Incorrect user name or password"),


    FAIL_OAUTH2(ResultPrefix.BUSINESS_PREFIX + "1011", "Oauth2 错误", "Oauth2 error"),

    FAIL_DATA_UNAUTHORIZED(ResultPrefix.BUSINESS_PREFIX + "1012", "数据未授权异常", "Data not authorized by the current role was requested"),

    FAIL_DATA_DUPLICATION(ResultPrefix.BUSINESS_PREFIX + "1013", "数据冲突异常", "The current data conflicts with existing data"),

    FAIL_INTERFACE_UNAUTHORIZED(ResultPrefix.BUSINESS_PREFIX + "1014", "接口未授权异常", "The interface is not authorized"),

    FAIL_HTTP_NOT_READABLE_EXCEPTION(ResultPrefix.BUSINESS_PREFIX + "1015", "请求参数转换异常", "The request parameter conversion is abnormal"),

    FAIL_FILE_UPLOAD_EXCEPTION(ResultPrefix.BUSINESS_PREFIX + "1016", "文件上传异常", "File Upload Exception"),

    FAIL_FILE_DOWNLOAD_EXCEPTION(ResultPrefix.BUSINESS_PREFIX + "1017", "文件下载异常", "File Download Exception"),

    /**
     * C 资源操作异常
     */
    FAIL_MEM_INSUFFICIENT_EXCEPTION(ResultPrefix.RESOURCE_PREFIX + "1001", "系统内存溢出异常", "System out of memory"),

    /**
     * D 状态码前缀：数据库异常
     */
    FAIL_DATABASE_EXCEPTION(ResultPrefix.DATABASE_PREFIX + "1001", "数据库操作异常", "Database operation exception"),

    /**
     * E 状态码前缀：脚本操作异常
     */
    FAIL_EXECUTOR_EXCEPTION(ResultPrefix.BASH_EXECUTOR_PREFIX + "1001", "脚本执行异常", "Error executing script"),

    FAIL_EXECUTOR_TIMEOUT_EXCEPTION(ResultPrefix.BASH_EXECUTOR_PREFIX + "1002", "脚本执行超时异常", "Execute script timeout"),

    FAIL_EXECUTOR_IO_EXCEPTION(ResultPrefix.BASH_EXECUTOR_PREFIX + "1003", "脚本执行 IO 异常", "Execute script IO exception"),

    /**
     * F 状态码前缀：分布式锁或本地锁异常
     */
    FAIL_LOCK_EXCEPTION(ResultPrefix.LOCK_PREFIX + "1001", "分布式锁异常", "The distributed lock operation exception"),

    /**
     * G 状态码前缀：远程调用异常
     */
    FAIL_REMOTE_INVOKE_EXCEPTION(ResultPrefix.REMOTE_INVOKE_PREFIX + "1001", "内部远程调用异常", "The internal remote invocation of the micro-service exception");


    private String code;
    private String messageCN;
    private String messageEN;


    ResultEnum(String code, String messageCN, String messageEN) {
        this.code = code;
        this.messageCN = messageCN;
        this.messageEN = messageEN;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessageCN() {
        return messageCN;
    }

    @Override
    public String getMessageEN() {
        return messageEN;
    }

    private static Map<String, ResultEnum> map = new HashMap<>();

    static {
        for (ResultEnum resultEnum : ResultEnum.values()) {
            map.put(resultEnum.code, resultEnum);
        }
    }

    public static ResultEnum getByCode(String code) {
        return map.get(code);
    }

}
