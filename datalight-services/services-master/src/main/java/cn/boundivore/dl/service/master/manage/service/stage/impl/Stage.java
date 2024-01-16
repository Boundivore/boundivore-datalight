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
package cn.boundivore.dl.service.master.manage.service.stage.impl;

import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.manage.service.bean.StageMeta;
import cn.boundivore.dl.service.master.manage.service.bean.TaskMeta;
import cn.boundivore.dl.service.master.manage.service.stage.AbstractStage;
import cn.boundivore.dl.service.master.manage.service.task.ITask;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Description: 设计具体执行当前 Stage 下每一个 Task 的执行逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/30
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class Stage extends AbstractStage {

    public Stage(StageMeta stageMeta) {
        super(stageMeta);
    }

    @Override
    public void runTaskBatch() throws Exception {
        ITask task;

        ArrayList<Future<TaskMeta>> taskFutureList = new ArrayList<>();

        //循环逐个获取任务并执行，该过程可能会并发跨越多个节点执行每个 Task
        //相同 priority 并行，不同 priority 串行
        Long priority = null;

        while ((task = pollTask()) != null) {
            TaskMeta taskMeta = task.getTaskMeta();

            if (!taskFutureList.isEmpty() && taskMeta.getPriority() != priority || taskMeta.isWait()) {
                //该优先级的任务已经全部提交，需检查后，方能提交后续的任务
                this.waitAndCheckTasks(taskFutureList);
            }

            //缓存 Future 到队列，后续阻塞读取
            Future<TaskMeta> f = super.jobService.submit(task);
            taskFutureList.add(f);
            priority = taskMeta.getPriority();

            // 判断当前 Task 是否需要阻塞执行
            if (taskMeta.isBlock()) {
                this.blockAndCheckCurrentTask(f);
            }

        }

        //最终检查一次所有任务
        this.waitAndCheckTasks(taskFutureList);

    }


    /**
     * Description: 检查当前 Stage 中是否存在失败的 Task，如果存在，则抛出异常，终止后续执行
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8 17:30
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param taskFutureList 已提交线程池的 Task 任务列表
     */
    private void waitAndCheckTasks(List<Future<TaskMeta>> taskFutureList) throws BException {
        //获取执行的任务中是否包含失败的任务
        List<TaskMeta> hasFailedList = taskFutureList.stream()
                .map(i -> {
                    try {
                        return i.get();
                    } catch (Exception e) {
                        log.error(ExceptionUtil.stacktraceToString(e));
                        return null;
                    }
                })
                .filter(i -> i == null || !i.getTaskResult().isSuccess())
                .collect(Collectors.toList());

        //当前 Stage 中，如果存在任何一个 Task 执行失败，则直接抛出异常，以终止后续 Stage 的运行
        Assert.isTrue(
                hasFailedList.isEmpty(),
                () -> new BException(
                        String.format(
                                "Stage(%s) 中包含的 Task 执行失败, 请排查后重新尝试",
                                this.stageMeta.getName()
                        )
                )
        );
    }

    /**
     * Description: 检查当前 Task 是否成功，如果不成功，则抛出异常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8 17:30
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param currentTask 当前提交的任务
     */
    private void blockAndCheckCurrentTask(Future<TaskMeta> currentTask) throws BException {
        //获取执行的任务中是否包含失败的任务
        TaskMeta taskMeta = null;
        try {
            taskMeta = currentTask.get();
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }

        Assert.isTrue(
                taskMeta != null && taskMeta.getTaskResult().isSuccess(),
                () -> new BException(
                        String.format(
                                "Stage(%s) 中包含的 Task 执行失败, 请排查后重新尝试",
                                this.stageMeta.getName()
                        )
                )
        );

    }
}
