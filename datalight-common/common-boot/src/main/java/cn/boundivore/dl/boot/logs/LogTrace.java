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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: LogTrace
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "LogTrace: Log 埋点日志")
public class LogTrace {
    @ApiModelProperty(name = "LogName", value = "日志名称", required = true)
    @JsonProperty(value = "LogName", required = true)
    private String logName;

    @ApiModelProperty(name = "UserId", value = "用户 ID", required = true)
    @JsonProperty(value = "UserId", required = true)
    private Long userId;

    @ApiModelProperty(name = "Mobile", value = "手机号", required = true)
    @JsonProperty(value = "Mobile", required = true)
    private String mobile;

    @ApiModelProperty(name = "TimeStamp", value = "请求发生的时间戳(毫秒数)", required = true)
    @JsonProperty(value = "TimeStamp", required = true)
    private Long timeStamp;

    @ApiModelProperty(name = "DateFormat", value = "请求发生的日期(yyyy-MM-dd HH:mm:ss)", required = true)
    @JsonProperty(value = "DateFormat", required = true)
    private String dateFormat;

    @ApiModelProperty(name = "LogType", value = "日志类型", required = true)
    @JsonProperty(value = "LogType", required = true)
    private LogTypeEnum logType;

    @ApiModelProperty(name = "ClassMethod", value = "请求的 类#方法()", required = true)
    @JsonProperty(value = "ClassMethod", required = true)
    private String classMethod;

    @ApiModelProperty(name = "Ip", value = "请求来源的IP", required = true)
    @JsonProperty(value = "Ip", required = true)
    private String ip;

    @ApiModelProperty(name = "Uri", value = "请求来源的 Uri", required = true)
    @JsonProperty(value = "Uri", required = true)
    private String uri;

    @ApiModelProperty(name = "ResultCode", value = "请求响应体返回的状态码", required = true)
    @JsonProperty(value = "ResultCode", required = true)
    private String resultCode;

    @ApiModelProperty(name = "ResultEnum", value = "请求响应体返回的状态枚举", required = true)
    @JsonProperty(value = "ResultEnum", required = true)
    private ResultEnum resultEnum;

    @ApiModelProperty(name = "Params", value = "请求入参", required = true)
    @JsonProperty(value = "Params", required = true)
    private Object[] params;

    @ApiModelProperty(name = "Result", value = "请求返回的结果", required = true)
    @JsonProperty(value = "Result", required = true)
    private Result<?> result;

}
