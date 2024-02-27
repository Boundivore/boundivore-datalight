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

import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.manage.node.bean.NodeJobMeta;
import cn.boundivore.dl.service.master.manage.node.task.INodeTask;
import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 保存当前 Job 的执行计划
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class NodePlan {

    /**
     * 计划进度
     */
    //当前计划 Task 任务总数
    @Getter
    private final int planTotal;
    //当前计划已经组装的 Task 任务数
    @Getter
    private int planCurrent = 0;
    @Getter
    private int planProgress = 0;

    /**
     * 执行进度
     */
    private final ReentrantLock lock = new ReentrantLock();
    //当前计划 Task 任务总数
    @Getter
    private final AtomicInteger execTotal = new AtomicInteger(0);
    //当前计划已经执行的 Task 任务数
    @Getter
    private final AtomicInteger execCurrent = new AtomicInteger(0);
    @Getter
    private final AtomicInteger execProgress = new AtomicInteger(0);

    //正在制定中的计划名称
    @Getter
    private String planName;

    @Getter
    private final LinkedBlockingQueue<INodeTask> tasks = new LinkedBlockingQueue<>();

    public NodePlan(NodeIntention nodeIntention) {
        this.planTotal = this.initPlanTotal(nodeIntention);
        log.info("计划总数: {}", this.planTotal);
    }

    public NodePlan(String planName,
                int planTotal,
                int planCurrent,
                int planProgress,
                int execTotal,
                int execCurrent,
                int execProgress) {
        this.planName = planName;
        this.planTotal = planTotal;
        this.planCurrent = planCurrent;
        this.planProgress = planProgress;

        this.execTotal.set(execTotal);
        this.execCurrent.set(execCurrent);
        this.execProgress.set(execProgress);
    }

    /**
     * Description: 获取当前待生成的计划总数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return int 当前计划总数
     */
    private int initPlanTotal(NodeIntention nodeIntention) {
        final AtomicInteger planTotalInteger = new AtomicInteger(0);

        planTotalInteger.getAndAdd(nodeIntention.getNodeList().size());

        return planTotalInteger.get();
    }

    /**
     * Description: 获取当前待执行的 NodeStep 总数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public void initExecTotal(NodeJobMeta nodeJobMeta) {
        AtomicInteger execTotalInteger = new AtomicInteger(0);

        nodeJobMeta.getNodeTaskMetaMap()
                .forEach((sk, sv) -> execTotalInteger.getAndAdd(
                                sv.getNodeStepMetaMap().size()
                        )
                );

        this.execTotal.set(execTotalInteger.get());
        log.info("执行异步任务总数: {}", this.execTotal);
    }


    /**
     * Description: 向 NodeJob 中提交 NodeTask
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeTask 当前组装好的 nodeTask
     * @return 返回 Stage 队列
     */
    public LinkedBlockingQueue<INodeTask> offerTask(INodeTask nodeTask) {
        Assert.isTrue(
                this.tasks.offer(nodeTask),
                () -> new BException("NodeTask 队列已满")
        );

        return this.tasks;
    }

    /**
     * Description: 清空当前计划的任务队列
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public void clear() {
        this.tasks.clear();
    }

    /**
     * Description: 更新当前计划生成的进度
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public int planProgress() {
        this.planCurrent++;

        this.planProgress = (int) (this.planCurrent * 1.0F / this.planTotal * 100);

        log.info("计划总数: {}, 当前: {}, 进度: {}%", planTotal, planCurrent, planProgress);

        return this.planProgress;
    }

    /**
     * Description: 更新所有异步 NodeStep 执行进度
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public AtomicInteger execProgress(String planName) {
        lock.lock();
        try {
            this.planName = planName;
            this.execProgress.set(
                    this.execCurrent.incrementAndGet() * 100 / this.execTotal.get()
            );

            log.info("执行 Step 总数: {}, 当前: {}, 进度: {}%", execTotal.get(), execCurrent.get(), execProgress.get());

            return this.execCurrent;
        } finally {
            lock.unlock();
        }
    }
}
