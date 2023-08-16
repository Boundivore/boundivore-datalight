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
package cn.boundivore.dl.starter.redis.lock;

import java.util.concurrent.TimeUnit;

/**
 * Description: IDistributedLock
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/18
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public interface IDistributedLock {
    String LOCK_KEY_PREFIX = "LOCK_KEY";

    ZLock lock(String key, long leaseTime, TimeUnit unit, boolean isFair) throws Exception;

    default ZLock lock(String key, long leaseTime, TimeUnit unit) throws Exception {
        return this.lock(key, leaseTime, unit, false);
    }
    default ZLock lock(String key, boolean isFair) throws Exception {
        return this.lock(key, -1, null, isFair);
    }
    default ZLock lock(String key) throws Exception {
        return this.lock(key, -1, null, false);
    }

    ZLock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, boolean isFair) throws Exception;

    default ZLock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws Exception {
        return this.tryLock(key, waitTime, leaseTime, unit, false);
    }
    default ZLock tryLock(String key, long waitTime, TimeUnit unit, boolean isFair) throws Exception {
        return this.tryLock(key, waitTime, -1, unit, isFair);
    }
    default ZLock tryLock(String key, long waitTime, TimeUnit unit) throws Exception {
        return this.tryLock(key, waitTime, -1, unit, false);
    }

    void unlock(Object lock) throws Exception;

    default void unlock(ZLock ZLock) throws Exception {
        if (ZLock != null) {
            this.unlock(ZLock.getLock());
        }
    }
}
