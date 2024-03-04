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
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractNodeRequest;
import cn.boundivore.dl.base.request.impl.master.NodeJobRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeJobVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.lock.LocalLock;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.*;
import cn.boundivore.dl.orm.service.single.impl.*;
import cn.boundivore.dl.service.master.env.DataLightEnv;
import cn.boundivore.dl.service.master.manage.node.bean.NodeJobCacheBean;
import cn.boundivore.dl.service.master.manage.node.bean.NodeJobMeta;
import cn.boundivore.dl.service.master.manage.node.bean.NodeStepMeta;
import cn.boundivore.dl.service.master.manage.node.bean.NodeTaskMeta;
import cn.boundivore.dl.service.master.manage.node.job.NodeIntention;
import cn.boundivore.dl.service.master.manage.node.job.NodeJob;
import cn.boundivore.dl.service.master.manage.node.job.NodeJobCacheUtil;
import cn.boundivore.dl.service.master.manage.node.job.NodePlan;
import cn.boundivore.dl.ssh.bean.TransferProgress;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Description: 组装节点的操作意图，并初始化 Job，最后执行
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
public class MasterNodeJobService {

    private final TDlNodeInitServiceImpl tDlNodeInitService;

    private final TDlNodeServiceImpl tDlNodeService;

    private final TDlNodeJobServiceImpl tDlNodeJobService;

    private final TDlNodeTaskServiceImpl tDlNodeTaskService;

    private final TDlNodeStepServiceImpl tDlNodeStepService;

    private final TDlNodeJobLogServiceImpl tDlNodeJobLogService;

    /**
     * Description: 执行节点异步操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/30
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 当前即将对节点进行的操作请求
     * @param isAsc   是否按照节点主机名正序执行，true：正序，false：逆序
     * @return NodeJobId
     */
    @LocalLock
    public Long initNodeJob(NodeJobRequest request, boolean isAsc) throws Exception {

        // 检查 NodeJob 合法性
        this.checkNodeJobIllegal(request);

        // 切换初始化节点的状态
        NodeStateEnum nodeStateEnum = this.resolveNodeStateFromAction(request.getNodeActionTypeEnum());
        if (nodeStateEnum != null) {
            this.switchNodeInitStateFromAction(request.getNodeInfoList(), nodeStateEnum);
        }

        List<String> hostnameList = request.getNodeInfoList()
                .stream()
                .map(AbstractNodeRequest.NodeInfoRequest::getHostname)
                .collect(Collectors.toList());

        final NodeIntention nodeIntention = new NodeIntention()
                .setClusterId(request.getClusterId())
                .setNodeActionTypeEnum(request.getNodeActionTypeEnum())
                .setNodeList(
                        this.intentionNodeList(
                                request.getClusterId(),
                                request.getNodeActionTypeEnum(),
                                hostnameList,
                                isAsc
                        )
                );

        // 创建并执行 NodeJob
        final NodeJob job = new NodeJob(nodeIntention).init();
        job.start();

        return job.getNodeJobMeta().getId();
    }

    /**
     * Description: 根据操作行为，返回此时节点应该处于的状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeActionTypeEnum 节点操作类型
     * @return NodeStateEnum 节点此时应处在的状态
     */
    public NodeStateEnum resolveNodeStateFromAction(NodeActionTypeEnum nodeActionTypeEnum) {
        switch (nodeActionTypeEnum) {
            case DETECT:
                return NodeStateEnum.DETECTING;
            case CHECK:
                return NodeStateEnum.CHECKING;
            case DISPATCH:
                return NodeStateEnum.PUSHING;
            case START_WORKER:
                return NodeStateEnum.STARTING_WORKER;
            case SHUTDOWN:
                return NodeStateEnum.STOPPED;
            case RESTART:
                return NodeStateEnum.RESTARTING;
            default:
                return null;
        }
    }

    /**
     * Description: 将初始化节点的状态切换到指定值
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeInfoList  节点信息列表
     * @param nodeStateEnum 将要切换到的状态
     */
    public void switchNodeInitStateFromAction(List<AbstractNodeRequest.NodeInfoRequest> nodeInfoList,
                                              NodeStateEnum nodeStateEnum) {

        List<Long> nodeIdList = nodeInfoList
                .stream()
                .map(AbstractNodeRequest.NodeInfoRequest::getNodeId)
                .collect(Collectors.toList());

        List<TDlNodeInit> tDlNodeInitList = this.tDlNodeInitService.lambdaQuery()
                .select()
                .in(TBasePo::getId, nodeIdList)
                .list()
                .stream()
                .peek(i -> i.setNodeInitState(nodeStateEnum))
                .collect(Collectors.toList());

        Assert.isTrue(
                this.tDlNodeInitService.updateBatchById(tDlNodeInitList),
                () -> new DatabaseException("更新初始化节点状态失败")
        );
    }

    /**
     * Description: 检查当前 NodeJob 合法性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 当前即将对节点进行的操作请求
     */
    private void checkNodeJobIllegal(NodeJobRequest request) {
        Long clusterId = request.getClusterId();
        List<Long> nodeIdList = request.getNodeInfoList()
                .stream()
                .map(AbstractNodeRequest.NodeInfoRequest::getNodeId)
                .collect(Collectors.toList());

        List<TDlNodeInit> tDlNodeInitList = this.tDlNodeInitService.lambdaQuery()
                .select()
                .eq(TDlNodeInit::getClusterId, clusterId)
                .in(TDlNodeInit::getId, nodeIdList)
                .list();

        Assert.isTrue(
                tDlNodeInitList.size() == nodeIdList.size(),
                () -> new BException("部分节点不存在")
        );

        // 检查节点状态是否合法
        NodeStateEnum finalBoundaryNodeStateEnum = this.getBoundaryInitNodeStateEnum(request.getNodeActionTypeEnum());
        if (finalBoundaryNodeStateEnum != null) {
            List<String> filterHostnameList = tDlNodeInitList.stream()
                    .filter(tDlNodeInit -> tDlNodeInit
                            .getNodeInitState()
                            .isLessThan(finalBoundaryNodeStateEnum)
                    )
                    .map(TDlNodeInit::getHostname)
                    .collect(Collectors.toList());

            Assert.isTrue(
                    filterHostnameList.isEmpty(),
                    () -> new BException(
                            String.format(
                                    "当前操作的主机名中，存在不符合状态的节点: %s",
                                    filterHostnameList
                            )
                    )
            );

        }
    }

    /**
     * Description: 返回节点的边界状态，即：当前操作应该满足大于等于边界状态（过滤小于边界状态的节点，并抛出异常）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeActionTypeEnum 当前节点的即将采取的行为（枚举）
     * @return 节点的边界状态，即：当前操作应该满足大于等于边界状态（过滤小于边界状态的节点，并抛出异常）
     */
    @Nullable
    private NodeStateEnum getBoundaryInitNodeStateEnum(NodeActionTypeEnum nodeActionTypeEnum) {

        NodeStateEnum boundaryNodeStateEnum = null;
        switch (nodeActionTypeEnum) {
            case DETECT:
                boundaryNodeStateEnum = NodeStateEnum.RESOLVED;
                break;
            case CHECK:
                boundaryNodeStateEnum = NodeStateEnum.ACTIVE;
                break;
            case DISPATCH:
                boundaryNodeStateEnum = NodeStateEnum.CHECK_OK;
                break;
            case START_WORKER:
                boundaryNodeStateEnum = NodeStateEnum.PUSH_OK;
                break;
        }

        //如果传递的主机名中不满足对应状态，则抛出异常
        return boundaryNodeStateEnum;
    }

    /**
     * Description: 根据 Action 判断从哪张表组装 IntentionNode 信息
     * 根据给定的集群 ID、节点行为类型、主机名列表和排序方式，从相应的数据库表中获取节点信息，并组装成意图节点信息。
     * 如果传入的节点行为类型为 SHUTDOWN 或 RESTART，则从 TDlNode 表中获取节点信息；
     * 如果传入的节点行为类型为 DETECT、CHECK 或 DISPATCH，则从 TDlNodeInit 表中获取节点信息。
     * 如果存在无效的主机名，将抛出异常。
     * <p>
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/30
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: BException - 如果节点行为类型非法或部分节点信息不存在
     *
     * @param clusterId          集群ID
     * @param nodeActionTypeEnum 节点行为类型
     * @param hostnameList       主机名列表
     * @param isAsc              是否升序排序
     * @return List<NodeIntention.Node> 封装意图节点信息的列表
     */
    public List<NodeIntention.Node> intentionNodeList(Long clusterId,
                                                      NodeActionTypeEnum nodeActionTypeEnum,
                                                      List<String> hostnameList,
                                                      boolean isAsc) {

        List<NodeIntention.Node> nodeIntentionList = new ArrayList<>();

        switch (nodeActionTypeEnum) {
            case SHUTDOWN:
            case RESTART:
                // 获取节点列表并进行排序
                nodeIntentionList.addAll(
                        this.tDlNodeService.lambdaQuery()
                                .select()
                                .eq(TDlNode::getClusterId, clusterId)
                                .in(TDlNode::getHostname, hostnameList)
                                .list()
                                .stream()
                                .map(i -> new NodeIntention.Node(
                                        i.getId(),
                                        i.getIpv4(),
                                        i.getHostname(),
                                        i.getSshPort().intValue(),
                                        DataLightEnv.PRIVATE_KEY_PATH,
                                        false

                                ))
                                .sorted((o1, o2) -> isAsc ?
                                        o1.getHostname().compareTo(o2.getHostname()) :
                                        o2.getHostname().compareTo(o1.getHostname())
                                )
                                .collect(Collectors.toList())
                );

                // 如果存在 Master 所在节点的操作意图，则将 Master 所在节点放置于最后
                this.moveMasterIntentionToEnd(nodeIntentionList);
                break;
            case DETECT:
            case CHECK:
            case DISPATCH:
            case START_WORKER:
                // 获取节点初始化列表并进行排序
                nodeIntentionList.addAll(
                        this.tDlNodeInitService.lambdaQuery()
                                .select()
                                .eq(TDlNodeInit::getClusterId, clusterId)
                                .in(TDlNodeInit::getHostname, hostnameList)
                                .list()
                                .stream()
                                .map(i -> new NodeIntention.Node(
                                        i.getId(),
                                        i.getIpv4(),
                                        i.getHostname(),
                                        i.getSshPort().intValue(),
                                        DataLightEnv.PRIVATE_KEY_PATH,
                                        false
                                ))
                                .sorted((o1, o2) -> isAsc ?
                                        o1.getHostname().compareTo(o2.getHostname()) :
                                        o2.getHostname().compareTo(o1.getHostname())
                                )
                                .collect(Collectors.toList())
                );
                break;
            default:
                // 抛出异常，表示非法的节点行为
                throw new BException(
                        String.format(
                                "非法的节点行为: %s",
                                nodeActionTypeEnum
                        )
                );
        }

        // 检查传入的主机名信息是否都已在表中有相应记录，如果存在部分没有，则证明传入了非法的主机名
        if (hostnameList.size() != nodeIntentionList.size()) {
            // 获取结果中存在的主机名列表
            List<String> resultHostnameList = nodeIntentionList.stream()
                    .map(NodeIntention.Node::getHostname)
                    .collect(Collectors.toList());

            // 过滤出不存在于结果中的主机名列表
            List<String> notExistHostname = hostnameList.stream()
                    .filter(i -> !resultHostnameList.contains(i))
                    .collect(Collectors.toList());

            // 抛出异常，表示部分节点信息不存在
            Assert.isTrue(
                    false,
                    () -> new BException(
                            String.format(
                                    "部分节点信息不存在: %s",
                                    notExistHostname
                            )
                    )
            );
        }

        return nodeIntentionList;
    }

    /**
     * Description: 在关机或重启时，如果当前操作的节点是 Master 节点，则放在最后操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeIntentionList 节点操作意图
     */
    private void moveMasterIntentionToEnd(List<NodeIntention.Node> nodeIntentionList) {

        // 查找是否存在对 Master 所在节点的操作意图
        int masterNodeIntentionIndex = -1;
        for (int i = 0; i < nodeIntentionList.size(); i++) {
            if (nodeIntentionList.get(i).getNodeIp().equals(DataLightEnv.MASTER_IP)) {
                masterNodeIntentionIndex = i;
                break;
            }
        }

        if (masterNodeIntentionIndex != -1) {
            NodeIntention.Node masterNodeIntention = nodeIntentionList.remove(masterNodeIntentionIndex);
            masterNodeIntention.setWait(true);
            nodeIntentionList.add(masterNodeIntention);
        }
    }

    /**
     * Description: 获取正在活跃的节点 JobID
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return NodeJobIdVo 活跃的  NodeJobId 信息
     */
    public Result<AbstractNodeJobVo.NodeJobIdVo> getActiveNodeJobId() {
        long activeJobId = NodeJobCacheUtil.getInstance().getActiveJobId().get();
        Assert.isTrue(
                activeJobId != 0L,
                () -> new BException("当前没有活跃的任务")
        );

        Long clusterId = NodeJobCacheUtil.getInstance()
                .get(activeJobId)
                .getNodeJobMeta()
                .getClusterId();

        return Result.success(
                new AbstractNodeJobVo.NodeJobIdVo(
                        clusterId,
                        activeJobId
                )
        );

    }

    /**
     * Description: 获取节点作业的进度信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeJobId 节点作业ID
     * @return 节点作业进度信息的结果对象
     */
    public Result<AbstractNodeJobVo.NodeJobProgressVo> getNodeJobProgress(Long nodeJobId) {
        // 从缓存中获取 NodeJobCacheBean 对象
        NodeJobCacheBean nodeJobCacheBean = NodeJobCacheUtil.getInstance().get(nodeJobId);

        if (nodeJobCacheBean == null) {
            // 内存缓存已失效，从数据库中读取
            nodeJobCacheBean = this.getJobCacheBeanFromDb(nodeJobId);
        }

        // 获取节点 NodeJob 的元数据信息
        NodeJobMeta nodeJobMeta = nodeJobCacheBean.getNodeJobMeta();
        // 获取节点 NodeJob 的计划信息
        NodePlan nodePlan = nodeJobCacheBean.getNodePlan();

        // 获取集群 ID
        Long clusterId = nodeJobMeta.getClusterId();

        // 创建结果对象
        AbstractNodeJobVo.NodeJobProgressVo nodeJobProgressVo = new AbstractNodeJobVo.NodeJobProgressVo()
                .setNodeJobId(nodeJobId)
                .setClusterId(clusterId);

        // 组装计划进度信息
        AbstractNodeJobVo.NodeJobPlanProgressVo nodeJobPlanProgressVo = this.createNodeJobPlanProgressVo(
                clusterId,
                nodeJobId,
                nodeJobMeta,
                nodePlan
        );
        nodeJobProgressVo.setNodeJobPlanProgressVo(nodeJobPlanProgressVo);

        // 组装执行进度信息
        AbstractNodeJobVo.NodeJobExecProgressVo nodeJobExecProgressVo = this.createNodeJobExecProgressVo(
                nodeJobId,
                clusterId,
                nodePlan
        );
        nodeJobProgressVo.setNodeJobExecProgressVo(nodeJobExecProgressVo);

        // 组装每个节点的执行进度信息
        List<AbstractNodeJobVo.ExecProgressPerNodeVo> execProgressPerNodeList = this.createExecProgressPerNodeList(nodeJobMeta);
        nodeJobExecProgressVo.setExecProgressPerNodeList(execProgressPerNodeList);

        return Result.success(nodeJobProgressVo);
    }

    /**
     * Description: 从数据库获取节点作业的进度信息，优先从内存读取，若无，则从数据库读取
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeJobId 节点作业ID
     * @return 节点作业进度信息的结果对象
     */
    public Result<ExecStateEnum> getNodeJobState(Long nodeJobId) {
        NodeJobCacheBean nodeJobCacheBean = NodeJobCacheUtil.getInstance().get(nodeJobId);

        if (nodeJobCacheBean != null) {
            return Result.success(
                    NodeJobCacheUtil.getInstance()
                            .get(nodeJobId)
                            .getNodeJobMeta()
                            .getExecStateEnum()
            );
        }

        TDlNodeJob tDlNodeJob = this.tDlNodeJobService.getById(nodeJobId);
        Assert.notNull(
                tDlNodeJob,
                () -> new BException("NodeJobId 不存在")
        );
        return Result.success(tDlNodeJob.getNodeJobState());
    }


    /**
     * Description: 创建 NodeJobPlanProgressVo 对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群ID
     * @param nodeJobId   节点作业ID
     * @param nodeJobMeta 节点作业元数据信息
     * @param nodePlan    节点作业计划信息
     * @return 创建的 NodeJobPlanProgressVo 对象
     */
    private AbstractNodeJobVo.NodeJobPlanProgressVo createNodeJobPlanProgressVo(Long clusterId,
                                                                                Long nodeJobId,
                                                                                NodeJobMeta nodeJobMeta,
                                                                                NodePlan nodePlan) {
        int planTotal = nodePlan.getPlanTotal();
        int planCurrent = nodePlan.getPlanCurrent();
        int planProgress = nodePlan.getPlanProgress();

        return new AbstractNodeJobVo.NodeJobPlanProgressVo(
                clusterId,
                nodeJobId,
                nodeJobMeta.getNodeActionTypeEnum(),
                planTotal,
                planCurrent,
                planProgress,
                nodePlan.getPlanName()
        );
    }

    /**
     * Description: 创建 NodeJobExecProgressVo 对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeJobId 节点作业ID
     * @param clusterId 集群ID
     * @param nodePlan  节点作业计划信息
     * @return 创建的 NodeJobExecProgressVo 对象
     */
    private AbstractNodeJobVo.NodeJobExecProgressVo createNodeJobExecProgressVo(Long nodeJobId,
                                                                                Long clusterId,
                                                                                NodePlan nodePlan) {
        int execTotal = nodePlan.getExecTotal().get();
        int execCurrent = nodePlan.getExecCurrent().get();
        int execProgress = nodePlan.getExecProgress().get();

        return new AbstractNodeJobVo.NodeJobExecProgressVo()
                .setJobExecStateEnum(NodeJobCacheUtil.getInstance().get(nodeJobId).getNodeJobMeta().getExecStateEnum())
                .setNodeJobId(nodeJobId)
                .setClusterId(clusterId)
                .setExecTotal(execTotal)
                .setExecCurrent(execCurrent)
                .setExecProgress(execProgress)
                .setExecProgressPerNodeList(new ArrayList<>());
    }

    /**
     * Description: 创建每个节点的执行进度信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeJobMeta 节点作业元数据信息
     * @return 每个节点的执行进度信息列表
     */
    private List<AbstractNodeJobVo.ExecProgressPerNodeVo> createExecProgressPerNodeList(NodeJobMeta nodeJobMeta) {
        List<AbstractNodeJobVo.ExecProgressPerNodeVo> execProgressPerNodeList = new ArrayList<>();

        nodeJobMeta.getNodeTaskMetaMap()
                .forEach((nodeTaskId, nodeTaskMeta) -> {
                    AbstractNodeJobVo.ExecProgressPerNodeVo execProgressPerNodeVo = this.createExecProgressPerNodeVo(nodeTaskMeta);
                    execProgressPerNodeList.add(execProgressPerNodeVo);
                    this.assembleExecProgressStepList(
                            execProgressPerNodeVo,
                            nodeTaskMeta
                    );
                });
        return execProgressPerNodeList;
    }

    /**
     * Description: 创建每个节点的执行进度信息对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeTaskMeta 节点任务元数据信息
     * @return 每个节点的执行进度信息对象
     */
    private AbstractNodeJobVo.ExecProgressPerNodeVo createExecProgressPerNodeVo(NodeTaskMeta nodeTaskMeta) {
        int[] progressArr = this.calculatePerNodeProgress(nodeTaskMeta);

        return new AbstractNodeJobVo.ExecProgressPerNodeVo(
                nodeTaskMeta.getNodeId(),
                nodeTaskMeta.getHostname(),
                nodeTaskMeta.getNodeIp(),
                nodeTaskMeta.getId(),
                nodeTaskMeta.getName(),
                progressArr[0],
                progressArr[1],
                progressArr[2],
                new ArrayList<>()
        );
    }

    /**
     * Description: 填充节点任务的步骤执行进度信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param execProgressPerNodeVo 节点任务的执行进度信息对象
     * @param nodeTaskMeta          节点任务元数据信息
     */
    private void assembleExecProgressStepList(AbstractNodeJobVo.ExecProgressPerNodeVo execProgressPerNodeVo,
                                              NodeTaskMeta nodeTaskMeta) {
        nodeTaskMeta.getNodeStepMetaMap().forEach((nodeStepId, nodeStepMeta) -> {
            AbstractNodeJobVo.ExecProgressStepVo execProgressStepVo = new AbstractNodeJobVo.ExecProgressStepVo(
                    nodeStepMeta.getType(),
                    nodeStepMeta.getId(),
                    nodeStepMeta.getName(),
                    nodeStepMeta.getExecStateEnum()
            );
            execProgressPerNodeVo.getExecProgressStepList().add(execProgressStepVo);
        });
    }

    /**
     * Description: 计算每个节点上任务和步骤的进度
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeTaskMeta 节点任务元数据信息
     * @return 每个节点上任务和步骤的进度数组
     */
    private int[] calculatePerNodeProgress(NodeTaskMeta nodeTaskMeta) {
        Collection<NodeStepMeta> nodeStepMetaCollection = nodeTaskMeta.getNodeStepMetaMap().values();

        int execTotal = nodeStepMetaCollection.size();
        int execCurrent = 0;
        int execProgress = 0;

        for (NodeStepMeta nodeStepMeta : nodeStepMetaCollection) {
            switch (nodeStepMeta.getExecStateEnum()) {
                case SUSPEND:
                case RUNNING:
                case ERROR:
                    break;
                case OK:
                    execCurrent++;
                    break;
            }
        }

        execProgress = execCurrent * 100 / execTotal;

        return new int[]{execTotal, execCurrent, execProgress};
    }


    /**
     * Description: 获取所有节点文件分发进度概览
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeJobId 节点作业ID
     * @return 返回获取所有节点文件分发进度概览
     */
    public Result<AbstractNodeJobVo.AllNodeJobTransferProgressVo> getNodeJobDispatchProgress(Long nodeJobId) {
        // 从缓存中获取 NodeJobCacheBean 对象
        NodeJobCacheBean nodeJobCacheBean = NodeJobCacheUtil.getInstance().get(nodeJobId);

        if (nodeJobCacheBean == null) {
            // 内存缓存已失效，从数据库中读取
            nodeJobCacheBean = this.getJobCacheBeanFromDb(nodeJobId);
        }

        final NodeJobCacheBean finalNodeJobCacheBean = nodeJobCacheBean;

        // 创建 AllNodeJobTransferProgressVo 对象
        AbstractNodeJobVo.AllNodeJobTransferProgressVo allNodeJobTransferProgressVo = new AbstractNodeJobVo.AllNodeJobTransferProgressVo();
        allNodeJobTransferProgressVo.setNodeJobId(nodeJobId);
        allNodeJobTransferProgressVo.setClusterId(nodeJobCacheBean.getNodeJobMeta().getClusterId());

        allNodeJobTransferProgressVo.setNodeJobTransferProgressList(
                nodeJobCacheBean.getNodeJobMeta()
                        .getNodeTaskMetaMap()
                        .values()
                        .stream()
                        .flatMap(nodeTaskMeta -> nodeTaskMeta
                                .getNodeStepMetaMap()
                                .values()
                                .stream()
                        )
                        .filter(nodeStepMeta -> nodeStepMeta.getTransferProgress() != null)
                        .map(nodeStepMeta -> {
                                    TransferProgress transferProgress = nodeStepMeta.getTransferProgress();
                                    TransferProgress.FileProgress currentFileProgress = transferProgress.getCurrentFileProgress();

                                    // 创建 NodeJobTransferProgressVo 对象
                                    return new AbstractNodeJobVo.NodeJobTransferProgressVo()
                                            .setNodeTaskId(nodeStepMeta.getNodeTaskMeta().getId())
                                            .setNodeStepId(nodeStepMeta.getId())
                                            .setNodeId(nodeStepMeta.getNodeTaskMeta().getNodeId())
                                            .setHostname(nodeStepMeta.getNodeTaskMeta().getHostname())
                                            .setExecState(finalNodeJobCacheBean.getNodeJobMeta().getExecStateEnum())
                                            // 字节进度
                                            .setFileBytesProgressVo(new AbstractNodeJobVo.FileBytesProgressVo(
                                                    transferProgress.getTotalBytes(),
                                                    transferProgress.getTotalProgress(),
                                                    transferProgress.getTotalTransferBytes().get()
                                            ))
                                            // 文件个数进度
                                            .setFileCountProgressVo(new AbstractNodeJobVo.FileCountProgressVo(
                                                    transferProgress.getTotalFileCount(),
                                                    transferProgress.getTotalFileCountProgress(),
                                                    transferProgress.getTotalTransferFileCount().get()
                                            ))
                                            // 正在传输的文件信息
                                            .setCurrentFileProgressVo(new AbstractNodeJobVo.CurrentFileProgressVo(
                                                    currentFileProgress.getFilename(),
                                                    currentFileProgress.getFileBytes(),
                                                    currentFileProgress.getFileTransferBytes().get(),
                                                    currentFileProgress.getFileProgress(),
                                                    currentFileProgress.getPrintSpeed()
                                            ));
                                }
                        )
                        .collect(Collectors.toList())
        );

        return Result.success(allNodeJobTransferProgressVo);
    }


    /**
     * Description: 获取指定节点文件分发进度详情
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeJobId  节点作业ID
     * @param nodeTaskId 节点任务 ID
     * @param nodeStepId 节点步骤 ID
     * @return 返回节点文件分发进度详情
     */
    public Result<AbstractNodeJobVo.NodeJobTransferProgressDetailVo> getNodeJobDispatchProgressDetail(Long nodeJobId,
                                                                                                      Long nodeTaskId,
                                                                                                      Long nodeStepId) {
        // 从缓存中获取 NodeJobCacheBean 对象
        NodeJobCacheBean nodeJobCacheBean = NodeJobCacheUtil.getInstance().get(nodeJobId);

        if (nodeJobCacheBean == null) {
            // 内存缓存已失效，从数据库中读取
            nodeJobCacheBean = this.getJobCacheBeanFromDb(nodeJobId);
        }

        // 获取节点 Job 的元数据信息
        NodeJobMeta nodeJobMeta = nodeJobCacheBean.getNodeJobMeta();

        Assert.isTrue(
                nodeJobMeta.getNodeActionTypeEnum() == NodeActionTypeEnum.DISPATCH,
                () -> new BException("当前 NodeJob 无正在传输的文件")
        );

        // 获取集群 ID
        Long clusterId = nodeJobMeta.getClusterId();

        // 获取当前节点的信息
        NodeTaskMeta nodeTaskMeta = nodeJobMeta.getNodeTaskMetaMap().get(nodeTaskId);
        NodeStepMeta nodeStepMeta = nodeTaskMeta.getNodeStepMetaMap().get(nodeStepId);

        Assert.notNull(
                nodeStepMeta,
                () -> new BException("无效的 NodeStepId")
        );

        // 创建 NodeJobTransferProgressDetailVo 对象
        AbstractNodeJobVo.NodeJobTransferProgressDetailVo nodeJobTransferProgressDetailVo = new AbstractNodeJobVo.NodeJobTransferProgressDetailVo();
        nodeJobTransferProgressDetailVo.setClusterId(clusterId);
        nodeJobTransferProgressDetailVo.setNodeJobId(nodeJobId);

        // 装配：NodeJobTransferProgressVo
        // 获取传输进度对象
        TransferProgress transferProgress = nodeStepMeta.getTransferProgress();
        Assert.notNull(
                transferProgress,
                () -> new BException("NodeStepId 错误或当前 NodeStep 无正在传输的文件")
        );

        AbstractNodeJobVo.NodeJobTransferProgressVo nodeJobTransferProgressVo = new AbstractNodeJobVo.NodeJobTransferProgressVo()
                .setNodeTaskId(nodeTaskMeta.getId())
                .setNodeStepId(nodeStepMeta.getId())
                .setNodeId(nodeTaskMeta.getNodeId())
                .setHostname(nodeTaskMeta.getHostname())
                .setExecState(nodeJobMeta.getExecStateEnum())
                // 字节进度
                .setFileBytesProgressVo(new AbstractNodeJobVo.FileBytesProgressVo(
                        transferProgress.getTotalProgress(),
                        transferProgress.getTotalBytes(),
                        transferProgress.getTotalTransferBytes().get()
                ))
                // 文件个数进度
                .setFileCountProgressVo(new AbstractNodeJobVo.FileCountProgressVo(
                        transferProgress.getTotalFileCount(),
                        transferProgress.getTotalFileCountProgress(),
                        transferProgress.getTotalTransferFileCount().get()
                ))
                // 当前正在传输的文件进度信息
                .setCurrentFileProgressVo(new AbstractNodeJobVo.CurrentFileProgressVo(
                        transferProgress.getCurrentFileProgress().getFilename(),
                        transferProgress.getCurrentFileProgress().getFileBytes(),
                        transferProgress.getCurrentFileProgress().getFileTransferBytes().get(),
                        transferProgress.getCurrentFileProgress().getFileProgress(),
                        transferProgress.getCurrentFileProgress().getPrintSpeed()
                ));
        nodeJobTransferProgressDetailVo.setNodeJobTransferProgressVo(nodeJobTransferProgressVo);

        // 装配：List<FileProgressVo>
        // 获取文件路径集合
        Set<TransferProgress.FilePath> filePaths = transferProgress.getFileProgressMap().keySet();

        nodeJobTransferProgressDetailVo.setFileProgressList(
                filePaths.stream()
                        .map(filePath -> {
                            // 获取文件传输进度对象
                            TransferProgress.FileProgress fileProgress = transferProgress.get(filePath);

                            // 创建文件进度对象并返回
                            return new AbstractNodeJobVo.FileProgressVo()
                                    .setFileDir(filePath.getFileDir())
                                    .setFilename(filePath.getFilename())
                                    .setFileBytes(fileProgress.getFileBytes())
                                    .setFileTransferBytes(fileProgress.getFileTransferBytes().get())
                                    .setFileProgress(fileProgress.getFileProgress())
                                    .setSpeed(fileProgress.getPrintSpeed());

                        })
                        .collect(Collectors.toList())
        );

        return Result.success(nodeJobTransferProgressDetailVo);
    }

    /**
     * Description: 获取节点作业日志列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId  集群 ID
     * @param nodeJobId  节点作业 ID
     * @param nodeTaskId 任务 ID
     * @param nodeStepId 步骤 ID
     * @return Result<AbstractJobVo.JobLogListVo> 日志信息列表
     */
    public Result<AbstractNodeJobVo.NodeJobLogListVo> getNodeJobLogList(Long clusterId,
                                                                        Long nodeJobId,
                                                                        Long nodeTaskId,
                                                                        Long nodeStepId) {

        LambdaQueryChainWrapper<TDlNodeJobLog> tDlNodeJobLogWrapper = this.tDlNodeJobLogService.lambdaQuery()
                .select()
                .eq(TDlNodeJobLog::getClusterId, clusterId)
                .eq(TDlNodeJobLog::getNodeJobId, nodeJobId);

        if (nodeTaskId != null) {
            tDlNodeJobLogWrapper = tDlNodeJobLogWrapper.eq(TDlNodeJobLog::getNodeTaskId, nodeTaskId);
        }

        if (nodeStepId != null) {
            tDlNodeJobLogWrapper = tDlNodeJobLogWrapper.eq(TDlNodeJobLog::getNodeStepId, nodeStepId);
        }

        List<TDlNodeJobLog> tDlNodeJobLogList = tDlNodeJobLogWrapper.list();
        String tag = tDlNodeJobLogList.isEmpty() ? null : tDlNodeJobLogList.get(0).getTag();

        List<AbstractNodeJobVo.NodeJobLogVo> nodeJobLogList = tDlNodeJobLogList
                .stream()
                .map(i -> new AbstractNodeJobVo.NodeJobLogVo(
                                i.getNodeJobId(),
                                i.getNodeTaskId(),
                                i.getNodeStepId(),
                                i.getLogStdout(),
                                i.getLogErrout()
                        )
                )
                .collect(Collectors.toList());

        AbstractNodeJobVo.NodeJobLogListVo nodeJobLogListVo = new AbstractNodeJobVo.NodeJobLogListVo(
                clusterId,
                tag,
                nodeJobLogList
        );

        return Result.success(nodeJobLogListVo);
    }

    /**
     * Description: 从数据库中恢复 NodeJobCacheBean
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeJobId 节点任务 ID
     * @return NodeJobCacheBean
     */
    private NodeJobCacheBean getJobCacheBeanFromDb(Long nodeJobId) {

        TDlNodeJob tDlNodeJob = this.tDlNodeJobService.getById(nodeJobId);
        Assert.notNull(
                tDlNodeJob,
                () -> new BException("不存在的 NodeJobId")
        );

        List<TDlNodeTask> tDlNodeTaskList = this.tDlNodeTaskService.lambdaQuery()
                .select()
                .eq(TDlNodeTask::getNodeJobId, nodeJobId)
                .orderByAsc(TDlNodeTask::getNum)
                .list();

        List<TDlNodeStep> tDlNodeStepList = this.tDlNodeStepService.lambdaQuery()
                .select()
                .eq(TDlNodeStep::getNodeJobId, nodeJobId)
                .orderByAsc(TDlNodeStep::getNum)
                .list();

        // 获取 NodeJobMeta 相关信息
        final NodeJobMeta nodeJobMeta = this.recoverNodeJobMeta(
                tDlNodeJob,
                tDlNodeTaskList,
                tDlNodeStepList
        );

        // 获取 NodePlan 相关信息
        String planName = CollUtil.getLast(tDlNodeStepList).getNodeStepName();
        int planTotal = tDlNodeStepList.size();
        int planCurrent = tDlNodeStepList.size();
        int planProgress = 100;

        int execTotal = tDlNodeStepList.size();
        int execCurrent = (int) tDlNodeStepList.stream().filter(i -> i.getNodeStepState() == ExecStateEnum.OK).count();
        int execProgress = execCurrent * 100 / execTotal;

        final NodePlan nodePlan = new NodePlan(
                planName,
                planTotal,
                planCurrent,
                planProgress,
                execTotal,
                execCurrent,
                execProgress
        );

        NodeJobCacheBean nodeJobCacheBean = new NodeJobCacheBean(nodeJobMeta, nodePlan);

        // 重新加载到内存
        NodeJobCacheUtil.getInstance().cache(nodeJobCacheBean);

        return nodeJobCacheBean;
    }


    /**
     * Description: 从数据库中恢复 NodeJobMeta 信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param tDlNodeJob      数据库 NodeJob 实体
     * @param tDlNodeTaskList 数据库 NodeTask 实体列表
     * @param tDlNodeStepList 数据库 NodeStep 实体列表
     * @return JobMeta Job 元数据信息
     */
    private NodeJobMeta recoverNodeJobMeta(TDlNodeJob tDlNodeJob,
                                           List<TDlNodeTask> tDlNodeTaskList,
                                           List<TDlNodeStep> tDlNodeStepList) {

        NodeJobMeta nodeJobMeta = new NodeJobMeta()
                .setId(tDlNodeJob.getId())
                .setClusterId(tDlNodeJob.getClusterId())
                .setTag(tDlNodeJob.getTag())
                .setNodeActionTypeEnum(tDlNodeJob.getNodeActionType())
                .setName(tDlNodeJob.getNodeJobName())
                .setExecStateEnum(tDlNodeJob.getNodeJobState())
                .setNodeJobResult(new NodeJobMeta.NodeJobResult(tDlNodeJob.getNodeJobState() == ExecStateEnum.OK))
                .setNodeTaskMetaMap(new LinkedHashMap<>());

        nodeJobMeta.setStartTime(tDlNodeJob.getStartTime());
        nodeJobMeta.setEndTime(tDlNodeJob.getEndTime());

        tDlNodeTaskList.stream()
                .filter(i -> i.getNodeJobId() == nodeJobMeta.getId())
                .forEach(tDlNodeTask -> {
                            NodeTaskMeta nodeTaskMeta = this.recoverNodeTaskMeta(
                                    nodeJobMeta,
                                    tDlNodeTask,
                                    tDlNodeStepList
                            );

                            nodeJobMeta.getNodeTaskMetaMap().put(nodeTaskMeta.getNodeId(), nodeTaskMeta);

                        }
                );


        return nodeJobMeta;
    }

    /**
     * Description: 从数据库中恢复 NodeJobMeta 信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeJobMeta     NodeJob 元数据信息
     * @param tDlNodeTask     数据库 NodeTask 实体
     * @param tDlNodeStepList 数据库 NodeStep 实体列表
     * @return JobMeta Job 元数据信息
     */
    private NodeTaskMeta recoverNodeTaskMeta(NodeJobMeta nodeJobMeta,
                                             TDlNodeTask tDlNodeTask,
                                             List<TDlNodeStep> tDlNodeStepList) {

        NodeTaskMeta nodeTaskMeta = new NodeTaskMeta()
                .setNodeJobMeta(nodeJobMeta)
                .setId(tDlNodeTask.getId())
                .setStartState(tDlNodeTask.getNodeStartState())
                .setFailState(tDlNodeTask.getNodeFailState())
                .setSuccessState(tDlNodeTask.getNodeSuccessState())
                .setCurrentNodeState(tDlNodeTask.getNodeCurrentState())
                .setWait(tDlNodeTask.getIsWait())
                .setName(tDlNodeTask.getNodeTaskName())
                .setHostname(tDlNodeTask.getHostname())
                .setSshPort(Integer.valueOf(tDlNodeTask.getSshPort()))
                .setPrivateKeyPath(tDlNodeTask.getPrivateKeyPath())
                .setNodeIp(tDlNodeTask.getNodeIp())
                .setNodeId(tDlNodeTask.getNodeId())
                .setNodeTaskStateEnum(tDlNodeTask.getNodeTaskState())
                .setNodeActionTypeEnum(tDlNodeTask.getNodeActionType())
                .setNodeTaskResult(new NodeTaskMeta.NodeTaskResult(tDlNodeTask.getNodeTaskState() == ExecStateEnum.OK))
                .setNodeStepMetaMap(new LinkedHashMap<>());

        nodeTaskMeta.setNum(tDlNodeTask.getNum());
        nodeTaskMeta.setStartTime(tDlNodeTask.getStartTime());
        nodeTaskMeta.setEndTime(tDlNodeTask.getEndTime());

        tDlNodeStepList.forEach(tDlNodeStep -> {
                    NodeStepMeta nodeStepMeta = this.recoverNodeStepMeta(
                            nodeTaskMeta,
                            tDlNodeStep
                    );
                    nodeTaskMeta.getNodeStepMetaMap().put(nodeStepMeta.getId(), nodeStepMeta);
                }
        );

        return nodeTaskMeta;
    }

    /**
     * Description: 从数据库中恢复 NodeStepMeta 信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeTaskMeta NodeTask 元数据信息
     * @param tDlNodeStep  数据库 NodeStep 实体
     * @return JobMeta Job 元数据信息
     */
    private NodeStepMeta recoverNodeStepMeta(NodeTaskMeta nodeTaskMeta,
                                             TDlNodeStep tDlNodeStep) {

        NodeStepMeta nodeStepMeta = new NodeStepMeta()
                .setNodeTaskMeta(nodeTaskMeta)
                .setId(tDlNodeStep.getId())
                .setType(tDlNodeStep.getNodeStepType())
                .setName(tDlNodeStep.getNodeStepName())
                .setShell(tDlNodeStep.getShell())
                .setArgs(NodeStepMeta.str2List(tDlNodeStep.getArgs()))
                .setInteractions(NodeStepMeta.str2List(tDlNodeStep.getInteractions()))
                .setExits(Integer.valueOf(tDlNodeStep.getExits()))
                .setTimeout(tDlNodeStep.getTimeout())
                .setSleep(tDlNodeStep.getSleep())
                .setExecStateEnum(tDlNodeStep.getNodeStepState())
                .setNodeStepResult(new NodeStepMeta.NodeStepResult(tDlNodeStep.getNodeStepState() == ExecStateEnum.OK));

        if (tDlNodeStep.getTotalBytes() != null && tDlNodeStep.getTotalBytes() != 0L) {
            nodeStepMeta.setTransferProgress(
                    new TransferProgress()
                            .setTotalBytes(tDlNodeStep.getTotalBytes())
                            .setTotalProgress(tDlNodeStep.getTotalProgress())
                            .setTotalTransferBytes(new AtomicLong(tDlNodeStep.getTotalTransferBytes()))

                            .setTotalFileCount(tDlNodeStep.getTotalFileCount())
                            .setTotalFileCountProgress(tDlNodeStep.getTotalFileCountProgress())
                            .setTotalTransferFileCount(new AtomicLong(tDlNodeStep.getTotalTransferFileCount()))

                            .setCurrentFileProgress(new TransferProgress.FileProgress().setFilename(tDlNodeStep.getCurrentTransferFileName()))
            );
        }

        nodeStepMeta.setNum(tDlNodeStep.getNum());
        nodeStepMeta.setStartTime(tDlNodeStep.getStartTime());
        nodeStepMeta.setEndTime(nodeStepMeta.getEndTime());

        return nodeStepMeta;
    }

    /**
     * Description: 检查是否存在异常状态的 NodeJob，若存在，则恢复
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
    public void checkNodeJobState() {

    }

}
