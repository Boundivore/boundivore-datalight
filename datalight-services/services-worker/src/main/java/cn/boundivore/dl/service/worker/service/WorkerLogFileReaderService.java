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

import cn.boundivore.dl.base.response.impl.common.AbstractLogFileVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Description: 读取日志文件相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerLogFileReaderService {


    /**
     * Description: 递归查找指定目录下的所有文件及文件夹，并返回一个树形结构的文件目录视图对象。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException, BException
     *
     * @param rootLogFileDirectory 待扫描的根路径
     * @return Result<AbstractLogFileVo.LogFileCollectionVo> 树状目录结构,文件使用文件名字符串表示
     */
    public Result<AbstractLogFileVo.LogFileCollectionVo> getLogFileCollection(String rootLogFileDirectory) {
        // 创建一个文件对象，表示根目录
        File rootFolder = new File(rootLogFileDirectory);
        // 调用递归方法，传入根目录文件对象，获取所有子文件和目录的树状结构
        AbstractLogFileVo.LogFileCollectionVo logFileCollection = listFilesRecursively(rootFolder);
        // 将结果包装在Result对象中并返回，表示操作成功
        return Result.success(logFileCollection);
    }

    /**
     * Description: 递归遍历指定文件夹，获取所有子文件夹和文件的路径，并构建一个树形结构的文件目录视图对象。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException, BException
     *
     * @param folder 当前遍历的文件夹对象
     * @return AbstractLogFileVo.LogFileCollectionVo 包含当前文件夹信息及其子文件夹和文件的树形结构视图对象
     */
    private AbstractLogFileVo.LogFileCollectionVo listFilesRecursively(File folder) {
        // 创建一个文件目录视图对象并初始化当前文件夹的基本信息
        AbstractLogFileVo.LogFileCollectionVo logFileCollectionVo = AbstractLogFileVo.LogFileCollectionVo.builder()
                .directoryName(folder.getName())
                .directoryPath(folder.getAbsolutePath())
                .filePathList(new ArrayList<>())
                .children(new ArrayList<>())
                .build();

        // 获取当前文件夹下所有文件和子文件夹
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是文件夹，则递归调用此方法，并将结果添加到children列表中
                    logFileCollectionVo.getChildren().add(listFilesRecursively(file));
                } else {
                    // 如果是文件，则将文件的绝对路径添加到filePathList列表中
                    logFileCollectionVo.getFilePathList().add(file.getAbsolutePath());
                }
            }
        }

        // 返回构建完成的文件目录视图对象
        return logFileCollectionVo;
    }


    /**
     * Description: 分步加载文件内容
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param filePath    文件绝对路径
     * @param startOffset 读取文件内容起始点
     * @param endOffset   读取文件内容结束点
     * @return Result<AbstractLogFileVo.LogFileContentVo> 文件内容响应体
     */
    public Result<AbstractLogFileVo.LogFileContentVo> loadFileContent(String filePath,
                                                                      Long startOffset,
                                                                      Long endOffset) {
        // 验证输入参数
        Assert.isFalse(
                filePath == null
                        || startOffset == null
                        || endOffset == null,
                () -> new IllegalArgumentException("非法的参数")
        );

        // 容错，防止前端调用传入负数
        startOffset = startOffset < 0 ? 0 : startOffset;
        endOffset = endOffset < 0 ? 0 : endOffset;

        Assert.isFalse(
                endOffset < startOffset,
                () -> new IllegalArgumentException("非法的偏移量数值")
        );

        long maxOffset;
        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            // 尝试跳过一个极大的数字来获取文件的总字符数
            maxOffset = br.skip(Long.MAX_VALUE);

        } catch (IOException e) {
            String error = ExceptionUtil.stacktraceToString(e);
            log.error(error);
            throw new BException(error);
        }

        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            // 尝试跳过 startOffset 之前的字符
            long actuallySkipped = br.skip(startOffset);
            if (actuallySkipped < startOffset) {
                // 如果实际跳过的字符数小于 startOffset，说明文件不够长，返回空内容
                AbstractLogFileVo.LogFileContentVo contentVo = AbstractLogFileVo.LogFileContentVo.builder()
                        .filePath(filePath)
                        .startOffset(startOffset)
                        .endOffset(startOffset) // 没有读取内容，endOffset 等于 startOffset
                        .content("")
                        .maxOffset(maxOffset)
                        .build();
                return Result.success(contentVo);
            }

            StringBuilder contentBuilder = new StringBuilder();
            long charactersToRead = endOffset - startOffset;
            int character;
            while (charactersToRead > 0 && (character = br.read()) != -1) {
                contentBuilder.append((char) character);
                charactersToRead--;
            }

            String content = contentBuilder.toString();

            AbstractLogFileVo.LogFileContentVo contentVo = AbstractLogFileVo.LogFileContentVo.builder()
                    .filePath(filePath)
                    .startOffset(startOffset)
                    .endOffset(startOffset + content.length())
                    .content(content)
                    .maxOffset(maxOffset)
                    .build();

            return Result.success(contentVo);
        } catch (IOException e) {
            String error = ExceptionUtil.stacktraceToString(e);
            log.error(error);
            throw new BException(error);
        }
    }
}
