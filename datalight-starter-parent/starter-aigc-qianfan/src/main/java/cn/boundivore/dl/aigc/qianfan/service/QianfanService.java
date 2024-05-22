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
package cn.boundivore.dl.aigc.qianfan.service;

import cn.boundivore.dl.aigc.qianfan.properties.AIGCQianfanProperties;
import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Description: 百度千帆大模型调用服务
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QianfanService {

    private final AIGCQianfanProperties aigcQianfanProperties;

    private Qianfan qianfan;

    @PostConstruct
    public void init() {
        this.qianfan = new Qianfan(
                this.aigcQianfanProperties.getAccessKey(),
                this.aigcQianfanProperties.getSecretKey()
        );

        log.info("Qianfan initialized with accessKey: {} and secretKey: {}",
                this.aigcQianfanProperties.getAccessKey(),
                this.aigcQianfanProperties.getSecretKey()
        );
    }

    /**
     * Description: 向 千帆 大模型服务发送消息，并返回响应结果
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param message 消息内容
     * @return String 响应内容
     */
    public String sendMessage(String message) {

        ChatResponse response = this.qianfan.chatCompletion()
                .model(aigcQianfanProperties.getModel()) // 使用model指定预置模型
                // .endpoint("completions_pro") // 也可以使用endpoint指定任意模型 (二选一)
                .addMessage("user", message) // 添加用户消息 (此方法可以调用多次，以实现多轮对话的消息传递)
                .temperature(0.7) // 自定义超参数
                .execute(); // 发起请求

        return response.getResult();
    }
}
