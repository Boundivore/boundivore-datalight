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

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Description: LockAspect
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/18
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Aspect
public class LockAspect {
    //获取方法参数值

    @Autowired(required = false)
    private IDistributedLock locker;

    private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@within(lock) || @annotation(lock)")
    public Object aroundLock(ProceedingJoinPoint point, Lock lock) throws Throwable {
        if (lock == null) {
            lock = point.getTarget().getClass().getDeclaredAnnotation(Lock.class);
        }
        String lockKey = lock.key();
        if (locker == null) {
            throw new RuntimeException("IDistributedLock object is null");
        }
        if (StrUtil.isBlank(lockKey)) {
            throw new RuntimeException("lockKey is null");
        }

        if (lockKey.contains("#")) {
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            Object[] args = point.getArgs();
            lockKey = getValBySpEL(lockKey, methodSignature, args);
        }
        ZLock lockObj = null;
        try {
            if (lock.waitTime() > 0) {
                lockObj = locker.tryLock(lockKey, lock.waitTime(), lock.leaseTime(), lock.unit(), lock.isFair());
            } else {
                lockObj = locker.lock(lockKey, lock.leaseTime(), lock.unit(), lock.isFair());
            }

            if (lockObj != null) {
                return point.proceed();
            } else {
                throw new RuntimeException("Lock wait timeout");
            }
        } finally {
            locker.unlock(lockObj);
        }
    }

    private String getValBySpEL(String spEL, MethodSignature methodSignature, Object[] args) {
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (paramNames != null && paramNames.length > 0) {
            Expression expression = spelExpressionParser.parseExpression(spEL);
            EvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            return expression.getValue(context).toString();
        }
        return null;
    }
}
