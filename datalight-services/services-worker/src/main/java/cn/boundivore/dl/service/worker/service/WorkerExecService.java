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
package cn.boundivore.dl.service.worker.service;

import cn.boundivore.dl.base.bash.BashResult;
import cn.boundivore.dl.base.constants.Constants;
import cn.boundivore.dl.base.request.impl.worker.ExecRequest;
import cn.boundivore.dl.base.result.ErrorMessage;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.result.ResultEnum;
import cn.boundivore.dl.boot.bash.BashExecutor;
import cn.boundivore.dl.exception.BashException;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: 工作节点执行脚本 Service
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerExecService {

    protected final BashExecutor bashExecutor;


    /**
     * Description: 执行 Worker 所在节点的 Bash 命令
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 执行命令请求体
     * @return Result<String>
     */
    public Result<String> exec(ExecRequest request) {

        boolean printLog = request.getPrintLog() == null || request.getPrintLog();
        long timeout = request.getTimeout() == null || request.getTimeout() <= 0 ?
                Constants.SCRIPT_DEFAULT_TIMEOUT :
                request.getTimeout();

        BashResult bashResult = bashExecutor.execute(
                request.getExec(),
                request.getExpectExitCode(),
                timeout,
                request.getArgs(),
                request.getInteractArgs(),
                printLog
        );

        if(bashResult.isSuccess()){
            return Result.success(
                    String.format(
                            "%s\n%s",
                            bashResult.getExecLog(),
                            bashResult.getResult()
                    )
            );
        }else{
            return Result.fail(
                    ResultEnum.FAIL_BUSINESS_EXCEPTION,
                    new ErrorMessage(
                            String.format(
                                    "%s\n%s\n%s",
                                    bashResult.getExitValue(),
                                    bashResult.getExecLog(),
                                    bashResult.getResult()
                            ))
            );
        }
    }
}
