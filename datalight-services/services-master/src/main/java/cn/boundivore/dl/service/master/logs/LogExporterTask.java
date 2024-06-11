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

import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.cloud.config.async.AbstractTask;
import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.orm.service.single.impl.TDlLogsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 异步输出日志埋点信息任务
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class LogExporterTask extends AbstractTask<String> {

    private final LogTrace logTrace;
    private final ObjectMapper objectMapper;

    private final TDlLogsServiceImpl tDlLogsService = SpringContextUtil.getBean(TDlLogsServiceImpl.class);

    public LogExporterTask(String desc, LogTrace logTrace) {
        super("LogExporterTask", desc);
        this.logTrace = logTrace;
        this.objectMapper = new ObjectMapper();
    }


    @Override
    protected Result<String> run() throws Exception {
        String json = objectMapper.writeValueAsString(logTrace);
        //TODO Export log to ElasticSearch or HBase or Kafka asynchronously.
        //TODO Temporarily store in a local file or console or MySQL.
        log.info(json);


        return Result.success();
    }
}
