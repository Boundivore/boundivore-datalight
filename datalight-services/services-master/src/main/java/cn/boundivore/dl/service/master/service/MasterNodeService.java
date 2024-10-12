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
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractNodeRequest;
import cn.boundivore.dl.base.request.impl.master.NodeJobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.utils.ReactiveAddressUtil;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.orm.service.single.impl.TDlComponentServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlNodeServiceImpl;
import cn.boundivore.dl.service.master.env.DataLightEnv;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    private final MasterInitProcedureService masterInitProcedureService;

    private final TDlNodeServiceImpl tDlNodeService;

    private final TDlComponentServiceImpl tDlComponentService;

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
    public Result<AbstractNodeJobVo.NodeJobIdVo> operateNode(NodeJobRequest request) throws Exception {
        Long jobId = -1L;

        switch (request.getNodeActionTypeEnum()) {
            case START:
                jobId = this.operateNode2Started(request);
                break;
            case SHUTDOWN:
            case RESTART:
                // 判断，如果当前操作仅包含 Master 所在节点，则抛出异常提示
                long withoutMasterNodeCount = request.getNodeInfoList()
                        .stream()
                        .filter(i -> !i.getHostname().equals(DataLightEnv.MASTER_HOSTNAME))
                        .count();

                Assert.isTrue(
                        withoutMasterNodeCount > 0,
                        () -> new BException("Master 所在节点不支持关机与重启操作")
                );

                jobId = this.masterNodeJobService.initNodeJob(
                        request,
                        DataLightEnv.MASTER_HOSTNAME,
                        true
                );
                break;
            default:
                jobId = this.masterNodeJobService.initNodeJob(
                        request,
                        true
                );

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
    public Long operateNode2Started(NodeJobRequest request) {
        // 检查节点是否均存在
        Map<Long, TDlNode> tDlNodeMap = this.checkNodeExistsById(
                request.getNodeInfoList()
                        .stream()
                        .map(AbstractNodeRequest.NodeInfoRequest::getNodeId)
                        .collect(Collectors.toList())
        );

        List<TDlNode> newTDlNodeList = tDlNodeMap.values()
                .stream()
                .peek(i -> {
//                            Assert.isTrue(
//                                    i.getNodeState() == NodeStateEnum.STOPPING ||
//                                            i.getNodeState() == NodeStateEnum.STOPPED ||
//                                            i.getNodeState() == NodeStateEnum.STARTING ||
//                                            i.getNodeState() == NodeStateEnum.RESTARTING,
//                                    () -> new BException(
//                                            String.format(
//                                                    "节点当前状态 %s 不能被标记为 %s",
//                                                    i.getNodeState(),
//                                                    NodeStateEnum.STARTED
//                                            )
//                                    ));

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
     * Description: 获取节点详情
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeId 节点 ID
     * @return Result<AbstractNodeVo.NodeDetailVo> 节点详情
     */
    public Result<AbstractNodeVo.NodeDetailVo> getNodeDetailById(Long nodeId) {

        TDlNode tDlNode = this.tDlNodeService.lambdaQuery()
                .select()
                .eq(TBasePo::getId, nodeId)
                .ne(TDlNode::getNodeState, NodeStateEnum.REMOVED)
                .one();

        Assert.notNull(
                tDlNode,
                () -> new BException("不存在的节点 ID")
        );


        return Result.success(
                new AbstractNodeVo.NodeDetailVo()
                        .setNodeId(tDlNode.getId())
                        .setHostname(tDlNode.getHostname())
                        .setNodeIp(tDlNode.getIpv4())
                        .setSshPort(tDlNode.getSshPort())
                        .setCpuArch(tDlNode.getCpuArch())
                        .setCpuCores(tDlNode.getCpuCores())
                        .setRam(tDlNode.getRam())
                        .setDiskTotal(tDlNode.getDisk())
                        .setNodeState(tDlNode.getNodeState())
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
     * @param clusterId 集群 ID
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
     * Description: 获取节点列表附带其上的组件信息
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
    public Result<AbstractNodeVo.NodeWithComponentListVo> getNodeListWithComponent(Long clusterId) {

        // 优化数据库查询，使用单个查询以减少数据库访问次数
        Map<Long, List<String>> nodeIdComponentNameMap = this.getNodeComponents(clusterId);

        // 处理节点信息，并与组件信息结合
        List<AbstractNodeVo.NodeWithComponentVo> nodeWithComponentList = this.getNodes(clusterId, nodeIdComponentNameMap);

        // 构建并返回结果
        return Result.success(
                new AbstractNodeVo.NodeWithComponentListVo(
                        clusterId,
                        nodeWithComponentList
                )
        );
    }

    /**
     * Description: 优化数据库查询，使用单个查询以减少数据库访问次数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/26
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Map<Long, List < String>> 返回
     */
    private Map<Long, List<String>> getNodeComponents(Long clusterId) {
        // 优化：使用方法来封装组件查询逻辑
        return this.tDlComponentService.lambdaQuery()
                .select(TDlComponent::getNodeId, TDlComponent::getComponentName)
                .notIn(TDlComponent::getComponentState, SCStateEnum.REMOVED, SCStateEnum.UNSELECTED)
                .eq(TDlComponent::getClusterId, clusterId)
                .list()
                .stream()
                .collect(Collectors.groupingBy(
                        TDlComponent::getNodeId,
                        Collectors.mapping(TDlComponent::getComponentName, Collectors.toList())
                ));
    }


    /**
     * Description: 处理节点信息，并与组件信息结合
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/26
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId              集群 ID
     * @param nodeIdComponentNameMap 节点 ID 与组件名称列表的映射关系
     * @return List<AbstractNodeVo.NodeWithComponentVo> 节点信息与组件信息列表
     */
    private List<AbstractNodeVo.NodeWithComponentVo> getNodes(Long clusterId,
                                                              final Map<Long, List<String>> nodeIdComponentNameMap) {
        return this.tDlNodeService.lambdaQuery()
                .eq(TDlNode::getClusterId, clusterId)
                .notIn(TDlNode::getNodeState, NodeStateEnum.REMOVED)
                .orderByAsc(TDlNode::getHostname)
                .list()
                .stream()
                .map(node -> this.mapNodeToNodeWithComponentVo(node, nodeIdComponentNameMap))
                .collect(Collectors.toList());
    }

    /**
     * Description: 逐个组装 NodeWithComponentVo 信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/26
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlNode                数据库节点信息
     * @param nodeIdComponentNameMap 节点 ID 与组件名称映射
     * @return AbstractNodeVo.NodeWithComponentVo 节点与组件信息
     */
    private AbstractNodeVo.NodeWithComponentVo mapNodeToNodeWithComponentVo(TDlNode tDlNode,
                                                                            Map<Long, List<String>> nodeIdComponentNameMap) {

        // 抽象方法来构建 NodeWithComponentVo 对象
        AbstractNodeVo.NodeDetailVo nodeDetailVo = new AbstractNodeVo.NodeDetailVo()
                .setNodeId(tDlNode.getId())
                .setHostname(tDlNode.getHostname())
                .setNodeIp(tDlNode.getIpv4())
                .setSshPort(tDlNode.getSshPort())
                .setCpuArch(tDlNode.getCpuArch())
                .setCpuCores(tDlNode.getCpuCores())
                .setRam(tDlNode.getRam())
                .setDiskTotal(tDlNode.getDisk())
                .setNodeState(tDlNode.getNodeState());

        List<String> componentNames = nodeIdComponentNameMap.getOrDefault(
                tDlNode.getId(),
                Collections.emptyList()
        );

        return new AbstractNodeVo.NodeWithComponentVo(
                nodeDetailVo,
                componentNames
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
        return this.tDlNodeService.lambdaQuery()
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
        return this.tDlNodeService.lambdaQuery()
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
     * @param hostnameList 主机名列表
     * @return List<TDlNodeInit> 节点列表
     */
    public List<TDlNode> getNodeListInHostnames(List<String> hostnameList) {
        return this.tDlNodeService.lambdaQuery()
                .select()
                .in(TDlNode::getHostname, hostnameList)
                .ne(TDlNode::getNodeState, NodeStateEnum.REMOVED)
                .list();
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
     * @param hostname 主机名
     * @return List<TDlNodeInit> 节点列表
     */
    public TDlNode getNodeListByHostname(String hostname) {
        return this.tDlNodeService.lambdaQuery()
                .select()
                .eq(TDlNode::getHostname, hostname)
                .ne(TDlNode::getNodeState, NodeStateEnum.REMOVED)
                .one();
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

//        Assert.notEmpty(
//                nodeIdList,
//                () -> new BException("存在空的节点信息")
//        );

        if(CollUtil.isEmpty(nodeIdList)) return new HashMap<>();

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

    /**
     * Description: 获取当前集群可用的节点个数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return int 可用节点个数
     */
    public long getNodeCount(Long clusterId) {
        return this.tDlNodeService.lambdaQuery()
                .select()
                .eq(TDlNode::getClusterId, clusterId)
                .ne(TDlNode::getNodeState, NodeStateEnum.REMOVED)
                .count();
    }

    /**
     * Description: 查询所有非 REMOVED 的节点列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return List<Long> 集群 ID 列表
     */
    public Set<Long> getClusterIdListWithInNode() {
        return this.tDlNodeService.lambdaQuery()
                .select()
                .ne(TDlNode::getNodeState, NodeStateEnum.REMOVED)
                .list()
                .stream()
                .map(TDlNode::getClusterId)
                .collect(Collectors.toSet());
    }

    /**
     * Description: 批量移除某个集群下的某些节点（可单独移除 1 个节点）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 节点 ID 列表请求体
     * @return 成功或失败
     */
    public Result<String> removeBatchByIds(AbstractNodeRequest.NodeIdListRequest request) {

        // 读取将要删除的节点信息
        List<TDlNode> tDlNodeList = this.tDlNodeService.lambdaQuery()
                .select()
                .eq(TDlNode::getClusterId, request.getClusterId())
                .ne(TDlNode::getNodeState, NodeStateEnum.REMOVED)
                .in(
                        TBasePo::getId,
                        request.getNodeIdList()
                                .stream()
                                .map(AbstractNodeRequest.NodeIdRequest::getNodeId)
                                .collect(Collectors.toList())
                )
                .list();

        Assert.notEmpty(
                tDlNodeList,
                () -> new BException("请求节点列表中不存在尚未移除的节点")
        );

        Assert.isTrue(
                tDlNodeList.size() == request.getNodeIdList().size(),
                () -> new BException("将要移除的列表中存在不符合移除条件的节点信息，请重新确认")
        );

        // 判断当前集群是否还有初始化步骤在进行
        Assert.isFalse(
                this.masterInitProcedureService.isExistInitProcedure(request.getClusterId()).getData(),
                () -> new BException("当前集群有正在初始化的步骤，请先完成或取消初始化步骤再执行移除节点操作")
        );

        // 判断本批次节点中是否存在未删除的组件
        Assert.isFalse(
                this.tDlComponentService.lambdaQuery()
                        .select()
                        .eq(TDlComponent::getClusterId, request.getClusterId())
                        .in(TDlComponent::getNodeId, request.getNodeIdList())
                        .ne(TDlComponent::getComponentState, SCStateEnum.REMOVED)
                        .exists(),
                () -> new BException("节点列表中的节点存在未移除的组件，请确认移除对应组件后，再移除节点")
        );

        // 所有操作将会彻底删除数据，审计功能中将会保留操作数据历史
        Assert.isTrue(
                this.tDlNodeService.removeBatchByIds(tDlNodeList),
                () -> new DatabaseException("移除节点失败")
        );


        return Result.success();
    }

}
