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
package cn.boundivore.dl.boot.zookeeper.lock;

import cn.hutool.core.util.StrUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

/**
 * Description: Lock 单机锁切面
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Aspect
@Component
@Order(1)
public class LockAspect {
    private final LockHandler lockHandler;

    @Autowired
    public LockAspect(LockHandler lockHandler) {
        this.lockHandler = lockHandler;
    }

    @Pointcut("@annotation(cn.boundivore.dl.boot.zookeeper.lock.Locked)")
    public void lockedMethod() {

    }

    @Around("lockedMethod()")
    public Object lockMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Locked lockedAnnotation = getLockedAnnotation(joinPoint);

        String lockName = lockedAnnotation.value();

        if (lockName == null || StrUtil.isBlank(lockName)) {
            lockName = joinPoint.getSignature().getName();
        }

        Lock lock = lockHandler.getLock(lockName);

        lock.lock();
        try {
            return joinPoint.proceed();
        } finally {
            lock.unlock();
        }
    }

    private Locked getLockedAnnotation(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        Signature signature = joinPoint.getSignature();
        Class<?> declaringType = signature.getDeclaringType();
        Method method = declaringType.getMethod(signature.getName(), getParameterTypes(joinPoint));
        return method.getAnnotation(Locked.class);
    }

    private Class<?>[] getParameterTypes(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        return parameterTypes;
    }
}

