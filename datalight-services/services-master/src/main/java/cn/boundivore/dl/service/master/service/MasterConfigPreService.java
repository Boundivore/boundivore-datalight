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
import cn.boundivore.dl.base.enumeration.impl.ProcedureStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.master.ConfigPreSaveRequest;
import cn.boundivore.dl.base.response.impl.master.ConfigPreVo;
import cn.boundivore.dl.base.response.impl.master.ServiceDependenciesVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlConfigPre;
import cn.boundivore.dl.orm.po.single.TDlService;
import cn.boundivore.dl.orm.service.single.impl.TDlConfigPreServiceImpl;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServicePlaceholder;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServicePlaceholder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 服务预配置、服务组件配置相关逻辑
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
public class MasterConfigPreService {

    private final MasterServiceService masterServiceService;

    private final TDlConfigPreServiceImpl tDlConfigPreService;

    private final MasterInitProcedureService masterInitProcedureService;


    /**
     * Description: 根据集群 ID 获取当前待部署服务中需要预配置的信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 当前操作的集群 ID
     * @return 返回预配置信息
     */
    public Result<ConfigPreVo> getConfigPreList(Long clusterId) {
        // 获取待部署的服务列表
        List<TDlService> tDlServiceList = masterServiceService.getTDlServiceListByState(
                clusterId,
                CollUtil.newArrayList(SCStateEnum.SELECTED, SCStateEnum.SELECTED_ADDITION)
        );

        // 根据待部署的服务列表，组装每一个服务应该操作的预配置信息
        List<ConfigPreVo.ServiceVo> serviceList = tDlServiceList.stream()
                .map(this::buildServiceVoFromTDlService)
                .collect(Collectors.toList());

        return Result.success(
                new ConfigPreVo(
                        clusterId,
                        serviceList
                )
        );
    }

//    public Result<String> getSavedConfigPreList(Long clusterId) {
//
//    }

    /**
     * Description: 根据给定的 TDlService 构建 ConfigPreVo.ServiceVo 对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlService DTO
     * @return ServiceVo 服务信息和占位信息响应体
     */
    private ConfigPreVo.ServiceVo buildServiceVoFromTDlService(TDlService tDlService) {

        ConfigPreVo.ServiceVo voService = new ConfigPreVo.ServiceVo(
                tDlService.getServiceName(),
                CollUtil.newArrayList()
        );

        // 获取配置文件
        YamlServicePlaceholder.Service yamlService = ResolverYamlServicePlaceholder.PLACEHOLDER_MAP.get(
                tDlService.getServiceName()
        );

        // 如果当前服务没有预配置信息，则直接返回空列表
        if (yamlService == null || yamlService.getPlaceholderInfos() == null ||
                yamlService.getPlaceholderInfos().isEmpty()) {
            return voService;
        }

        // 根据 yaml 中配置的 PlaceHolderInfo 信息，组装对应的 Vo 信息，并设置到 voService 中
        List<ConfigPreVo.PlaceholderInfoVo> placeholderInfoList = yamlService.getPlaceholderInfos()
                .stream()
                .map(this::buildPlaceholderInfoVoFromYaml)
                .collect(Collectors.toList());

        voService.setPlaceholderInfoList(placeholderInfoList);
        return voService;
    }

    /**
     * Description: 根据给定的 DeployPlaceholderYaml.PlaceholderInfo 构建 ConfigPreVo.PlaceholderInfoVo 对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param placeholderInfo Yaml 中的 Placeholder 信息
     * @return PlaceholderInfoVo 返回占位信息响应体
     */
    private ConfigPreVo.PlaceholderInfoVo buildPlaceholderInfoVoFromYaml(YamlServicePlaceholder.PlaceholderInfo placeholderInfo) {
        String templatedFilePath = placeholderInfo.getTemplatedFilePath();
        List<YamlServicePlaceholder.Property> yamlPropertyList = placeholderInfo.getProperties();

        List<ConfigPreVo.PropertyVo> voPropertyList = yamlPropertyList.stream()
                .map(p -> new ConfigPreVo.PropertyVo(p.getPlaceholder(), p.getDescribe(), p.getDefaultValue()))
                .collect(Collectors.toList());

        return new ConfigPreVo.PlaceholderInfoVo(templatedFilePath, voPropertyList);
    }


    /**
     * Description: Zookeeper 的特殊处理，将 ZOOKEEPER.yaml 读取到内存中的 {{DATA_DIR}} 替换为真实目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public void replaceZookeeperMyIdDir2DataDir(Long clusterId) {
        String serviceName = "ZOOKEEPER";
        String dataDirPlaceholder = "{{DATA_DIR}}";

        TDlConfigPre zookeeper = this.tDlConfigPreService.lambdaQuery()
                .select()
                .eq(TDlConfigPre::getClusterId, clusterId)
                .eq(TDlConfigPre::getServiceName, serviceName)
                .eq(TDlConfigPre::getPlaceholder, dataDirPlaceholder)
                .one();

        Assert.notNull(
                zookeeper,
                () -> new DatabaseException("未初始化 Zookeeper 初始目录")
        );

        List<YamlServiceDetail.ConfDir> confDirList = ResolverYamlServiceDetail.SERVICE_MAP
                .get(serviceName)
                .getConfDirs();

        for (YamlServiceDetail.ConfDir confDir : confDirList) {
            if (confDir.getServiceConfDir().equals(dataDirPlaceholder)) {
                confDir.setServiceConfDir(zookeeper.getValue());
                break;
            }
        }
    }

    /**
     * Description: 保存用户通过页面修改的预配置信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 修改后的预配置信息
     * @return 保存成功则正常返回，反之抛出异常
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> saveConfigPre(ConfigPreSaveRequest request) {

        Long clusterId = request.getClusterId();
        List<ConfigPreSaveRequest.ServiceRequest> serviceList = request.getServiceList();

        //删除之前的预配置信息
        this.deleteConfigPreBatch(clusterId);

        List<TDlConfigPre> tDlConfigPreList = serviceList.stream()
                .flatMap(
                        service -> service.getPlaceholderInfoList()
                                .stream()
                                .flatMap(
                                        placeholderInfo -> placeholderInfo.getPropertyList()
                                                .stream()
                                                .map(property -> {//根据传入的信息，创建每一个预配置信息项
                                                            TDlConfigPre tDlConfigPre = new TDlConfigPre();
                                                            tDlConfigPre.setClusterId(clusterId);
                                                            tDlConfigPre.setServiceName(service.getServiceName());
                                                            tDlConfigPre.setTemplatedConfigPath(placeholderInfo.getTemplatedFilePath());
                                                            tDlConfigPre.setPlaceholder(property.getPlaceholder());
                                                            tDlConfigPre.setDefaultValue(property.getDefaultValue());
                                                            tDlConfigPre.setValue(
                                                                    StrUtil.isBlank(property.getValue()) ?
                                                                            property.getDefaultValue() :
                                                                            property.getValue()
                                                            );

                                                            return tDlConfigPre;
                                                        }
                                                )
                                )
                )
                .collect(Collectors.toList());

        //批量保存到数据库
        Assert.isTrue(
                this.tDlConfigPreService.saveBatch(tDlConfigPreList),
                () -> new DatabaseException("保存预配置信息到失败")
        );

        // 记录预配置 Procedure
        this.masterInitProcedureService.persistServiceComponentProcedure(
                request.getClusterId(),
                null,
                ProcedureStateEnum.PROCEDURE_PRE_CONFIG
        );

        return Result.success();
    }


    /**
     * Description: 批量删除指定集群下的预配置信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return 无删除 返回 false
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public boolean deleteConfigPreBatch(Long clusterId) {
        List<TDlConfigPre> list = this.tDlConfigPreService.lambdaQuery()
                .select()
                .eq(TDlConfigPre::getClusterId, clusterId)
                .list();

        return this.tDlConfigPreService.removeBatchByIds(list);
    }

    /**
     * Description: 根据服务名称获取当前服务的配置文件目录和模板目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param serviceName 当前服务名
     * @return 服务配置目录与模板目录信息
     */
    public List<ServiceDependenciesVo.ConfDirVo> getConfDirList(String serviceName) {
        YamlServiceDetail.Service service = ResolverYamlServiceDetail.SERVICE_MAP.get(serviceName);
        List<YamlServiceDetail.ConfDir> confDirs = service.getConfDirs();

        return confDirs.stream()
                .map(i -> new ServiceDependenciesVo.ConfDirVo(
                                i.getServiceConfDir(),
                                i.getTemplatedDir()
                        )
                )
                .collect(Collectors.toList());

    }

    /**
     * Description: 获取已经保存的预配置信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   当前集群 ID
     * @param serviceName 当前服务名
     * @return 返回已经保存的预配置信息
     */
    public List<ServiceDependenciesVo.PropertyVo> getPropertyList(Long clusterId, String serviceName) {
        List<TDlConfigPre> tDlConfigPreList = this.tDlConfigPreService.lambdaQuery()
                .select()
                .eq(TDlConfigPre::getClusterId, clusterId)
                .eq(TDlConfigPre::getServiceName, serviceName)
                .list();

        return tDlConfigPreList.stream()
                .map(i -> new ServiceDependenciesVo.PropertyVo(
                                i.getTemplatedConfigPath(),
                                i.getPlaceholder(),
                                i.getValue()
                        )
                )
                .collect(Collectors.toList());
    }

}
