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

import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.response.impl.master.AbstractComponentPlacementVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceManifest;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceDetail;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 自动推荐组件部署时分布情况
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/5/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterComponentPlacementAdvisorService {

    private final MasterClusterService masterClusterService;

    private final MasterNodeService masterNodeService;


    /**
     * Description: 获取组件分布推荐
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId    集群 ID
     * @param serviceNames 多个服务名称（英文逗号拼接）
     * @return Result<AbstractComponentPlacementVo.PlacementAdvisorVo> 组件分布推荐信息
     */
    public Result<AbstractComponentPlacementVo.PlacementAdvisorVo> getComponentPlacementRecommendation(Long clusterId,
                                                                                                       String serviceNames) {

        // 检查集群 ID 是否存在
        Assert.notNull(
                this.masterClusterService.getClusterById(clusterId).getData().getClusterId(),
                () -> new BException("集群 ID 不存在")
        );

        // 对传递进来的服务列表按照优先级进行排序
        List<String> serviceNameSortedList = Arrays.stream(serviceNames.split(","))
                .filter(StrUtil::isNotBlank)
                .sorted((o1, o2) -> {
                    long o1Priority = ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP.get(o1).getPriority();
                    long o2Priority = ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP.get(o2).getPriority();
                    return Long.compare(o1Priority, o2Priority);
                })
                .collect(Collectors.toList());

        // 组装推荐的 Service 信息
        List<AbstractComponentPlacementVo.ServicePlacementVo> servicePlacementVoList = serviceNameSortedList.stream()
                .map(serviceName -> {

                    AbstractComponentPlacementVo.ServicePlacementVo servicePlacementVo = new AbstractComponentPlacementVo.ServicePlacementVo();
                    servicePlacementVo.setServiceName(serviceName);

                    // 组装推荐的 Component 信息
                    servicePlacementVo.setComponentPlacementList(
                            this.genComponentPlacementList(clusterId, serviceName)
                    );

                    return servicePlacementVo;
                })
                .collect(Collectors.toList());


        AbstractComponentPlacementVo.PlacementAdvisorVo placementAdvisorVo = new AbstractComponentPlacementVo.PlacementAdvisorVo(
                clusterId,
                servicePlacementVoList
        );

        return Result.success(placementAdvisorVo);
    }

    /**
     * Description: 生成推荐的组件分布列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     * @return AbstractComponentPlacementVo.ComponentPlacementVo 推荐的组件信息
     */
    private List<AbstractComponentPlacementVo.ComponentPlacementVo> genComponentPlacementList(Long clusterId,
                                                                                              String serviceName) {

        // 获取当前服务组件静态配置信息
        YamlServiceDetail.Service service = ResolverYamlServiceDetail.SERVICE_MAP.get(serviceName);

        // 获取可用节点信息（已按照 hostname 正序排序）
        List<AbstractNodeVo.NodeDetailVo> nodeDetailList = this.masterNodeService.getNodeList(clusterId)
                .getData()
                .getNodeDetailList()
                .stream()
                .filter(i -> i.getNodeState() == NodeStateEnum.STARTED)
                .collect(Collectors.toList());

        // 返回组件推荐分布列表
        return service.getComponents()
                .stream()
                .flatMap(componentFinal -> {
                    List<AbstractComponentPlacementVo.ComponentPlacementVo> list = new ArrayList<>();

                    long maxPlacement = componentFinal.getMax() == -1L ? Long.MAX_VALUE : componentFinal.getMax();
                    // 暂时不做最小部署推荐
                    long minPlacement = componentFinal.getMin() == -1L ? 0L : componentFinal.getMin();

                    for (int count = 1; count <= maxPlacement && count <= nodeDetailList.size(); count++) {
                        AbstractNodeVo.NodeDetailVo nodeDetail = nodeDetailList.get(count - 1);
                        list.add(
                                new AbstractComponentPlacementVo.ComponentPlacementVo(
                                        componentFinal.getName(),
                                        SCStateEnum.SELECTED,
                                        nodeDetail.getNodeId(),
                                        nodeDetail.getNodeState(),
                                        nodeDetail.getNodeIp(),
                                        nodeDetail.getHostname()
                                )
                        );
                    }

                    return list.stream();
                })
                .collect(Collectors.toList());
    }

}
