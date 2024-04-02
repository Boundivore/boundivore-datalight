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
import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ProcedureStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.enumeration.impl.ServiceTypeEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractServiceComponentRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractClusterVo;
import cn.boundivore.dl.base.response.impl.master.AbstractServiceComponentVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlService;
import cn.boundivore.dl.orm.service.single.impl.TDlServiceServiceImpl;
import cn.boundivore.dl.service.master.converter.IServiceComponentConverter;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceManifest;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.boundivore.dl.base.enumeration.impl.SCStateEnum.*;

/**
 * Description: 服务操作相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/15
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterServiceService {

    private final TDlServiceServiceImpl tDlServiceService;

    private final MasterClusterService masterClusterService;

    private final IServiceComponentConverter iServiceComponentConverter;

    private final MasterInitProcedureService masterInitProcedureService;

    /**
     * Description: 获取所有服务信息列表，并附带服务在当前集群的状态，用于选择部署服务时使用
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Result<AbstractServiceComponentVo.ServiceVo> 当前集群中的所有服务信息
     */
    public Result<AbstractServiceComponentVo.ServiceVo> getServiceList(Long clusterId) {

        return Result.success(new AbstractServiceComponentVo.ServiceVo(
                        clusterId,
                        ResolverYamlServiceManifest.SERVICE_MANIFEST_YAML
                                .getDataLight()
                                .getDlcVersion(),
                        this.getServiceSummaryVoMap(clusterId)
                                .values()
                                .stream()
                                .sorted(Comparator.comparing(AbstractServiceComponentVo.ServiceSummaryVo::getPriority))
                                .collect(Collectors.toList())
                )
        );
    }

    /**
     * Description: 获取服务名称与服务概览的映射关系
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/18
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Map<ServiceName, AbstractServiceComponentVo.ServiceSummaryVo>
     */
    public Map<String, AbstractServiceComponentVo.ServiceSummaryVo> getServiceSummaryVoMap(Long clusterId) {
        // 获取当前集群中已经部署的服务
        final List<TDlService> tDlServiceList = this.getTDlServiceList(clusterId);

        // 将上述服务名称和服务状态映射到 Map 集合中
        final Map<String, SCStateEnum> serviceNameMap = new HashMap<>();
        tDlServiceList.forEach(i -> serviceNameMap.put(i.getServiceName(), i.getServiceState()));

        return ResolverYamlServiceDetail.SERVICE_MAP
                .values()
                .stream()
                .map(i -> this.iServiceComponentConverter
                        .convert2ServiceSummaryVo(i)
                        .setScStateEnum(
                                // 如果当前集群中不存在该服务，则服务状态默认为 UNSELECTED
                                serviceNameMap.getOrDefault(
                                        i.getName(),
                                        UNSELECTED
                                )
                        )
                )
                .collect(Collectors.toMap(AbstractServiceComponentVo.ServiceSummaryVo::getServiceName, i -> i));
    }

    /**
     * Description: 选择准备部署的服务，本接口多次操作为幂等性操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 被操作的服务
     * @return 成功返回 success() 失败抛出异常
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> saveServiceSelected(AbstractServiceComponentRequest.ServiceSelectRequest request) {

        // 服务公共检查
        this.checkServiceCommon(request);

        // 获取数据库中当前集群已存在的服务列表
        // <ServiceName, TDlService>
        Map<String, TDlService> tdlServiceMap = this.getServiceListByCluster(request.getClusterId());

        // 创建数据库条目实例
        List<TDlService> newTDlServiceList = request.getServiceList()
                .stream()
                .map(i -> {
                            // 如果数据库中存在，则 ID 不变，改变状态，否则生成新的条目
                            TDlService tDlService = tdlServiceMap.getOrDefault(i.getServiceName(), new TDlService());
                            SCStateEnum currentSCStateEnum = tDlService.getServiceState() == null ?
                                    UNSELECTED :
                                    tDlService.getServiceState();

                            // 使用服务选择时状态机切换状态
                            SCStateEnum newSCStateEnum = currentSCStateEnum.transitionSelectedServiceState(i.getScStateEnum());

                            tDlService.setClusterId(request.getClusterId());
                            tDlService.setServiceName(i.getServiceName());
                            tDlService.setServiceState(newSCStateEnum);
                            tDlService.setPriority(
                                    ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP
                                            .get(i.getServiceName())
                                            .getPriority()
                            );

                            return tDlService;
                        }
                )
                .sorted(Comparator.comparing(TDlService::getPriority))
                .collect(Collectors.toList());

        // 获取指定集群详细信息
        AbstractClusterVo.ClusterVo clusterVo = this.masterClusterService.getClusterById(
                request.getClusterId()
        ).getData();

        // 检查当前集群如果是计算集群，则不允许部署 STORAGE 类型的服务
        if (clusterVo.getClusterTypeEnum() == ClusterTypeEnum.COMPUTE) {
            this.checkComputeClusterServiceLegality(newTDlServiceList);

            // 检查 COMPUTE 集群服务依赖合理性
            this.checkServiceDependenciesInComputeCluster(clusterVo.getClusterId(), newTDlServiceList);
        } else {
            // 检查 MIXED 集群服务依赖合理性
            this.checkServiceDependencies(newTDlServiceList);
        }


        Assert.isTrue(
                this.tDlServiceService.saveOrUpdateBatch(newTDlServiceList),
                () -> new DatabaseException("记录服务选项到数据库失败")
        );

        // 记录服务 Procedure
        this.masterInitProcedureService.persistServiceComponentProcedure(
                request.getClusterId(),
                null,
                ProcedureStateEnum.PROCEDURE_SELECT_SERVICE
        );


        return Result.success();
    }

    /**
     * Description: 服务的公共项检查
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/17
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 被操作的服务
     */
    private void checkServiceCommon(AbstractServiceComponentRequest.ServiceSelectRequest request) {
        // 检查单次请求中是否重复传递了相同的服务名
        Assert.isTrue(
                request.getServiceList()
                        .stream()
                        .map(AbstractServiceComponentRequest.ServiceRequest::getServiceName)
                        .distinct()
                        .count() == request.getServiceList().size(),
                () -> new BException("单次请求中，重复传递了相同的服务名称")
        );

        // 检查服务名称是否合法
        request.getServiceList().forEach(
                i -> Assert.notNull(
                        ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP.get(i.getServiceName()),
                        () -> new BException(
                                String.format(
                                        "不存在的服务名称: %s",
                                        i.getServiceName()
                                )
                        )
                )
        );

        // 检查当前集群 ID 是否存在
        AbstractClusterVo.ClusterVo clusterVo = this.masterClusterService.getClusterById(
                request.getClusterId()
        ).getData();

        Assert.notNull(
                clusterVo,
                () -> new BException("未找到对应的集群 ID")
        );

        /*
         *  为确保选择服务的意图明确，检查接口调用方传递的服务列表是否完整
         * （每次传递进来的请求，必须包含 0-SERVICE-MANIFEST.yaml 配置文件定义的所有服务名）
         */
        // 检查服务数量是否与配置一致
        Assert.isTrue(
                request.getServiceList().size() == ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP.size(),
                () -> new BException("请求需包含全部服务名称以及用户的选择意图")
        );

        // 检查服务名称正确性
        request.getServiceList().forEach(
                i -> Assert.notNull(
                        ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP.get(i.getServiceName()),
                        () -> new BException(
                                String.format(
                                        "传递的服务名称不存在: %s",
                                        i
                                )
                        )
                )
        );

        // 检查服务列表中的状态值是否合法（前端传递进来只有两种意图：选择 SELECTED 或 不选择 UNSELECTED）
        Assert.isTrue(
                request.getServiceList()
                        .stream()
                        .noneMatch(i -> i.getScStateEnum() != SELECTED && i.getScStateEnum() != UNSELECTED),
                () -> new BException(
                        String.format(
                                "选择服务的过程中只允许传递 %s 或 %s 的枚举",
                                SELECTED,
                                UNSELECTED
                        )
                )
        );
    }

    /**
     * Description: 获取数据库中当前集群已存在的服务列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/17
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 目标集群 ID
     * @return Map<String, TDlService> 服务名称与服务实体的映射关系
     */
    private Map<String, TDlService> getServiceListByCluster(Long clusterId) {
        return this.tDlServiceService.lambdaQuery()
                .select()
                .eq(TDlService::getClusterId, clusterId)
                .list()
                .stream()
                .collect(Collectors.toMap(TDlService::getServiceName, i -> i));
    }

    /**
     * Description: 检查 COMPUTE 集群服务依赖项，非法时抛出异常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/17
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId         当前集群 ID
     * @param newTDlServiceList 准备入库的用户选择部署服务的结果列表
     */
    private void checkServiceDependenciesInComputeCluster(Long clusterId,
                                                          List<TDlService> newTDlServiceList) {

        // 获取被关联的集群 ID
        Long relativeClusterId = this.masterClusterService
                .getClusterRelative(clusterId)
                .getData()
                .getClusterId();

        // 获取数据库中被关联集群中已存在的服务列表
        // <ServiceName, TDlService>
        Map<String, TDlService> mixedServiceMap = this.getServiceListByCluster(relativeClusterId);

        // 去重：计算集群中包含的服务，会从存储集群中去除
        newTDlServiceList.forEach(computeTDlService -> {
                    // 除 STORAGE 类型的服务必须独立存在于每个集群，因此不应依赖 MIXED 存储集群
                    ServiceTypeEnum computeServiceType = ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP
                            .get(computeTDlService.getServiceName())
                            .getType();

                    if (computeServiceType != ServiceTypeEnum.STORAGE) {
                        mixedServiceMap.remove(computeTDlService.getServiceName());
                    }
                }
        );

        List<TDlService> newTDlServiceListWithoutStorage = newTDlServiceList.stream()
                .peek(computeTDlService -> {
                    // 除 STORAGE 类型的服务必须独立存在于每个集群，因此不应依赖 MIXED 存储集群
                    ServiceTypeEnum computeServiceType = ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP
                            .get(computeTDlService.getServiceName())
                            .getType();

                    if (computeServiceType != ServiceTypeEnum.STORAGE) {
                        mixedServiceMap.remove(computeTDlService.getServiceName());
                    }
                })
                .filter(computeTDlService -> {
                    // 计算集群中的 STORAGE 类型的服务条目应从列表中移除后，再进行判断
                    // （该移除并非真正移除，而是不在判断过程中出现，计算集群服务列表中已然应该存在 STORAGE 服务，且必须为 UNSELECTED、REMOVED 或条目不存在）
                    ServiceTypeEnum computeServiceType = ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP
                            .get(computeTDlService.getServiceName())
                            .getType();

                    return (computeServiceType != ServiceTypeEnum.STORAGE);
                })
                .collect(Collectors.toList());

        // 存储集群和计算集群的服务列表合计
        List<TDlService> mixedComputeTDlServiceList = new ArrayList<>();

        mixedComputeTDlServiceList.addAll(newTDlServiceListWithoutStorage);
        mixedComputeTDlServiceList.addAll(mixedServiceMap.values());

        mixedComputeTDlServiceList.sort(Comparator.comparing(TDlService::getPriority));

        this.checkServiceDependencies(mixedComputeTDlServiceList);
    }

    /**
     * Description: 检查服务依赖项，非法时抛出异常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param newTDlServiceList 待检查的服务列表
     */
    private void checkServiceDependencies(List<TDlService> newTDlServiceList) {

        // <ServiceName, TDlService>
        Map<String, TDlService> tdlServiceMap = newTDlServiceList.stream()
                .collect(
                        Collectors.toMap(
                                TDlService::getServiceName,
                                i -> i
                        )
                );

        newTDlServiceList.stream()
                .filter(i -> i.getServiceState() != UNSELECTED && i.getServiceState() != REMOVED)
                .forEach(
                        tDlService -> ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP
                                .get(tDlService.getServiceName())
                                .getDependencies()
                                .forEach(dependencyServiceName -> {
                                            // 获取被依赖服务的当前状态
                                            SCStateEnum dependencyServiceState = tdlServiceMap
                                                    .get(dependencyServiceName)
                                                    .getServiceState();

                                            Assert.isTrue(
                                                    dependencyServiceState != UNSELECTED &&
                                                            dependencyServiceState != REMOVED,
                                                    () -> new BException(
                                                            String.format(
                                                                    "服务 %s 依赖服务 %s，请重新检查服务选项",
                                                                    tDlService.getServiceName(),
                                                                    dependencyServiceName
                                                            )
                                                    )
                                            );
                                        }
                                )
                );

    }

    /**
     * Description: 检查 计算集群 服务部署合法性，非法时抛出异常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param newTDlServiceList 准备入库的用户选择部署服务的结果列表
     */
    private void checkComputeClusterServiceLegality(List<TDlService> newTDlServiceList) {
        newTDlServiceList.stream()
                .filter(i -> i.getServiceState() != UNSELECTED && i.getServiceState() != REMOVED)
                .forEach(
                        tDlService -> Assert.isFalse(
                                ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP
                                        .get(tDlService.getServiceName())
                                        .getType() == ServiceTypeEnum.STORAGE,
                                () -> new BException(
                                        String.format(
                                                "计算集群不允许部署存储服务: %s",
                                                tDlService.getServiceName()
                                        )
                                )
                        )
                );
    }


    /**
     * Description: 根据给定值改变服务状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     * @param scStateEnum 服务变更为指定状态
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void switchServiceState(Long clusterId,
                                   String serviceName,
                                   SCStateEnum scStateEnum) {

        TDlService tDlService = tDlServiceService.lambdaQuery()
                .select()
                .eq(TDlService::getClusterId, clusterId)
                .eq(TDlService::getServiceName, serviceName)
                .ne(TDlService::getServiceState, REMOVED)
                .one();

        tDlService.setServiceState(scStateEnum);

        Assert.isTrue(
                tDlServiceService.updateById(tDlService),
                () -> new DatabaseException(
                        String.format(
                                "服务 %s 状态 %s 变更失败",
                                serviceName,
                                scStateEnum
                        )
                )
        );
    }

    /**
     * Description: 获取指定集群中、指定服务的当前状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     * @return 当前服务状态
     */
    public SCStateEnum getServiceState(Long clusterId, String serviceName) {
        TDlService tDlService = tDlServiceService.lambdaQuery()
                .select()
                .eq(TDlService::getClusterId, clusterId)
                .eq(TDlService::getServiceName, serviceName)
                .ne(TDlService::getServiceState, REMOVED)
                .one();

        if (tDlService == null) {
            tDlService = new TDlService();
            tDlService.setClusterId(clusterId);
            tDlService.setServiceName(serviceName);
            tDlService.setServiceState(UNSELECTED);
        }

        return tDlService.getServiceState();
    }


    /**
     * Description: 根据集群 ID，获取指定状态下的服务列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param scStateEnum 指定服务状态
     * @return 服务列表 Po
     */
    public List<TDlService> getTDlServiceListByState(Long clusterId, List<SCStateEnum> scStateEnum) {
        return tDlServiceService.lambdaQuery()
                .select()
                .eq(TDlService::getClusterId, clusterId)
                .in(TDlService::getServiceState, scStateEnum)
                .list();
    }

    /**
     * Description: 根据集群 ID 获取服务列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return 服务列表 Po
     */
    public List<TDlService> getTDlServiceList(Long clusterId) {
        return this.tDlServiceService.lambdaQuery()
                .select()
                .eq(TDlService::getClusterId, clusterId)
                .list();
    }

    /**
     * Description: 返回服务实体列表，通过 isPriorityAsc 调整执行顺序（true 为正序，false 为倒序）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId       集群 ID
     * @param serviceNameList 服务名称列表
     * @param isPriorityAsc   是否按照优先级升序排序，true 为升序，false 为降序
     * @return 服务在数据库中记录的实体
     */
    public List<TDlService> getTDlServiceListSorted(Long clusterId,
                                                    List<String> serviceNameList,
                                                    boolean isPriorityAsc) {
        LambdaQueryChainWrapper<TDlService> in = this.tDlServiceService.lambdaQuery()
                .select()
                .eq(TDlService::getClusterId, clusterId)
                .in(TDlService::getServiceName, serviceNameList);

        // 根据优先级升序或降序排序服务列表
        return isPriorityAsc ?
                in.orderByAsc(TDlService::getPriority).list() :
                in.orderByDesc(TDlService::getPriority).list();
    }


    /**
     * Description: 移除指定服务。注：移除服务操作不需要前端主动发起，当用户移除某服务下所有组件时，该服务将自动被移除
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void removeServiceByName(Long clusterId, String serviceName) {

        // 查询指定服务信息
        TDlService tDlService = this.tDlServiceService.lambdaQuery()
                .select()
                .eq(TDlService::getClusterId, clusterId)
                .eq(TDlService::getServiceName, serviceName)
                .ne(TDlService::getServiceState, REMOVED)
                .one();

        Assert.notNull(
                tDlService,
                () -> new BException("指定集群中无对应服务")
        );

        // 所有操作将会彻底删除数据，审计功能中将会保留操作数据历史
        Assert.isTrue(
                this.tDlServiceService.removeById(tDlService.getId()),
                () -> new DatabaseException("同步移除服务失败")
        );

    }


    /**
     * Description: 检查是否存在异常状态的 Service，若存在，则恢复
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void checkServiceState() {

    }
}
