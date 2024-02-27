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
package cn.boundivore.dl.ssh.bean;

import cn.boundivore.dl.ssh.listener.UpdateDatabaseCallback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description: 程序包推送进度
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Slf4j
public class TransferProgress implements Serializable {

    private static final long serialVersionUID = -1233597235169035159L;

    private UpdateDatabaseCallback updateDatabaseCallback;

    /**
     * 文件字节数
     */
    // 当前节点总待传输字节数
    private long totalBytes = 0L;

    // 当前节点传输字节总进度
    private long totalProgress = 0L;

    // 当前节点已传输字节数
    private AtomicLong totalTransferBytes = new AtomicLong(0L);

    /**
     * 文件个数
     */
    // 当前节点总待传输文件数
    private long totalFileCount = 0L;

    // 当前节点传输文件个数总进度
    private long totalFileCountProgress = 0L;

    // 当前节点已传输文件数
    private AtomicLong totalTransferFileCount = new AtomicLong(0L);

    /**
     * 当前正在传输的文件
     */
    private FileProgress currentFileProgress;


    private LinkedHashMap<FilePath, FileProgress> fileProgressMap = new LinkedHashMap<>();

    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class FilePath {

        private String fileDir;

        private String filename;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class FileProgress implements Serializable {

        private static final long serialVersionUID = -7659059436032348207L;

        private String filename;

        private long fileBytes = 0L;

        private AtomicLong fileTransferBytes = new AtomicLong(0L);

        private long fileProgress = 0L;

        private String printSpeed = "0 b/s";

        /**
         * Description: 更新文件传输进度
         * Created by: Boundivore
         * E-mail: boundivore@foxmail.com
         * Creation time: 2023/7/7
         * Modification description:
         * Modified by:
         * Modification time:
         * Throws:
         *
         * @param deltaTransferBytes 本次传输的字节数
         * @return 更新后的文件进度对象
         */
        public FileProgress updateFileProgress(long deltaTransferBytes, String showSpeed) {
            this.fileProgress = this.fileBytes == 0 ?
                    100 :
                    (fileTransferBytes.addAndGet(deltaTransferBytes) * 100) / this.fileBytes;

            this.printSpeed = showSpeed;

            return this;
        }

    }

    /**
     * Description: 将文件路径和文件进度信息存储到进度对象中
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param filePath     文件路径对象、文件目录和文件名
     * @param fileProgress 文件进度对象，包含文件名和文件字节数
     */
    public void put(FilePath filePath, FileProgress fileProgress) {
        this.fileProgressMap.put(
                filePath,
                fileProgress
        );
    }

    /**
     * Description: 获取文件进度信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param filePath 文件路径对象、文件目录和文件名
     * @return fileProgress 文件进度对象，包含文件名和文件字节数
     */
    public FileProgress get(FilePath filePath) {
        return this.fileProgressMap.get(
                filePath
        );
    }


    /**
     * Description: 更新总体传输进度
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param deltaTransferBytes 本次传输的字节数
     * @return 更新后的进度对象
     */
    public TransferProgress updateTotalProgress(long deltaTransferBytes) {
        this.totalProgress = this.totalBytes == 0 ?
                100 :
                (totalTransferBytes.addAndGet(deltaTransferBytes) * 100) / this.totalBytes;

        return this;
    }

    /**
     * Description: 初始化待传输总字节数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 待传输总字节数
     */
    public TransferProgress initTotalProgress() {
        this.totalBytes = 0;
        this.fileProgressMap.forEach(
                (filePath, fileProgress) -> this.totalBytes += fileProgress.getFileBytes()
        );
        return this;
    }

    /**
     * Description: 初始化待传输总文件数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 待传输总文件数
     */
    public TransferProgress initTotalFileCount() {
        this.totalFileCount = fileProgressMap.size();
        return this;
    }

    /**
     * Description: 更新已传输文件个数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 更新后的进度对象
     */
    public TransferProgress updateTotalTransferFileCount() {
        this.totalFileCountProgress = this.totalFileCount == 0 ?
                100 :
                (this.totalTransferFileCount.incrementAndGet() * 100) / this.totalFileCount;
        return this;
    }

    /**
     * Description: 将传输进度更新到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param transferProgress 当前文件传输进度
     * @return TransferProgress
     */
    public TransferProgress updateDatabase(TransferProgress transferProgress) {
        if (updateDatabaseCallback != null) {
            updateDatabaseCallback.update(transferProgress);
        }
        return transferProgress;
    }
}
