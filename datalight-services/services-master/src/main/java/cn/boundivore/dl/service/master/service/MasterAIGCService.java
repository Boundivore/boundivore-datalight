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
package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.aigc.qianfan.service.QianfanService;
import cn.boundivore.dl.base.request.impl.master.AbstractAIGCRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.env.DataLightEnv;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: AIGC 内容生成
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterAIGCService {

    private final DataLightEnv dataLightEnv;

    private final QianfanService qianfanService;


    /**
     * Description: 调用 AIGC 模型，生成内容
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request AIGC 请求体
     * @return Result<String> AIGC 返回内容
     */
    public Result<String> sendMessage(AbstractAIGCRequest.SendMessageRequest request) {

        String result = "";
        switch (request.getAigcType()) {
            case QIANFAN:
                Assert.notBlank(
                        dataLightEnv.getQianfanAccessKey(),
                        () -> new BException("千帆 AccessKey 尚未配置")
                );

                Assert.notBlank(
                        dataLightEnv.getQianfanSecretKey(),
                        () -> new BException("千帆 SecretKey 尚未配置")
                );

                Assert.notBlank(
                        dataLightEnv.getQianfanModel(),
                        () -> new BException("千帆 Model 尚未配置")
                );

                result = this.qianfanService.sendMessage(request.getMessage());
                break;
            default:
                throw new IllegalArgumentException(
                        String.format(
                                "未知的模型类型: %s",
                                request.getAigcType()
                        )
                );
        }
        return Result.success(result);
    }


}
