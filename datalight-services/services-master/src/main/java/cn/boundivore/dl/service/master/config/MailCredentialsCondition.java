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
package cn.boundivore.dl.service.master.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Description: 判断是否设置了邮件告警的密码
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/14
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class MailCredentialsCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String mailUsername = context.getEnvironment().getProperty("spring.mail.username");
        String mailPassword = context.getEnvironment().getProperty("spring.mail.password");
        return mailUsername != null
                && !mailUsername.isEmpty()
                && mailPassword != null
                && !mailPassword.isEmpty();
    }
}