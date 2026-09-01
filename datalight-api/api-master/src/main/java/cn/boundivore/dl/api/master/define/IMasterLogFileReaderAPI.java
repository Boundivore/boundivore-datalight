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

import cn.boundivore.dl.base.response.impl.common.AbstractLogFileVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.NotNull;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.WORKER_URL_PREFIX;


/**
 * Description: Master 读取日志文件相关接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/21
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Master 接口：读取日志文件相关", description = "IMasterLogFileReaderAPI")
@FeignClient(
        name = "IMasterLogFileReaderAPI",
        contextId = "IMasterLogFileReaderAPI",
        path = WORKER_URL_PREFIX
)
public interface IMasterLogFileReaderAPI {

    @GetMapping(value = "/log/file/getLogRootDirectory")
    @Operation(summary = "获取日志根目录路径", description = "获取日志根目录路径")
    Result<AbstractLogFileVo.RootDirectoryVo> getLogRootDirectory() throws Exception;

    @GetMapping(value = "/log/file/getLogCollectionWithNodeId")
    @Operation(summary = "根据节点 ID 获取日志树状集合", description = "根据节点 ID 获取日志树状集合")
    Result<AbstractLogFileVo.LogFileCollectionVo> getLogCollectionWithNodeId(
            @Parameter(name = "NodeId", description = "节点 ID")
            @RequestParam(value = "NodeId", required = true)
            @NotNull(message = "节点 ID 不能为空")
            Long nodeId,

            @Parameter(name = "RootLogFileDirectory", description = "日志文件根目录")
            @RequestParam(value = "RootLogFileDirectory", required = true)
            @NotNull(message = "根路径不能为空")
            String rootLogFileDirectory
    ) throws Exception;


    @GetMapping(value = "/log/file/loadFileContentWithNodeId")
    @Operation(summary = "根据节点 ID 分步加载文件内容", description = "根据节点 ID 分步加载文件内容")
    Result<AbstractLogFileVo.LogFileContentVo> loadFileContentWithNodeId(
            @Parameter(name = "NodeId", description = "节点 ID")
            @RequestParam(value = "NodeId", required = true)
            @NotNull(message = "节点 ID 不能为空")
            Long nodeId,

            @Parameter(name = "FilePath", description = "文件绝对路径")
            @RequestParam(value = "FilePath", required = true)
            @NotNull(message = "文件绝对路径不能为空")
            String filePath,

            @Parameter(name = "StartOffset", description = "起始偏移量(包含)")
            @RequestParam(value = "StartOffset", required = true)
            @NotNull(message = "起始偏移量不能为空")
            Long startOffset,

            @Parameter(name = "EndOffset", description = "结束偏移量(不包含)")
            @RequestParam(value = "EndOffset", required = true)
            @NotNull(message = "结束偏移量不能为空")
            Long endOffset
    ) throws Exception;

}
