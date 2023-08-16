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
package cn.boundivore.dl.cloud.feign;

import feign.Request;

import java.util.concurrent.TimeUnit;

/**
 * Description: 微服务内部调用的请求配置
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/3
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class RequestOptionsGenerator {
    /**
     * Description: 调用内部 API 的超时设置
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param connectionTimeout 连接超时
     * @param readTimeout       读取超时
     * @return Request.Options
     */
    public static Request.Options getRequestOptions(long connectionTimeout,
                                                    long readTimeout) {
        return new Request.Options(
                connectionTimeout,
                TimeUnit.MILLISECONDS,
                readTimeout,
                TimeUnit.MILLISECONDS,
                false
        );
    }
}
