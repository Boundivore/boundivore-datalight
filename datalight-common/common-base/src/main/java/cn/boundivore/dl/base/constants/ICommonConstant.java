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
package cn.boundivore.dl.base.constants;

/**
 * Description: CommonConstant
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public interface ICommonConstant {


    int TIMEOUT_TRANSACTION_SECONDS = 100;
    long TIMEOUT_LOCK_ACQUIRE_MILLISECONDS = (TIMEOUT_TRANSACTION_SECONDS + 1) * 1000L;

    /**
     * Token Header
     */
    String TOKEN_HEADER = "Authorization";

    /**
     * Root user
     */
    String ADMIN_USER_NAME = "master-admin";


    String MONTH_FORMAT = "yyyy-MM";
    String DATE_FORMAT = "yyyy-MM-dd";
    String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String SIMPLE_MONTH_FORMAT = "yyyyMM";
    String SIMPLE_DATE_FORMAT = "yyyyMMdd";
    String SIMPLE_DATETIME_FORMAT = "yyyyMMddHHmmss";
    String TIME_ZONE_GMT8 = "GMT+8";

    /**
     * param of tenant_id
     */
    String TENANT_ID_PARAM = "tenant_id";

}
