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
package cn.boundivore.dl.service.master.manage.service.job;

import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.manage.service.bean.JobMeta;
import cn.boundivore.dl.service.master.manage.service.stage.IStage;
import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
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
public class Plan implements Serializable {
    private static final long serialVersionUID = 6310575816335634033L;

    /**
     * 计划进度
     */
    //当前计划 Step 任务总数
    @Getter
    private final int planTotal;
    //当前计划已经组装的 Step 任务数
    @Getter
    private int planCurrent = 0;
    @Getter
    private int planProgress = 0;

    @Getter
    private String planName;

    /**
     * 执行进度
     */
    private transient final ReentrantLock lock = new ReentrantLock();

    //当前计划 Step 任务总数
    @Getter
    private final AtomicInteger execTotal = new AtomicInteger(0);
    //当前计划已经执行的 Step 任务数
    @Getter
    private final AtomicInteger execCurrent = new AtomicInteger(0);
    @Getter
    private final AtomicInteger execProgress = new AtomicInteger(0);

    @Getter
    private transient final LinkedBlockingQueue<IStage> stages = new LinkedBlockingQueue<>();

    public Plan(Intention intention) {
        //考虑到组装逻辑和创建任务实例为两个独立的过程，因此，总进度乘以 2 可以控制的更精确
        this.planTotal = this.initPlanTotal(intention) * 2;
        log.info("计划总数: {}", this.planTotal);
    }

    public Plan(String planName,
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
    private int initPlanTotal(Intention intention) {
        final AtomicInteger planTotalInteger = new AtomicInteger(0);

        for (Intention.Service service : intention.getServiceList()) {
            for (Intention.Component component : service.getComponentList()) {
                planTotalInteger.getAndAdd(component.getNodeList().size());
            }
        }

        return planTotalInteger.get();
    }

    /**
     * Description: 获取当前待执行的 Step 总数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12 17:07
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public void initExecTotal(JobMeta jobMeta) {
        AtomicInteger execTotalInteger = new AtomicInteger(0);

        jobMeta.getStageMetaMap().forEach((sk, sv) -> {
            sv.getTaskMetaMap().forEach((tk, tv) -> {
                execTotalInteger.getAndAdd(tv.getStepMetaMap().size());
            });
        });

        this.execTotal.set(execTotalInteger.get());
        log.info("执行异步任务总数: {}", this.execTotal);
    }


    /**
     * Description: 向计划中提交 Stage
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9 14:36
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param stage 当前组装好的 Stage
     * @return 返回 Stage 队列
     */
    public LinkedBlockingQueue<IStage> offerStage(IStage stage) {
        Assert.isTrue(
                this.stages.offer(stage),
                () -> new BException("Job 队列已满")
        );

        return this.stages;
    }

    /**
     * Description: 清空当前计划的任务队列
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/9 14:46
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public void clear() {
        this.stages.clear();
    }

    /**
     * Description: 更新当前计划生成的进度
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12 16:57
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
     * Description: 更新所有异步 Step 执行进度
     * TODO 如果追求更快的并发执行速度，可不在此处更新总进度，而是在 Step 完成后向 Map 集合 put <StepId, 1>
     *     最终前端每次调用接口时，通过 count 1 实现 Step 完成数和进度的统计并返回；
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12 16:57
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public AtomicInteger execProcess(String planName) {
        lock.lock();
        try {
            this.planName = planName;
            this.execProgress.set(
                    this.execCurrent.incrementAndGet() * 100 / this.execTotal.get()
            );

            log.info("执行 Step 总数: {}, 当前执行完毕: {}, 进度: {}%",
                    execTotal.get(),
                    execCurrent.get(),
                    execProgress.get()
            );

            return this.execCurrent;
        } finally {
            lock.unlock();
        }
    }
}
