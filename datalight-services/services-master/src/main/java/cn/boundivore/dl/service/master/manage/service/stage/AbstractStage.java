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
package cn.boundivore.dl.service.master.manage.service.stage;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.manage.service.bean.StageMeta;
import cn.boundivore.dl.service.master.manage.service.job.JobService;
import cn.boundivore.dl.service.master.manage.service.task.ITask;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description: 单个节点上，针对单个服务的、一系列任务的封装，包括控制任务执行，任务元数据管理等
 * Stage 线程运行性质：同服务、不同组件（即不同 Task）、不同节点（即不同 Task）
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Getter
public abstract class AbstractStage implements IStage {
    //Stage 执行必要的元数据信息
    protected final StageMeta stageMeta;

    protected JobService jobService = SpringContextUtil.getBean(JobService.class);

    //保存当前阶段的一系列 Task
    private final LinkedBlockingQueue<ITask> tasks = new LinkedBlockingQueue<>();


    public AbstractStage(@NotNull StageMeta stageMeta) {
        this.stageMeta = stageMeta;
        this.jobService.updateStageDatabase(this.stageMeta);
    }


    @Override
    public StageMeta call() throws Exception {
        try {
            //记录 Stage 起始时间
            this.stageMeta.setStartTime(System.currentTimeMillis());

            //更新当前 Stage 执行状态到内存缓存和数据库
            this.updateStageExecutionStatus(ExecStateEnum.RUNNING);

            //执行前：变更当前服务起始状态到数据库
            this.stageMeta.setCurrentState(SCStateEnum.CHANGING);
            this.jobService.switchServiceState(this.stageMeta);

            this.runTaskBatch();
            this.success();
        } catch (BException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            this.fail();
        } finally {
            ExecStateEnum execStateEnum = this.stageMeta.getStageResult().isSuccess() ?
                    ExecStateEnum.OK :
                    ExecStateEnum.ERROR;

            //执行后：变更当前服务起始状态到数据库
            this.jobService.switchServiceState(this.stageMeta);

            //记录 Stage 结束时间(自动计算耗时)
            stageMeta.setEndTime(System.currentTimeMillis());
            log.info("Stage: StageName: {}, ServiceName: {}, Duration: {} ms",
                    stageMeta.getName(),
                    stageMeta.getServiceName(),
                    stageMeta.getDuration()
            );

            //更新当前 Stage 执行状态到内存缓存和数据库
            this.updateStageExecutionStatus(execStateEnum);
        }

        return this.stageMeta;
    }


    @Override
    public StageMeta.StageResult success() {
        this.stageMeta.getStageResult().setSuccess(true);
        this.stageMeta.setCurrentState(SCStateEnum.DEPLOYED);
        return this.stageMeta.getStageResult();
    }

    @Override
    public StageMeta.StageResult fail() {
        this.stageMeta.getStageResult().setSuccess(false);

        this.stageMeta.setCurrentState(
                this.jobService.determineServiceStateViaComponent(
                        this.stageMeta.getJobMeta().getClusterMeta().getCurrentClusterId(),
                        this.stageMeta.getServiceName()
                )
        );

        return this.stageMeta.getStageResult();
    }

    @Override
    public LinkedBlockingQueue<ITask> offerTask(ITask task) {
        Assert.isTrue(
                tasks.offer(task),
                () -> new BException(
                        String.format(
                                "Task 保存失败, 名称: %s, 节点: %s",
                                task.getTaskMeta().getName(),
                                task.getTaskMeta().getHostname()
                        )
                )
        );
        return this.tasks;
    }

    @Override
    public ITask pollTask() {
        return tasks.poll();
    }

    @Override
    public StageMeta getStageMeta() {
        return this.stageMeta;
    }

    /**
     * Description: 更新当前 Stage 执行状态到内存缓存和数据库
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
    private void updateStageExecutionStatus(ExecStateEnum execStateEnum) {
        // 更新当前作业的执行状态到内存缓存
        this.jobService.updateStageMemory(this.stageMeta, execStateEnum);
        // 更新当前作业的执行状态到数据库
        this.jobService.updateStageDatabase(this.stageMeta);
    }
}
