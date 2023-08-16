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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;


public class ServletResponseUtil {
    private ServletResponseUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void responseWriter(ObjectMapper objectMapper, HttpServletResponse response, String msg, int httpStatus) throws IOException {
        Result<?> result = Result.of(null, httpStatus, msg);
        responseWrite(objectMapper, response, result);
    }

    public static void responseSucceed(ObjectMapper objectMapper, HttpServletResponse response, Object obj) throws IOException {
        Result<?> result = Result.success(obj);
        responseWrite(objectMapper, response, result);
    }

    public static void responseFailed(ObjectMapper objectMapper, HttpServletResponse response, String msg) throws IOException {
        Result<?> result = Result.fail(new ErrorMessage(msg));
        responseWrite(objectMapper, response, result);
    }

    private static void responseWrite(ObjectMapper objectMapper, HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (
                Writer writer = response.getWriter()
        ) {
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        }
    }
}
