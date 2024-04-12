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
package cn.boundivore.dl.api.worker.define;

import cn.boundivore.dl.base.response.impl.common.AbstractLogFileVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.WORKER_URL_PREFIX;


/**
 * Description: 读取日志文件相关接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/21
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IWorkerLogFileReaderAPI", tags = {"Worker 接口：读取日志文件相关"})
@FeignClient(
        name = "IWorkerLogFileReaderAPI",
        contextId = "IWorkerLogFileReaderAPI",
        path = WORKER_URL_PREFIX
)
public interface IWorkerLogFileReaderAPI {

    @GetMapping(value = "/log/file/getLogFileCollection")
    @ApiOperation(notes = "获取日志文件列表", value = "获取日志文件列表")
    Result<AbstractLogFileVo.LogFileCollectionVo> getLogFileCollection(
            @ApiParam(name = "RootLogFileDirectory", value = "日志文件根目录")
            @RequestParam(value = "RootLogFileDirectory", required = true)
            String rootLogFileDirectory
    ) throws Exception;

    @GetMapping(value = "/log/file/loadFileContent")
    @ApiOperation(notes = "分步加载文件内容", value = "分步加载文件内容")
    Result<AbstractLogFileVo.LogFileContentVo> loadFileContent(
            @ApiParam(name = "FilePath", value = "文件绝对路径")
            @RequestParam(value = "FilePath", required = true)
            @NotNull(message = "文件绝对路径不能为空")
            String filePath,

            @ApiParam(name = "StartOffset", value = "起始偏移量(包含)")
            @RequestParam(value = "StartOffset", required = true)
            @NotNull(message = "起始偏移量不能为空")
            Long startOffset,

            @ApiParam(name = "EndOffset", value = "结束偏移量(不包含)")
            @RequestParam(value = "EndOffset", required = true)
            @NotNull(message = "结束偏移量不能为空")
            Long endOffset
    ) throws Exception;

}
