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

import cn.boundivore.dl.base.enumeration.impl.ClusterStateEnum;
import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractClusterRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractClusterVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlCluster;
import cn.boundivore.dl.orm.service.single.impl.TDlClusterServiceImpl;
import cn.boundivore.dl.service.master.converter.IClusterConverter;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceManifest;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 集群操作相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterClusterService {

    private final TDlClusterServiceImpl tDlClusterService;

    private final IClusterConverter iClusterConverter;

    private final MasterInitProcedureService masterInitProcedureService;

    private final MasterNodeService masterNodeService;

    /**
     * Description: 新建集群
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 新建集群请求体
     * @return Result<AbstractClusterVo.ClusterVo> 创建后的集群信息
     */
    public Result<AbstractClusterVo.ClusterVo> clusterNew(AbstractClusterRequest.NewClusterRequest request) {

        // 检查 DLC 合法性
        String dlcVersion = request.getDlcVersion();
        String dlcVersionInYaml = ResolverYamlServiceManifest.SERVICE_MANIFEST_YAML
                .getDataLight()
                .getDlcVersion();

        Assert.isTrue(
                dlcVersion.equals(dlcVersionInYaml),
                () -> new BException("无法匹配对应的 DlcVersion")
        );

        // TODO 使用正则判断集群名称的合法性
        // 检查集群名称是否重复
        String clusterName = request.getClusterName();
        Assert.isFalse(
                this.tDlClusterService.lambdaQuery()
                        .select()
                        .eq(TDlCluster::getClusterName, clusterName)
                        .exists(),
                () -> new BException("集群名称已存在，请重新编辑集群名称")
        );

        // 检查如果为存储集群，则不允许关联其他集群
        ClusterTypeEnum clusterTypeEnum = request.getClusterTypeEnum();
        Long relativeClusterId = request.getRelativeClusterId();

        Assert.isFalse(
                relativeClusterId != null && clusterTypeEnum == ClusterTypeEnum.MIXED,
                () -> new BException("存储集群不允许关联其他集群")
        );

        // 如果为计算集群，则判断被关联的集群是否存在，且为存储集群，且集群处于正常工作状态
        if (clusterTypeEnum == ClusterTypeEnum.COMPUTE) {
            TDlCluster relativeTDlCluster = this.tDlClusterService.lambdaQuery()
                    .select()
                    .eq(TBasePo::getId, relativeClusterId)
                    .one();

            // 被关联集群是否存在
            Assert.notNull(
                    relativeTDlCluster,
                    () -> new BException("计算集群未匹配到将要关联的存储集群")
            );

            // 被关联集群是否为存储集群
            Assert.isTrue(
                    relativeTDlCluster.getClusterType() == ClusterTypeEnum.MIXED,
                    () -> new BException("计算集群只能关联存储集群")
            );

            // 被关联集群是否处于正常工作状态
            Assert.isTrue(
                    relativeTDlCluster.getClusterState() != ClusterStateEnum.REMOVED,
                    () -> new BException("将要关联的集群已被移除")
            );
        }

        // 新增集群
        TDlCluster tDlCluster = iClusterConverter.convert2TDlCluster(request);
        tDlCluster.setVersion(0L);
        tDlCluster.setClusterState(ClusterStateEnum.MAINTENANCE);

        Assert.isTrue(
                this.tDlClusterService.save(tDlCluster),
                () -> new DatabaseException("新增集群失败")
        );

        return Result.success(
                this.iClusterConverter.convert2ClusterVo(tDlCluster)
        );
    }

    /**
     * Description: 通过集群 ID 查询集群信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Result<AbstractClusterVo.ClusterVo> 集群信息
     */
    public Result<AbstractClusterVo.ClusterVo> getClusterById(Long clusterId) {
        TDlCluster tDlCluster = this.tDlClusterService.getById(clusterId);

        Assert.notNull(
                tDlCluster,
                () -> new DatabaseException("未找到对应集群信息")
        );

        return Result.success(
                this.iClusterConverter.convert2ClusterVo(tDlCluster)
        );
    }

    /**
     * Description: 查询指定类型的集群信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterTypeEnum 集群类型
     * @return Result<AbstractClusterVo.ClusterListVo> 集群信息列表
     */
    public Result<AbstractClusterVo.ClusterListVo> getClusterListByClusterType(ClusterTypeEnum clusterTypeEnum) {
        return Result.success(
                new AbstractClusterVo.ClusterListVo(
                        this.tDlClusterService.lambdaQuery()
                                .select()
                                .eq(TDlCluster::getClusterType, clusterTypeEnum)
                                .ne(TDlCluster::getClusterState, ClusterStateEnum.REMOVED)
                                .list()
                                .stream()
                                .map(iClusterConverter::convert2ClusterVo)
                                .collect(Collectors.toList())
                )
        );
    }

    /**
     * Description: 获取所有集群信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<AbstractClusterVo.ClusterListVo> 所有集群信息列表
     */
    public Result<AbstractClusterVo.ClusterListVo> getClusterList() {

        Set<Long> clusterIdListWithInProcedure = this.masterInitProcedureService.getClusterIdListWithInProcedure();
        Set<Long> clusterIdListWithInNode = this.masterNodeService.getClusterIdListWithInNode();

        return Result.success(
                new AbstractClusterVo.ClusterListVo(
                        this.tDlClusterService.lambdaQuery()
                                .select()
                                .ne(TDlCluster::getClusterState, ClusterStateEnum.REMOVED)
                                .list()
                                .stream()
                                .map(iClusterConverter::convert2ClusterVo)
                                .peek(i -> {
                                    i.setIsExistInitProcedure(clusterIdListWithInProcedure.contains(i.getClusterId()));
                                    i.setHasAlreadyNode(clusterIdListWithInNode.contains(i.getClusterId()));
                                })
                                .collect(Collectors.toList())
                )
        );
    }

    /**
     * Description: 查询依赖了指定集群的计算集群信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 被依赖集群 ID
     * @return Result<AbstractClusterVo.ClusterListVo> 集群信息列表
     */
    public Result<AbstractClusterVo.ClusterListVo> getComputeClusterListByRelativeClusterId(Long clusterId) {
        return Result.success(
                new AbstractClusterVo.ClusterListVo(
                        this.tDlClusterService.lambdaQuery()
                                .select()
                                .eq(TDlCluster::getRelativeClusterId, clusterId)
                                .ne(TDlCluster::getClusterState, ClusterStateEnum.REMOVED)
                                .list()
                                .stream()
                                .map(this.iClusterConverter::convert2ClusterVo)
                                .collect(Collectors.toList())
                )
        );
    }

    /**
     * Description: 查询当前集群依赖的集群信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 当前集群 ID
     * @return Result<AbstractClusterVo.ClusterVo> 被依赖的集群信息
     */
    public Result<AbstractClusterVo.ClusterVo> getClusterRelative(Long clusterId) {
        TDlCluster currentTDlCluster = this.tDlClusterService.lambdaQuery()
                .select()
                .eq(TBasePo::getId, clusterId)
                .ne(TDlCluster::getClusterState, ClusterStateEnum.REMOVED)
                .one();

        // 集群信息必须存在
        Assert.notNull(
                currentTDlCluster,
                () -> new BException(
                        String.format(
                                "无效的集群 ID: %s",
                                clusterId
                        )
                )
        );

        // 必须为计算集群
        Assert.isTrue(
                currentTDlCluster.getClusterType() == ClusterTypeEnum.COMPUTE,
                () -> new BException(
                        String.format(
                                "该集群非计算集群 ID: %s",
                                clusterId
                        )
                )
        );

        // 必须已关联存储集群
        Assert.notNull(
                currentTDlCluster.getRelativeClusterId(),
                () -> new BException(
                        String.format(
                                "该集群无关联任何集群 ID: %s",
                                clusterId
                        )
                )
        );

        // 读取被关联的集群
        TDlCluster mixedTDlCluster = this.tDlClusterService.lambdaQuery()
                .select()
                .eq(TBasePo::getId, currentTDlCluster.getRelativeClusterId())
                .eq(TDlCluster::getClusterType, ClusterTypeEnum.MIXED)
                .ne(TDlCluster::getClusterState, ClusterStateEnum.REMOVED)
                .one();

        // 被关联的集群信息必须存在
        Assert.notNull(
                mixedTDlCluster,
                () -> new BException(
                        String.format(
                                "无效的被关联集群 ID: %s",
                                currentTDlCluster.getRelativeClusterId()
                        )
                )
        );

        return Result.success(
                this.iClusterConverter.convert2ClusterVo(mixedTDlCluster)
        );
    }
}
