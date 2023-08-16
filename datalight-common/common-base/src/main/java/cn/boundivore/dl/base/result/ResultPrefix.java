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
package cn.boundivore.dl.base.result;

/**
 * Description: the prefix of Result
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class ResultPrefix {

    /**
     * 状态码前缀：成功
     */
    public static final String SUCCESS_PREFIX = "0";
    /**
     * 状态码前缀：网关类异常
     */
    public static final String GATEWAY_PREFIX = "A";
    /**
     * 状态码前缀：业务类通用异常
     */
    public static final String BUSINESS_PREFIX = "B";
    /**
     * 状态码前缀：资源操作异常
     */
    public static final String RESOURCE_PREFIX = "C";
    /**
     * 状态码前缀：数据库异常
     */
    public static final String DATABASE_PREFIX = "D";
    /**
     * 状态码前缀：脚本操作异常
     */
    public static final String BASH_EXECUTOR_PREFIX = "E";
    /**
     * 状态码前缀：分布式锁或本地锁异常
     */
    public static final String LOCK_PREFIX = "F";
    /**
     * 状态码前缀：远程调用异常
     */
    public static final String REMOTE_INVOKE_PREFIX = "G";


}
