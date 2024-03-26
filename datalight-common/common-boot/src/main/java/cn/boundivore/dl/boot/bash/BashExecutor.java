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
package cn.boundivore.dl.boot.bash;

import cn.boundivore.dl.base.bash.AutoFlushingPumpStreamHandler;
import cn.boundivore.dl.base.bash.BashLogOutputStream;
import cn.boundivore.dl.base.bash.BashResult;
import cn.boundivore.dl.base.bash.exec.*;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Description: bash/shell 执行器
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@Slf4j
@Component
public class BashExecutor {


    public BashResult execute(String cmd,
                              String[] cmdArgs,
                              String[] interactArgs,
                              boolean printLog) {
        return this.execute(
                cmd,
                0,
                ExecuteWatchdog.INFINITE_TIMEOUT,
                cmdArgs,
                interactArgs,
                printLog
        );
    }


    public BashResult execute(String cmd,
                              long timeout,
                              String[] cmdArgs,
                              String[] interactArgs,
                              boolean printLog) {
        return this.execute(
                cmd,
                0,
                timeout,
                cmdArgs,
                interactArgs,
                printLog
        );
    }


    /**
     * Description: 直接执行一个 Bash 命令，并设置脚本超时时间
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/5
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param cmd             shell 命令
     * @param cmdArgs         shell 参数
     * @param timeout         执行超时时间
     * @param interactArgs    交互参数，其中奇数位为 "脚本执行时遇到的响应关键字"，偶数位为 "捕获响应关键字后输入的内容"
     *                        例如：["are you ok?", "yes", "Are you hungry?", "no", "done."]
     *                        当捕获到 "are you ok" 时，会向 channel 中输入 yes
     *                        当捕获到 "are you ok" 时，会向 channel 中输入 no
     *                        当捕获到 "done." 时，会结束当前 channel，并返回。
     * @param printLog        是否打印日志
     * @param expectExitValue 指定正常退出的退出码，默认 0 判定为脚本正常执行完毕的退出
     * @return BashResult 执行结果
     */
    public BashResult execute(String cmd,
                              int expectExitValue,
                              long timeout,
                              String[] cmdArgs,
                              String[] interactArgs,
                              boolean printLog) {
        if (cmdArgs == null) cmdArgs = new String[0];

        CommandLine cmdLine = CommandLine.parse(cmd).addArguments(cmdArgs);

        return this.result(
                cmdLine,
                expectExitValue,
                timeout,
                interactArgs,
                printLog
        );
    }

    /**
     * Description: 执行指定目录下的 .sh 脚本文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/5
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param bashFile        指定目录下的 .sh 脚本文件
     * @param cmdArgs         shell 参数
     * @param timeout         执行超时时间
     * @param interactArgs    交互参数，其中奇数位为 "脚本执行时遇到的响应关键字"，偶数位为 "捕获响应关键字后输入的内容"
     *                        例如：["are you ok?", "yes", "Are you hungry?", "no", "done."]
     *                        当捕获到 "are you ok" 时，会向 channel 中输入 yes
     *                        当捕获到 "are you ok" 时，会向 channel 中输入 no
     *                        当捕获到 "done." 时，会结束当前 channel，并返回。
     * @param printLog        是否打印日志
     * @param expectExitValue 指定正常退出的退出码，默认 0 判定为脚本正常执行完毕的退出
     * @return BashResult 执行结果
     */
    public BashResult executeBashFile(File bashFile,
                                      int expectExitValue,
                                      long timeout,
                                      String[] cmdArgs,
                                      String[] interactArgs,
                                      boolean printLog) {

        if (cmdArgs == null) cmdArgs = new String[0];
        CommandLine cmdLine = new CommandLine(bashFile).addArguments(cmdArgs);

        return this.result(
                cmdLine,
                expectExitValue,
                timeout,
                interactArgs,
                printLog
        );
    }


    /**
     * Description: 异步执行指定目录下的 .sh 脚本文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/5
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param bashFile     指定目录下的 .sh 脚本文件
     * @param cmdArgs      shell 参数
     * @param timeout      执行超时时间
     * @param interactArgs 交互参数，其中奇数位为 "脚本执行时遇到的响应关键字"，偶数位为 "捕获响应关键字后输入的内容"
     *                     例如：["are you ok?", "yes", "Are you hungry?", "no", "done."]
     *                     当捕获到 "are you ok" 时，会向 channel 中输入 yes
     *                     当捕获到 "are you ok" 时，会向 channel 中输入 no
     *                     当捕获到 "done." 时，会结束当前 channel，并返回。
     */
    public void executeBashFileAsync(File bashFile,
                                     long timeout,
                                     String[] cmdArgs,
                                     String[] interactArgs) {

        if (cmdArgs == null) cmdArgs = new String[0];
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        CommandLine cmdLine = new CommandLine(bashFile);
        cmdLine.addArguments(cmdArgs);

        ExecuteWatchdog executeWatchdog = new ExecuteWatchdog(timeout);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWatchdog(executeWatchdog);

        try {
            executor.execute(cmdLine, resultHandler);
        } catch (IOException e) {
           log.error(ExceptionUtil.stacktraceToString(e));
        }
    }

    /**
     * Description: 执行 bash 并响应结果
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/5
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param cmdLine         最终拼装好的 bash 命令
     * @param timeout         执行超时时间
     * @param interactArgs    交互参数，其中奇数位为 "脚本执行时遇到的响应关键字"，偶数位为 "捕获响应关键字后输入的内容"
     *                        例如：["are you ok?", "yes", "Are you hungry?", "no", "done."]
     *                        当捕获到 "are you ok" 时，会向 channel 中输入 yes
     *                        当捕获到 "are you ok" 时，会向 channel 中输入 no
     *                        当捕获到 "done." 时，会结束当前 channel，并返回。
     * @param printLog        是否打印日志
     * @param expectExitValue 指定正常退出的退出码，默认 0 判定为脚本正常执行完毕的退出
     * @return BashResult 执行结果
     */
    private BashResult result(CommandLine cmdLine,
                              int expectExitValue,
                              long timeout,
                              String[] interactArgs,
                              boolean printLog) {

        String execLog = String.format(
                "CommandLine: %s, ExpectExitValue: %s, ExpectTimeout: %s",
                cmdLine.toString(),
                expectExitValue,
                timeout
        );

        DefaultExecutor executor = new DefaultExecutor();

        try {
            BashLogOutputStream logOutputStream = new BashLogOutputStream(interactArgs);

            if (ArrayUtil.isEmpty(interactArgs)) {
                executor.setStreamHandler(new AutoFlushingPumpStreamHandler(logOutputStream));
            } else {
                if (interactArgs.length % 2 != 1)
                    throw new IllegalArgumentException("交互参数必须是奇数");

                executor.setStreamHandler(
                        new AutoFlushingPumpStreamHandler(
                                logOutputStream,
                                logOutputStream,
                                logOutputStream.getPipedInputStream())
                );
            }

            ExecuteWatchdog executeWatchdog = new ExecuteWatchdog(timeout);

            executor.setWatchdog(executeWatchdog);
            executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
            executor.setExitValue(expectExitValue);

            int exitValue = executeCommand(executor, cmdLine);
            String outResult = logOutputStream.getOutputLines().toString();

            BashResult bashResult = BashResult.builder()
                    .result(outResult)
                    .execLog(execLog)
                    .exitValue(exitValue)
                    .isSuccess(!executor.isFailure(exitValue))
                    .build();

            String printLogMsg = String.format(
                    "执行: %s, 结果: %s",
                    execLog,
                    bashResult.toString()
            );

            if (printLog) {
                log.info(printLogMsg);
            } else {
                log.debug(printLogMsg);
            }

            return bashResult;
        } catch (IOException e) {
            this.handleError("I/O异常", execLog, e);
        } catch (IllegalArgumentException e) {
            this.handleError("交互参数错误", execLog, e);
        }

        return null;
    }

    /**
     * Description: 执行命令行命令
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException 当执行命令时发生 I/O 异常
     *
     * @param executor 执行器
     * @param cmdLine  命令行
     * @return 命令的退出值
     */
    private int executeCommand(DefaultExecutor executor, CommandLine cmdLine) throws IOException {
        try {
            return executor.execute(cmdLine);
        } catch (ExecuteException e) {
            if (executor.getWatchdog() != null && executor.getWatchdog().killedProcess()) {
                log.error("进程关闭超时[{}]", cmdLine);
                log.error(ExceptionUtil.stacktraceToString(e));
                return -100;
            }
        }

        return -101;
    }

    /**
     * Description: 处理异常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param errorMessage 错误消息
     * @param execLog      执行日志
     * @param e            异常对象
     */
    private void handleError(String errorMessage, String execLog, Exception e) {
        log.error(errorMessage + ": {}", execLog);
        log.error(ExceptionUtil.stacktraceToString(e));
    }
}
