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
package cn.boundivore.dl.service.master.logs;


import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;

import java.lang.annotation.*;

/**
 * Description: 日志注解，用于审计、埋点功能
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Logs {
    /**
     * Description: 日志名称
     * Created by: Boundivore
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return String
     */
    String name() default "";

    /**
     * Description: 日志类型
     * Created by: Boundivore
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return LogTypeEnum
     */
    LogTypeEnum logType() default LogTypeEnum.MASTER;

    /**
     * Description: Whether to logs results, false is default.
     * Created by: Boundivore
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return boolean
     */
    boolean isPrintResult() default true;
}
