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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: Constants
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class Constants {
    public static final int CACHE_CAPACITY = 5;
    //HeartBeat timeout in mills
    // 如有必要，可通过配置文件或接口灵活控制
    public static final long HEART_BEAT_TIMEOUT = 60 * 1000L;

    //30 Minutes
    public final static long SCRIPT_BIG_LONG_TIMEOUT = 30 * 60 * 1000L;

    //20 Minutes
    public final static long SCRIPT_LONG_TIMEOUT = 20 * 60 * 1000L;

    //5 Minutes
    public final static long SCRIPT_MIDDLE_TIMEOUT = 5 * 60 * 1000L;

    //1 Minutes
    public final static long SCRIPT_SHORT_TIMEOUT = 1 * 60 * 1000L;

    //5 Seconds
    public final static long SCRIPT_TINY_TIMEOUT = 1 * 5 * 1000L;

    public final static long SCRIPT_DEFAULT_TIMEOUT = SCRIPT_MIDDLE_TIMEOUT;


    //Grafana
    public final static String GRAFANA_HTTP_PORT = "3000";
    public final static String GRAFANA_ADMIN_PASSWORD = "1qaz!QAZ";
    public final static String GRAFANA_SUB_ADMIN_PASSWORD = "123456";
    public final static String GRAFANA_USER_PASSWORD = "datalight";
}
