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
package cn.boundivore.dl.base.bash;

import cn.boundivore.dl.base.bash.exec.LogOutputStream;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Description: BashLogOutputStream
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class BashLogOutputStream extends LogOutputStream {
    // 保存输出的日志行
    private StringBuilder outputLines = new StringBuilder();

    // 管道输入流
    private PipedInputStream pipedInputStream;

    // 管道输出流
    private PipedOutputStream pipedOutputStream;

    // 交互参数
    private String[] interactArgs;

    /**
     * 构造方法
     *
     * @param interactArgs 交互参数
     * @throws IOException 如果创建管道输入/输出流时发生 I/O 异常
     */
    public BashLogOutputStream(String... interactArgs) throws IOException {
        if (ArrayUtil.isNotEmpty(interactArgs)) {
            this.interactArgs = interactArgs;
            this.pipedInputStream = new PipedInputStream();  // 创建管道输入流
            this.pipedOutputStream = new PipedOutputStream(pipedInputStream);  // 创建管道输出流并与输入流连接
        }
    }

    @Override
    protected void processLine(String line, int logLevel) {
        // 将日志行添加到输出行中
        this.outputLines.append(line);
        if (log.isDebugEnabled()) {
            // 输出日志行到调试日志
            log.debug(line);
        }
        if (ArrayUtil.isNotEmpty(this.interactArgs)) {
            // 执行交互逻辑
            this.interact(line);
        }
    }

    /**
     * Description: 执行交互逻辑
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param line
     */
    private void interact(String line) {
        try {
            for (int i = 0; i < interactArgs.length; i += 2) {
                if (i == interactArgs.length - 1) {
                    if (StrUtil.contains(line, interactArgs[i])) {
                        this.pipedOutputStream.flush();
                        this.pipedOutputStream.close();
                    }
                    return;
                } else {
                    if (StrUtil.contains(line, interactArgs[i])) {
                        this.pipedOutputStream.write(
                                String.format(
                                        "%s\n",
                                        interactArgs[i + 1]).getBytes(CharsetUtil.CHARSET_UTF_8)
                        );
                        this.pipedOutputStream.flush();
                    }
                }
            }
        } catch (IOException e) {
            log.error("交互时发生异常", e);
        }
    }

    @Override
    public void flush() {
        super.flush();
    }
}
