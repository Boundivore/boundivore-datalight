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
package cn.boundivore.dl.ssh.listener;

import cn.boundivore.dl.ssh.bean.TransferProgress;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.TransferListener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Description: 自定义文件传输进度监听器, 该类实现了 TransferListener 接口，用于监听文件传输的进度。
 * 它会在文件传输过程中打印文件名、传输进度、传输速度和已传输大小等信息，并提供更新传输进度的功能。
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/7
 * Modification description:
 * Modified by:
 * Modification time:
 */
@Slf4j
public class CustomFileTransferListener implements TransferListener {
    private static final int BYTES_IN_KB = 1024;

    private final String relPath;
    private final Consumer<Void> progressUpdateCallback;
    private final SFTPClient sftpClient;
    private final TransferProgress transferProgress;

    public CustomFileTransferListener(SFTPClient sftpClient) {
        this(
                null,
                sftpClient,
                ""
        );
    }

    public CustomFileTransferListener(TransferProgress transferProgress,
                                      SFTPClient sftpClient) {
        this(
                transferProgress,
                sftpClient,
                ""
        );
    }

    public CustomFileTransferListener(TransferProgress transferProgress,
                                      SFTPClient sftpClient,
                                      String relPath) {

        this(
                transferProgress,
                sftpClient, relPath, s -> {
                }
        );

    }

    public CustomFileTransferListener(TransferProgress transferProgress,
                                      SFTPClient sftpClient,
                                      String relPath,
                                      Consumer<Void> progressUpdateCallback) {

        this.transferProgress = transferProgress;
        this.sftpClient = sftpClient;
        this.relPath = relPath;
        this.progressUpdateCallback = progressUpdateCallback;
    }

    @Override
    public TransferListener directory(String name) {
        String dirPath = this.relPath + name + "/";
        log.info("开始传输路径 {}", dirPath);
        return new CustomFileTransferListener(transferProgress, this.sftpClient, dirPath);
    }

    @Override
    public StreamCopier.Listener file(String filename, long fileTotalSize) {
        String filePath = this.relPath + filename;

        log.info("=> 开始传输文件: {}, 路径: {}, 字节数: {} bytes", filename, filePath, fileTotalSize);

        if (fileTotalSize == 0) {
            // 如果文件为 0 字节，则直接更新进度到 100%，只需要远程创建文件句柄，无需传输
            this.updateTransferProgress(relPath, filename, 0L, "0 b/s");
            this.updateTransferFileCountProgress(relPath, filename);
        }

        return new StreamCopier.Listener() {
            long previousTransferTimestamp = System.currentTimeMillis();
            long previousTransferredSize = 0L;
            final Set<String> isRepeatSet = new HashSet<>();

            @Override
            public void reportProgress(long transferred) throws IOException {
                float progress = fileTotalSize == 0 ? 100.0F : (transferred * 100F) / fileTotalSize;
                String progressFormat = String.format("%.1f", progress);
                float frequencyPrint = Float.parseFloat(progressFormat) * 10 % 2;

                if (frequencyPrint == 0 && !isRepeatSet.contains(progressFormat)) {
                    long currentTs = System.currentTimeMillis();
                    long diffSize = transferred - previousTransferredSize;
                    long diffTs = currentTs - previousTransferTimestamp;
                    long speed = (diffTs != 0) ? 1000 * diffSize / diffTs : diffSize;

                    String printSpeed;
                    if (speed / BYTES_IN_KB / BYTES_IN_KB > 0) {
                        printSpeed = String.format("%.1f", (speed / (float) (BYTES_IN_KB * BYTES_IN_KB))) + " mb/s";
                    } else if (speed / BYTES_IN_KB > 0) {
                        printSpeed = String.format("%.1f", (speed / (float) BYTES_IN_KB)) + " kb/s";
                    } else {
                        printSpeed = speed + " b/s";
                    }

                    previousTransferredSize = transferred;
                    previousTransferTimestamp = currentTs;

                    String showTransferredSize;
                    if (transferred / BYTES_IN_KB / BYTES_IN_KB / BYTES_IN_KB > 0) {
                        showTransferredSize = String.format("%.2f", (transferred / (float) (BYTES_IN_KB * BYTES_IN_KB * BYTES_IN_KB))) + " GB";
                    } else if (transferred / BYTES_IN_KB / BYTES_IN_KB > 0) {
                        showTransferredSize = String.format("%.2f", (transferred / (float) (BYTES_IN_KB * BYTES_IN_KB))) + " MB";
                    } else if (transferred / BYTES_IN_KB > 0) {
                        showTransferredSize = String.format("%.2f", (transferred / (float) BYTES_IN_KB)) + " KB";
                    } else {
                        showTransferredSize = transferred + " B";
                    }

                    updateTransferProgress(relPath, filename, diffSize, printSpeed);

                    log.debug(
                            "=> 文件名: {}, 进度: {}%, 速度: {}, 已传输: {}",
                            filename,
                            progressFormat,
                            printSpeed,
                            showTransferredSize
                    );

                    isRepeatSet.add(progressFormat);
                }

                if (progress == 100) {
                    updateTransferFileCountProgress(relPath, filename);
                    progressUpdateCallback.accept(null);
                }
            }
        };
    }

    /**
     * Description: 更新传输进度, 此方法用于更新传输进度，并返回更新后的文件传输进度对象。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param fileDir            文件目录
     * @param filename           文件名
     * @param deltaTransferBytes 传输字节数的增量
     * @param showSpeed          显示速度
     * @return TransferProgress.FileProgress 更新后的文件传输进度对象
     */
    private TransferProgress.FileProgress updateTransferProgress(String fileDir,
                                                                 String filename,
                                                                 long deltaTransferBytes,
                                                                 String showSpeed) {

        if (this.transferProgress != null) {
            TransferProgress.FilePath filePath = new TransferProgress.FilePath(fileDir, filename);
            TransferProgress.FileProgress fileProgress = this.transferProgress.get(filePath)
                    .updateFileProgress(deltaTransferBytes, showSpeed);

            this.transferProgress.updateTotalProgress(deltaTransferBytes);
            this.transferProgress.setCurrentFileProgress(fileProgress);

            return fileProgress;
        }

        return null;
    }

    /**
     * Description: 更新传输进度, 此方法用于更新传输进度，并返回更新后的文件传输进度对象。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param fileDir  文件目录
     * @param filename 文件名
     * @return TransferProgress.FileProgress 更新后的文件传输进度对象
     */
    private TransferProgress.FileProgress updateTransferFileCountProgress(String fileDir,
                                                                          String filename) {

        if (this.transferProgress != null) {
            TransferProgress.FilePath filePath = new TransferProgress.FilePath(fileDir, filename);
            TransferProgress.FileProgress fileProgress = this.transferProgress.get(filePath);

            this.transferProgress.updateTotalTransferFileCount();

            return fileProgress;
        }

        return null;
    }
}


