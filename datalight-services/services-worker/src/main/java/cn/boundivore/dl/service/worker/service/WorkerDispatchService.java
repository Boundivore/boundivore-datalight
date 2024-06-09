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

import cn.boundivore.dl.base.response.impl.common.AbstractDataLightDirVo;
import cn.boundivore.dl.base.result.Result;
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
public class WorkerDispatchService {


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
     * @return Result<AbstractDataLightDirVo.DataLightDirCollectionVo> 树状目录结构,文件使用文件名字符串表示
     */
    public Result<AbstractDataLightDirVo.DataLightDirCollectionVo> getDataLightDirTree(String rootLogFileDirectory) {
        // 创建一个文件对象，表示根目录
        File rootFolder = new File(rootLogFileDirectory);
        // 调用递归方法，传入根目录文件对象，获取所有子文件和目录的树状结构
        AbstractDataLightDirVo.DataLightDirCollectionVo dataLightDirCollectionVo = listFilesRecursively(rootFolder);
        // 将结果包装在Result对象中并返回，表示操作成功
        return Result.success(dataLightDirCollectionVo);
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
    private AbstractDataLightDirVo.DataLightDirCollectionVo listFilesRecursively(File folder) {
        // 创建一个文件目录视图对象并初始化当前文件夹的基本信息
        AbstractDataLightDirVo.DataLightDirCollectionVo dataLightDirCollectionVo = AbstractDataLightDirVo.DataLightDirCollectionVo.builder()
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
                    dataLightDirCollectionVo.getChildren().add(listFilesRecursively(file));
                } else {
                    // 如果是文件，则将文件的绝对路径添加到filePathList列表中
                    dataLightDirCollectionVo.getFilePathList().add(file.getAbsolutePath());
                }
            }
        }

        // 返回构建完成的文件目录视图对象
        return dataLightDirCollectionVo;
    }
}
