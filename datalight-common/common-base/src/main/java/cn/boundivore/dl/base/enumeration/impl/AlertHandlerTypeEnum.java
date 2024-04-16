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
package cn.boundivore.dl.base.enumeration.impl;

import cn.boundivore.dl.base.enumeration.IBaseEnum;

/**
 * Description: AlertHandlerTypeEnum
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/16
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public enum AlertHandlerTypeEnum implements IBaseEnum {

    ALERT_IGNORE("0", "静默告警信息"),
    ALERT_LOG("1", "打印告警日志"),
    ALERT_WEICHAT("2", "发送到微信"),
    ALERT_DINGDING("3", "发送到钉钉"),
    ALERT_FEISHU("4", "发送到飞书"),
    ALERT_INTERFACE("5", "发送到指定接口");

    private final String code;
    private final String message;

    AlertHandlerTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
