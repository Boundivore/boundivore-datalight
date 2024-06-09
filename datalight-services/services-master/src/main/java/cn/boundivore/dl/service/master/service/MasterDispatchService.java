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
package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.base.response.impl.common.AbstractDataLightDirVo;
import cn.boundivore.dl.base.response.impl.common.AbstractLogFileVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.resolver.ResolverYamlDirectory;
import cn.boundivore.dl.service.master.resolver.yaml.YamlDirectory;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

/**
 * Description: 重分发文件相关
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterDispatchService {

    private final RemoteInvokeWorkerService remoteInvokeWorkerService;

    /**
     * Description: 获取指定节点目录下的内容，树状结构
     * 结构举例：
     * >目录1
     *      文件1
     *      文件2
     *      >目录2
     *          文件3
     *          文件4
     *      >目录3
     *          文件5
     *          文件6
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<AbstractDataLightDirVo.DataLightDirCollectionVo> 目录、文件树状结构
     */
    public Result<AbstractDataLightDirVo.DataLightDirCollectionVo> getDataLightDirTree() throws Exception {

        String datalightDirectory = "/opt/datalight";

        return this.remoteInvokeWorkerService.iWorkerDispatchAPI("localhost")
                .getDataLightDirTree(datalightDirectory);
    }

}
