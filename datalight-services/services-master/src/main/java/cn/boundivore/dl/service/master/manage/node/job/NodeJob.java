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
package cn.boundivore.dl.service.master.manage.node.job;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.converter.INodeStepConverter;
import cn.boundivore.dl.service.master.manage.node.bean.NodeJobMeta;
import cn.boundivore.dl.service.master.manage.node.bean.NodeStepMeta;
import cn.boundivore.dl.service.master.manage.node.bean.NodeTaskMeta;
import cn.boundivore.dl.service.master.manage.node.task.INodeTask;
import cn.boundivore.dl.service.master.manage.node.task.impl.NodeTask;
import cn.boundivore.dl.service.master.resolver.ResolverYamlNode;
import cn.boundivore.dl.service.master.resolver.yaml.YamlNodeAction;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description: 为当前服务组装一个 NodeJob
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/30
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class NodeJob extends Thread {

    @Getter
    private NodeJobMeta nodeJobMeta;

    @Getter
    private final NodeIntention nodeIntention;

    @Getter
    private final NodePlan nodePlan;

    private final NodeJobService nodeJobService = SpringContextUtil.getBean(NodeJobService.class);

    private final INodeStepConverter iNodeStepConverter = SpringContextUtil.getBean(INodeStepConverter.class);

    private boolean isInit;

    public NodeJob(NodeIntention nodeIntention) {
        this.nodeIntention = nodeIntention;
        this.nodePlan = new NodePlan(this.nodeIntention);
    }


    /**
     * Description: 1、初始化各类异步任务 Meta 信息；2、根据初始化好的 Meta 信息，初始化执行计划
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return NodeJob
     */
    public NodeJob init() throws InterruptedException {
        this.initJobMeta();
        this.plan(this.nodeJobMeta);

        this.nodeJobService.updateNodeJobDatabase(this.nodeJobMeta);
        NodeJobCache.getInstance().cache(this);

        this.nodePlan.initExecTotal(this.nodeJobMeta);
        this.isInit = true;
        return this;
    }

    /**
     * Description: 初始化 NodeJobMeta 并组装 NodeTask, NodeStep
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    private void initJobMeta() throws InterruptedException {
        long nodeJobMetaId = IdWorker.getId();

        Assert.isTrue(
                NodeJobCache.getInstance().setActiveJobId(nodeJobMetaId),
                () -> new BException(
                        String.format(
                                "安全起见，不允许同时对集群节点进行变更，已有其他活跃的任务正在运行: %s",
                                NodeJobCache.getInstance().getActiveJobId()
                        )
                )
        );

        this.nodeJobMeta = new NodeJobMeta()
                .setTag(IdUtil.fastSimpleUUID())
                .setId(nodeJobMetaId)
                .setName(nodeIntention.getNodeActionTypeEnum().name())
                .setNodeTaskMetaMap(new LinkedHashMap<>())
                .setExecStateEnum(ExecStateEnum.SUSPEND)
                .setNodeJobResult(new NodeJobMeta.NodeJobResult(false))
                .setClusterId(nodeIntention.getClusterId())
                .setNodeActionTypeEnum(nodeIntention.getNodeActionTypeEnum());

        this.nodeIntention.getNodeList()
                .forEach(i -> {
                            final NodeTaskMeta nodeTaskMeta = this.initTaskMeta(this.nodeJobMeta, i);

                            this.nodeJobMeta.getNodeTaskMetaMap().put(
                                    nodeTaskMeta.getId(),
                                    nodeTaskMeta
                            );
                        }
                );
    }

    /**
     * Description: 组装节点异步 NodeTask 的元数据信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/30
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeJobMeta Job 元数据信息
     * @param node        节点意图信息
     * @return NodeTaskMeta
     */
    private NodeTaskMeta initTaskMeta(NodeJobMeta nodeJobMeta, NodeIntention.Node node) {

        YamlNodeAction.DataLight dataLight = ResolverYamlNode.NODE_INIT_YAML.getDataLight();

        //获取异步任务中，配置文件中设定的组件的状态变化值
        NodeStateEnum startState = null;
        NodeStateEnum failState = null;
        NodeStateEnum successState = null;

        for (YamlNodeAction.Action action : dataLight.getActions()) {
            if (action.getType() != this.nodeIntention.getNodeActionTypeEnum()) continue;

            startState = action.getStartState();
            failState = action.getFailState();
            successState = action.getSuccessState();
        }


        NodeStateEnum finalStartState = startState;
        NodeStateEnum finalFailState = failState;
        NodeStateEnum finalSuccessState = successState;

        NodeTaskMeta nodeTaskMeta = new NodeTaskMeta()
                .setNodeJobMeta(nodeJobMeta)
                .setId(IdWorker.getId())
                .setName(
                        String.format(
                                "%s:%s",
                                nodeJobMeta.getName(),
                                node.getHostname()
                        )
                )
                .setWait(node.isWait())
                .setId(IdWorker.getId())

                .setHostname(node.getHostname())
                .setSshPort(node.getSshPort())
                .setPrivateKeyPath(node.getPrivateKeyPath())
                .setNodeIp(node.getNodeIp())
                .setNodeId(node.getNodeId())

                .setNodeActionTypeEnum(nodeJobMeta.getNodeActionTypeEnum())

                .setStartState(finalStartState)
                .setFailState(finalFailState)
                .setSuccessState(finalSuccessState)

                .setNodeTaskStateEnum(ExecStateEnum.SUSPEND)
                .setNodeTaskResult(new NodeTaskMeta.NodeTaskResult(false))
                .setNodeStepMetaMap(new LinkedHashMap<>());

        //同一个 NodeTaskMeta 中，会根据 NodeActionTypeEnum，封装一组 NodeStepMeta
        List<NodeStepMeta> nodeStepMetaList = this.initNodeStepMeta(nodeTaskMeta);
        nodeStepMetaList.forEach(
                s -> nodeTaskMeta.getNodeStepMetaMap().put(s.getId(), s)
        );

        return nodeTaskMeta;
    }

    /**
     * Description: 组装节点异步 NodeStep 的元数据信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/30
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeTaskMeta 异步 NodeTask 的元数据信息
     * @return NodeStepMeta 元数据列表
     */
    private List<NodeStepMeta> initNodeStepMeta(NodeTaskMeta nodeTaskMeta) {

        YamlNodeAction.DataLight dataLight = ResolverYamlNode.NODE_INIT_YAML.getDataLight();
        List<YamlNodeAction.Action> actions = dataLight.getActions();

        YamlNodeAction.Action action = CollUtil.findOne(
                actions,
                i -> i.getType() == nodeTaskMeta.getNodeActionTypeEnum()
        );


        return action.getSteps()
                .stream()
                //转换器转换部分属性值，其余属性值通过 set 方法设定
                .map(i -> this.iNodeStepConverter.convert2NodeStepMeta(i)
                        .setNodeTaskMeta(nodeTaskMeta)

                        .setId(IdWorker.getId())
                        .setName(String.format(
                                        "%s:%s",
                                        nodeTaskMeta.getName(),
                                        i.getName()
                                )
                        )
                        .setExecStateEnum(ExecStateEnum.SUSPEND)
                        .setNodeStepResult(new NodeStepMeta.NodeStepResult(false)))
                .collect(Collectors.toList());
    }


    /**
     * Description: 根据元数据信息，为当前 NodeJob 生成任务执行计划
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeJobMeta 工作元数据信息
     */
    private void plan(NodeJobMeta nodeJobMeta) {
        nodeJobMeta.getNodeTaskMetaMap()
                .forEach((kTask, vTask) -> {
                            log.info(
                                    "Task: {}, Id: {}, Tag: {}",
                                    vTask.getName(),
                                    vTask.getId(),
                                    vTask.getNodeJobMeta().getTag()
                            );

                            INodeTask iNodeTask = new NodeTask(vTask);

                            this.nodePlan.offerTask(iNodeTask);

                            //更新计划执行进度
                            this.nodePlan.planProgress();
                        }
                );

    }

    /**
     * Description: 执行当前 NodeJob
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     */
    private void execute() {
        // 记录 NodeJob 起始时间
        this.nodeJobMeta.setStartTime(System.currentTimeMillis());

        // 更新当前作业的执行状态到内存缓存和数据库
        updateJobExecutionStatus(ExecStateEnum.RUNNING);

        List<Future<NodeTaskMeta>> taskFutureList = new ArrayList<>();

        for (INodeTask task : this.nodePlan.getTasks()) {
            NodeTaskMeta nodeTaskMeta = task.getNodeTaskMeta();

            try {
                // 如果任务需要等待完成，则等待所有任务完成
                if (!taskFutureList.isEmpty() && nodeTaskMeta.isWait()) {
                    this.waitAndCheckTasks(taskFutureList);
                }

                // 提交任务并获取任务的 Future 对象
                Future<NodeTaskMeta> future = this.nodeJobService.submit(task);
                taskFutureList.add(future);

            } catch (Exception e) {
                // 记录节点操作异常
                log.error(
                        "节点操作异常, Node: {}, {}",
                        nodeTaskMeta.getHostname(),
                        ExceptionUtil.stacktraceToString(e)
                );
            }
        }

        //最终检查一次所有任务
        boolean isAllSuccess = this.waitAndCheckTasks(taskFutureList);

        // 设置当前 NodeJob 作业的成功状态
        this.nodeJobMeta.getNodeJobResult().setSuccess(isAllSuccess);

        // 记录作业结束时间(自动计算耗时)
        this.nodeJobMeta.setEndTime(System.currentTimeMillis());

        // 输出作业结束信息
        log.info(
                "结束 NodeJob: {}, 耗时: {} ms",
                nodeJobMeta.getName(),
                nodeJobMeta.getDuration()
        );

        // 根据作业的成功状态，确定执行状态枚举
        ExecStateEnum execStateEnum = this.nodeJobMeta.getNodeJobResult().isSuccess() ?
                ExecStateEnum.OK :
                ExecStateEnum.ERROR;

        // 更新当前作业的执行状态到内存缓存和数据库
        this.updateJobExecutionStatus(execStateEnum);

        // 清除所有可能残留的异步任务
        this.nodePlan.clear();

        // 释放当前活动作业的标识符
        NodeJobCache.getInstance().releaseActiveNodeJobId();
    }

    /**
     * Description: 更新当前作业的执行状态到内存缓存和数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param execStateEnum 当前状态
     */
    private void updateJobExecutionStatus(ExecStateEnum execStateEnum) {
        // 更新当前作业的执行状态到内存缓存
        this.nodeJobService.updateJobMemory(this.nodeJobMeta, execStateEnum);
        // 更新当前作业的执行状态到数据库
        this.nodeJobService.updateNodeJobDatabase(this.nodeJobMeta);
    }


    /**
     * Description: 调用 job.start() 后，将按照执行计划开始执行当前 Job
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Override
    public void run() {
        Assert.isTrue(
                this.isInit,
                () -> new BException("执行 NodeJob 前需要先调用 init() 初始化任务计划")
        );

        this.execute();
    }

    /**
     * Description: 阻塞队列中的任务并等待结果，全部成功 返回 true，包含任何失败则返回 false
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param taskFutureList 已提交线程池的 Task 任务列表
     * @return 全部成功 返回 true，包含任何失败则返回 false
     */
    private boolean waitAndCheckTasks(List<Future<NodeTaskMeta>> taskFutureList) throws BException {
        //获取执行的任务中是否包含失败的任务
        List<NodeTaskMeta> hasFailedList = taskFutureList.stream()
                .map(i -> {
                    try {
                        return i.get(30, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        log.error(ExceptionUtil.stacktraceToString(e));
                        return null;
                    }
                })
                .filter(i -> i == null || !i.getNodeTaskResult().isSuccess())
                .collect(Collectors.toList());

        return hasFailedList.isEmpty();
    }
}
