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
package cn.boundivore.dl.cloud.utils;

import cn.boundivore.dl.base.result.ErrorMessage;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.utils.JsonUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * Description: WebfluxResponseUtil
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class WebfluxResponseUtil {

    public static Mono<Void> responseWriter(ServerWebExchange exchange, int httpStatus, String msg) {
        Result<String> result = Result.of(String.valueOf(httpStatus), "", msg);
        return responseWrite(exchange, httpStatus, result);
    }

    public static Mono<Void> responseFailed(ServerWebExchange exchange, String msg) {
        Result<String> result = Result.fail(new ErrorMessage(msg));
        return responseWrite(exchange, HttpStatus.INTERNAL_SERVER_ERROR.value(), result);
    }

    public static Mono<Void> responseFailed(ServerWebExchange exchange, int httpStatus, String msg) {
        Result<String> result = Result.fail(new ErrorMessage(msg));
        return responseWrite(exchange, httpStatus, result);
    }

    public static Mono<Void> responseWrite(ServerWebExchange exchange, int httpStatus, Result<String> result) {
        if (httpStatus == 0) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setAccessControlAllowCredentials(true);
        response.getHeaders().setAccessControlAllowOrigin("*");
        response.setStatusCode(HttpStatus.valueOf(httpStatus));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer buffer = dataBufferFactory.wrap(JsonUtil.createJsonString(result).getBytes(Charset.defaultCharset()));
        return response.writeWith(Mono.just(buffer)).doOnError(error -> DataBufferUtils.release(buffer));
    }
}
