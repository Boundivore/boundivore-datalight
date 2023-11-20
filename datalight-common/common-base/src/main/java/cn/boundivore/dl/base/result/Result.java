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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

import static cn.boundivore.dl.base.result.ResultEnum.FAIL_UNKNOWN;
import static cn.boundivore.dl.base.result.ResultEnum.SUCCESS;


/**
 * Description: the result of response
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@Schema(
        name = "Result",
        description = "HTTP 返回结果"
)
public class Result<T> implements Serializable {
    @Schema(name = "Timestamp", title = "毫秒时间戳", required = true)
    @JsonProperty("Timestamp")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long timestamp;

    @Schema(name = "Code", title = "返回码", required = true)
    @JsonProperty("Code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;

    @Schema(name = "Message", title = "返回消息", required = true)
    @JsonProperty("Message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @Schema(name = "MessageType", title = "消息类型", required = true)
    @JsonProperty("MessageType")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String messageType;

    @Schema(name = "Page", title = "分页信息", required = true)
    @JsonProperty(value = "Page", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Page page;

    @Schema(name = "Data", title = "返回数据", required = true)
    @JsonProperty("Data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;


    @JsonIgnore
    public boolean isSuccess() {
        return this.getCode().equals(SUCCESS.getCode());
    }

    public static Result<String> success(){
        Result<String> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = SUCCESS.getCode();
        result.messageType = SUCCESS.getMessageCN();
        result.message = SUCCESS.getMessageCN();
        result.data = "";
        return result;
    }

    public static Result<String> success(ResultEnum re){
        Result<String> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = re.getCode();
        result.messageType = SUCCESS.getMessageCN();
        result.message = re.getMessageCN();
        result.data = "";
        return result;
    }

    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = SUCCESS.getCode();
        result.messageType = SUCCESS.getMessageCN();
        result.message = SUCCESS.getMessageCN();
        result.data = data;
        return result;
    }

    public static <T> Result<T> successWithPage(T data, Page page){
        Result<T> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = SUCCESS.getCode();
        result.messageType = SUCCESS.getMessageCN();
        result.message = SUCCESS.getMessageCN();
        result.data = data;

        result.page = page;
        return result;
    }

    public static <T> Result<T> successWithPage(T data,
                                                Long currentPage,
                                                Long totalPage,
                                                Long pageSize,
                                                Long totoSize){
        Result<T> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = SUCCESS.getCode();
        result.messageType = SUCCESS.getMessageCN();
        result.message = SUCCESS.getMessageCN();
        result.data = data;

        result.page = new Page(currentPage, totalPage, pageSize, totoSize);
        return result;
    }

    public static <T> Result<T> success(ResultEnum re, T data){
        Result<T> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = re.getCode();
        result.messageType = re.getMessageCN();
        result.message = re.getMessageCN();
        result.data = data;
        return result;
    }

    public static <T> Result<T> of(String code, T data, String message){
        Result<T> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = code;
        result.messageType = "";
        result.message = message;
        result.data = data;
        return result;
    }

    public static Result<String> fail(){
        Result<String> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = FAIL_UNKNOWN.getCode();
        result.messageType = SUCCESS.getMessageCN();
        result.message = FAIL_UNKNOWN.getMessageCN();
        result.data = "";
        return result;
    }

    public static Result<String> fail(ResultEnum re){
        Result<String> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = re.getCode();
        result.messageType = re.getMessageCN();
        result.message = re.getMessageCN();
        result.data = "";
        return result;
    }

    public static Result<String> fail(ResultEnum re, ErrorMessage errorMessage){
        Result<String> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = re.getCode();
        result.messageType = re.getMessageCN();
        result.message = errorMessage.toString();
        result.data = "";
        return result;
    }

    public static Result<String> fail(ErrorMessage errorMessage){
        Result<String> result = new Result<>();
        result.timestamp = System.currentTimeMillis();
        result.code = FAIL_UNKNOWN.getCode();
        result.messageType = FAIL_UNKNOWN.getMessageCN();
        result.message = errorMessage.toString();
        result.data = "";
        return result;
    }
}
