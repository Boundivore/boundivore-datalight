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

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Description: RedisToolsConstants
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/18
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@ConditionalOnClass(RedissonClient.class)
@ConditionalOnProperty(prefix = "data.light.lock", name = "type", havingValue = "REDIS", matchIfMissing = true)
public class RedissonDistributedLock implements IDistributedLock {
    @Resource
    private RedissonClient redisson;

    private ZLock getLock(String key, boolean isFair) {
        RLock rLock;
        if (isFair) {
            rLock = redisson.getFairLock(
                    String.format(
                            "%s:%s",
                            LOCK_KEY_PREFIX,
                            key
                    )
            );
        } else {
            rLock = redisson.getLock(
                    String.format(
                            "%s:%s",
                            LOCK_KEY_PREFIX,
                            key
                    )
            );
        }
        return new ZLock(rLock, this);
    }

    @Override
    public ZLock lock(String key, long leaseTime, TimeUnit unit, boolean isFair) {
        ZLock zZLock = getLock(key, isFair);
        RLock rLock = (RLock) zZLock.getLock();
        rLock.lock(leaseTime, unit);
        return zZLock;
    }

    @Override
    public ZLock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, boolean isFair) throws InterruptedException {
        ZLock zZLock = getLock(key, isFair);
        RLock rLock = (RLock) zZLock.getLock();
        if (rLock.tryLock(waitTime, leaseTime, unit)) {
            return zZLock;
        }
        return null;
    }

    @Override
    public void unlock(Object lock) {
        if (lock != null) {
            if (lock instanceof RLock) {
                RLock rLock = (RLock) lock;
                if (rLock.isLocked()) {
                    rLock.unlock();
                }
            } else {
                throw new RuntimeException("Requires RLock type");
            }
        }
    }
}
