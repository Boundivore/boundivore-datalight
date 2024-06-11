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
package cn.boundivore.dl.cloud.config.async;

import cn.boundivore.dl.base.result.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * Description: 通用请求异步任务抽象类
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public abstract class AbstractTask<T> implements Callable<Result<T>> {

    protected String taskName;
    protected String desc;

    public AbstractTask(String taskName, String desc) {
        this.taskName = taskName;
        this.desc = desc;
    }

    @Override
    public Result<T> call() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug(String.format("准备执行异步任务: %s", taskName));
        }

        Result<T> r = run();

        if (log.isDebugEnabled()) {
            log.debug(String.format("异步任务完成: Name: %s, Desc: %s", taskName, desc));
        }
        return r;
    }

    protected abstract Result<T> run() throws Exception;
}
