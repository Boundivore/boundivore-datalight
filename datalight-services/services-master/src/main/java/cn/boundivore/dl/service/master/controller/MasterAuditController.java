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
package cn.boundivore.dl.service.master.controller;

import cn.boundivore.dl.api.master.define.IMasterAuditAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.response.impl.master.AbstractAuditVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterAuditController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = true)
public class MasterAuditController implements IMasterAuditAPI {

    private final MasterAuditService masterAuditService;

    @Override
    public Result<AbstractAuditVo.AuditLogSimpleList> getAuditLogSimpleList(Long currentPage,
                                                                            Long pageSize,
                                                                            String principal,
                                                                            Long userId,
                                                                            String opName,
                                                                            Long startTs,
                                                                            Long endTs,
                                                                            String uri,
                                                                            String Ip,
                                                                            LogTypeEnum logType) throws Exception {
        return this.masterAuditService.getAuditLogSimpleList(
                currentPage,
                pageSize,
                principal,
                userId,
                opName,
                startTs,
                endTs,
                uri,
                Ip,
                logType
        );
    }

    @Override
    public Result<AbstractAuditVo.AuditLogDetail> getAuditLogDetail(Long auditLogId) throws Exception {
        return this.masterAuditService.getAuditLogDetail(auditLogId);
    }
}
