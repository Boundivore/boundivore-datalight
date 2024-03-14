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

import cn.boundivore.dl.base.constants.Constants;
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.manage.node.bean.NodeJobCacheBean;
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
public class NodeJobCacheUtil {

    private static volatile NodeJobCacheUtil instance;
    private static final Object lock = new Object();

    //<JobId, Job>
    private final Cache<Long, NodeJobCacheBean> cache;

    private final ReentrantLock activeJobLock;

    @Getter
    private final AtomicLong activeNodeJobId = new AtomicLong(0L);

    private NodeJobCacheUtil() {
        this.activeJobLock = new ReentrantLock();

        // 内存中缓存若干 NodeJob
        this.cache = CacheUtil.newFIFOCache(Constants.CACHE_CAPACITY);
        this.cache.setListener((key, nodeJobCacheBean) ->
                log.info("NodeJobCacheBean 缓存清除: {}({})",
                        nodeJobCacheBean.getNodeJobMeta().getName(),
                        nodeJobCacheBean.getNodeJobMeta().getId()
                )
        );
    }

    public static NodeJobCacheUtil getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new NodeJobCacheUtil();
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
     * @param nodeJobCacheBean 执行异步操作的 NodeJob 缓存
     * @return boolean 如果符合缓存逻辑，则缓存并返回 true ，否则返回 false
     */
    public boolean cache(NodeJobCacheBean nodeJobCacheBean) {
        if (!cache.isFull()) {
            cache.put(nodeJobCacheBean.getNodeJobMeta().getId(), nodeJobCacheBean);
            return true;
        } else if (this.clearExpiration()) {
            cache.put(nodeJobCacheBean.getNodeJobMeta().getId(), nodeJobCacheBean);
            return true;
        }

        Assert.isTrue(
                false,
                () -> new BException("缓存节点任务信息失败")
        );

        return false;
    }

    /**
     * Description: 根据 NodeJobId 获取缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeJobId 全局唯一的 nodeJobId
     * @return NodeJobCacheBean
     */
    public NodeJobCacheBean get(Long nodeJobId) {
        return cache.get(nodeJobId);
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

        Iterator<CacheObj<Long, NodeJobCacheBean>> iterator = cache.cacheObjIterator();
        while (iterator.hasNext()) {
            CacheObj<Long, NodeJobCacheBean> next = iterator.next();
            ExecStateEnum execStateEnum = next.getValue().getNodeJobMeta().getExecStateEnum();
            if (execStateEnum != ExecStateEnum.RUNNING && execStateEnum != ExecStateEnum.SUSPEND) {
                onRemoveJobIdList.add(next.getKey());
            }
        }


        onRemoveJobIdList.forEach(cache::remove);

        return !onRemoveJobIdList.isEmpty();
    }


    /**
     * Description: 尝试设置活跃的 nodeJobId
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Throws: InterruptedException 如果线程在指定时间内被中断，则抛出异常
     *
     * @param nodeJobId nodeJobId
     * @param timeout   超时时间
     * @param unit      超时时间单位
     * @return boolean 如果成功设置活跃的 Job ID，则返回 true，否则返回 false
     */
    public boolean setActiveNodeJobId(Long nodeJobId,
                                      long timeout,
                                      TimeUnit unit) throws InterruptedException {

        if (this.activeJobLock.tryLock(timeout, unit)) {
            try {
                if (activeNodeJobId.get() == 0L) {
                    this.activeNodeJobId.set(nodeJobId);
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
     * Description: 设置活跃的 nodeJobId
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Throws: InterruptedException 如果线程在指定时间内被中断，则抛出异常
     *
     * @param nodeJobId Job ID
     * @return boolean 如果成功设置活跃的 nodeJobId，则返回 true，否则返回 false
     */
    public boolean setActiveNodeJobId(Long nodeJobId) throws InterruptedException {
        return this.setActiveNodeJobId(nodeJobId, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Description: 释放活跃的 nodeJobId
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/8
     * Throws:
     *
     */
    public void releaseActiveNodeJobId() {
        this.activeNodeJobId.set(0L);
    }
}
