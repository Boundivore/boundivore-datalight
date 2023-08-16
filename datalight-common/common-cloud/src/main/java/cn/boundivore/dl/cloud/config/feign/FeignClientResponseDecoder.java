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
@Configuration
public class FeignClientResponseDecoder extends SpringDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public FeignClientResponseDecoder(ObjectFactory<HttpMessageConverters> messageConverters,
                                      ObjectProvider<HttpMessageConverterCustomizer> customizers) {
        super(messageConverters, customizers);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {

        Result<?> result = null;

        // 如果没有任何返回内容，则直接通过状态码判定，返回成功
        if (response.body() == null) {
            if (response.status() >= 200 && response.status() < 300) {
                result = Result.success();
            } else {
                result = Result.fail(
                        ResultEnum.FAIL_UNKNOWN,
                        new ErrorMessage(response.status() + ":" + response.reason())
                );
            }

            return result;
        }

        // 正常解析
        String bodyJson = Util.toString(response.body().asReader(Util.UTF_8));
        try {
            result = objectMapper.readValue(bodyJson, Result.class);
        } catch (Exception ignored) {
            // 解析 Json 失败
            if (response.status() >= 200 && response.status() < 300) {
                result = Result.success(bodyJson);
            }else{
                result = Result.fail(
                        ResultEnum.FAIL_UNKNOWN,
                        new ErrorMessage(response.status() + ":" + response.reason())
                );
            }

            return super.decode(
                    response.toBuilder()
                            .body(objectMapper.writeValueAsString(result), Util.UTF_8)
                            .build(),
                    type
            );
        }

        if (result.getCode() != null && ResultEnum.getByCode(result.getCode()) != ResultEnum.SUCCESS) {
            result = Result.fail(
                    ResultEnum.FAIL_UNKNOWN,
                    new ErrorMessage(response.status() + ":" + response.reason() + "->" + result.getMessage())
            );
        }

        return super.decode(
                response.toBuilder()
                        .body(objectMapper.writeValueAsString(result), Util.UTF_8)
                        .build(),
                type
        );
    }
}
