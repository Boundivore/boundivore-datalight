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

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.manage.node.job.NodeJob;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: 用于缓存 Job 实例
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class JobCache {

    private static volatile JobCache instance;
    private static final Object lock = new Object();

    //<JobId, Job>
    private final Cache<Long, Job> cache;

    private final ReentrantLock activeJobLock;

    @Getter
    private final AtomicLong activeJobId = new AtomicLong();

    private JobCache() {
        this.activeJobLock = new ReentrantLock();

        //内存中最多缓存 2个 Job，一个为当前活跃的 Job，另一个为上一个历史 Jon，便于查询
        this.cache = CacheUtil.newFIFOCache(2);
        this.cache.setListener((key, job) ->
                log.info("Job 缓存清除: {}({})",
                        job.getJobMeta().getName(),
                        job.getJobMeta().getId()
                )
        );
    }

    public static JobCache getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new JobCache();
                }
            }
        }
        return instance;
    }

    /**
     * Description: 缓存 Job
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param job 执行操作的异步 Job
     * @return boolean 如果符合缓存逻辑，则缓存并返回 true ，否则返回 false
     */
    public boolean cache(Job job) {
        if (!cache.isFull()) {
            cache.put(job.getJobMeta().getId(), job);
            return true;
        } else if (this.clearExpiration()) {
            cache.put(job.getJobMeta().getId(), job);
            return true;
        }

        Assert.isTrue(
                false,
                () -> new BException("缓存异步任务信息失败")
        );

        return false;
    }

    /**
     * Description: 根据 JobId 获取缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param jobId 全局唯一的 JobId
     * @return Job
     */
    public Job get(Long jobId) {
        Job job = cache.get(jobId);
        Assert.notNull(
                job,
                () -> new BException("查询任务已过期，请通过历史查询接口查询")
        );
        return cache.get(jobId);
    }

    /**
     * Description: 清除缓存中已经结束的 Job
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return boolean 如何成功腾出空间，则返回 true，否则返回 false
     */
    private boolean clearExpiration() {
        List<Long> onRemoveJobIdList = new ArrayList<>();

        Iterator<CacheObj<Long, Job>> iterator = cache.cacheObjIterator();
        while (iterator.hasNext()) {
            CacheObj<Long, Job> next = iterator.next();
            if (next.getValue().getJobMeta().getExecStateEnum() != ExecStateEnum.RUNNING) {
                onRemoveJobIdList.add(next.getKey());
            }
        }


        onRemoveJobIdList.forEach(cache::remove);

        return !onRemoveJobIdList.isEmpty();
    }


    /**
     * Description: 尝试设置活跃的 Job ID
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Throws: InterruptedException 如果线程在指定时间内被中断，则抛出异常
     *
     * @param jobId   Job ID
     * @param timeout 超时时间
     * @param unit    超时时间单位
     * @return boolean 如果成功设置活跃的 Job ID，则返回 true，否则返回 false
     */
    public boolean setActiveJobId(Long jobId,
                                  long timeout,
                                  TimeUnit unit) throws InterruptedException {

        if (this.activeJobLock.tryLock(timeout, unit)) {
            try {
                if (activeJobId.get() == 0L) {
                    this.activeJobId.set(jobId);
                    return true;
                }
                return false;
            } finally {
                this.activeJobLock.unlock();
            }
        }
        return false;
    }

    /**
     * Description: 设置活跃的 Job ID
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Throws: InterruptedException 如果线程在指定时间内被中断，则抛出异常
     *
     * @param jobId Job ID
     * @return boolean 如果成功设置活跃的 Job ID，则返回 true，否则返回 false
     */
    public boolean setActiveJobId(Long jobId) throws InterruptedException {
        return this.setActiveJobId(jobId, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Description: 释放活跃的 Job ID
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8 10:23
     * Throws:
     *
     * @param jobId Job ID
     * @return boolean 如果成功释放活跃的 Job ID，则返回 true，否则返回 false
     */
    public boolean releaseActiveJobId(Long jobId) {
        Assert.isTrue(
                this.activeJobId.get() != 0L,
                () -> new BException("当前没有活跃的 Job")
        );

        Assert.isTrue(
                jobId != null && jobId != 0L,
                () -> new BException("JobId 不能为空")
        );

        return this.activeJobId.compareAndSet(jobId, 0L);
    }


}
