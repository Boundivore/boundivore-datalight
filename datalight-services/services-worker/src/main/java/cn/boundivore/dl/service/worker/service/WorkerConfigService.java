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
import cn.boundivore.dl.base.request.impl.worker.ConfigDiffRequest;
import cn.boundivore.dl.base.request.impl.worker.ConfigFileRequest;
import cn.boundivore.dl.base.response.impl.common.ConfigHistoryVersionVo;
import cn.boundivore.dl.base.response.impl.worker.ConfigDifferVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.bash.BashExecutor;
import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.exception.BException;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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
     * Modification description: 优化代码结构，增强错误处理和安全性
     * Modified by: Boundivore
     * Modification time: 2024/12/24
     * Throws: BException - 参数验证失败或文件操作异常时抛出
     *
     * @param request 修改配置文件请求体
     * @return Result<String> 操作结果
     */
    public Result<String> config(ConfigFileRequest request) {
        // 参数校验
        this.validateRequest(request);

        log.info("开始操作配置文件: path={}, version={}", request.getPath(), request.getConfigVersion());

        try {
            File file = FileUtil.file(request.getPath());

            // 创建并验证目录结构
            this.createAndValidateDirectories(file);

            // 如果文件存在，进行备份
            if (FileUtil.exist(file)) {
                this.backupExistingFile(file, request.getConfigVersion());
            }

            // 写入新内容并设置权限
            this.updateFileContent(file, request);

            // 设置文件所有权
            this.updateFileOwnership(file, request.getUser(), request.getGroup());

            log.info("配置文件操作成功: {}", request.getPath());
            return Result.success();

        } catch (BException e) {
            throw e;
        } catch (Exception e) {
            log.error("配置文件操作失败: {}", e.getMessage(), e);
            throw new BException("配置文件操作失败: " + e.getMessage());
        }
    }

    /**
     * Description: 验证请求参数的合法性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 参数验证失败时抛出
     *
     * @param request 配置文件请求对象
     */
    private void validateRequest(ConfigFileRequest request) {
        Assert.notNull(request, () -> new BException("请求参数不能为空"));
        Assert.notBlank(request.getPath(), () -> new BException("文件路径不能为空"));
        Assert.notBlank(request.getContentBase64(), () -> new BException("文件内容不能为空"));
        Assert.notBlank(request.getUser(), () -> new BException("用户名不能为空"));
        Assert.notBlank(request.getGroup(), () -> new BException("用户组不能为空"));
        Assert.notNull(request.getConfigVersion(), () -> new BException("配置版本不能为空"));

        this.validatePathSecurity(request.getPath());
    }

    /**
     * Description: 验证文件路径的安全性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 检测到不安全的路径时抛出
     *
     * @param path 待验证的文件路径
     */
    private void validatePathSecurity(String path) {
        if (path.contains("..") || path.contains("://")) {
            throw new BException("非法的文件路径: " + path);
        }
    }

    /**
     * Description: 创建并验证文件的目录结构
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 目录创建或验证失败时抛出
     *
     * @param file 目标文件对象
     */
    private void createAndValidateDirectories(File file) {
        try {
            FileUtil.mkParentDirs(file);
            File parentDir = file.getParentFile();
            if (!parentDir.exists() || !parentDir.isDirectory()) {
                throw new BException("创建父目录失败: " + parentDir.getPath());
            }
        } catch (Exception e) {
            throw new BException("创建目录结构失败: " + e.getMessage());
        }
    }

    /**
     * Description: 备份现有的配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description: 优化文件时间戳更新逻辑
     * Modified by: Boundivore
     * Modification time: 2024/12/24
     * Throws: BException - 文件备份或时间戳更新失败时抛出
     *
     * @param file          源文件对象
     * @param configVersion 配置文件版本号
     */
    private void backupExistingFile(File file, Long configVersion) {
        this.assertPathIsFile(file);

        try {
            // 准备备份目录
            File bakDir = FileUtil.file(file.getParentFile(), "bak");
            FileUtil.mkdir(bakDir);

            // 准备备份文件
            String backupName = String.format("%s.%s", file.getName(), configVersion);
            File backupFile = FileUtil.file(bakDir, backupName);

            // 执行备份
            FileUtil.copyFile(
                    file,
                    backupFile,
                    StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // 更新备份文件时间戳为当前时间
            try {
                Files.setLastModifiedTime(
                        backupFile.toPath(),
                        FileTime.from(Instant.now())
                );
                log.info("已更新备份文件时间戳: {}", backupFile.getPath());
            } catch (IOException e) {
                log.warn("更新备份文件时间戳失败: {}", e.getMessage());
                // 继续执行，因为这不是致命错误
            }

            log.info("配置文件备份成功: {}", backupFile.getPath());
        } catch (Exception e) {
            throw new BException("配置文件备份失败: " + e.getMessage());
        }
    }

    /**
     * Description: 更新文件内容并设置基本权限
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 文件写入或权限设置失败时抛出
     *
     * @param file    目标文件对象
     * @param request 配置文件请求对象
     */
    private void updateFileContent(File file, ConfigFileRequest request) {
        try {
            String content = Base64.decodeStr(request.getContentBase64());
            boolean success = this.createAndWriteFile(file, content);
            if (!success) {
                throw new BException("文件写入或权限设置失败");
            }
        } catch (Exception e) {
            throw new BException("更新文件内容失败: " + e.getMessage());
        }
    }

    /**
     * Description: 更新文件的所有者和组权限
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 权限更新失败时抛出
     *
     * @param file  目标文件对象
     * @param user  新的文件所有者
     * @param group 新的文件所属组
     */
    private void updateFileOwnership(File file, String user, String group) {
        try {
            String scriptPath = String.format(
                    "%s/%s",
                    SpringContextUtil.SCRIPTS_DIR,
                    "common-chown.sh"
            );

            BashResult result = this.bashExecutor.execute(
                    scriptPath,
                    0,
                    3000,
                    new String[]{file.getAbsolutePath(), user, group},
                    new String[0],
                    true
            );

            if (!result.isSuccess()) {
                throw new BException("更改文件所有权失败: " + result.getResult());
            }

            log.debug("文件所有权更新成功: path={}, user={}, group={}", file.getPath(), user, group);
        } catch (Exception e) {
            throw new BException("更改文件所有权失败: " + e.getMessage());
        }
    }

    /**
     * Description: 创建新的文件并写入数据
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description: 优化错误处理
     * Modified by: Boundivore
     * Modification time: 2024/12/24
     * Throws: BException - 文件创建或写入失败时可能抛出
     *
     * @param file    即将创建的文件
     * @param content 待写入文件的数据
     * @return boolean 写入并授权成功返回 true 否则返回 false
     */
    private boolean createAndWriteFile(File file, String content) {
        try {
            return FileUtil.writeString(
                    content,
                    file,
                    CharsetUtil.CHARSET_UTF_8
            ).setExecutable(true, false);
        } catch (Exception e) {
            log.error("文件创建或写入失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Description: 当前路径不是文件则抛出异常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 当路径不是文件时抛出
     *
     * @param file 对应路径的File实例
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
     * @return File 本次操作将会创建的多级父目录的最高一级
     */
    private File getNewRelativeRootParentFile(File file) {
        File rootParentFile = file.getParentFile();
        while (!FileUtil.getParent(rootParentFile, 1).exists()) {
            rootParentFile = FileUtil.getParent(rootParentFile, 1);
        }
        return rootParentFile;
    }

    /**
     * Description: 获取配置文件本地历史信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 参数校验失败或文件操作异常时抛出
     *
     * @param currentConfigVersion 当前配置文件版本号
     * @param currentConfigId      当前配置文件ID
     * @param filename             当前生效的配置文件名称
     * @param configPath           当前生效的配置文件路径
     * @return Result<ConfigHistoryVersionVo> 配置文件历史信息
     */
    public Result<ConfigHistoryVersionVo> getConfigVersionInfo(Long currentConfigVersion,
                                                               Long currentConfigId,
                                                               String filename,
                                                               String configPath) {
        // 参数校验
        this.validateParameters(currentConfigVersion, currentConfigId, filename, configPath);

        try {
            // 构建备份目录路径
            Path originalPath = Paths.get(configPath);
            Path bakDirPath = originalPath.getParent().resolve("bak");

            // 检查备份目录是否存在
            if (!Files.exists(bakDirPath)) {
                log.warn("备份目录不存在: {}", bakDirPath);
                return Result.success(
                        this.buildEmptyResponse(currentConfigId)
                );
            }

            // 获取并处理所有版本文件信息
            List<ConfigHistoryVersionVo.ConfigHistoryVersionSummaryVo> versionSummaries = LongStream.rangeClosed(1, currentConfigVersion)
                    .parallel()
                    .mapToObj(version -> {
                        String versionFileName = String.format("%s.%d", filename, version);
                        Path versionPath = bakDirPath.resolve(versionFileName);

                        return processVersionFile(version, versionFileName, versionPath);
                    })
                    .filter(Objects::nonNull)
                    // 按版本号排序
                    .sorted(Comparator.comparing(ConfigHistoryVersionVo.ConfigHistoryVersionSummaryVo::getHistoryConfigVersion))
                    .collect(Collectors.toList());

            // 构建返回对象
            ConfigHistoryVersionVo response = ConfigHistoryVersionVo.builder()
                    .currentConfigId(currentConfigId)
                    .configHistoryVersionSummaryVoList(versionSummaries)
                    .build();

            return Result.success(response);

        } catch (Exception e) {
            log.error("获取配置历史版本信息失败: {}", e.getMessage(), e);
            throw new BException("获取配置历史版本信息失败: " + e.getMessage());
        }
    }

    /**
     * Description: 处理单个版本文件信息，获取文件时间戳等元数据
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException - 文件操作异常时抛出
     *
     * @param version  配置文件版本号
     * @param fileName 配置文件名称
     * @param filePath 配置文件路径
     * @return ConfigHistoryVersionSummaryVo 配置文件版本信息摘要，文件不存在时返回null
     */
    private ConfigHistoryVersionVo.ConfigHistoryVersionSummaryVo processVersionFile(Long version,
                                                                                    String fileName,
                                                                                    Path filePath) {
        try {
            if (!Files.exists(filePath)) {
                log.warn("版本文件不存在: {}", filePath);
                return null;
            }

            // 获取文件最后修改时间
            FileTime fileTime = Files.getLastModifiedTime(filePath);
            long timestamp = fileTime.toMillis();

            // 格式化时间
            String formattedDateTime = this.formatDateTime(timestamp);

            // 构建版本信息
            return ConfigHistoryVersionVo.ConfigHistoryVersionSummaryVo.builder()
                    .historyConfigVersion(version)
                    .configVersionFileName(fileName)
                    .configVersionTimestamp(timestamp)
                    .configVersionDateTimeFormat(formattedDateTime)
                    .build();

        } catch (IOException e) {
            log.warn("读取版本文件信息失败: {}, error: {}", filePath, e.getMessage());
            return null;
        }
    }

    /**
     * Description: 将时间戳格式化为指定格式的日期时间字符串
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param timestamp 时间戳（毫秒）
     * @return String 格式化后的日期时间字符串（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String formatDateTime(long timestamp) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .format(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(timestamp),
                        ZoneId.systemDefault()
                ));
    }


    /**
     * Description: 构建空的配置历史版本响应对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param currentConfigId 当前配置文件ID
     * @return ConfigHistoryVersionVo 空的配置历史版本响应对象
     */
    private ConfigHistoryVersionVo buildEmptyResponse(Long currentConfigId) {
        return ConfigHistoryVersionVo.builder()
                .currentConfigId(currentConfigId)
                .configHistoryVersionSummaryVoList(Collections.emptyList())
                .build();
    }

    /**
     * Description: 验证请求参数的有效性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 参数验证失败时抛出
     *
     * @param currentConfigVersion 当前配置版本
     * @param currentConfigId      当前配置ID
     * @param filename             文件名
     * @param configPath           配置路径
     */
    private void validateParameters(Long currentConfigVersion,
                                    Long currentConfigId,
                                    String filename,
                                    String configPath) {
        Assert.notNull(
                currentConfigVersion,
                () -> new BException("当前配置版本不能为空")
        );

        Assert.notNull(
                currentConfigId,
                () -> new BException("当前配置ID不能为空")
        );

        Assert.notBlank(
                filename,
                () -> new BException("文件名不能为空")
        );

        Assert.notBlank(
                configPath,
                () -> new BException("配置路径不能为空")
        );

        // 验证路径安全性
        if (configPath.contains("..") || configPath.contains("://")) {
            throw new BException("非法的配置路径: " + configPath);
        }

        // 验证版本号
        if (currentConfigVersion < 1) {
            throw new BException("无效的版本号: " + currentConfigVersion);
        }
    }

    /**
     * Description: 获取历史配置文件详细信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description: 完善文件读取和摘要生成逻辑
     * Modified by: Boundivore
     * Modification time: 2024/12/24
     * Throws: BException - 文件操作或编码转换失败时抛出
     *
     * @param filename   文件名称
     * @param configPath 文件路径
     * @return Result<ConfigHistoryVersionVo.ConfigVersionDetailVo> 历史配置文件详细信息
     */
    public Result<ConfigHistoryVersionVo.ConfigVersionDetailVo> getConfigVersionDetail(String filename,
                                                                                       String configPath,
                                                                                       Long historyConfigVersion) {
        log.info("开始获取配置文件详情: filename={}, path={}", filename, configPath);

        try {
            // 参数验证
            Assert.notBlank(filename, () -> new BException("文件名不能为空"));
            Assert.notBlank(configPath, () -> new BException("文件路径不能为空"));

            // 构建完整文件路径并验证
            String configPathParent = FileUtil.file(configPath).getParent();
            File file = FileUtil.file(
                    String.format(
                            "%s/bak/%s.%s",
                            configPathParent,
                            filename,
                            historyConfigVersion
                    )
            );
            this.validateConfigFile(file);

            // 读取文件内容
            String content = this.readFileContent(file);

            // 生成base64编码和sha256摘要
            String base64Content = this.generateBase64(content);
            String sha256 = this.generateSha256(content);

            // 构建返回对象
            ConfigHistoryVersionVo.ConfigVersionDetailVo detailVo = ConfigHistoryVersionVo.ConfigVersionDetailVo.builder()
                    .configFileName(filename)
                    .configFilePath(configPath)
                    .configData(base64Content)
                    .sha256(sha256)
                    .build();

            log.info("配置文件详情获取成功: filename={}, sha256={}", filename, sha256);
            return Result.success(detailVo);

        } catch (BException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取配置文件详情失败: {}", e.getMessage(), e);
            throw new BException("获取配置文件详情失败: " + e.getMessage());
        }
    }

    /**
     * Description: 验证配置文件的有效性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 文件验证失败时抛出
     *
     * @param file 配置文件对象
     */
    private void validateConfigFile(File file) {
        if (!FileUtil.exist(file)) {
            throw new BException("配置文件不存在: " + file.getPath());
        }

        if (!FileUtil.isFile(file)) {
            throw new BException("指定路径不是文件: " + file.getPath());
        }

        // 可选：添加文件大小限制检查
        long fileSize = FileUtil.size(file);
        if (fileSize > 10 * 1024 * 1024) { // 例如限制10MB
            throw new BException("配置文件大小超出限制: " + FileUtil.readableFileSize(fileSize));
        }
    }

    /**
     * Description: 读取文件内容
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 文件读取失败时抛出
     *
     * @param file 配置文件对象
     * @return String 文件内容
     */
    private String readFileContent(File file) {
        try {
            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
            if (StrUtil.isBlank(content)) {
                log.warn("配置文件内容为空: {}", file.getPath());
            }
            return content;
        } catch (IORuntimeException e) {
            throw new BException("读取配置文件失败: " + e.getMessage());
        }
    }


    /**
     * Description: 生成文件的SHA256摘要
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 摘要生成失败时抛出
     *
     * @param content 文件内容
     * @return String SHA256摘要
     */
    private String generateSha256(String content) {
        try {
            return SecureUtil.sha256(content);
        } catch (Exception e) {
            throw new BException("生成SHA256摘要失败: " + e.getMessage());
        }
    }

    /**
     * Description: 生成Base64编码
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - Base64编码失败时抛出
     *
     * @param content 原始内容
     * @return String Base64编码后的内容
     */
    private String generateBase64(String content) {
        try {
            return Base64.encode(content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BException("生成Base64编码失败: " + e.getMessage());
        }
    }

    /**
     * Description: 对比配置文件差异，并返回差异项
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 待检查的配置文件请求体
     * @return Result<ConfigDifferVo> 配置文件差异列表
     */
    public Result<ConfigDifferVo> configDiff(ConfigDiffRequest request) {
        // 构建返回结果对象
        ConfigDifferVo configDifferVo = ConfigDifferVo.builder()
                .clusterId(request.getClusterId())
                .nodeId(request.getNodeId())
                .serviceName(request.getServiceName())
                .configDetailList(new ArrayList<>())
                .build();

        // 遍历检查每个配置文件
        for (ConfigDiffRequest.ConfigInfoRequest configInfoRequest : request.getConfigInfoList()) {
            String configFilePath = configInfoRequest.getConfigPath();
            File configFile = new File(configFilePath);

            if (!configFile.exists() || !configFile.isFile()) {
                log.warn("配置文件不存在: {}", configFilePath);
                continue;
            }

            // 读取文件内容
            String content = FileUtil.readString(configFile, CharsetUtil.CHARSET_UTF_8);

            // 生成 SHA256 摘要
            String calculatedSha256 = this.generateSha256(content);

            // 生成 Base64 编码
            String base64Content = this.generateBase64(content);

            // 比对SHA256值
            if (!calculatedSha256.equals(configInfoRequest.getSha256())) {
                // 如果不一致，添加到差异列表
                ConfigDifferVo.ConfigDetailVo detailVo = ConfigDifferVo.ConfigDetailVo.builder()
                        .filename(configInfoRequest.getFilename())
                        .sha256(calculatedSha256)
                        .configData(base64Content)
                        .configPath(configInfoRequest.getConfigPath())
                        .build();

                configDifferVo.getConfigDetailList().add(detailVo);
                log.info("发现配置文件差异: {}", configInfoRequest);
            }
        }

        return Result.success(configDifferVo);
    }
}
