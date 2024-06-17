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
import cn.boundivore.dl.base.enumeration.impl.ClusterStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.boot.lock.LocalLock;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlCluster;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.orm.po.single.TDlService;
import cn.boundivore.dl.orm.service.single.impl.TDlClusterServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlComponentServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlNodeServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlServiceServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 重置集群、节点、服务、组件等状态相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterResetService {

    private final TDlClusterServiceImpl tDlClusterService;

    private final TDlNodeServiceImpl tDlNodeService;

    private final TDlServiceServiceImpl tDlServiceService;

    private final TDlComponentServiceImpl tDlComponentService;


    /**
     * Description: 检查是否存在异常状态的 Cluster，若存在，则恢复
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
    @LocalLock
    public void checkClusterState() {
        // 获取集群列表
        List<TDlCluster> tDlClusterList = this.tDlClusterService.list();

        // 重置集群状态并过滤需要更新的集群
        List<TDlCluster> resetTDlClusterList = tDlClusterList.stream()
                .filter(tDlCluster -> {
                    ClusterStateEnum currentClusterState = tDlCluster.getClusterState();
                    ClusterStateEnum resetClusterState = currentClusterState.resetClusterState();
                    if (currentClusterState != resetClusterState) {
                        tDlCluster.setClusterState(resetClusterState);
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // 如果没有更新，直接返回
        if (CollUtil.isEmpty(resetTDlClusterList)) {
            return;
        }

        Assert.isTrue(
                this.tDlClusterService.updateBatchById(resetTDlClusterList),
                () -> new DatabaseException("数据库重置集群状态失败")
        );
    }

    /**
     * Description: 检查是否存在异常状态的 Node，若存在，则恢复
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
    @LocalLock
    public void checkNodeState() {
        // 获取节点列表
        List<TDlNode> tDlNodeList = this.tDlNodeService.list();

        // 重置节点状态并过滤需要更新的节点
        List<TDlNode> resetTDlNodeList = tDlNodeList.stream()
                .filter(tDlNode -> {
                    NodeStateEnum currentNodeState = tDlNode.getNodeState();
                    NodeStateEnum resetNodeState = currentNodeState.resetNodeState();
                    if (currentNodeState != resetNodeState) {
                        tDlNode.setNodeState(resetNodeState);
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // 如果没有更新，直接返回
        if (CollUtil.isEmpty(resetTDlNodeList)) {
            return;
        }

        Assert.isTrue(
                this.tDlNodeService.updateBatchById(resetTDlNodeList),
                () -> new DatabaseException("数据库重置节点状态失败")
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
    @LocalLock
    public void checkServiceState() {
        // 获取服务列表
        List<TDlService> tDlServiceList = this.tDlServiceService.list();

        // 重置服务状态并过滤需要更新的服务
        List<TDlService> resetTDlServiceList = tDlServiceList.stream()
                .filter(tDlService -> {
                    SCStateEnum currentState = tDlService.getServiceState();
                    SCStateEnum resetState = currentState.resetServiceState();
                    if (currentState != resetState) {
                        tDlService.setServiceState(resetState);
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // 如果没有更新，直接返回
        if (CollUtil.isEmpty(resetTDlServiceList)) {
            return;
        }

        Assert.isTrue(
                this.tDlServiceService.updateBatchById(resetTDlServiceList),
                () -> new DatabaseException("数据库重置服务状态失败")
        );
    }

    /**
     * Description: 检查是否存在异常状态的 Component，若存在，则恢复
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
    @LocalLock
    public void checkComponentState() {
        // 获取组件列表
        List<TDlComponent> tDlComponentList = this.tDlComponentService.list();

        // 重置服务状态并过滤需要更新的服务
        List<TDlComponent> resetTDlComponentList = tDlComponentList.stream()
                .filter(tDlComponent -> {
                    SCStateEnum currentState = tDlComponent.getComponentState();
                    SCStateEnum resetState = currentState.resetComponentState();
                    if (currentState != resetState) {
                        tDlComponent.setComponentState(resetState);
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // 如果没有更新，直接返回
        if (CollUtil.isEmpty(resetTDlComponentList)) {
            return;
        }

        Assert.isTrue(
                this.tDlComponentService.updateBatchById(resetTDlComponentList),
                () -> new DatabaseException("数据库重置组件状态失败")
        );
    }
}
