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
package cn.boundivore.dl.boot.zookeeper.lock;


import cn.boundivore.dl.base.constants.ICommonConstant;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.LOCAL_VARIABLE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {
    /**
     * Description: Displays the specified lock path
     * Created by: Boundivore
     * Creation time: 2023/5/23
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return String
     */
    String withPath() default "";

    /**
     * Description: Whether the lock name contains the class name and method name
     * Created by: Boundivore
     * Creation time: 2023/5/13
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return boolean
     */
    boolean withClassFunctionName() default true;

    /**
     * Description: Use the UserId in the Token as the lock key.
     * Created by: Boundivore
     * Creation time: 2023/5/23
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return boolean
     */
    boolean withUserIdInToken() default false;

    /**
     * Description: Use the MerchantId in the Token as the lock key.
     * Created by: Boundivore
     * Creation time: 2023/5/23
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return boolean
     */
    boolean withMerchantIdInToken() default false;

    /**
     * Description: The tostring() that specifies the index of arguments in the function serves as the lock
     * Created by: Boundivore
     * Creation time: 2023/5/23
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return int[]
     */
    int[] withParamIndexArr() default {};

    /**
     * Description: The wait timeout for acquiring a lock
     * Created by: Boundivore
     * Creation time: 2023/5/23
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return long
     */
    long waitTime() default ICommonConstant.TIMEOUT_LOCK_ACQUIRE_MILLISECONDS;

    /**
     * Description: Unit of time
     * Created by: Boundivore
     * Creation time: 2023/5/23
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return TimeUnit
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
