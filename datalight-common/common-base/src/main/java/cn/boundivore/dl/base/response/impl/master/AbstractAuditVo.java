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
package cn.boundivore.dl.base.response.impl.master;


import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.response.IVo;
import cn.boundivore.dl.base.result.ResultEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Description: 审计相关响应体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/3/17
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAuditVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractClusterVo.AuditLogSimpleListVo",
            description = "AbstractClusterVo.AuditLogSimpleListVo 审计日志缩略信息列表信息"
    )
    public static class AuditLogSimpleListVo implements IVo {
        private static final long serialVersionUID = -2237685206202222155L;

        @Schema(name = "AuditLogSimpleList", title = "审计日志缩略信息列表", required = true)
        @JsonProperty(value = "AuditLogSimpleList", required = true)
        private List<AuditLogSimpleVo> auditLogSimpleVoList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractClusterVo.AuditLogSimpleVo",
            description = "AbstractClusterVo.AuditLogSimpleVo 审计日志缩略信息"
    )
    public static class AuditLogSimpleVo implements IVo {
        private static final long serialVersionUID = -2237685706202232155L;

        @Schema(name = "AuditLogId", title = "审计日志 ID", required = true)
        @JsonProperty(value = "AuditLogId", required = true)
        private Long auditLogId;

        @Schema(name = "OpName", title = "操作名称", required = true)
        @JsonProperty(value = "OpName", required = true)
        private String opName;

        @Schema(name = "UserId", title = "用户 ID", required = true)
        @JsonProperty(value = "UserId", required = true)
        private Long userId;

        @Schema(name = "Principal", title = "主体名称", required = true)
        @JsonProperty(value = "Principal", required = true)
        private String principal;

        @Schema(name = "Timestamp", title = "操作发生时间", required = true)
        @JsonProperty(value = "Timestamp", required = true)
        private Long timestamp;

        @Schema(name = "DateFormat", title = "操作发生时间格式化", required = true)
        @JsonProperty(value = "DateFormat", required = true)
        private String dateFormat;

        @Schema(name = "LogType", title = "日志类型", required = true)
        @JsonProperty(value = "LogType", required = true)
        private LogTypeEnum logType;

        @Schema(name = "ClassName", title = "涉及的类名", required = true)
        @JsonProperty(value = "ClassName", required = true)
        private String className;

        @Schema(name = "MethodName", title = "涉及的方法名", required = true)
        @JsonProperty(value = "MethodName", required = true)
        private String methodName;

        @Schema(name = "Ip", title = "操作者 IP", required = true)
        @JsonProperty(value = "Ip", required = true)
        private String ip;

        @Schema(name = "Uri", title = "操作的接口路径", required = true)
        @JsonProperty(value = "Uri", required = true)
        private String uri;

        @Schema(name = "ResultCode", title = "操作业务响应状态码", required = true)
        @JsonProperty(value = "ResultCode", required = true)
        private String resultCode;

        @Schema(name = "ResultEnum", title = "操作结果枚举值", required = true)
        @JsonProperty(value = "ResultEnum", required = true)
        private ResultEnum resultEnum;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractClusterVo.AuditLogDetailVo",
            description = "AbstractClusterVo.AuditLogDetailVo 审计日志详细信息"
    )
    public static class AuditLogDetailVo implements IVo {
        private static final long serialVersionUID = -2237685706202222155L;

        @Schema(name = "AuditLogId", title = "审计日志 ID", required = true)
        @JsonProperty(value = "AuditLogId", required = true)
        private Long auditLogId;

        @Schema(name = "OpName", title = "操作名称", required = true)
        @JsonProperty(value = "OpName", required = true)
        private String opName;

        @Schema(name = "UserId", title = "用户 ID", required = true)
        @JsonProperty(value = "UserId", required = true)
        private Long userId;

        @Schema(name = "Principal", title = "主体名称", required = true)
        @JsonProperty(value = "Principal", required = true)
        private String principal;

        @Schema(name = "Timestamp", title = "操作发生时间", required = true)
        @JsonProperty(value = "Timestamp", required = true)
        private Long timestamp;

        @Schema(name = "DateFormat", title = "操作发生时间格式化", required = true)
        @JsonProperty(value = "DateFormat", required = true)
        private String dateFormat;

        @Schema(name = "LogType", title = "日志类型", required = true)
        @JsonProperty(value = "LogType", required = true)
        private LogTypeEnum logType;

        @Schema(name = "ClassName", title = "涉及的类名", required = true)
        @JsonProperty(value = "ClassName", required = true)
        private String className;

        @Schema(name = "MethodName", title = "涉及的方法名", required = true)
        @JsonProperty(value = "MethodName", required = true)
        private String methodName;

        @Schema(name = "Ip", title = "操作者 IP", required = true)
        @JsonProperty(value = "Ip", required = true)
        private String ip;

        @Schema(name = "Uri", title = "操作的接口路径", required = true)
        @JsonProperty(value = "Uri", required = true)
        private String uri;

        @Schema(name = "ResultCode", title = "操作业务响应状态码", required = true)
        @JsonProperty(value = "ResultCode", required = true)
        private String resultCode;

        @Schema(name = "ResultEnum", title = "操作结果枚举值", required = true)
        @JsonProperty(value = "ResultEnum", required = true)
        private ResultEnum resultEnum;

        @Schema(name = "ParamsBase64", title = "操作入参 BASE64", required = true)
        @JsonProperty(value = "ParamsBase64", required = true)
        private String paramsBase64;

        @Schema(name = "ResultBase64", title = "操作响应体 BASE64", required = true)
        @JsonProperty(value = "ResultBase64", required = true)
        private String resultBase64;
    }


}
