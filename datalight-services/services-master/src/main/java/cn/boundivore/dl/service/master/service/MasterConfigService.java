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

import cn.boundivore.dl.api.worker.define.IWorkerConfigAPI;
import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractServiceComponentRequest;
import cn.boundivore.dl.base.request.impl.master.ConfigSaveByGroupRequest;
import cn.boundivore.dl.base.request.impl.master.ConfigSaveRequest;
import cn.boundivore.dl.base.request.impl.worker.ConfigFileRequest;
import cn.boundivore.dl.base.response.impl.common.ConfigHistoryVersionVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeVo;
import cn.boundivore.dl.base.response.impl.master.ConfigListByGroupVo;
import cn.boundivore.dl.base.response.impl.master.ConfigSummaryListVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.mapper.custom.ComponentNodeMapper;
import cn.boundivore.dl.orm.mapper.custom.ConfigNodeMapper;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.custom.ConfigNodeDto;
import cn.boundivore.dl.orm.po.single.TDlConfig;
import cn.boundivore.dl.orm.po.single.TDlConfigContent;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.orm.service.single.impl.TDlConfigContentServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlConfigServiceImpl;
import cn.boundivore.dl.plugin.base.bean.PluginConfigEvent;
import cn.boundivore.dl.plugin.base.bean.PluginConfigResult;
import cn.boundivore.dl.service.master.bean.ConfigContentPersistedMaps;
import cn.boundivore.dl.service.master.boardcast.ConfigEvent;
import cn.boundivore.dl.service.master.boardcast.ConfigEventPublisher;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 服务组件配置文件相关逻辑
 * TODO 考虑修改配置后，是否需要重启组件，同时考虑检查配置文件未发生变动，则不更新重启标记，发生变动则更新标记
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
public class MasterConfigService {

    private final TDlConfigServiceImpl tDlConfigService;

    private final TDlConfigContentServiceImpl tDlConfigContentService;

    private final MasterNodeService masterNodeService;

    private final ConfigNodeMapper configNodeMapper;

    private final ComponentNodeMapper componentNodeMapper;

    private final ConfigEventPublisher publisher;

    private final RemoteInvokeWorkerService remoteInvokeWorkerService;

    private final MasterComponentService masterComponentService;


    /**
     * Description: 根据 pluginConfigResult 判断是否需要对配置文件进行后续操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19 13:41
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param pluginConfigResult 插件对配置文件修改后的最终结果
     * @return 本次与插件修改后的配置文件结果是否有效
     */
    public boolean isPluginConfigResultValid(PluginConfigResult pluginConfigResult) {
        Assert.notNull(
                pluginConfigResult,
                () -> new BException("PluginConfigResult 不允许为 null, 如无配置修改，请在插件中返回实例，且配置集合为空集合")
        );

        return CollUtil.isNotEmpty(pluginConfigResult.getConfigMap());
    }


    /**
     * Description: 保存或更新配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: DatabaseException 数据库操作失败抛出 DatabaseException 异常
     *
     * @param request                  待保存或更新的配置列表
     * @param groupTDlConfigContentMap <clusterId + filename + sha256, TDlConfigContent> 已存储的配置文件内容
     * @return Result<String>
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> saveConfigOrUpdateBatch(ConfigSaveRequest request,
                                                  Map<String, TDlConfigContent> groupTDlConfigContentMap) {
        // 获取集群 ID 和服务名
        final Long clusterId = request.getClusterId();
        final String serviceName = request.getServiceName();

        // 根据请求中的配置列表进行处理
        List<TDlConfig> tDlConfigList = request.getConfigList()
                .stream()
                .map(i -> {
                            // 组装 TDlConfig 对象
                            return this.createOrUpdateTDlConfig(
                                    groupTDlConfigContentMap,
                                    clusterId,
                                    serviceName,
                                    i.getNodeId(),
                                    i.getConfigPath(),
                                    i.getFilename(),
                                    i.getConfigData(),
                                    i.getSha256()
                            );

                        }
                )
                .filter(Objects::nonNull) // 过滤配置未发生变更的条目
                .collect(Collectors.toList());

        // 批量保存或更新 TDlConfig 对象
        if (!tDlConfigList.isEmpty()) {
            Assert.isTrue(
                    this.tDlConfigService.saveOrUpdateBatch(tDlConfigList),
                    () -> new DatabaseException("批量保存配置文件失败")
            );

            // 远程更新节点上对应的配置文件
            this.saveConfig2NodeBatch(tDlConfigList);

            // 在事务完成后执行发布 "配置变更" 事件
            this.publishConfigChange(clusterId, serviceName);
        }

        return Result.success();
    }

    /**
     * Description: 判断数据库中是否已存在需要引用的配置文件内容
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: DatabaseException 数据库操作失败抛出 DatabaseException 异常
     *
     * @param request 待保存或更新的配置列表
     * @return ConfigContentPersistedMaps
     */
    public ConfigContentPersistedMaps getConfigContentPersisted(ConfigSaveRequest request) throws DatabaseException {

        // 将请求中的列表按照 filename 和 sha256 进行聚合，并通过该聚合的结果，查询数据库中是否已经存在此类配置文件
        // <clusterId + filename + sha256, ConfigRequest>
        Map<String, ConfigSaveRequest.ConfigRequest> configRequestMap = request.getConfigList()
                .stream()
                .collect(
                        Collectors.toMap(
                                i -> request.getClusterId() + i.getFilename() + i.getSha256(),
                                i -> i,
                                (existing, replacement) -> existing
                        )
                );

        // 查询数据库中是否已经存在此类配置文件
        // <clusterId + filename + sha256, TDlConfigContent>
        Map<String, TDlConfigContent> tDlConfigContentMap = configRequestMap.values()
                .stream()
                .map(i -> this.getTDlConfigContentBySha256(
                                request.getClusterId(),
                                i.getFilename(),
                                i.getSha256()
                        )
                )
                .filter(Objects::nonNull)
                .collect(
                        Collectors.toMap(
                                i -> i.getClusterId() + i.getFilename() + i.getSha256(),
                                i -> i
                        )
                );

        // 用于后续判断，如果存在某个配置文件尚未入库，则执行线程安全的配置文件修改策略，否则，可并发修改配置文件
        return new ConfigContentPersistedMaps()
                .setGroupConfigRequestMap(configRequestMap)
                .setGroupTDlConfigContentMap(tDlConfigContentMap);
    }


    /**
     * Description: 根据指定条件创建或更新 TDlConfig 对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     * @param nodeId      节点 ID
     * @param configPath  配置文件路径
     * @param filename    文件名
     * @param configData  配置数据
     * @param sha256      SHA256 值
     * @return TDlConfig 创建或更新后的 TDlConfig 对象
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public TDlConfig createOrUpdateTDlConfig(Map<String, TDlConfigContent> tDlConfigContentMap,
                                             Long clusterId,
                                             String serviceName,
                                             Long nodeId,
                                             String configPath,
                                             String filename,
                                             String configData,
                                             String sha256) {

        TDlConfig tDlConfig = this.tDlConfigService.lambdaQuery()
                .select()
                .eq(TDlConfig::getClusterId, clusterId)
                .eq(TDlConfig::getServiceName, serviceName)
                .eq(TDlConfig::getNodeId, nodeId)
                .eq(TDlConfig::getConfigPath, configPath)
                .one();

        if (tDlConfig == null) {
            tDlConfig = new TDlConfig();
            tDlConfig.setVersion(0L);
            tDlConfig.setConfigVersion(0L);
            tDlConfig.setConfigContentId(-1L);
        }

        // 获取配置文件内容数据库实例
        TDlConfigContent tDlConfigContent = this.getOrNewTDlConfigContent(
                tDlConfigContentMap,
                clusterId,
                filename,
                configData,
                sha256
        );

        // EASY_TO_FIX: 此处可进一步优化性能：如果配置未发生变更，则不修改配置文件，直接远程写如配置文件内容到对应节点
//        if (tDlConfig.getConfigContentId().longValue() == tDlConfigContent.getId()) {
//            return null;
//        }

        tDlConfig.setClusterId(clusterId);
        tDlConfig.setNodeId(nodeId);
        tDlConfig.setServiceName(serviceName);
        tDlConfig.setConfigContentId(tDlConfigContent.getId());
        tDlConfig.setFilename(filename);
        tDlConfig.setConfigPath(configPath);
        tDlConfig.setConfigVersion(tDlConfig.getConfigVersion() + 1L);

        return tDlConfig;
    }

    /**
     * Description: 获取或生成一个新的 TDlConfigContent 实例
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/31
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId  集群 ID
     * @param filename   配置文件名
     * @param configData 配置文件数据内容
     * @param sha256     配置文件内容 SHA256
     * @return 配置文件内容的数据库实例
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public TDlConfigContent getOrNewTDlConfigContent(Map<String, TDlConfigContent> tDlConfigContentMap,
                                                     Long clusterId,
                                                     String filename,
                                                     String configData,
                                                     String sha256) {

        TDlConfigContent tDlConfigContent = null;
        if (tDlConfigContentMap != null && !tDlConfigContentMap.isEmpty()) {
            tDlConfigContent = tDlConfigContentMap.get(clusterId + filename + sha256);
        }

        // 读取数据库中可能存在的配置文件内容
        if (tDlConfigContent == null) {
            tDlConfigContent = this.getTDlConfigContentBySha256(clusterId, filename, sha256);
        }

        // 如果 tDlConfigContent 此时为空，则说明数据库中无此配置内容实例，需入库，如不为空，则说明已存在同样内容的配置实例，则跳过入库
        if (tDlConfigContent == null) {
            tDlConfigContent = new TDlConfigContent();
            tDlConfigContent.setVersion(0L);
            tDlConfigContent.setClusterId(clusterId);
            tDlConfigContent.setFilename(filename);
            tDlConfigContent.setSha256(sha256);
            tDlConfigContent.setConfigData(configData);

            Assert.isTrue(
                    this.tDlConfigContentService.save(tDlConfigContent),
                    () -> new DatabaseException("保存配置文件内容失败")
            );
        }

        return tDlConfigContent;

    }

    /**
     * Description: 获取配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/31
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @param filename  配置文件名
     * @param sha256    配置文件内容 SHA256
     * @return 配置文件内容的数据库实例
     */
    private TDlConfigContent getTDlConfigContentBySha256(Long clusterId,
                                                         String filename,
                                                         String sha256) {
        return this.tDlConfigContentService.lambdaQuery()
                .select()
                .eq(TDlConfigContent::getClusterId, clusterId)
                .eq(TDlConfigContent::getSha256, sha256)
                .eq(TDlConfigContent::getFilename, filename)
                .one();
    }


    /**
     * Description: 将配置文件保存到对应节点的指定位置
     * （后续提速可考虑异步多线程并发操作，或保持安全的做法，单线程操作，并添加变更进度条接口）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException 出现任何一个配置文件修改失败则抛出异常，中断后续操作
     *
     * @param tDlConfigList 配置文件列表
     */
    private void saveConfig2NodeBatch(List<TDlConfig> tDlConfigList) throws BException {

        tDlConfigList.forEach(i -> {

            TDlConfigContent tDlConfigContent = this.tDlConfigContentService.getById(i.getConfigContentId());

            // 组装修改本地配置文件请求
            ConfigFileRequest configFileRequest = new ConfigFileRequest()
                    .setPath(i.getConfigPath())
                    .setConfigVersion(i.getConfigVersion())
                    .setFilename(i.getFilename())
                    .setContentBase64(tDlConfigContent.getConfigData())
                    .setSha256(tDlConfigContent.getSha256());

            // 根据 NodeId 列表获取 Node 详情
            List<Long> nodeIdList = tDlConfigList.stream()
                    .map(TDlConfig::getNodeId)
                    .collect(Collectors.toList());

            Map<Long, TDlNode> nodeMap = masterNodeService.getNodeMap(nodeIdList);

            Result<String> result = remoteInvokeWorkerService
                    .iWorkerConfigAPI(nodeMap.get(i.getNodeId()).getIpv4())
                    .config(configFileRequest);

            Assert.isTrue(
                    result.isSuccess(),
                    () -> new BException(
                            String.format(
                                    "Worker 变更本地配置文件失败: %s",
                                    result.getMessage()
                            )
                    )
            );
        });
    }


    /**
     * Description: 发布服务配置变动
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     */
    private void publishConfigChange(Long clusterId, String serviceName) {

        // 获取指定集群下，该服务的所有配置文件
        List<ConfigNodeDto> configNodeDtoList = this.configNodeMapper.selectConfigNodeDto(
                clusterId,
                serviceName
        );

        // 创建一个映射，将 ConfigEventData 映射到对应的 ConfigEventNode 列表
        Map<PluginConfigEvent.ConfigEventData, List<PluginConfigEvent.ConfigEventNode>> configEventDataMap = new HashMap<>();

        // 遍历配置节点列表
        for (ConfigNodeDto configNodeDto : configNodeDtoList) {
            // 创建一个 ConfigEventData 实例
            PluginConfigEvent.ConfigEventData configEventData = new PluginConfigEvent.ConfigEventData()
                    // 设置 SHA256 值
                    .setSha256(configNodeDto.getSha256())
                    // 设置文件名
                    .setFilename(configNodeDto.getFilename())
                    // 设置配置路径
                    .setConfigPath(configNodeDto.getConfigPath())
                    // 设置配置数据
                    .setConfigData(configNodeDto.getConfigData());

            // 创建一个 ConfigEventNode 实例
            PluginConfigEvent.ConfigEventNode configEventNode = new PluginConfigEvent.ConfigEventNode(
                    configNodeDto.getNodeId(),
                    configNodeDto.getHostname(),
                    configNodeDto.getIpv4(),
                    configNodeDto.getConfigVersion()
            );


            // 获取 ConfigEventNode 列表，并置于当前 Map 的引用
            List<PluginConfigEvent.ConfigEventNode> configEventNodeList = configEventDataMap.computeIfAbsent(
                    configEventData,
                    k -> new ArrayList<>()
            );
            configEventNodeList.add(configEventNode);

            // 将 ConfigEventNode 列表设置到对应的 ConfigEventData 中
            if (configEventData.getConfigEventNodeList() == null || configEventData.getConfigEventNodeList().isEmpty()) {
                configEventData.setConfigEventNodeList(configEventNodeList);
            }
        }

        // 查询并设置组件在节点中的分布情况
        // 创建一个映射，将 ComponentName 映射到对应的 ConfigEventNode 列表
        Map<String, List<PluginConfigEvent.ConfigEventComponentNode>> configEventComponentNameMap =
                this.componentNodeMapper.selectComponentNodeNotInStatesDto(
                                clusterId,
                                serviceName,
                                CollUtil.newArrayList(
                                        SCStateEnum.UNSELECTED,
                                        SCStateEnum.REMOVED
                                )
                        )
                        .stream()
                        .map(i ->
                                new Pair<>(
                                        i.getComponentName(),
                                        new PluginConfigEvent.ConfigEventComponentNode(
                                                i.getNodeId(),
                                                i.getHostname(),
                                                i.getIpv4()
                                        )
                                )
                        )
                        .collect(
                                Collectors.groupingBy(
                                        Pair::getKey,
                                        Collectors.mapping(Pair::getValue, Collectors.toList())
                                )
                        );


        // 将映射中的所有键（ConfigEventData）添加到 PluginConfigEvent 的 ConfigEventDataList 中
        List<PluginConfigEvent.ConfigEventData> configEventDataList = new ArrayList<>(configEventDataMap.keySet());

        // 创建一个 PluginConfigEvent 实例
        final PluginConfigEvent pluginConfigEvent = new PluginConfigEvent();
        // 设置集群 ID
        pluginConfigEvent.setClusterId(clusterId);
        // 设置服务名
        pluginConfigEvent.setServiceName(serviceName);
        // 设置 ConfigEventDataList
        pluginConfigEvent.setConfigEventDataList(configEventDataList);
        // 设置
        pluginConfigEvent.setConfigEventComponentMap(configEventComponentNameMap);

        //封装到 ConfigEvent 事件中
        final ConfigEvent configEvent = new ConfigEvent(
                pluginConfigEvent
        );

        //发布配置变更事件
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        publisher.publishEvent(configEvent);
                    }
                }
        );
    }

    /**
     * Description: 获取指定集群下，指定服务下的配置文件列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     * @return Result<ConfigSummaryListVo> 配置信息概览列表
     */
    public Result<ConfigSummaryListVo> getConfigSummaryList(Long clusterId, String serviceName) {

        List<TDlConfig> tDlConfigList = tDlConfigService.lambdaQuery()
                .select(
                        TDlConfig::getFilename,
                        TDlConfig::getConfigPath
                )
                .eq(TDlConfig::getClusterId, clusterId)
                .eq(TDlConfig::getServiceName, serviceName)
                .list();

        List<ConfigSummaryListVo.ConfigSummaryVo> configSummaryList = tDlConfigList.stream()
                .map(i -> new ConfigSummaryListVo.ConfigSummaryVo(
                        i.getFilename(),
                        i.getConfigPath()
                ))
                .distinct()
                .collect(Collectors.toList());


        return Result.success(
                new ConfigSummaryListVo(
                        clusterId,
                        serviceName,
                        configSummaryList
                )
        );
    }


    /**
     * Description: 按照 SHA256 分组返回配置文件信息，即，按照相同服务、相同组件、相同 SHA256 进行分组，
     * 以降低重复内容，提升性能
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     * @param configPath  配置文件路径
     * @return Result<ConfigListByGroupVo> 当前指定服务的配置文件，会按照相同的内容聚合节点
     */
    public Result<ConfigListByGroupVo> getConfigListByGroup(Long clusterId,
                                                            String serviceName,
                                                            String configPath) {

        // 获取指定集群下，该服务下，指定配置文件的所有信息
        List<ConfigNodeDto> configNodeDtoList = this.configNodeMapper.selectConfigNodeDtoByConfigPath(
                clusterId,
                serviceName,
                configPath
        );

        // 创建一个映射，将 ConfigGroup 映射到对应的 ConfigNode 列表
        Map<ConfigListByGroupVo.ConfigGroupVo, List<ConfigListByGroupVo.ConfigNodeVo>> configGroupVoListMap = new HashMap<>();

        // 遍历配置节点列表
        for (ConfigNodeDto configNodeDto : configNodeDtoList) {
            // 创建一个 ConfigGroup 实例
            ConfigListByGroupVo.ConfigGroupVo configGroup = new ConfigListByGroupVo.ConfigGroupVo();
            // 设置 SHA256 值
            configGroup.setSha256(configNodeDto.getSha256());
            // 设置文件名
            configGroup.setFilename(configNodeDto.getFilename());
            // 设置配置路径
            configGroup.setConfigPath(configNodeDto.getConfigPath());
            // 设置配置数据
            configGroup.setConfigData(configNodeDto.getConfigData());


            // 创建 ConfigNode 实例
            ConfigListByGroupVo.ConfigNodeVo configNodeVo = new ConfigListByGroupVo.ConfigNodeVo(
                    configNodeDto.getNodeId(),
                    configNodeDto.getHostname(),
                    configNodeDto.getIpv4(),
                    configNodeDto.getConfigVersion()
            );

            // 获取 ConfigNode 列表
            List<ConfigListByGroupVo.ConfigNodeVo> configNodeList = configGroupVoListMap.computeIfAbsent(
                    configGroup,
                    k -> new ArrayList<>()
            );
            if (!configNodeList.contains(configNodeVo)) {
                configNodeList.add(configNodeVo);
            }
            // 将 ConfigNode 列表设置到对应的 ConfigGroup 中
            if (configGroup.getConfigNodeList() == null || configGroup.getConfigNodeList().isEmpty()) {
                configGroup.setConfigNodeList(configNodeList);
            }
        }


        // 将映射中的所有键（ConfigGroup）添加到 ConfigListByGroupVo 的 ConfigGroupList 中
        List<ConfigListByGroupVo.ConfigGroupVo> configGroupList = new ArrayList<>(configGroupVoListMap.keySet());


        return Result.success(
                new ConfigListByGroupVo(
                        clusterId,
                        serviceName,
                        configGroupList
                )
        );
    }


    /**
     * Description: 按照 SHA256 分组返回配置文件信息，即，按照相同服务、相同组件、相同 SHA256 进行分组提交
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 按照相同配置文件内容分组后的配置文件信息
     * @return Result<String>
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> saveConfigByGroup(ConfigSaveByGroupRequest request) {

        final Long clusterId = request.getClusterId();
        final String serviceName = request.getServiceName();

        final List<TDlConfig> tDlConfigList = request.getConfigGroupList()
                .stream()
                .flatMap(configGroupRequest -> configGroupRequest.getConfigNodeList()
                        .stream()
                        .map(configNodeRequest ->
                                this.createOrUpdateTDlConfig(
                                        new HashMap<>(),
                                        clusterId,
                                        serviceName,
                                        configNodeRequest.getNodeId(),
                                        configGroupRequest.getConfigPath(),
                                        configGroupRequest.getFilename(),
                                        configGroupRequest.getConfigData(),
                                        configGroupRequest.getSha256()
                                )
                        )
                )
                // 无变动的配置不更新
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        // 批量保存或更新 TDlConfig 对象
        if (!tDlConfigList.isEmpty()) {
            Assert.isTrue(
                    this.tDlConfigService.saveOrUpdateBatch(tDlConfigList),
                    () -> new DatabaseException("批量保存配置文件失败")
            );

            //远程更新节点上对应的配置文件
            this.saveConfig2NodeBatch(tDlConfigList);

            // 在事务完成后执行发布 "配置变更" 事件
            this.publishConfigChange(clusterId, serviceName);
        }

        // 更新组件是否需要重启标记
        List<Long> nodeIdList = request.getConfigGroupList()
                .stream()
                .flatMap(i -> i.getConfigNodeList()
                        .stream()
                        .map(ConfigSaveByGroupRequest.ConfigNodeRequest::getNodeId)
                )
                .collect(Collectors.toList());

        this.masterComponentService.updateComponentRestartMark(
                new AbstractServiceComponentRequest.UpdateNeedRestartRequest(
                        request.getClusterId(),
                        request.getServiceName(),
                        nodeIdList,
                        true
                )
        );


        return Result.success();
    }

    /**
     * Description: 获取某服务下的所有配置文件列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   指定集群 ID
     * @param serviceName 服务名称
     * @return 配置文件列表（去重，绝对路径）
     */
    public List<String> getConfigPathList(Long clusterId, String serviceName) {
        return this.tDlConfigService.lambdaQuery()
                .select(TDlConfig::getConfigPath)
                .eq(TDlConfig::getClusterId, clusterId)
                .eq(TDlConfig::getServiceName, serviceName)
                .groupBy(TDlConfig::getConfigPath)
                .list()
                .stream()
                .map(TDlConfig::getConfigPath)
                .collect(Collectors.toList());
    }

    /**
     * Description: 根据文件路径列表获取配置文件信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId    集群 ID
     * @param nodeId       节点 ID
     * @param filePathList 文件路径列表
     * @return List<TDlConfig> 配置文件列表
     */
    public List<TDlConfig> getTDlConfigListByPathList(Long clusterId,
                                                      Long nodeId,
                                                      String serviceName,
                                                      List<String> filePathList) {
        List<TDlConfig> tDlConfigList = this.tDlConfigService.lambdaQuery()
                .select()
                .eq(TDlConfig::getClusterId, clusterId)
                .eq(TDlConfig::getNodeId, nodeId)
                .eq(TDlConfig::getServiceName, serviceName)
                .in(TDlConfig::getConfigPath, filePathList)
                .list();

        return tDlConfigList;

    }

    /**
     * Description: 根据配置文件列表获取对应配置文件内容集合
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlConfigList 配置文件列表
     * @return Map<Long, String> <配置文件内容 ID, 配置文件内容>
     */
    public Map<Long, String> getConfigContentMap(List<TDlConfig> tDlConfigList) {
        // 参数校验
        if (CollUtil.isEmpty(tDlConfigList)) {
            return Collections.emptyMap();
        }

        // 获取所有配置内容ID
        List<Long> configContentIdList = tDlConfigList.stream()
                .map(TDlConfig::getConfigContentId)
                .filter(Objects::nonNull)  // 过滤空值
                .distinct()  // 去重
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(configContentIdList)) {
            return Collections.emptyMap();
        }

        // 查询并转换为Map
        return this.tDlConfigContentService.lambdaQuery()
                .select(TDlConfigContent::getId, TDlConfigContent::getConfigData)  // 只查询需要的字段
                .in(TBasePo::getId, configContentIdList)
                .list()
                .stream()
                .collect(
                        Collectors.toMap(
                                TDlConfigContent::getId,
                                TDlConfigContent::getConfigData,
                                (existing, replacement) -> existing,
                                HashMap::new
                        )
                );
    }

    /**
     * Description: 获取指定配置文件历史版本列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param nodeId      节点 ID
     * @param serviceName 服务名称
     * @param filename    文件名称
     * @param configPath  配置文件路径
     * @return Result<ConfigVersionVo> 配置文件历史版本列表
     */
    public Result<ConfigHistoryVersionVo> getConfigVersionInfo(Long clusterId,
                                                               Long nodeId,
                                                               String serviceName,
                                                               String filename,
                                                               String configPath) throws Exception {

        TDlConfig tDlConfig = this.tDlConfigService.lambdaQuery()
                .select()
                .eq(TDlConfig::getClusterId, clusterId)
                .eq(TDlConfig::getNodeId, nodeId)
                .eq(TDlConfig::getServiceName, serviceName)
                .eq(TDlConfig::getFilename, filename)
                .eq(TDlConfig::getConfigPath, configPath)
                .one();


        Assert.notNull(
                tDlConfig,
                () -> new BException("未找到对应配置信息")
        );

        // 远程调用 Worker 获取历史配置文件信息
        AbstractNodeVo.NodeDetailVo nodeDetailVo = this.masterNodeService.getNodeDetailById(nodeId).getData();

        IWorkerConfigAPI iWorkerConfigAPI = this.remoteInvokeWorkerService.iWorkerConfigAPI(nodeDetailVo.getNodeIp());


        return iWorkerConfigAPI.getConfigVersionInfo(
                tDlConfig.getConfigVersion(),
                tDlConfig.getId(),
                tDlConfig.getFilename(),
                tDlConfig.getConfigPath()
        );
    }

    /**
     * Description: 获取指定历史配置文件详细信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/12/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId            集群 ID
     * @param nodeId               节点 ID
     * @param serviceName          服务名称
     * @param filename             文件名称
     * @param configPath           配置文件路径
     * @param historyConfigVersion 历史配置文件版本
     * @return Result<ConfigVersionVo> 配置文件历史版本列表
     */
    public Result<ConfigHistoryVersionVo.ConfigVersionDetailVo> getConfigVersionDetail(Long clusterId,
                                                                                       Long nodeId,
                                                                                       String serviceName,
                                                                                       String filename,
                                                                                       String configPath,
                                                                                       Long historyConfigVersion) throws Exception {

        // 获取当前配置文件信息
        TDlConfig tDlConfig = this.tDlConfigService.lambdaQuery()
                .select()
                .eq(TDlConfig::getClusterId, clusterId)
                .eq(TDlConfig::getNodeId, nodeId)
                .eq(TDlConfig::getServiceName, serviceName)
                .eq(TDlConfig::getFilename, filename)
                .eq(TDlConfig::getConfigPath, configPath)
                .one();

        // 远程调用 Worker 获取历史配置文件详情
        AbstractNodeVo.NodeDetailVo nodeDetailVo = this.masterNodeService.getNodeDetailById(nodeId).getData();

        IWorkerConfigAPI iWorkerConfigAPI = this.remoteInvokeWorkerService.iWorkerConfigAPI(nodeDetailVo.getHostname());

        Result<ConfigHistoryVersionVo.ConfigVersionDetailVo> configVersionDetail = iWorkerConfigAPI.getConfigVersionDetail(
                tDlConfig.getFilename(),
                tDlConfig.getConfigPath(),
                historyConfigVersion
        );

        // 拼装剩余信息
        configVersionDetail.getData()
                .setClusterId(tDlConfig.getClusterId())
                .setNodeId(nodeDetailVo.getNodeId())
                .setNodeIp(nodeDetailVo.getNodeIp())
                .setHostname(nodeDetailVo.getHostname())
                .setServiceName(tDlConfig.getServiceName())
                .setCurrentConfigVersion(tDlConfig.getConfigVersion())
                .setHistoryConfigVersion(historyConfigVersion);


        return configVersionDetail;
    }

}
