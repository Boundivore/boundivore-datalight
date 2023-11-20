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
package cn.boundivore.dl.base.request.impl.worker;

import cn.boundivore.dl.base.constants.Constants;
import cn.boundivore.dl.base.enumeration.impl.ExecTypeEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Description: ExecRequest
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ExecRequest", description = "ExecRequest: 执行脚本请求体")
@NoArgsConstructor
public class ExecRequest implements IRequest {
    @ApiModelProperty(name = "ScriptType", value = "执行操作的类型", required = true)
    @JsonProperty(value = "ScriptType", required = true)
    private ExecTypeEnum execTypeEnum;

    @ApiModelProperty(name = "Name", value = "Task 名称", required = true)
    @JsonProperty("Name")
    private String name;

    @ApiModelProperty(name = "Exec", value = "Task 待执行的脚本或语句", required = true)
    @JsonProperty("Exec")
    private String exec;

    @ApiModelProperty(name = "ExpectExitCode", value = "期望的执行成功退出码", required = true)
    @JsonProperty("ExpectExitCode")
    private Integer expectExitCode = 0;

    @ApiModelProperty(name = "Timeout", value = "执行脚本的超时时间", required = true)
    @JsonProperty("Timeout")
    private Long timeout = -1L;

    @ApiModelProperty(name = "Args", value = "执行脚本时的参数", required = true)
    @JsonProperty("Args")
    private String[] args;

    @ApiModelProperty(name = "InteractArgs", value = "执行时，需要持续交互的参数列表，偶数位代表判断条件，奇数位代表传送参数，最后一位代表关闭输出流的条件", required = true)
    @JsonProperty("InteractArgs")
    private String[] interactArgs;

    @ApiModelProperty(name = "PrintLog", value = "是否打印该语句执行结果", required = false)
    @JsonProperty("PrintLog")
    private Boolean printLog;


    public ExecRequest(ExecTypeEnum execTypeEnum,
                       String name,
                       String exec,
                       Integer expectExitCode,
                       Long timeout,
                       String[] args,
                       String[] interactArgs,
                       Boolean printLog) {
        this.execTypeEnum = execTypeEnum;
        this.name = name;
        this.exec = exec;
        this.expectExitCode = expectExitCode;
        this.timeout = timeout <= 0 ? Constants.SCRIPT_DEFAULT_TIMEOUT : timeout;
        this.args = args;
        this.interactArgs = interactArgs;
        this.printLog = printLog;
    }
}
