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

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ProcedureStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.master.JobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractJobVo;
import cn.boundivore.dl.base.response.impl.master.ConfigListByGroupVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.service.single.impl.TDlComponentServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.boundivore.dl.base.enumeration.impl.SCStateEnum.*;

/**
 * Description: 即将执行迁移部署
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/17
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class MasterMigrateHDFSService {

    private final MasterJobService masterJobService;

    private final MasterServiceService masterServiceService;

    private final TDlComponentServiceImpl tDlComponentService;

    private final MasterConfigService masterConfigService;

    private final MasterInitProcedureService masterInitProcedureService;

    /**
     * Description: 开始生成迁移部署计划，并执行计划
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/17
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param request 将要迁移部署的目标集群和将要迁移部署的服务，必须有且仅有 HDFS
     * @return Result<AbstractJobVo.JobIdVo> 集群 ID & JobID
     */
    public Result<AbstractJobVo.JobIdVo> migrate(JobRequest request) throws Exception {
        //检查当前操作意图是否正确
        ActionTypeEnum actionTypeEnum = request.getActionTypeEnum();
        Assert.isTrue(
                actionTypeEnum == ActionTypeEnum.MIGRATE,
                () -> new IllegalArgumentException(String.format("错误的意图: %s", actionTypeEnum))
        );

        // 检查迁移部署意图的合理性
        this.checkMigrateNameNodeLegality(
                request.getClusterId(),
                request.getServiceNameList()
        );


        Long jobId = this.masterJobService.initJob(
                request,
                this.masterJobService.isPriorityAsc(request.getActionTypeEnum())
        );

        // 记录部署 Procedure
        this.masterInitProcedureService.persistServiceComponentProcedure(
                request.getClusterId(),
                jobId,
                ProcedureStateEnum.PROCEDURE_MIGRATE_DEPLOYING
        );

        return Result.success(
                new AbstractJobVo.JobIdVo(
                        request.getClusterId(),
                        jobId
                )
        );
    }

    /**
     * Description: 检查部署的合理性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/17
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId       集群 ID
     * @param serviceNameList 服务部署列表
     */
    private void checkMigrateNameNodeLegality(Long clusterId,
                                              List<String> serviceNameList) {

        Assert.isTrue(
                serviceNameList.size() == 1,
                () -> new BException("迁移部署每次技能操作一个服务")
        );

        Assert.isTrue(
                serviceNameList.contains("HDFS"),
                () -> new BException("迁移部署仅针对 HDFS 有效")
        );


        SCStateEnum hdfsServiceState = this.masterServiceService.getServiceState(
                clusterId,
                "HDFS"
        );

        Assert.isTrue(
                hdfsServiceState == SCStateEnum.SELECTED
                        || hdfsServiceState == SCStateEnum.SELECTED_ADDITION
                        || hdfsServiceState == SCStateEnum.CHANGING,
                () -> new BException(
                        String.format(
                                "迁移部署操作必须选择处于 %s、%s、%s状态的服务",
                                SCStateEnum.SELECTED,
                                SCStateEnum.SELECTED_ADDITION,
                                SCStateEnum.CHANGING
                        )
                )
        );
    }

    /**
     * Description: 返回刚迁移后的 NameNode 中 hdfs-site.xml 的最新内容，并将最新内容覆盖之前的 hdfs-site.xml 的配置文件内容，
     * 最终按照 SHA256 分组返回配置文件信息，即，按照相同服务、相同组件、相同 SHA256 进行分组，以降低重复内容，提升性能。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Result<ConfigListByGroupVo> 当前指定服务的配置文件，会按照相同的内容聚合节点
     */
    public Result<ConfigListByGroupVo> getNewHdfsSiteConfigListByGroup(Long clusterId) {

        String hdfsServiceName = "HDFS";

        // 通过数据库变更的时间戳来判断最近一次变更的是哪个节点上的 NameNode 组件
        List<TDlComponent> tDlComponentList = this.tDlComponentService.lambdaQuery()
                .select()
                .eq(TDlComponent::getClusterId, clusterId)
                .eq(TDlComponent::getServiceName, hdfsServiceName)
                .notIn(TDlComponent::getComponentState, UNSELECTED, REMOVED, SELECTED)
                .like(TDlComponent::getComponentName, "%" + "NameNode" + "%")
                .orderByDesc(TBasePo::getUpdateTime)
                .list();

        Assert.isTrue(
                tDlComponentList.size() == 2,
                () -> new BException("未找到有效数量的 NameNode 实例")
        );

        // 时间戳最大的 NameNode 为最新的刚迁移的 NameNode
        TDlComponent maxUpdateTimeNameNode = CollUtil.getFirst(tDlComponentList);


        // 获取 hdfs-site.xml 的配置文件路径
        String configPath = this.masterConfigService.getConfigPathList(clusterId, hdfsServiceName)
                .stream()
                .filter(path -> path.contains("hdfs-site.xml"))
                .findFirst()
                .orElseThrow(() -> new BException("hdfs-site.xml 配置文件路径未找到"));


        Result<ConfigListByGroupVo> configListByGroup = this.masterConfigService.getConfigListByGroup(
                clusterId,
                hdfsServiceName,
                configPath
        );


        ConfigListByGroupVo configListByGroupVo = configListByGroup.getData();
        List<ConfigListByGroupVo.ConfigGroupVo> configGroupList = configListByGroupVo.getConfigGroupList();

        ConfigListByGroupVo.ConfigGroupVo targetGroupVo = null;
        List<ConfigListByGroupVo.ConfigGroupVo> groupsToRemove = new ArrayList<>();
        List<ConfigListByGroupVo.ConfigNodeVo> nodesToAdd = new ArrayList<>();

        // 确保 ConfigGroupList 中包含 nodeId 为最新迁移 NameNode 所在的节点 ID
        Long nodeId = maxUpdateTimeNameNode.getNodeId();

        boolean containsNodeId = false;
        for (ConfigListByGroupVo.ConfigGroupVo groupVo : configGroupList) {

            if (!containsNodeId) {
                for (ConfigListByGroupVo.ConfigNodeVo nodeVo : groupVo.getConfigNodeList()) {
                    if (nodeVo.getNodeId().equals(nodeId)) {
                        containsNodeId = true;

                        // 找到目标 NodeId 的 ConfigGroupVo 实例
                        targetGroupVo = groupVo;
                        break;
                    }
                }
            }

            if (!containsNodeId) {
                // 将当前组的节点加入到待添加列表中，并记录当前组为待删除
                nodesToAdd.addAll(groupVo.getConfigNodeList());
                groupsToRemove.add(groupVo);
            }
        }

        if (targetGroupVo != null) {
            // 删除不包含目标 NodeId 的 ConfigGroupVo 实例
            configGroupList.removeAll(groupsToRemove);

            // 将被删除的那些节点加入到包含 nodeId 为 1 的 ConfigGroupVo 的 configNodeList 中
            targetGroupVo.getConfigNodeList().addAll(nodesToAdd);
        }

        return configListByGroup;

    }
}
