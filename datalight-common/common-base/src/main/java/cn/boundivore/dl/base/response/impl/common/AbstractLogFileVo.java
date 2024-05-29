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
package cn.boundivore.dl.base.response.impl.common;

import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description: 读取日志文件响应体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractLogFileVo {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractLogFileVo.RootDirectoryVo",
            description = "AbstractLogFileVo.RootDirectoryVo 日志根目录路径响应体"
    )
    public final static class RootDirectoryVo implements IVo {

        private static final long serialVersionUID = -9178897731336811616L;

        @Schema(name = "RootDirectoryPath", title = "日志根目录路径", required = true)
        @JsonProperty(value = "RootDirectoryPath", required = true)
        private String rootDirectoryPath;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractLogFileVo.LogFileCollectionVo",
            description = "AbstractLogFileVo.LogFileCollectionVo 日志文件集合响应体"
    )
    public final static class LogFileCollectionVo implements IVo {

        private static final long serialVersionUID = -7832117035956694023L;

        @Schema(name = "DirectoryName", title = "当前目录名称", required = true)
        @JsonProperty(value = "DirectoryName", required = true)
        private String directoryName;

        @Schema(name = "DirectoryPath", title = "当前目录路径", required = true)
        @JsonProperty(value = "DirectoryPath", required = true)
        private String directoryPath;

        @Schema(name = "FilePathList", title = "当前目录下的文件绝对路径", required = true)
        @JsonProperty(value = "FilePathList", required = true)
        private List<String> filePathList;

        @Schema(name = "Children", title = "子文件和文件夹", required = true)
        @JsonProperty(value = "Children", required = true)
        private List<LogFileCollectionVo> children;

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractLogFileVo.LogFileContentVo",
            description = "AbstractLogFileVo.LogFileContentVo 日志文件内容响应体"
    )
    public final static class LogFileContentVo implements IVo {

        private static final long serialVersionUID = 8468604500008540525L;

        @Schema(name = "FilePath", title = "文件绝对路径", required = true)
        @JsonProperty(value = "FilePath", required = true)
        private String filePath;

        @Schema(name = "StartOffset", title = "本次内容起始偏移量, [StartOffset, EndOffset)", required = true)
        @JsonProperty(value = "StartOffset", required = true)
        private Long startOffset;

        @Schema(name = "EndOffset", title = "本次内容结束偏移量, [StartOffset, EndOffset)", required = true)
        @JsonProperty(value = "EndOffset", required = true)
        private Long endOffset;

        @Schema(name = "Content", title = "文件内容", required = true)
        @JsonProperty(value = "Content", required = true)
        private String content;

        @Schema(name = "MaxOffset", title = "日志当前本次最大偏移量", required = true)
        @JsonProperty(value = "MaxOffset", required = true)
        private Long maxOffset;
    }


}
