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

import cn.boundivore.dl.base.bash.BashResult;
import cn.boundivore.dl.base.request.impl.worker.ConfigFileRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.bash.BashExecutor;
import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.BashException;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.StandardCopyOption;

/**
 * Description: Worker 修改配置文件相关
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerConfigService {

    private final BashExecutor bashExecutor;

    /**
     * Description: 修改 Worker 所在节点上的配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 修改配置文件请求体
     * @return Result<String>
     */
    public Result<String> config(ConfigFileRequest request) {

        log.info("正在操作配置文件: {}", request.getPath());

        File file = FileUtil.file(request.getPath());

        //创建文件的父目录
        File rootParentDirFile = this.getNewRelativeRootParentFile(file);
        FileUtil.mkParentDirs(file);

        if (FileUtil.exist(file)) {

            //检查是文件而非目录
            this.assertPathIsFile(file);

            // 重命名并拷贝文件
            String newFilename = String.format(
                    "%s.%s",
                    file.getName(),
                    request.getConfigVersion()
            );

            // 历史配置文件备份目录
            File bakDirFile = FileUtil.file(file.getParentFile(), "bak");
            if (!FileUtil.exist(bakDirFile)) {
                FileUtil.mkdir(bakDirFile);
            }

            // 目标备份后的文件路径
            File destFile = FileUtil.file(bakDirFile, newFilename);

            // 执行备份，拷贝原文件属性，且覆盖可能存在的重名文件
            FileUtil.copyFile(
                    file,
                    destFile,
                    StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }


        String content = Base64.decodeStr(request.getContentBase64());
        Assert.isTrue(
                this.createAndWriteFile(file, content),
                () -> new BException("为配置文件授权可执行权限失败")
        );

        // 为文件变更所属以及可执行权限
        this.chown(
                file.getAbsolutePath(),
                request.getUser(),
                request.getGroup()
        );

        return Result.success();
    }

    /**
     * Description: 查找本次操作将会创建的多级父目录的最高一级
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/9/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param file 当前准备操作的配置文件
     * @return 本次操作将会创建的多级父目录的最高一级
     */
    private File getNewRelativeRootParentFile(File file) {
        File rootParentFile = file.getParentFile();
        while (!FileUtil.getParent(rootParentFile, 1).exists()) {
            rootParentFile = FileUtil.getParent(rootParentFile, 1);
        }
        return rootParentFile;
    }

    /**
     * Description: 为指定目录以及目录下文件授权
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/31
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param filePath 授权的指定文件
     */
    private void chown(String filePath, String user, String group) {
        String cmd = String.format(
                "%s/%s",
                SpringContextUtil.SCRIPTS_DIR,
                "common-chown.sh"
        );

        BashResult bashResult = this.bashExecutor.execute(
                cmd,
                0,
                3000,
                new String[]{filePath, user, group},
                new String[0],
                true
        );

        Assert.isTrue(
                bashResult.isSuccess(),
                () -> new BashException(
                        String.format(
                                "脚本执行失败: %s",
                                cmd
                        )
                )
        );


    }

    /**
     * Description: 创建新的文件并写入数据
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param file    即将创建的文件
     * @param content 待写入文件的数据
     * @return boolean 写入并授权成功返回 true 否则返回 false
     */
    private boolean createAndWriteFile(File file, String content) {
        return FileUtil.writeString(
                content,
                file,
                CharsetUtil.CHARSET_UTF_8
        ).setExecutable(true, false);
    }

    /**
     * Description: 文件不存在抛出异常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param file 当前文件
     */
    private void assertFileNotExist(File file) {
        Assert.isTrue(
                !FileUtil.exist(file),
                () -> new BException(
                        String.format(
                                "文件不存在: %s",
                                file.getPath()
                        )
                )
        );
    }

    /**
     * Description: 当前路径不是文件则抛出异常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException
     *
     * @param file 对应路径的 File 实例
     */
    private void assertPathIsFile(File file) {
        Assert.isTrue(
                FileUtil.isFile(file),
                () -> new BException(
                        String.format(
                                "该路径不是一个文件: %s",
                                file.getPath()
                        )
                )
        );
    }
}
