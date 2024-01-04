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
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractNodeInitRequest;
import cn.boundivore.dl.base.request.impl.master.NodeInfoRequest;
import cn.boundivore.dl.base.request.impl.master.NodeJobRequest;
import cn.boundivore.dl.base.request.impl.master.ParseHostnameRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractClusterVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeInitVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.response.impl.master.ParseHostnameVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.orm.po.single.TDlNodeInit;
import cn.boundivore.dl.orm.service.single.impl.TDlNodeInitServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlNodeServiceImpl;
import cn.boundivore.dl.service.master.manage.node.bean.NodeResources;
import cn.boundivore.dl.service.master.tools.HostnameParser;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 节点初始化操作相关逻辑
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
public class MasterNodeInitService {

    private final MasterClusterService masterClusterService;

    private final TDlNodeInitServiceImpl tDlNodeInitService;

    private final TDlNodeServiceImpl tDlNodeService;

    private final MasterNodeJobService masterNodeJobService;

    /**
     * Description: 解析主机名正则，返回有效主机名列表与无效主机名列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 主机名与 SSH 端口号的请求体
     * @return Result<ParseHostnameVo> 解析后返回合法主机名与非法主机名
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<ParseHostnameVo> parseHostname(ParseHostnameRequest request) {

        Long clusterId = request.getClusterId();
        Long sshPort = request.getSshPort();

        //解码 base64
        String hostnameStr = Base64.decodeStr(
                request.getHostnameBase64(),
                CharsetUtil.UTF_8
        );

        HostnameParser.Hostnames hostnames = HostnameParser.parse(hostnameStr);
        List<String> validHostnameList = hostnames.getValidHostnames();
        List<String> invalidHostnameList = hostnames.getInvalidHostnames();

        //清除可能存在的过期数据
        List<TDlNodeInit> oldTdlNodeInitList = tDlNodeInitService.lambdaQuery()
                .select()
                .eq(TDlNodeInit::getClusterId, clusterId)
                .in(TDlNodeInit::getHostname, validHostnameList)
                .list();

        tDlNodeInitService.removeBatchByIds(oldTdlNodeInitList);

        //保存本批次的节点数据
        List<TDlNodeInit> tDlNodeInitList = validHostnameList.stream()
                .map(hostname -> {
                    TDlNodeInit tDlNodeInit = new TDlNodeInit();
                    tDlNodeInit.setVersion(0L);
                    tDlNodeInit.setClusterId(clusterId);
                    tDlNodeInit.setHostname(hostname);
                    tDlNodeInit.setSshPort(sshPort);
                    tDlNodeInit.setNodeInitState(NodeStateEnum.RESOLVED);

                    String na = "";
                    Long naLong = 0L;

                    tDlNodeInit.setIpv4(na);
                    tDlNodeInit.setIpv6(na);
                    tDlNodeInit.setCpuArch(na);
                    tDlNodeInit.setCpuCores(naLong);
                    tDlNodeInit.setRam(naLong);
                    tDlNodeInit.setDisk(naLong);
                    tDlNodeInit.setOsVersion(na);

                    return tDlNodeInit;
                })
                .collect(Collectors.toList());

        Assert.isTrue(
                tDlNodeInitService.saveBatch(tDlNodeInitList),
                () -> new DatabaseException("保存节点初始信息失败")
        );

        return Result.success(
                new ParseHostnameVo(
                        clusterId,
                        sshPort,
                        validHostnameList,
                        invalidHostnameList
                )
        );
    }

    /**
     * Description: 根据 SSH 端口号，异步探测节点是否可连通
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/30
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 节点操作请求
     * @return Result<AbstractNodeJobVo.NodeJobIdVo> 返回集群 ID 与 NodeJobId
     */
    public Result<AbstractNodeJobVo.NodeJobIdVo> detectNode(NodeJobRequest request) throws Exception {
        Assert.isTrue(
                request.getNodeActionTypeEnum() == NodeActionTypeEnum.DETECT,
                () -> new BException(
                        String.format(
                                "调用接口与节点操作意图不匹配: %s, 期望意图: %s",
                                request.getNodeActionTypeEnum(),
                                NodeActionTypeEnum.DETECT
                        )
                )
        );

        Long nodeJobId = this.masterNodeJobService.initNodeJob(request, true);

        return Result.success(
                new AbstractNodeJobVo.NodeJobIdVo(
                        request.getClusterId(),
                        nodeJobId
                )
        );
    }

    /**
     * Description: 根据用户选择的节点，开始异步初始化工作，包括节点详细检查
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 初始化节点请求体
     * @return Result<AbstractNodeJobVo.NodeJobIdVo> 返回集群 ID 与 NodeJobId
     */
    public Result<AbstractNodeJobVo.NodeJobIdVo> checkNode(NodeJobRequest request) throws Exception {
        Assert.isTrue(
                request.getNodeActionTypeEnum() == NodeActionTypeEnum.CHECK,
                () -> new BException(
                        String.format(
                                "调用接口与节点操作意图不匹配: %s, 期望意图: %s",
                                request.getNodeActionTypeEnum(),
                                NodeActionTypeEnum.CHECK
                        )
                )
        );

        List<TDlNodeInit> tDlNodeInitList = this.tDlNodeInitService.lambdaQuery()
                .select()
                .eq(TDlNodeInit::getClusterId, request.getClusterId())
                .in(
                        TBasePo::getId,
                        request.getNodeInfoList()
                                .stream()
                                .map(NodeInfoRequest::getNodeId)
                                .collect(Collectors.toList())
                )
                .list();

        Assert.notEmpty(
                tDlNodeInitList,
                () -> new BException(
                        String.format(
                                "无法找到对应的节点信息: %s",
                                request.getNodeInfoList()
                                        .stream()
                                        .map(NodeInfoRequest::getNodeId)
                                        .collect(Collectors.toList())
                        )
                )
        );

        // 检查节点合法性
        this.checkNodeLegality(
                request.getClusterId(),
                tDlNodeInitList.stream()
                        .map(TDlNodeInit::getHostname)
                        .collect(Collectors.toList()),
                tDlNodeInitList.stream()
                        .map(TDlNodeInit::getIpv4)
                        .collect(Collectors.toList())
        );

        Long nodeJobId = this.masterNodeJobService.initNodeJob(request, true);
        return Result.success(
                new AbstractNodeJobVo.NodeJobIdVo(
                        request.getClusterId(),
                        nodeJobId
                )
        );
    }

    /**
     * Description: 检查节点合法性，检查如果是 COMPUTE 集群，
     * 本次添加的节点的主机名与 IP，是否在当前集群以及所关联的集群中的节点重复，如果重复，则抛出异常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId    当前集群 ID
     * @param hostnameList 将要添加的主机名列表
     * @param ipv4List     将要添加的 IPV4 地址列表
     */
    private void checkNodeLegality(Long clusterId,
                                   List<String> hostnameList,
                                   List<String> ipv4List) {

        List<Long> clusterIdList = new ArrayList<>();

        AbstractClusterVo.ClusterVo currentCluster = this.masterClusterService
                .getClusterById(clusterId)
                .getData();

        clusterIdList.add(currentCluster.getClusterId());

        if (currentCluster.getClusterTypeEnum() == ClusterTypeEnum.COMPUTE) {
            AbstractClusterVo.ClusterVo relativeCluster = this.masterClusterService
                    .getClusterRelative(clusterId)
                    .getData();
            clusterIdList.add(relativeCluster.getClusterId());
        }

        // 检查对应集群中是否存在重复的节点信息
        Assert.isFalse(
                this.tDlNodeService.lambdaQuery()
                        .select()
                        .in(TDlNode::getClusterId, clusterIdList)
                        .ne(TDlNode::getNodeState, NodeStateEnum.REMOVED)
                        .and(i -> i
                                .in(TDlNode::getHostname, hostnameList)
                                .or()
                                .in(TDlNode::getIpv4, ipv4List)
                        )
                        .exists(),
                () -> new BException("服役的集群中存在相同的 主机名 或 IP")
        );

    }


    /**
     * Description: 向指定节点异步分发安装包
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/30
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 节点操作请求
     * @return Result<AbstractNodeJobVo.NodeJobIdVo> 返回集群 ID 与 NodeJobId
     */
    public Result<AbstractNodeJobVo.NodeJobIdVo> dispatchNode(NodeJobRequest request) throws Exception {
        Assert.isTrue(
                request.getNodeActionTypeEnum() == NodeActionTypeEnum.DISPATCH,
                () -> new BException(
                        String.format(
                                "调用接口与节点操作意图不匹配: %s, 期望意图: %s",
                                request.getNodeActionTypeEnum(),
                                NodeActionTypeEnum.DISPATCH
                        )
                )
        );

        Long nodeJobId = this.masterNodeJobService.initNodeJob(request, true);
        return Result.success(
                new AbstractNodeJobVo.NodeJobIdVo(
                        request.getClusterId(),
                        nodeJobId
                )
        );
    }


    /**
     * Description: 切换初始化过程中的节点的状态
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
    public void switchNodeInitState(Long clusterId,
                                    Long nodeId,
                                    NodeStateEnum nodeStateEnum) {

        TDlNodeInit tDlNodeInit = tDlNodeInitService.getById(nodeId);
        tDlNodeInit.setNodeInitState(nodeStateEnum);

        Assert.isTrue(
                tDlNodeInitService.updateById(tDlNodeInit),
                () -> new BException("更新初始化过程中的节点状态失败")
        );

    }

    /**
     * Description: 将探测到的物理资源更新到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeId        节点 ID
     * @param nodeResources 当前探测到的节点物理资源
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void updateNodeResourceInDatabase(Long nodeId, NodeResources nodeResources) throws Exception {
        TDlNodeInit tDlNodeInit = this.tDlNodeInitService.getById(nodeId);
        Assert.notNull(tDlNodeInit, () -> new DatabaseException("没有找到对应节点初始化信息"));

        tDlNodeInit.setIpv4(nodeResources.getNodeIp());
        tDlNodeInit.setCpuArch(nodeResources.getCpuArch());
        tDlNodeInit.setCpuCores(nodeResources.getCpuCores());
        tDlNodeInit.setRam(nodeResources.getRam());
        tDlNodeInit.setDisk(nodeResources.getDiskTotal());
        tDlNodeInit.setOsVersion(nodeResources.getOsVersion());

        Assert.isTrue(
                this.tDlNodeInitService.updateById(tDlNodeInit),
                () -> new DatabaseException("更新节点初始化物理资源信息失败")
        );
    }

    /**
     * Description: 用于获取节点初始化任务 Parse 之后的节点初始化列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群ID
     * @return 包含节点初始化信息的 Result对象
     */
    public Result<AbstractNodeInitVo.NodeInitVo> initParseList(Long clusterId) {
        return this.initList(
                clusterId,
                CollUtil.newArrayList(
                        NodeStateEnum.RESOLVED
                )
        );
    }


    /**
     * Description: 用于获取节点初始化任务 Detect 之后的节点初始化列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群ID
     * @return 包含节点初始化信息的 Result对象
     */
    public Result<AbstractNodeInitVo.NodeInitVo> initDetectList(Long clusterId) {
        return this.initList(
                clusterId,
                CollUtil.newArrayList(
                        NodeStateEnum.DETECTING,
                        NodeStateEnum.ACTIVE,
                        NodeStateEnum.INACTIVE
                )
        );
    }

    /**
     * Description: 用于获取节点初始化任务 Check 之后的节点初始化列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群ID
     * @return 包含节点初始化信息的 Result对象
     */
    public Result<AbstractNodeInitVo.NodeInitVo> initCheckList(Long clusterId) {
        return this.initList(
                clusterId,
                CollUtil.newArrayList(
                        NodeStateEnum.CHECKING,
                        NodeStateEnum.CHECK_OK,
                        NodeStateEnum.CHECK_ERROR
                )
        );
    }

    /**
     * Description: 用于获取节点初始化任务 Dispatch 之后的节点初始化列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群ID
     * @return 包含节点初始化信息的 Result对象
     */
    public Result<AbstractNodeInitVo.NodeInitVo> initDispatchList(Long clusterId) {
        return this.initList(
                clusterId,
                CollUtil.newArrayList(
                        NodeStateEnum.PUSHING,
                        NodeStateEnum.PUSH_OK,
                        NodeStateEnum.PUSH_ERROR
                )
        );
    }

    /**
     * Description: 获取节点初始化列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId         集群ID
     * @param nodeStateEnumList 节点状态列表
     * @return 初始化结果
     */
    private Result<AbstractNodeInitVo.NodeInitVo> initList(Long clusterId,
                                                           List<NodeStateEnum> nodeStateEnumList) {
        List<AbstractNodeInitVo.NodeInitDetailVo> nodeInitDetailList = tDlNodeInitService.lambdaQuery()
                .select()
                .eq(TDlNodeInit::getClusterId, clusterId)
                .in(TDlNodeInit::getNodeInitState, nodeStateEnumList)
                .orderByAsc(TDlNodeInit::getHostname)
                .list()
                .stream()
                .map(i -> new AbstractNodeInitVo.NodeInitDetailVo()
                        .setNodeId(i.getId())
                        .setHostname(i.getHostname())
                        .setNodeIp(i.getIpv4())
                        .setSshPort(i.getSshPort())
                        .setCpuArch(i.getCpuArch())
                        .setCpuCores(i.getCpuCores())
                        .setRam(i.getRam())
                        .setDiskTotal(i.getDisk())
                        .setNodeState(i.getNodeInitState())
                )
                .collect(Collectors.toList());

        return Result.success(
                new AbstractNodeInitVo.NodeInitVo(
                        clusterId,
                        nodeInitDetailList
                )
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
    public List<TDlNodeInit> getNodeInitListInHostnames(Long clusterId, List<String> hostnameList) {
        return this.tDlNodeInitService.lambdaQuery()
                .select()
                .eq(TDlNodeInit::getClusterId, clusterId)
                .in(TDlNodeInit::getHostname, hostnameList)
                .list();
    }

    /**
     * Description: 添加初始化成功的节点到已服役节点列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 准备添加到服役列表的节点
     * @return 成功则直接返回，失败则抛出异常
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> addNode(AbstractNodeInitRequest.NodeInitInfoListRequest request) {
        List<TDlNode> tDlNodeList = this.tDlNodeInitService.lambdaQuery()
                .select()
                .eq(TDlNodeInit::getClusterId, request.getClusterId())
                .in(
                        TBasePo::getId,
                        request.getNodeInfoList()
                                .stream()
                                .map(NodeInfoRequest::getNodeId)
                                .collect(Collectors.toList())
                )
                .list()
                .stream()
                .map(i -> {
                    Assert.isTrue(
                            i.getNodeInitState() == NodeStateEnum.PUSH_OK,
                            () -> new BException(
                                    String.format(
                                            "节点 %s 初始化状态为 %s 方可服役到集群, 当前状态: %s",
                                            i.getHostname(),
                                            NodeStateEnum.PUSH_OK,
                                            i.getNodeInitState()
                                    )
                            )
                    );

                    TDlNode tDlNode = new TDlNode();
                    tDlNode.setId(i.getId());
                    tDlNode.setVersion(0L);
                    tDlNode.setClusterId(i.getClusterId());
                    tDlNode.setHostname(i.getHostname());
                    tDlNode.setIpv4(i.getIpv4());
                    tDlNode.setIpv6(i.getIpv6());
                    tDlNode.setSshPort(i.getSshPort());
                    tDlNode.setCpuArch(i.getCpuArch());
                    tDlNode.setCpuCores(i.getCpuCores());
                    tDlNode.setRam(i.getRam());
                    tDlNode.setDisk(i.getDisk());
                    tDlNode.setNodeState(NodeStateEnum.STARTED);
                    tDlNode.setOsVersion(i.getOsVersion());

                    return tDlNode;

                })
                .collect(Collectors.toList());

        Assert.notEmpty(
                tDlNodeList,
                () -> new BException(
                        String.format(
                                "无法找到对应的节点信息: %s",
                                request.getNodeInfoList()
                                        .stream()
                                        .map(NodeInfoRequest::getNodeId)
                                        .collect(Collectors.toList())
                        )
                )
        );

        // 节点合法性检查
        this.checkNodeLegality(
                request.getClusterId(),
                tDlNodeList.stream()
                        .map(TDlNode::getHostname)
                        .collect(Collectors.toList()),
                tDlNodeList.stream()
                        .map(TDlNode::getIpv4)
                        .collect(Collectors.toList())
        );


        Assert.isTrue(
                tDlNodeService.saveBatch(tDlNodeList),
                () -> new DatabaseException("添加节点到服役列表失败")
        );

        // 删除 NodeInit 表信息
        Assert.isTrue(
                tDlNodeInitService.removeBatchByIds(
                        request.getNodeInfoList()
                                .stream()
                                .map(NodeInfoRequest::getNodeId)
                                .collect(Collectors.toList())
                ),
                () -> new DatabaseException("删除节点初始化信息失败")
        );

        return Result.success();
    }


}
