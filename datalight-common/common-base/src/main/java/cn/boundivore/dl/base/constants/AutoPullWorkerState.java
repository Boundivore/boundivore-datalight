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
package cn.boundivore.dl.base.constants;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Worker 自动拉起开关缓存
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class AutoPullWorkerState {

    public static final ConcurrentHashMap<Long, AutoPullWorkerState.CacheBean> AUTO_PULL_WORKER_CACHE = new ConcurrentHashMap<>();

    /**
     * Description: 设置自动拉起 Worker 开关缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param cacheBean Worker 自动拉起开关状态
     */
    public static void putAutoPullWorkerState(AutoPullWorkerState.CacheBean cacheBean) {
        AUTO_PULL_WORKER_CACHE.put(cacheBean.getClusterId(), cacheBean);
    }

    /**
     * Description: 获取缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     */
    public static AutoPullWorkerState.CacheBean getAutoPullWorkerState(Long clusterId) {
        return AUTO_PULL_WORKER_CACHE.get(clusterId);
    }

    @Getter
    public static final class CacheBean {

        @Setter
        private long clusterId;

        private boolean autoPullWorker = true;

        private long closeAutoPullDurationWorker = 10 * 60 * 1000L;

        private long closeAutoPullBeginTimeWorker;

        private long closeAutoPullEndTimeWorker;

        /**
         * Description: 设置关闭 Worker 进程自动拉起开关状态的持续时间，以及开关状态
         * Created by: Boundivore
         * E-mail: boundivore@foxmail.com
         * Creation time: 2024/3/27
         * Modification description:
         * Modified by:
         * Modification time:
         * Throws:
         *
         * @param autoPullWorker 开光状态
         * @param duration       关闭持续时间
         */
        public void updatePullWorker(boolean autoPullWorker, long duration) {
            this.autoPullWorker = autoPullWorker;

            this.closeAutoPullDurationWorker = duration;
            this.closeAutoPullBeginTimeWorker = System.currentTimeMillis();
            this.closeAutoPullEndTimeWorker = closeAutoPullBeginTimeWorker + closeAutoPullDurationWorker;
        }

    }
}
