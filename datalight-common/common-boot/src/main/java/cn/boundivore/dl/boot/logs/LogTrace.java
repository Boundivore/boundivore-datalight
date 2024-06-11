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
package cn.boundivore.dl.boot.logs;

import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.result.ResultEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: LogTrace
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "LogTrace",
        description = "LogTrace: Log 埋点日志"
)
public class LogTrace {
    @Schema(name = "LogName", title = "日志名称", required = true)
    @JsonProperty(value = "LogName", required = true)
    private String logName;

    @Schema(name = "UserId", title = "用户 ID", required = true)
    @JsonProperty(value = "UserId", required = true)
    private Long userId;

    @Schema(name = "TimeStamp", title = "请求发生的时间戳(毫秒数)", required = true)
    @JsonProperty(value = "TimeStamp", required = true)
    private Long timeStamp;

    @Schema(name = "DateFormat", title = "请求发生的日期(yyyy-MM-dd HH:mm:ss)", required = true)
    @JsonProperty(value = "DateFormat", required = true)
    private String dateFormat;

    @Schema(name = "LogType", title = "日志类型", required = true)
    @JsonProperty(value = "LogType", required = true)
    private LogTypeEnum logType;

    @Schema(name = "ClassName", title = "请求的类", required = true)
    @JsonProperty(value = "ClassName", required = true)
    private String className;

    @Schema(name = "MethodName", title = "请求的方法", required = true)
    @JsonProperty(value = "MethodName", required = true)
    private String methodName;

    @Schema(name = "Ip", title = "请求来源的IP", required = true)
    @JsonProperty(value = "Ip", required = true)
    private String ip;

    @Schema(name = "Uri", title = "请求来源的 Uri", required = true)
    @JsonProperty(value = "Uri", required = true)
    private String uri;

    @Schema(name = "ResultCode", title = "请求响应体返回的状态码", required = true)
    @JsonProperty(value = "ResultCode", required = true)
    private String resultCode;

    @Schema(name = "ResultEnum", title = "请求响应体返回的状态枚举", required = true)
    @JsonProperty(value = "ResultEnum", required = true)
    private ResultEnum resultEnum;

    @Schema(name = "Params", title = "请求入参", required = true)
    @JsonProperty(value = "Params", required = true)
    private Object[] params;

    @Schema(name = "Result", title = "请求返回的结果", required = true)
    @JsonProperty(value = "Result", required = true)
    private Result<?> result;

}
