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
import cn.boundivore.dl.orm.po.single.TDlLogs;
import cn.boundivore.dl.orm.service.single.impl.TDlLogsServiceImpl;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private final List<LogTrace> logTraces;
    private final ObjectMapper objectMapper;

    private final TDlLogsServiceImpl tDlLogsService = SpringContextUtil.getBean(TDlLogsServiceImpl.class);

    public LogExporterTask(String desc, List<LogTrace> logTraces) {
        super("LogExporterTask", desc);
        this.logTraces = logTraces;
        this.objectMapper = new ObjectMapper();
    }


    /**
     * Description: 批量写入到数据库
     * // TODO Export log to ElasticSearch or HBase or Kafka asynchronously.
     * // TODO Temporarily store in a local file or console or MySQL.
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<String> 成功或失败
     */
    @Override
    protected Result<String> run() {

        try {
            List<TDlLogs> tDlLogsList = logTraces.stream()
                    .map(logTrace -> {
                        try {
                            TDlLogs tDlLogs = new TDlLogs();
                            tDlLogs.setLogName(logTrace.getLogName());
                            tDlLogs.setUserId(logTrace.getUserId());
                            tDlLogs.setTimestamp(logTrace.getTimeStamp());
                            tDlLogs.setDateFormat(logTrace.getDateFormat());
                            tDlLogs.setLogType(logTrace.getLogType());
                            tDlLogs.setClassName(logTrace.getClassName());
                            tDlLogs.setMethodName(logTrace.getMethodName());
                            tDlLogs.setIp(logTrace.getIp());
                            tDlLogs.setUri(logTrace.getUri());
                            tDlLogs.setResultCode(logTrace.getResultCode());
                            tDlLogs.setResultEnum(logTrace.getResultEnum());
                            tDlLogs.setParams(objectMapper.writeValueAsString(logTrace.getParams()));
                            tDlLogs.setResult(objectMapper.writeValueAsString(logTrace.getResult()));

                            return tDlLogs;
                        } catch (JsonProcessingException e) {
                            log.error(ExceptionUtil.stacktraceToString(e));
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            this.tDlLogsService.saveBatch(tDlLogsList);

        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }

        return Result.success();
    }
}
