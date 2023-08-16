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
package cn.boundivore.dl.boot.logs;


import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Logs {
    /**
     * Description: Log name
     * Created by: Boundivore
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     * @return String
     */
    String name() default "";

    /**
     * Description: Log type
     * Created by: Boundivore
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     * @return LogTypeEnum
     */
    LogTypeEnum logType() default LogTypeEnum.MASTER;

    /**
     * Description: Whether to logs results, false is default.
     * Created by: Boundivore
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     * @return boolean
     */
    boolean isPrintResult() default true;
}
