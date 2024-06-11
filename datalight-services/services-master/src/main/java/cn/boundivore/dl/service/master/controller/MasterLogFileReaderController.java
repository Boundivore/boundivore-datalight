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
package cn.boundivore.dl.service.master.controller;

import cn.boundivore.dl.api.master.define.IMasterLogFileReaderAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.response.impl.common.AbstractLogFileVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterLogFileReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterLogFileReaderController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/15
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = true)
public class MasterLogFileReaderController implements IMasterLogFileReaderAPI {

    private final MasterLogFileReaderService masterLogFileReaderService;


    @Override
    public Result<AbstractLogFileVo.RootDirectoryVo> getLogRootDirectory() throws Exception {
        return this.masterLogFileReaderService.getLogRootDirectory();
    }

    @Override
    public Result<AbstractLogFileVo.LogFileCollectionVo> getLogCollectionWithNodeId(Long nodeId,
                                                                                    String rootLogFileDirectory) throws Exception {
        return this.masterLogFileReaderService.getLogCollectionWithNodeId(
                nodeId,
                rootLogFileDirectory
        );
    }

    @Override
    public Result<AbstractLogFileVo.LogFileContentVo> loadFileContentWithNodeId(Long nodeId,
                                                                                String filePath,
                                                                                Long startOffset,
                                                                                Long endOffset) throws Exception {
        return this.masterLogFileReaderService.loadFileContentWithNodeId(
                nodeId,
                filePath,
                startOffset,
                endOffset
        );
    }
}
