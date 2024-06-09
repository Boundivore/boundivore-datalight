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
package cn.boundivore.dl.ssh.tools;

import cn.boundivore.dl.ssh.bean.TransferProgress;
import cn.boundivore.dl.ssh.listener.CustomFileTransferListener;
import cn.boundivore.dl.ssh.verifier.NoneHostKeyVerifier;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.sftp.SFTPFileTransfer;
import org.aspectj.weaver.BCException;

import java.io.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description: 用于 SSH 了解到指定主机的工具类
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/3
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class SshTool {
    private static volatile SshTool instance;

    private SshTool() {
    }

    public static SshTool getInstance() {
        if (instance == null) {
            synchronized (SshTool.class) {
                if (instance == null) {
                    instance = new SshTool();
                }
            }
        }
        return instance;
    }

    /**
     * Description: 创建 SSH 连接并认证，连接到指定的主机和端口
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param host           主机地址
     * @param port           端口号
     * @param username       用户名
     * @param privateKeyPath 私钥路径
     * @throws IOException 连接异常
     */
    public SSHClient connect(String host, int port, String username, String privateKeyPath) throws IOException {
        SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new NoneHostKeyVerifier());

        sshClient.connect(host, port);
        sshClient.authPublickey(username, privateKeyPath);

        return sshClient;
    }

    /**
     * Description: 检测与指定主机的SSH连接是否成功
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param host           主机名或IP地址
     * @param port           SSH端口号
     * @param username       连接用户名
     * @param privateKeyPath 私钥文件地址
     * @return 如果连接成功则返回true，否则返回false
     */
    public boolean detectConnection(String host,
                                    int port,
                                    String username,
                                    String privateKeyPath) {
        try {
            try (SSHClient testClient = new SSHClient()) {
                testClient.addHostKeyVerifier(new NoneHostKeyVerifier());
                testClient.connect(host, port);
                testClient.authPublickey(username, privateKeyPath);
                testClient.disconnect();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Description: 执行脚本命令，并返回执行结果的输出
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param sshClient SshClient 客户端
     * @param script    要执行的脚本命令
     * @return 执行结果输出
     * @throws IOException 执行异常
     */
    public ExecResult exec(SSHClient sshClient, String script) throws IOException {
        return this.exec(sshClient, script, null, null);
    }

    /**
     * Description: 执行脚本命令，并返回执行结果的输出，并设定超时时间
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param sshClient SshClient 客户端
     * @param script    要执行的脚本命令
     * @param timeout   超时时间
     * @param unit      超时单位
     * @return 执行结果输出
     * @throws IOException 执行异常
     */
    public ExecResult exec(SSHClient sshClient, String script, Long timeout, TimeUnit unit) throws IOException {
        return this.exec(sshClient, script, timeout, unit, false);
    }

    /**
     * Description: 阻塞等待执行结果
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param sshClient ssh 客户端
     * @param script    即将执行的命令
     * @param timeout   超时时间
     * @param unit      超时单位
     * @param inPTY     是否使用伪终端执行
     * @return 执行结果
     */
    private ExecResult exec(SSHClient sshClient, String script, Long timeout, TimeUnit unit, boolean inPTY) throws IOException {
        try (Session session = sshClient.startSession()) {
            Session.Command command = session.exec(script);

            if (inPTY) {
                session.allocateDefaultPTY();
            }

            String output = this.readCommandOutput(command.getInputStream());
            String error = this.readCommandOutput(command.getErrorStream());

            if (timeout != null && unit != null) {
                command.join(timeout, unit);
            } else {
                command.join();
            }

            return new ExecResult(
                    command.getExitStatus(),
                    output + "\n" + error
            );
        }
    }

    /**
     * Description: 从输入流中获取命令输出，并将其转换为字符串
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param inputStream 输入流
     * @return 命令输出字符串
     * @throws IOException 读取输入流异常
     */
    private String readCommandOutput(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        reader.close();
        return output.toString();
    }


    /**
     * Description: 执行带有交互功能的脚本，并根据指定的交互输入自动回答
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param sshClient         SshClient 客户端
     * @param script            要执行的脚本
     * @param interactiveInputs 交互输入的键值对，键表示要匹配的关键字，值表示要自动回答的内容
     * @return 执行结果输出
     * @throws IOException 执行异常
     */
    public String executeInteractiveScript(SSHClient sshClient, String script, Map<String, String> interactiveInputs) throws IOException {

        try (Session session = sshClient.startSession()) {
            Session.Command command = session.exec(script);

            // 获取输入流和输出流
            InputStream inputStream = command.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = command.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);

            // 读取输出并处理交互
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");

                // 检查是否需要输入交互内容
                for (Map.Entry<String, String> entry : interactiveInputs.entrySet()) {
                    if (line.contains(entry.getKey())) {
                        // 输入交互内容
                        writer.println(entry.getValue());
                        writer.flush();
                        break;
                    }
                }
            }

            // 等待命令执行完成
            command.join();

            // 关闭输入流和输出流
            reader.close();
            writer.close();

            return output.toString();
        }
    }

    /**
     * Description: 断开 SSH 连接
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param sshClient SshClient 客户端
     * @throws IOException 关闭连接异常
     */
    public void disconnect(SSHClient sshClient) throws IOException {
        if (sshClient.isConnected()) {
            sshClient.disconnect();
        }
    }


    /**
     * Description: 使用SFTPClient上传或下载文件夹，并输出传输进度信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param transferProgress 推送进度信息
     * @param sshClient        SshClient 客户端
     * @param localPath        本地文件夹路径
     * @param remotePath       远程文件夹路径
     * @param isUpload         是否上传文件夹，如果为 false 则为下载文件夹
     * @throws IOException 文件传输异常
     */
    public void transfer(TransferProgress transferProgress,
                         SSHClient sshClient,
                         String localPath,
                         String remotePath,
                         boolean isUpload) throws IOException {

        try (SFTPClient sftpClient = sshClient.newSFTPClient()) {

            SFTPFileTransfer fileTransfer = sftpClient.getFileTransfer();

            fileTransfer.setTransferListener(
                    new CustomFileTransferListener(transferProgress, sftpClient)
            );

            if (isUpload) {
                fileTransfer.upload(localPath, remotePath);
                this.exec(sshClient, "chmod +x -R " + remotePath);
            } else {
                fileTransfer.download(remotePath, localPath);
                FileUtil.file(localPath).setExecutable(true, false);
            }
        }
    }

    /**
     * Description: 初始化节点推送进度
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException  如果在初始化过程中发生I/O错误
     *
     * @param transferPath 传输路径
     * @param sshClient    SSH客户端
     * @param isUpload     上传或下载
     * @return NodeTransferProgress - 节点传输进度
     */
    public TransferProgress initNodePushProgress(String transferPath,
                                                 SSHClient sshClient,
                                                 boolean isUpload) throws IOException {
        // 实例化 NodePushProgress
        TransferProgress transferProgress = new TransferProgress();

        // 遍历目录获取所有文件和字节数
        if (isUpload) {
            this.listFilesRecursive(transferProgress, transferPath);
        } else {
            try (SFTPClient sftpClient = sshClient.newSFTPClient()) {
                this.listFilesRecursive(transferProgress, transferPath, sftpClient);
            }
        }

        Assert.notEmpty(
                transferProgress.getFileProgressMap(),
                () -> new RuntimeException("无效的传输: 待传输的目录下不存在任何文件")
        );

        transferProgress.initTotalProgress();
        transferProgress.initTotalFileCount();

        transferProgress.setCurrentFileProgress(
                CollUtil.getLast(
                        transferProgress.getFileProgressMap().values()
                )
        );

        return transferProgress;
    }

    /**
     * Description: 递归获取指定目录下所有文件的绝对路径和文件字节数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param transferProgress 进度对象，用于记录文件信息
     * @param transferPath     目录对象，表示待遍历的目录
     */
    private void listFilesRecursive(TransferProgress transferProgress, String transferPath) {

        File rootFile = FileUtil.file(transferPath);

        File[] files = rootFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归进入子目录
                    this.listFilesRecursive(transferProgress, file.getAbsolutePath());
                } else {
                    // 组装待传输文件
                    this.assembleTransFileLocal(transferProgress, file);
                }
            }
        }
    }

    /**
     * Description: 组装本地待传输文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param transferProgress 进度对象，用于记录文件信息
     * @param file             待传输文件
     */
    private void assembleTransFileLocal(TransferProgress transferProgress, File file) {
        // 输出文件路径和字节数
        String parentDir = this.convertPath(file.getParent()) + "/";
        log.info("本地待传输文件路径：{}{}", parentDir, file.getName());

        // 输出文件路径和字节数
        transferProgress.put(
                new TransferProgress.FilePath()
                        .setFileDir(parentDir)
                        .setFilename(file.getName()),
                new TransferProgress.FileProgress()
                        .setFilename(file.getName())
                        .setFileBytes(file.length())
        );
    }

    /**
     * Description: 递归获取远端节点指定目录下所有文件的绝对路径和文件字节数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param transferProgress 进度对象，用于记录文件信息
     * @param transferPath     远端目录对象，表示待遍历的目录
     * @param sftpClient       进度对象，用于记录文件信息
     */
    private void listFilesRecursive(TransferProgress transferProgress,
                                    String transferPath,
                                    SFTPClient sftpClient) throws IOException {

        sftpClient.ls(transferPath, resource -> {
            if (resource.isDirectory()) {
                // 递归进入子目录
                try {
                    listFilesRecursive(transferProgress, resource.getPath(), sftpClient);
                } catch (IOException e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                }
            } else {
                // 组装待传输文件
                this.assembleTransFileRemote(transferProgress, resource);
            }
            return true;
        });
    }

    /**
     * Description: 组装远端待传输文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param transferProgress 进度对象，用于记录文件信息
     * @param resource         待传输文件
     */
    private void assembleTransFileRemote(TransferProgress transferProgress, RemoteResourceInfo resource) {
        // 输出文件路径和字节数
        String parentDir = this.convertPath(resource.getParent()) + "/";
        log.info("远端待传输文件路径：{}{}", parentDir, resource.getName());

        transferProgress.put(
                new TransferProgress.FilePath()
                        .setFileDir(parentDir)
                        .setFilename(resource.getName()),
                new TransferProgress.FileProgress()
                        .setFilename(resource.getName())
                        .setFileBytes(resource.getAttributes().getSize())
        );
    }

    /**
     * Description: 该函数用于将给定的路径转换为指定格式。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param path 要转换的路径字符串
     * @return 转换后的路径字符串
     */
    public String convertPath(String path) {
        // 查找第一次出现 "datalight" 的索引位置
        int index = path.indexOf("datalight");

        Assert.isTrue(
                index != -1,
                () -> new BCException("安全起见，传输的文件路径必须为 datalight 路径，当前路径中不包含 datalight")
        );

        // 如果找到了 "datalight"
        if (index != -1) {
            // 截取 "datalight" 之后的部分
            String convertedPath = path.substring(index);

            // 替换路径中的反斜杠为斜杠
            convertedPath = convertedPath.replace("\\", "/");

            // 去除路径开头的斜杠
            if (convertedPath.startsWith("/")) {
                convertedPath = convertedPath.substring(1);
            }

            // 返回转换后的路径
            return convertedPath;
        }

        // 如果未找到 "datalight"，则返回原始路径
        return path;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Accessors(chain = true)
    public static class ExecResult {
        private Integer exitCode;

        private String output;
    }


}

