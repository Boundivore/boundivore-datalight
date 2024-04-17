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
package cn.boundivore.dl.service.master.mail;

import cn.boundivore.dl.api.third.define.IThirdPrometheusAPI;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterAlertNoticeService;
import cn.boundivore.dl.service.master.service.RemoteInvokePrometheusService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;

/**
 * Description: 测试邮件发送
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/16
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */

@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.yaml")
@Slf4j
public class MailTest {

    @Autowired
    private MasterAlertNoticeService masterAlertNoticeService;


    @PostConstruct
    public void init() {

    }

    @Test
    public void sendMail() {
        Result<String> result = this.masterAlertNoticeService.sendToEmail(null);
        log.info("测试邮件发送：{}", result);
    }
}
