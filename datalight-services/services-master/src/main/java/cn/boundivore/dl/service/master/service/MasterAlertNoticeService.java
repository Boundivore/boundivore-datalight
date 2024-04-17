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

import cn.boundivore.dl.base.request.impl.common.AlertWebhookPayloadRequest;
import cn.boundivore.dl.base.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

/**
 * Description: 发送告警信息到指定为止相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterAlertNoticeService {

    private final JavaMailSenderImpl javaMailSender;

    /**
     * Description: 发送告警信息到微信
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警请求体
     * @return Result<String> 成功或失败
     */
    public Result<String> sendToWeiChat(AlertWebhookPayloadRequest request) {
        return Result.success();
    }

    /**
     * Description: 发送告警信息到钉钉
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警请求体
     * @return Result<String> 成功或失败
     */
    public Result<String> sendToDingDing(AlertWebhookPayloadRequest request) {
        return Result.success();
    }

    /**
     * Description: 发送告警信息到飞书
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警请求体
     * @return Result<String> 成功或失败
     */
    public Result<String> sendToFeiShu(AlertWebhookPayloadRequest request) {
        return Result.success();
    }

    /**
     * Description: 发送告警信息到电子邮件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警请求体
     * @return Result<String> 成功或失败
     */
    public Result<String> sendToEmail(AlertWebhookPayloadRequest request) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("boundivore@foxmail.com");
        message.setTo("616616769@qq.com");
        message.setSubject("测试邮件发送");
        message.setText("哈哈哈哈哈");

        javaMailSender.send(message);

        return Result.success();
    }

    /**
     * Description: 发送告警信息到指定接口
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警请求体
     * @return Result<String> 成功或失败
     */
    public Result<String> sendToTargetInterface(AlertWebhookPayloadRequest request) {
        return Result.success();
    }


}


