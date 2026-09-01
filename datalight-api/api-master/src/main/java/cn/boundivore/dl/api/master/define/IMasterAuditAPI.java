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
package cn.boundivore.dl.api.master.define;

import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.response.impl.master.AbstractAuditVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 审计相关接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：审计相关", description = "IMasterAuditAPI")
@FeignClient(
        name = "IMasterAuditAPI",
        contextId = "IMasterAuditAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterAuditAPI {

    @GetMapping(value = "/audit/getAuditLogSimpleList")
    @Operation(summary = "获取审计日志缩略信息列表", description = "获取审计日志缩略信息列表")
    Result<AbstractAuditVo.AuditLogSimpleListVo> getAuditLogSimpleList(
            @Parameter(name = "CurrentPage", description = "当前页码")
            @RequestParam(value = "CurrentPage", required = false)
            Long currentPage,

            @Parameter(name = "PageSize", description = "每页条目数")
            @RequestParam(value = "PageSize", required = false)
            Long pageSize,

            @Parameter(name = "Principal", description = "用户主体")
            @RequestParam(value = "Principal", required = false)
            String principal,

            @Parameter(name = "UserId", description = "用户 ID")
            @RequestParam(value = "UserId", required = false)
            Long userId,

            @Parameter(name = "OpName", description = "操作名称")
            @RequestParam(value = "OpName", required = false)
            String opName,

            @Parameter(name = "StartTs", description = "起始时间(包含)")
            @RequestParam(value = "StartTs", required = false)
            Long startTs,

            @Parameter(name = "EndTs", description = "结束时间(包含)")
            @RequestParam(value = "EndTs", required = false)
            Long endTs,

            @Parameter(name = "Uri", description = "操作路径")
            @RequestParam(value = "Uri", required = false)
            String uri,

            @Parameter(name = "Ip", description = "操作 IP")
            @RequestParam(value = "Ip", required = false)
            String Ip,

            @Parameter(name = "LogType", description = "日志类型")
            @RequestParam(value = "LogType", required = false)
            LogTypeEnum logType,

            @Parameter(name = "IsOrderByDescTs", description = "是否根据时间戳降序")
            @RequestParam(value = "IsOrderByDescTs", required = false, defaultValue = "true")
            Boolean isOrderByDescTs
    ) throws Exception;

    @GetMapping(value = "/audit/getAuditLogDetail")
    @Operation(summary = "根据审计日志 ID 获取日志详情", description = "根据审计日志 ID 获取日志详情")
    Result<AbstractAuditVo.AuditLogDetailVo> getAuditLogDetail(
            @Parameter(name = "AuditLogId", description = "审计日志 ID")
            @RequestParam(value = "AuditLogId", required = true)
            Long auditLogId
    ) throws Exception;
}
