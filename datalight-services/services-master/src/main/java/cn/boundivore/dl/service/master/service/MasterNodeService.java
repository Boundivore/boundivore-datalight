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
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.request.impl.master.NodeInfoRequest;
import cn.boundivore.dl.base.request.impl.master.NodeJobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.orm.service.single.impl.TDlNodeServiceImpl;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: 节点操作相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/21
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterNodeService {

    private final TDlNodeServiceImpl tDlNodeService;

    private final MasterNodeJobService masterNodeJobService;

    /**
     * Description: 对已服役的节点进行异步操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/30
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 当前即将对节点进行的操作请求
     * @return Result<String>
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<AbstractNodeJobVo.NodeJobIdVo> operateNode(NodeJobRequest request) throws Exception {
        Long jobId = -1L;
        if (request.getNodeActionTypeEnum() == NodeActionTypeEnum.START) {
            jobId = this.operateNode2Started(request);
        } else {
            jobId = this.masterNodeJobService.initNodeJob(request, true);
        }

        return Result.success(
                new AbstractNodeJobVo.NodeJobIdVo(
                        request.getClusterId(),
                        jobId
                )
        );
    }

    /**
     * Description: 手动标记某个节点为已启动节点，该操作不会产生异步任务，而是直接标记对应节点为已启动
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 节点操作请求体
     * @return 返回固定值为 -1 的 NodeJobId，因为此操作不会产生异步任务
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Long operateNode2Started(NodeJobRequest request) {
        // 检查节点是否均存在
        Map<Long, TDlNode> tDlNodeMap = this.checkNodeExistsById(
                request.getNodeInfoList()
                        .stream()
                        .map(NodeInfoRequest::getNodeId)
                        .collect(Collectors.toList())
        );

        List<TDlNode> newTDlNodeList = tDlNodeMap.values()
                .stream()
                .peek(i -> {
                            Assert.isTrue(
                                    i.getNodeState() == NodeStateEnum.STOPPING ||
                                            i.getNodeState() == NodeStateEnum.STOPPED ||
                                            i.getNodeState() == NodeStateEnum.STARTING ||
                                            i.getNodeState() == NodeStateEnum.RESTARTING,
                                    () -> new BException(
                                            String.format(
                                                    "节点当前状态 %s 不能被标记为 %s",
                                                    i.getNodeState(),
                                                    NodeStateEnum.STARTED
                                            )
                                    ));

                            i.setNodeState(NodeStateEnum.STARTED);
                        }
                )
                .collect(Collectors.toList());

        Assert.isTrue(
                this.tDlNodeService.updateBatchById(newTDlNodeList),
                () -> new DatabaseException("变更节点状态失败")
        );

        // 标记节点为 STARTED 不会产生异步任务，所以 NodeJobId 为 -1;
        return -1L;
    }

    /**
     * Description: 根据 NodeId 列表，组装 NodeId 到 TDlNode 的映射
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeIdList 待查 NodeId 列表
     * @return 返回 <NodeId, TDlNode>
     */
    public Map<Long, TDlNode> getNodeMap(List<Long> nodeIdList) {

        // 获取 NodeId 列表中的所有 TdlNode 实体
        List<TDlNode> tDlNodeList = tDlNodeService.listByIds(nodeIdList);

        //<NodeId, TdlNode>
        final Map<Long, TDlNode> nodeMap = new HashMap<>();

        tDlNodeList.forEach(i -> nodeMap.put(i.getId(), i));

        return nodeMap;
    }

    /**
     * Description: 切换服役过程中的节点的状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId     集群 ID
     * @param nodeId        节点 ID
     * @param nodeStateEnum 初始化过程中的节点切换为指定状态
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void switchNodeState(Long clusterId,
                                Long nodeId,
                                NodeStateEnum nodeStateEnum) {
        TDlNode tDlNode = tDlNodeService.getById(nodeId);
        tDlNode.setNodeState(nodeStateEnum);

        Assert.isTrue(
                tDlNodeService.updateById(tDlNode),
                () -> new BException("更新服役过程中的节点状态失败")
        );

    }

    /**
     * Description: 获取节点列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群ID
     * @return 节点列表结果
     */
    public Result<AbstractNodeVo.NodeVo> getNodeList(Long clusterId) {
        List<AbstractNodeVo.NodeDetailVo> nodeDetailList = tDlNodeService.lambdaQuery()
                .select()
                .eq(TDlNode::getClusterId, clusterId)
                .notIn(TDlNode::getNodeState, NodeStateEnum.REMOVED)
                .orderByAsc(TDlNode::getHostname)
                .list()
                .stream()
                .map(i -> new AbstractNodeVo.NodeDetailVo()
                        .setNodeId(i.getId())
                        .setHostname(i.getHostname())
                        .setNodeIp(i.getIpv4())
                        .setSshPort(i.getSshPort())
                        .setCpuArch(i.getCpuArch())
                        .setCpuCores(i.getCpuCores())
                        .setRam(i.getRam())
                        .setDiskTotal(i.getDisk())
                        .setNodeState(i.getNodeState())
                )
                .collect(Collectors.toList());

        return Result.success(
                new AbstractNodeVo.NodeVo(
                        clusterId,
                        nodeDetailList
                )
        );
    }

    /**
     * Description: 获取节点列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeStateEnumList 节点状态
     * @return List<TDlNode>
     */
    public List<TDlNode> getNodeListByState(List<NodeStateEnum> nodeStateEnumList) {
        return tDlNodeService.lambdaQuery()
                .select()
                .in(TDlNode::getNodeState, nodeStateEnumList)
                .orderByAsc(TDlNode::getHostname)
                .list();
    }

    /**
     * Description: 获取节点列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param ipList IP 列表
     * @return List<TDlNode>
     */
    public List<TDlNode> getNodeListByIpList(List<String> ipList) {
        return tDlNodeService.lambdaQuery()
                .select()
                .in(TDlNode::getIpv4, ipList)
                .orderByAsc(TDlNode::getHostname)
                .list();
    }

    /**
     * Description: 批量更新状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlNodeList 待更新列表
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void updateBatchById(List<TDlNode> tDlNodeList) {
        Assert.isTrue(
                this.tDlNodeService.updateBatchById(tDlNodeList),
                () -> new DatabaseException("节点状态更新失败")
        );
    }

    /**
     * Description: 根据主机名列表获取主机信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId    集群 ID
     * @param hostnameList 主机名列表
     * @return List<TDlNodeInit> 节点列表
     */
    public List<TDlNode> getNodeListInHostnames(Long clusterId, List<String> hostnameList) {
        return this.tDlNodeService.lambdaQuery()
                .select()
                .eq(TDlNode::getClusterId, clusterId)
                .in(TDlNode::getHostname, hostnameList)
                .list();
    }

    /**
     * Description: 根据主机 ID 列表获取主机信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId  集群 ID
     * @param nodeIdList 主机 ID 列表
     * @return List<TDlNodeInit> 节点列表
     */
    public List<TDlNode> getNodeListInNodeIds(Long clusterId, List<Long> nodeIdList) {
        return this.tDlNodeService.lambdaQuery()
                .select()
                .eq(TDlNode::getClusterId, clusterId)
                .in(TDlNode::getId, nodeIdList)
                .list();
    }

    /**
     * Description: 检查节点 ID 列表中对应的节点是否全部存在且节点全部可用
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/17
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeIdList 节点 ID 列表
     * @return <节点 ID, TDlNode>
     */
    public Map<Long, TDlNode> checkNodeExistsById(List<Long> nodeIdList) {

        Assert.notEmpty(
                nodeIdList,
                () -> new BException("存在空的节点信息")
        );

        // <Long, TDlNode>
        Map<Long, TDlNode> tDlNodeMap = this.tDlNodeService.lambdaQuery()
                .select()
                .ne(TDlNode::getNodeState, NodeStateEnum.REMOVED)
                .in(TBasePo::getId, nodeIdList)
                .list()
                .stream()
                .collect(Collectors.toMap(TBasePo::getId, i -> i));

        if (tDlNodeMap.size() != nodeIdList.size()) {
            nodeIdList.forEach(i ->
                    Assert.notNull(
                            tDlNodeMap.get(i),
                            () -> new BException(
                                    String.format(
                                            "无效的节点 ID: %s",
                                            i
                                    )
                            )
                    )
            );
        }

        return tDlNodeMap;
    }

}
