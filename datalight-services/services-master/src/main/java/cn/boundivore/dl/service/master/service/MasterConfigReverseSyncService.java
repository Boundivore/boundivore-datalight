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

import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.base.request.impl.master.AbstractReverseSyncRequest;
import cn.boundivore.dl.base.request.impl.master.ConfigSaveRequest;
import cn.boundivore.dl.base.request.impl.worker.ConfigDiffRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeVo;
import cn.boundivore.dl.base.response.impl.worker.ConfigDifferVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlConfig;
import cn.boundivore.dl.orm.po.single.TDlConfigContent;
import cn.boundivore.dl.orm.service.single.impl.TDlConfigContentServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlConfigServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 配置文件反向同步
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/12/25
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MasterConfigReverseSyncService {

    private final TDlConfigServiceImpl tDlConfigService;

    private final TDlConfigContentServiceImpl tDlConfigContentService;

    private final MasterNodeService masterNodeService;

    private final MasterConfigSyncService masterConfigSyncService;

    private final RemoteInvokeWorkerService remoteInvokeWorkerService;

    /**
     * Description: 反向同步配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 业务异常
     * DatabaseException - 数据库异常
     *
     * @param request 集群 ID、节点 ID、服务名称
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = {DatabaseException.class, BException.class}
    )
    public Result<String> reverseSyncConfig(AbstractReverseSyncRequest.ReverseSyncRequest request) {
        try {
            // 1. 获取所有配置文件基本信息
            List<TDlConfig> tDlConfigList = this.queryConfigList(request);

            // 2. 构建必要的映射关系
            Map<String, TDlConfig> pathConfigMap = this.buildPathConfigMap(tDlConfigList);
            Map<Long, TDlConfigContent> contentMap = this.queryConfigContentMap(tDlConfigList);

            // 3. 获取节点信息并执行远程调用
            AbstractNodeVo.NodeDetailVo nodeDetailVo = this.queryNodeDetail(request.getNodeId());
            ConfigDifferVo configDifferVo = this.executeRemoteDiff(
                    request,
                    tDlConfigList,
                    contentMap,
                    nodeDetailVo
            );

            // 4. 处理差异更新
            if (CollUtil.isNotEmpty(configDifferVo.getConfigDetailList())) {
                this.processDifferenceUpdate(request, pathConfigMap, contentMap, configDifferVo);
            } else {
                log.info("未发现配置文件差异，跳过更新");
            }

            return Result.success();
        } catch (BException e) {
            log.error("配置同步失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("配置同步发生系统异常", e);
            throw new RuntimeException("平台内部逻辑异常: " + e.getMessage());
        }
    }


    /**
     * Description: 查询指定条件下的配置文件列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 未找到配置文件时抛出
     *
     * @param request 包含查询条件的请求对象
     * @return List<TDlConfig> 配置文件列表
     */
    private List<TDlConfig> queryConfigList(AbstractReverseSyncRequest.ReverseSyncRequest request) {
        List<TDlConfig> tDlConfigList = this.tDlConfigService.lambdaQuery()
                .select()
                .eq(TDlConfig::getClusterId, request.getClusterId())
                .eq(TDlConfig::getNodeId, request.getNodeId())
                .eq(TDlConfig::getServiceName, request.getServiceName())
                .list();

        Assert.notEmpty(
                tDlConfigList,
                () -> new BException("未找到对应配置文件信息")
        );
        return tDlConfigList;
    }

    /**
     * Description: 构建配置文件路径到配置对象的映射关系
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlConfigList 配置文件列表
     * @return Map<String, TDlConfig> <ConfigFilePath, TDlConfig> 路径到配置的映射
     */
    private Map<String, TDlConfig> buildPathConfigMap(List<TDlConfig> tDlConfigList) {
        return tDlConfigList.stream()
                .filter(Objects::nonNull)
                .filter(config -> StrUtil.isNotEmpty(config.getConfigPath()) && StrUtil.isNotEmpty(config.getFilename()))
                .collect(
                        Collectors.toMap(
                                config -> {
                                    String fullPath = Paths.get(config.getConfigPath(), config.getFilename())
                                            .normalize()
                                            .toString();
                                    return fullPath.replace('\\', '/'); // 统一使用正斜杠
                                },
                                Function.identity(),
                                (existing, replacement) -> {
                                    log.warn("发现重复的配置文件路径: {}, 保留ID={}的配置",
                                            Paths.get(existing.getConfigPath(), existing.getFilename()),
                                            existing.getId()
                                    );
                                    return existing;
                                },
                                HashMap::new
                        )
                );
    }

    /**
     * Description: 查询配置内容ID到配置内容的映射关系
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlConfigList 配置文件列表
     * @return Map<Long, TDlConfigContent> <TDlConfigContentId, TDlConfig> 内容 ID 到配置内容的映射
     */
    private Map<Long, TDlConfigContent> queryConfigContentMap(List<TDlConfig> tDlConfigList) {
        List<Long> contentIds = tDlConfigList.stream()
                .map(TDlConfig::getConfigContentId)
                .collect(Collectors.toList());

        return this.tDlConfigContentService.lambdaQuery()
                .select(TDlConfigContent.class, i -> !i.getColumn().equals("config_data"))
                .in(CollUtil.isNotEmpty(contentIds), TBasePo::getId, contentIds)
                .list()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        TBasePo::getId,
                        Function.identity(),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));
    }

    /**
     * Description: 查询节点详细信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 节点不存在时抛出
     *
     * @param nodeId 节点ID
     * @return AbstractNodeVo.NodeDetailVo 节点详细信息
     */
    private AbstractNodeVo.NodeDetailVo queryNodeDetail(Long nodeId) {
        AbstractNodeVo.NodeDetailVo nodeDetailVo = this.masterNodeService
                .getNodeDetailById(nodeId)
                .getData();
        Assert.notNull(nodeDetailVo, () -> new BException("未找到节点信息"));
        return nodeDetailVo;
    }

    /**
     * Description: 执行远程配置差异对比
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 远程调用失败时抛出
     *
     * @param request       请求参数
     * @param tDlConfigList 配置文件列表
     * @param contentMap    配置内容映射
     * @param nodeDetailVo  节点详情
     * @return ConfigDifferVo 配置差异结果
     */
    private ConfigDifferVo executeRemoteDiff(
            AbstractReverseSyncRequest.ReverseSyncRequest request,
            List<TDlConfig> tDlConfigList,
            Map<Long, TDlConfigContent> contentMap,
            AbstractNodeVo.NodeDetailVo nodeDetailVo) {

        // 构建差异请求
        ConfigDiffRequest configDiffRequest = this.buildDiffRequest(request, tDlConfigList, contentMap);

        // 执行远程调用
        ConfigDifferVo configDifferVo = this.remoteInvokeWorkerService
                .iWorkerConfigAPI(nodeDetailVo.getHostname())
                .configDiff(configDiffRequest)
                .getData();

        Assert.notNull(configDifferVo, () -> new BException("远程调用返回结果为空"));
        return configDifferVo;
    }

    /**
     * Description: 构建配置差异对比请求
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 配置信息列表为空时抛出
     *
     * @param request       请求参数
     * @param tDlConfigList 配置文件列表
     * @param contentMap    配置内容映射
     * @return ConfigDiffRequest 差异对比请求
     */
    private ConfigDiffRequest buildDiffRequest(
            AbstractReverseSyncRequest.ReverseSyncRequest request,
            List<TDlConfig> tDlConfigList,
            Map<Long, TDlConfigContent> contentMap) {
        // 构建配置信息列表
        List<ConfigDiffRequest.ConfigInfoRequest> configInfoList = tDlConfigList.stream()
                .map(config -> {
                    TDlConfigContent configContent = contentMap.get(config.getConfigContentId());
                    if (configContent == null) {
                        log.warn("未找到配置内容信息, configContentId: {}", config.getConfigContentId());
                        return null;
                    }

                    return ConfigDiffRequest.ConfigInfoRequest.builder()
                            .filename(config.getFilename())
                            .sha256(configContent.getSha256())
                            .configPath(config.getConfigPath())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Assert.notEmpty(
                configInfoList,
                () -> new BException("配置文件信息列表为空")
        );

        return ConfigDiffRequest.builder()
                .clusterId(request.getClusterId())
                .nodeId(request.getNodeId())
                .serviceName(request.getServiceName())
                .configInfoList(configInfoList)
                .build();
    }

    /**
     * Description: 处理配置差异更新
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: DatabaseException - 数据库操作异常
     *
     * @param request        请求参数
     * @param pathConfigMap  路径配置映射
     * @param contentMap     配置内容映射
     * @param configDifferVo 配置差异结果
     */
    private void processDifferenceUpdate(
            AbstractReverseSyncRequest.ReverseSyncRequest request,
            Map<String, TDlConfig> pathConfigMap,
            Map<Long, TDlConfigContent> contentMap,
            ConfigDifferVo configDifferVo) {

        log.info("发现本地文件与数据库对比差异: {} 个", configDifferVo.getConfigDetailList().size());

        ConfigSaveRequest configSaveRequest = new ConfigSaveRequest();
        configSaveRequest.setClusterId(request.getClusterId());
        configSaveRequest.setServiceName(request.getServiceName());
        configSaveRequest.setConfigList(
                configDifferVo.getConfigDetailList()
                        .stream()
                        .map(config -> {
                            TDlConfig dlConfig = pathConfigMap.get(config.getConfigPath());
                            if (dlConfig != null) {
                                TDlConfigContent content = contentMap.get(dlConfig.getConfigContentId());
                                if (content != null) {

                                    log.info("准备反向同步配置: {}, 数据库SHA256: {}, 本地SHA256: {}",
                                            config.getConfigPath(),
                                            content.getSha256(),
                                            config.getSha256()
                                    );
                                }
                            }

                            return ConfigSaveRequest.ConfigRequest.builder()
                                    .nodeId(request.getNodeId())
                                    .filename(config.getFilename())
                                    .configPath(config.getConfigPath())
                                    .configData(config.getConfigData())
                                    .sha256(config.getSha256())
                                    .build();
                        })
                        .collect(Collectors.toList())
        );

        this.masterConfigSyncService.saveConfigOrUpdateBatch(configSaveRequest);
    }
}
