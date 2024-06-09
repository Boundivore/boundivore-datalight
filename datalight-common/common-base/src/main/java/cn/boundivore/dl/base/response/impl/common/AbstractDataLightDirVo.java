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
 * Description: DataLight 充分发文件相关响应体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractDataLightDirVo {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractDataLightDirVo.DataLightDirCollectionVo",
            description = "AbstractDataLightDirVo.DataLightDirCollectionVo DataLight 部署目录集合响应体"
    )
    public final static class DataLightDirCollectionVo implements IVo {

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
        private List<DataLightDirCollectionVo> children;

    }

}
