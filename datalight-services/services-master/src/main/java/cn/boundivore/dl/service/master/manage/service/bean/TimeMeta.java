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
package cn.boundivore.dl.service.master.manage.service.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Description: 记录异步任务执行的起始时间，结束时间，耗时
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/8
 * Modification description:
 * Modified by: 
 * Modification time: 
 * Version: V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeMeta implements Serializable {
    private static final long serialVersionUID = 8113149905924187253L;

    protected long startTime;

    protected long endTime;

    protected long duration;

    public long setEndTime(long endTime) {
        this.endTime = endTime;
        return this.duration = this.endTime - this.startTime;
    }
}
