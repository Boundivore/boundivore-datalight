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

import cn.boundivore.dl.base.response.impl.common.AbstractLogFileVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.resolver.ResolverYamlDirectory;
import cn.boundivore.dl.service.master.resolver.yaml.YamlDirectory;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: 日志管理相关功能
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/15
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterLogFileReaderService {

    private final RemoteInvokeWorkerService remoteInvokeWorkerService;

    private final MasterNodeService masterNodeService;


    /**
     * Description: 获取日志目录根路径
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<AbstractLogFileVo.RootDirectoryVo> 日志目录根路径
     */
    public Result<AbstractLogFileVo.RootDirectoryVo> getLogRootDirectory() {
        YamlDirectory.Directory directoryYaml = ResolverYamlDirectory.DIRECTORY_YAML.getDatalight();
        return Result.success(
                new AbstractLogFileVo.RootDirectoryVo(
                        directoryYaml.getLogDir()
                )
        );
    }


    /**
     * Description: 获取指定节点目录下的内容，树状结构
     * 结构举例：
     * >目录1
     * 文件1
     * 文件2
     * >目录2
     * 文件3
     * 文件4
     * >目录3
     * 文件5
     * 文件6
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeId               节点 ID
     * @param rootLogFileDirectory 带扫描的根目录
     * @return Result<AbstractLogFileVo.LogFileCollectionVo> 目录、文件树状结构
     */
    public Result<AbstractLogFileVo.LogFileCollectionVo> getLogCollectionWithNodeId(Long nodeId,
                                                                                    String rootLogFileDirectory) throws Exception {

        YamlDirectory.Directory directoryYaml = ResolverYamlDirectory.DIRECTORY_YAML.getDatalight();
        String logDir = directoryYaml.getLogDir()
                .substring(
                        0, directoryYaml.getLogDir().length() - (directoryYaml.getLogDir().endsWith("/") ? 1 : 0)
                );
        String requestDir = rootLogFileDirectory.substring(
                0, directoryYaml.getLogDir().length() - (directoryYaml.getLogDir().endsWith("/") ? 1 : 0)
        );

        Assert.isTrue(
                logDir.equals(requestDir),
                () -> new BException("传入的路径并非日志路径")
        );


        // 获取节点 IP
        String nodeIp = this.masterNodeService.getNodeDetailById(nodeId)
                .getData()
                .getNodeIp();

        return this.remoteInvokeWorkerService.iWorkerLogFileReaderAPI(nodeIp)
                .getLogFileCollection(rootLogFileDirectory);
    }

    /**
     * Description: 分步读取日志内容
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeId      节点 ID
     * @param filePath    日志文件绝对路径
     * @param startOffset 本次读取的起始偏移量(包含)
     * @param endOffset   本次读取的结束偏移量(不包含)
     * @return Result<AbstractLogFileVo.LogFileContentVo> 日志文件内容
     */
    public Result<AbstractLogFileVo.LogFileContentVo> loadFileContentWithNodeId(Long nodeId,
                                                                                String filePath,
                                                                                Long startOffset,
                                                                                Long endOffset) throws Exception {


        YamlDirectory.Directory directoryYaml = ResolverYamlDirectory.DIRECTORY_YAML.getDatalight();
        String logDir = directoryYaml.getLogDir()
                .substring(
                        0, directoryYaml.getLogDir().length() - (directoryYaml.getLogDir().endsWith("/") ? 1 : 0)
                );

        Assert.isTrue(
                filePath.contains(logDir),
                () -> new BException("不允许查看非日志系统之外的文件内容")
        );

        // 获取节点 IP
        String nodeIp = this.masterNodeService.getNodeDetailById(nodeId)
                .getData()
                .getNodeIp();


        return this.remoteInvokeWorkerService.iWorkerLogFileReaderAPI(nodeIp)
                .loadFileContent(
                        filePath,
                        startOffset,
                        endOffset
                );
    }


}
