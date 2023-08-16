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
package cn.boundivore.dl.service.master.manage.node.task.impl;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.manage.node.bean.NodeStepMeta;
import cn.boundivore.dl.service.master.manage.node.bean.NodeTaskMeta;
import cn.boundivore.dl.service.master.manage.node.job.NodeJobCache;
import cn.boundivore.dl.service.master.manage.node.task.AbstractNodeTask;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 针对同一节点的串行 NodeStep 组成的任务
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class NodeTask extends AbstractNodeTask {

    public NodeTask(NodeTaskMeta nodeTaskMeta) {
        super(nodeTaskMeta);
        //将当前 NodeTask 中的所有 NodeStep 信息初始化到数据库
        this.nodeTaskMeta.getNodeStepMetaMap().forEach((k, v) -> {
            super.nodeJobService.updateNodeStepDatabase(v);
        });
    }

    @Override
    public void run() throws Exception {

        nodeTaskMeta.getNodeStepMetaMap().forEach((nodeStepId, nodeStepMeta) -> {

            //更新当前 NodeStep 执行状态到内存缓存和数据库
            this.updateStepExecutionStatus(nodeStepMeta, ExecStateEnum.RUNNING);

            String exceptionStr = "";

            try {
                //记录 Step 起始时间
                nodeStepMeta.setStartTime(System.currentTimeMillis());

                //执行指定 Step 的任务
                String output;
                switch (nodeStepMeta.getType()) {
                    case COMMAND:
                        output = super.command(nodeStepMeta);
                        break;
                    case SCRIPT:
                        output = super.script(nodeStepMeta);
                        break;
                    case SCAN:
                        output = super.scan(nodeStepMeta);
                        break;
                    case SCAN_RESOURCES:
                        output = super.scanResources(nodeStepMeta);
                        break;
                    case CHECK_ENV:
                        output = super.checkEnv(nodeStepMeta);
                        break;
                    case PUSH:
                        output = super.push(nodeStepMeta);
                        break;
                    default:
                        throw new BException(
                                String.format(
                                        "NodeStep 错误，未知的 NodeStep 类型: %s",
                                        nodeStepMeta.getType()
                                )
                        );
                }

                nodeStepMeta.getNodeStepResult().setSuccess(true);
                super.nodeJobService.saveLog(nodeStepMeta, output, "");
            } catch (Exception e) {
                exceptionStr = ExceptionUtil.stacktraceToString(e);
                super.nodeJobService.saveLog(nodeStepMeta, "", exceptionStr);
                log.error(exceptionStr);
            } finally {
                //如果 yaml 配置文件中发现当前步骤需要暂停等待进程初始化，则按照配置进行休眠
                if (nodeStepMeta.getSleep() > 0) {
                    ThreadUtil.safeSleep(nodeStepMeta.getSleep());
                }

                //获取 NodeJobCache 缓存键
                Long nodeJobId = nodeTaskMeta.getNodeJobMeta().getId();

                //更新执行进度到内存
                NodeJobCache.getInstance()
                        .get(nodeJobId)
                        .getNodePlan()
                        .execProgress(nodeStepMeta.getName());

                //记录 NodeStep 结束时间(自动计算耗时)
                nodeStepMeta.setEndTime(System.currentTimeMillis());

                log.info("Step: NodeStepName: {}, NodeStepType: {}, NodeAction: {}, Duration: {} ms",
                        nodeStepMeta.getName(),
                        nodeStepMeta.getType(),
                        nodeTaskMeta.getNodeActionTypeEnum(),
                        nodeStepMeta.getDuration()
                );


                ExecStateEnum execStateEnum = nodeStepMeta.getNodeStepResult().isSuccess() ?
                        ExecStateEnum.OK :
                        ExecStateEnum.ERROR;

                //更新当前 NodeStep 执行状态到内存缓存和数据库
                this.updateStepExecutionStatus(nodeStepMeta, execStateEnum);

                String finalExceptionStr = exceptionStr;
                Assert.isTrue(
                        nodeStepMeta.getNodeStepResult().isSuccess(),
                        () -> new BException(
                                String.format(
                                        "NodeStep 执行失败: %s, NodeStepType: %s, Details: %s",
                                        nodeStepMeta.getName(),
                                        nodeStepMeta.getType(),
                                        finalExceptionStr
                                )
                        )
                );
            }

        });
    }

    /**
     * Description: 更新当前 Step 执行状态到内存缓存和数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeStepMeta  当前 NodeStep
     * @param execStateEnum 当前状态
     */
    private void updateStepExecutionStatus(NodeStepMeta nodeStepMeta, ExecStateEnum execStateEnum) {
        // 更新当前作业的执行状态到内存缓存
        this.nodeJobService.updateNodeStepMemory(nodeStepMeta, execStateEnum);
        // 更新当前作业的执行状态到数据库
        this.nodeJobService.updateNodeStepDatabase(nodeStepMeta);
    }
}
