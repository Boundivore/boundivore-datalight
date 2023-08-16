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
 * Description: StepTypeEnum
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public enum StepTypeEnum implements IBaseEnum {

    SCRIPT("0", "执行./script目录下脚本"),
    COMMAND("1", "执行指定脚本或命令"),
    JAR("2", "执行指定 Jar 包");

    private final String code;
    private final String message;

    StepTypeEnum(String code, String message) {
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
