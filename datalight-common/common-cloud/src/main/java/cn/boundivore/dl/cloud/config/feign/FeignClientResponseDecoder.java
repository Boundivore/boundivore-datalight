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
package cn.boundivore.dl.cloud.config.feign;

import cn.boundivore.dl.base.result.ErrorMessage;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.result.ResultEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.Util;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Description: Feign 调用异常解析器
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class FeignClientResponseDecoder extends SpringDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public FeignClientResponseDecoder(ObjectFactory<HttpMessageConverters> messageConverters,
                                      ObjectProvider<HttpMessageConverterCustomizer> customizers) {
        super(messageConverters, customizers);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {

        Result<?> result;

        // 如果没有任何返回内容，则直接通过状态码判定，返回成功
        if (response.body() == null) {
            result = getResultByResponseCode(response, null);
            return result;
        }

        // 正常解析
        String bodyJson = Util.toString(response.body().asReader(Util.UTF_8));
        try {
            result = objectMapper.readValue(bodyJson, Result.class);
        } catch (Exception ignored) {
            // 解析 Json 失败
            result = getResultByResponseCode(response, bodyJson);
            return decodeResponse(response, result, type);
        }

        // 如果结果对象中的状态码字段不为空，并且不等于成功的状态码，则根据响应和结果对象中的消息创建一个失败的结果对象。
        if (result.getCode() != null && ResultEnum.getByCode(result.getCode()) != ResultEnum.SUCCESS) {
            result = createFailResult(response, result.getMessage());
        }

        return decodeResponse(response, result, type);
    }

    /**
     * Description: 根据响应状态码生成结果。如果状态码在200~300之间，返回成功结果，否则返回失败结果。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param response Feign的响应对象
     * @param bodyJson 消息体字符串
     * @return Result 返回封装后的结果对象
     */
    private Result<?> getResultByResponseCode(Response response, String bodyJson) {
        if (response.status() >= 200 && response.status() < 300) {
            return bodyJson != null ? Result.success(bodyJson) : Result.success();
        } else {
            return createFailResult(response, null);
        }
    }

    /**
     * Description: 根据响应和额外的消息创建失败的结果对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param response          Feign的响应对象
     * @param additionalMessage 额外的消息
     * @return Result 返回封装后的结果对象
     */
    private Result<?> createFailResult(Response response, String additionalMessage) {
        String message = response.status() + ":" + response.reason();
        if (additionalMessage != null) {
            message += "->" + additionalMessage;
        }
        return Result.fail(ResultEnum.FAIL_UNKNOWN, new ErrorMessage(message));
    }

    /**
     * Description: 解码响应对象，并返回处理后的结果
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException, FeignException
     *
     * @param response 需要解码的Feign响应对象
     * @param result   封装后的结果对象
     * @param type     解码的类型
     * @return Object 解码后的对象
     */
    private Object decodeResponse(Response response, Result<?> result, Type type) throws IOException, FeignException {
        return super.decode(
                response.toBuilder()
                        .body(objectMapper.writeValueAsString(result), Util.UTF_8)
                        .build(),
                type
        );
    }
}
