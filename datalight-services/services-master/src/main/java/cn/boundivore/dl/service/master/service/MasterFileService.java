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

import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.po.single.TDlConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Description: Master 文件操作相关服务类，提供配置文件下载、压缩等功能
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/12/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MasterFileService {

    private final MasterConfigService masterConfigService;

    /**
     * Description: 文件大小上限常量，限制压缩包大小不超过100MB
     */
    private static final long MAX_ZIP_SIZE = 100 * 1024 * 1024;

    /**
     * Description: 文件操作缓冲区大小常量，用于优化IO性能
     */
    private static final int BUFFER_SIZE = 8192;

    /**
     * Description: 下载配置文件，将指定服务的配置文件打包下载
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 业务异常，包括文件不存在、内容不完整等
     * IOException - IO异常，包括文件读写、压缩、下载等过程中的异常
     * SecurityException - 安全异常，包括不安全的文件路径等
     *
     * @param clusterId   集群ID
     * @param nodeId      节点ID
     * @param serviceName 服务名称
     * @param filePathArr 待下载的文件路径列表(英文逗号分隔)
     * @param response    HTTP响应对象
     */
    public void download(Long clusterId,
                         Long nodeId,
                         String serviceName,
                         String filePathArr,
                         HttpServletResponse response) throws Exception {
        // 获取配置文件列表
        List<String> pathList = Optional.ofNullable(filePathArr)
                .map(str -> Arrays.stream(str.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        Path tempDir = null;
        String zipFilePath = null;

        try {

            List<TDlConfig> tDlConfigListByPathList = this.masterConfigService.getTDlConfigListByPathList(
                    clusterId,
                    nodeId,
                    serviceName,
                    pathList
            );

            // 验证配置文件列表完整性
            this.validateConfigList(pathList, tDlConfigListByPathList);

            // 创建临时目录及确定zip文件路径
            tempDir = Files.createTempDirectory(serviceName + "_");
            zipFilePath = tempDir.getParent().resolve(serviceName + ".zip").toString();

            // 获取配置内容并创建文件结构
            Map<Long, String> configContentMap = this.masterConfigService.getConfigContentMap(
                    tDlConfigListByPathList
            );
            this.createFileStructure(tempDir, tDlConfigListByPathList, configContentMap);

            // 检查文件大小并压缩
            this.validateTotalSize(tempDir.toFile());
            this.compressDirectory(tempDir.toFile(), zipFilePath);

            // 执行下载
            this.downloadZipFile(zipFilePath, serviceName, response);

            log.info("配置文件下载成功: serviceName={}, fileCount={}", serviceName, pathList.size());

        } catch (Exception e) {
            log.error("配置文件下载失败: serviceName={}, error={}", serviceName, e.getMessage(), e);
            throw new BException("下载配置文件失败：" + e.getMessage());
        } finally {
            // 确保响应完全发送后再清理
            try {
                response.flushBuffer();
            } catch (IOException e) {
                log.warn("刷新响应缓冲区失败", e);
            }

            this.cleanupTempFiles(tempDir, zipFilePath);
        }
    }

    /**
     * Description: 验证配置文件列表完整性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 当存在未找到的配置文件时抛出
     *
     * @param filePathList 请求的文件路径列表
     * @param configList   实际查询到的配置列表
     */
    private void validateConfigList(List<String> filePathList, List<TDlConfig> configList) {
        if (configList.size() != filePathList.size()) {
            Set<String> configPaths = configList.stream()
                    .map(TDlConfig::getConfigPath)
                    .collect(Collectors.toSet());

            List<String> missingPaths = filePathList.stream()
                    .filter(path -> !configPaths.contains(path))
                    .collect(Collectors.toList());

            throw new BException(
                    String.format("以下配置文件未找到：%s",
                            String.join(", ", missingPaths))
            );
        }
    }

    /**
     * Description: 创建文件结构并写入内容
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException - 文件操作异常
     * SecurityException - 不安全的文件路径
     * BException - 配置内容不完整
     *
     * @param tempDir    临时目录路径
     * @param configList 配置文件列表
     * @param contentMap 配置内容映射
     */
    private void createFileStructure(Path tempDir,
                                     List<TDlConfig> configList,
                                     Map<Long, String> contentMap) throws IOException {
        // 在临时目录下创建服务目录
        Path serviceDir = tempDir.resolve(configList.get(0).getServiceName());
        Files.createDirectories(serviceDir);

        for (TDlConfig config : configList) {
            this.validatePath(config.getConfigPath());

            // 从完整路径中提取相对路径
            // 例如从 "/srv/datalight/HDFS/etc/hadoop/core-site.xml"
            // 提取 "etc/hadoop/core-site.xml"
            String fullPath = config.getConfigPath();
            String serviceName = config.getServiceName();
            int startIndex = fullPath.indexOf(serviceName) + serviceName.length() + 1;
            String relativePath = fullPath.substring(startIndex);

            // 在服务目录下创建文件
            Path filePath = serviceDir.resolve(relativePath);
            Files.createDirectories(filePath.getParent());

            // 写入文件内容
            String content = contentMap.getOrDefault(config.getConfigContentId(), "");
            Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));

            log.info("已创建配置文件: {}", filePath.toAbsolutePath());
        }

        log.info("服务 {} 的配置文件结构创建完成", configList.get(0).getServiceName());
    }

    /**
     * Description: 压缩目录为ZIP文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException - 压缩过程中的IO异常
     *
     * @param sourceDir   要压缩的源目录
     * @param zipFilePath 目标ZIP文件路径
     */
    private void compressDirectory(File sourceDir, String zipFilePath) throws IOException {
        // 找到服务目录（应该只有一个子目录）
        File[] serviceDirs = sourceDir.listFiles(File::isDirectory);
        if (serviceDirs == null || serviceDirs.length != 1) {
            throw new BException("临时目录结构异常");
        }

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ZipOutputStream zipOut = new ZipOutputStream(bos);
             Stream<Path> pathStream = Files.walk(sourceDir.toPath())) {

            Path sourcePath = sourceDir.toPath();  // 使用临时目录作为基准路径

            pathStream
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try {
                            // 使用临时目录作为基准计算相对路径，这样会保留服务名目录
                            String relativePath = sourcePath.relativize(path).toString();
                            ZipEntry zipEntry = new ZipEntry(relativePath);
                            zipOut.putNextEntry(zipEntry);
                            Files.copy(path, zipOut);
                            zipOut.closeEntry();

                            log.info("添加文件到压缩包: {}", relativePath);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }


    /**
     * Description: 下载ZIP文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException - 文件下载过程中的IO异常
     *
     * @param zipFilePath ZIP文件路径
     * @param serviceName 服务名称
     * @param response    HTTP响应对象
     */
    private void downloadZipFile(String zipFilePath,
                                 String serviceName,
                                 HttpServletResponse response) throws IOException {
        // 先设置响应头
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(serviceName + ".zip", "UTF-8"));

        // 确保完整的文件传输
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             BufferedInputStream bis = new BufferedInputStream(fis, BUFFER_SIZE);
             OutputStream os = response.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(os, BUFFER_SIZE)) {

            copyWithProgress(bis, bos, new File(zipFilePath).length());
            bos.flush();  // 确保缓冲区数据写入
        }
    }

    /**
     * Description: 带进度监控的流复制
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException - 流操作异常
     *
     * @param input     输入流
     * @param output    输出流
     * @param totalSize 总大小，用于计算进度
     */
    private void copyWithProgress(InputStream input, OutputStream output, long totalSize)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        long count = 0;
        // 记录上次输出的百分比
        int lastPercent = -1;
        int n;

        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;

            if (totalSize > 0) {
                int currentPercent = (int) ((count * 100) / totalSize);
                // 当前百分比为10的倍数，且与上次输出的不同时才输出
                if (currentPercent % 10 == 0 && currentPercent != lastPercent) {
                    log.info("传输进度: {}%", currentPercent);
                    lastPercent = currentPercent;
                }
            }
        }

        // 确保在传输完成时输出100%
        if (totalSize > 0 && count >= totalSize && lastPercent != 100) {
            log.info("传输进度: 100%");
        }
    }

    /**
     * Description: 计算目录总大小
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param directory 要计算大小的目录
     * @return long 目录总大小（字节）
     */
    private long calculateTotalSize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    size += file.length();
                }
            }
        }
        return size;
    }

    /**
     * Description: 验证文件总大小是否超过限制
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 当文件总大小超过限制时抛出
     *
     * @param directory 要验证的目录
     */
    private void validateTotalSize(File directory) {
        long totalSize = calculateTotalSize(directory);
        if (totalSize > MAX_ZIP_SIZE) {
            throw new BException("文件总大小超过限制：" + (MAX_ZIP_SIZE / 1024 / 1024) + "MB");
        }
    }

    /**
     * Description: 验证文件路径安全性，防止路径穿越等安全问题
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: SecurityException - 当检测到不安全的文件路径时抛出
     *
     * @param path 要验证的文件路径
     */
    private void validatePath(String path) {
        if (path == null || path.contains("..") || path.contains("://")) {
            throw new SecurityException("不安全的文件路径: " + path);
        }
    }

    /**
     * Description: 清理临时文件和目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException - 文件删除异常
     *
     * @param tempDir     临时目录路径
     * @param zipFilePath ZIP文件路径
     */
    private void cleanupTempFiles(Path tempDir, String zipFilePath) {
        try {
            if (tempDir != null) {
                try (Stream<Path> pathStream = Files.walk(tempDir)
                        .sorted(Comparator.reverseOrder())) {
                    pathStream.forEach(path -> {
                        try {
                            Files.delete(path);
                            log.info("已删除临时文件: {}", path);
                        } catch (IOException e) {
                            log.warn("删除文件失败: {}", path);
                        }
                    });
                }
            }
            if (zipFilePath != null) {
                Files.deleteIfExists(Paths.get(zipFilePath));
                log.info("已删除临时压缩包: {}", zipFilePath);
            }
        } catch (IOException e) {
            log.warn("清理临时文件失败", e);
        }
    }
}