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
package cn.boundivore.dl.service.master.manage.service.task.impl;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.manage.service.bean.StepMeta;
import cn.boundivore.dl.service.master.manage.service.bean.TaskMeta;
import cn.boundivore.dl.service.master.manage.service.job.JobCache;
import cn.boundivore.dl.service.master.manage.service.task.AbstractTask;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 针对同一节点、同一组件的串行 Step 组成的任务
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/24
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class Task extends AbstractTask {

    public Task(TaskMeta taskMeta) {
        super(taskMeta);
        //将当前 Task 中的所有 Step 信息初始化到数据库
        this.taskMeta.getStepMetaMap().forEach((k, v) -> {
            super.jobService.updateStepDatabase(v);
        });
    }

    @Override
    public void run() throws Exception {

        taskMeta.getStepMetaMap().forEach((stepId, stepMeta) -> {

            String exceptionStr = "";

            try {
                //记录 Step 起始时间
                stepMeta.setStartTime(System.currentTimeMillis());

                //更新当前 Step 执行状态到内存缓存和数据库
                this.updateStepExecutionStatus(stepMeta, ExecStateEnum.RUNNING);

                //执行指定 Step 的任务
                String output;
                switch (stepMeta.getType()) {
                    case COMMAND:
                        output = super.command(stepMeta);
                        break;
                    case SCRIPT:
                        output = super.script(stepMeta);
                        break;
                    case COMMON_SCRIPT:
                        output = super.commonScript(stepMeta);
                        break;
                    case JAR:
                        output = super.jar(stepMeta);
                        break;
                    default:
                        throw new BException(
                                String.format(
                                        "Step 错误，未知的 Step 类型: %s",
                                        stepMeta.getType()
                                )
                        );
                }
                stepMeta.getStepResult().setSuccess(true);
                super.jobService.saveLog(stepMeta, output, "");
            } catch (Exception e) {
                exceptionStr = ExceptionUtil.stacktraceToString(e);
                super.jobService.saveLog(stepMeta, "", exceptionStr);
                log.error(exceptionStr);
            } finally {
                //如果 yaml 配置文件中发现当前步骤需要暂停等待进程初始化，则按照配置进行休眠
                if (stepMeta.getSleep() > 0) {
                    ThreadUtil.safeSleep(stepMeta.getSleep());
                }

                //获取 JobCache 缓存键
                Long jobId = taskMeta.getStageMeta().getJobMeta().getId();
                //更新执行进度到内存
                JobCache.getInstance()
                        .get(jobId)
                        .getPlan()
                        .execProcess(stepMeta.getName());

                ExecStateEnum execStateEnum = stepMeta.getStepResult().isSuccess() ?
                        ExecStateEnum.OK :
                        ExecStateEnum.ERROR;

                //记录 Step 结束时间(自动计算耗时)
                stepMeta.setEndTime(System.currentTimeMillis());

                log.info("Step: StepName: {}, StepType: {}, Action: {}, Duration: {} ms",
                        stepMeta.getName(),
                        stepMeta.getType(),
                        taskMeta.getActionTypeEnum(),
                        stepMeta.getDuration()
                );

                //更新当前 Step 执行状态到内存缓存和数据库
                this.updateStepExecutionStatus(stepMeta, execStateEnum);

                String finalExceptionStr = exceptionStr;
                Assert.isTrue(
                        stepMeta.getStepResult().isSuccess(),
                        () -> new BException(
                                String.format(
                                        "Step 执行失败: %s, StepType: %s, Details: %s",
                                        stepMeta.getName(),
                                        stepMeta.getType(),
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
     * @param stepMeta      StepMeta 元数据信息
     * @param execStateEnum 当前状态
     */
    private void updateStepExecutionStatus(StepMeta stepMeta, ExecStateEnum execStateEnum) {
        // 更新当前作业的执行状态到内存缓存
        this.jobService.updateStepMemory(stepMeta, execStateEnum);
        // 更新当前作业的执行状态到数据库
        this.jobService.updateStepDatabase(stepMeta);
    }
}
