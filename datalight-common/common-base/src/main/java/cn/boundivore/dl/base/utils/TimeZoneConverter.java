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
package cn.boundivore.dl.base.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Description: 时区转换工具
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class TimeZoneConverter {

    /**
     * Description: 将 UTC 时间转换为北京时间
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param utcTime UTC时间字符串,格式为"yyyy-MM-dd'T'HH:mm:ss'Z'"
     * @return String 北京时间字符串,格式为"yyyy-MM-dd HH:mm:ss"
     */
    public static String utcToBeijingTimeStr(String utcTime) {
        ZonedDateTime utcDateTime = ZonedDateTime.parse(utcTime);
        ZonedDateTime beijingTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
        return beijingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
