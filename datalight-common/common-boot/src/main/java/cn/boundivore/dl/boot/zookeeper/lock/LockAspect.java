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

//import cn.hutool.core.lang.Assert;
//import cn.hutool.core.util.ArrayUtil;
//import cn.hutool.core.util.StrUtil;
//import com.jinji.trans.exception.LockException;
//import com.jinji.trans.zookeeper.service.ZookeeperCommonService;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.curator.framework.recipes.locks.InterProcessMutex;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * Description: AOP intercepts the method annotated with @Lock
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/10
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
//@Aspect
//@Component
@Slf4j
public class LockAspect {

//    @Resource
//    private ZookeeperCommonService zookeeperCommonService;
//
////    @Resource
////    private TokenParserService tokenParserService;
//
//
//    @Pointcut(value = "@annotation(com.jinji.trans.facade.zookeeper.lock.Lock)")
//    public void lockPointCut() {
//    }
//
//    @SneakyThrows
//    @Around(value = "lockPointCut()")
//    public Object lock(ProceedingJoinPoint joinPoint) {
//
//        Object result = null;
//        InterProcessMutex mutex = null;
//        try {
//            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//            Method method = methodSignature.getMethod();
//
//            Lock lockAnnotation = method.getAnnotation(Lock.class);
//            String lockWithPath = lockAnnotation.withPath();
//            boolean withClassFunctionName = lockAnnotation.withClassFunctionName();
//            boolean lockWithUserIdInToken = lockAnnotation.withUserIdInToken();
//            boolean lockWithMerchantIdInToken = lockAnnotation.withMerchantIdInToken();
//            int[] lockWithParamIndexArr = lockAnnotation.withParamIndexArr();
//
//            long waitTime = lockAnnotation.waitTime();
//            TimeUnit timeUnit = lockAnnotation.unit();
//
//            /*
//                If Path is specified, the specified Path is used.
//                Otherwise, the class name and method name are used as locks
//             */
//            if (StrUtil.isBlank(lockWithPath)) {
//                //Class#Method
//                //Class full name is too long, so use Class simple name.
//                if (withClassFunctionName) {
//                    String className = joinPoint.getTarget().getClass().getSimpleName();
//                    String methodName = method.getName();
//                    lockWithPath = String.format("%s#%s", className, methodName);
//                }
//            }
//
//            if (lockWithUserIdInToken) {
////                Long userId = tokenParserService.getUserIdFromToken();
////                lockWithPath = String.format("%s#%s", lockWithPath, userId);
//            }
//
//            if (lockWithMerchantIdInToken) {
////                Long merchantId = tokenParserService.getMerchantIdFromToken();
////                lockWithPath = String.format("%s#%s", lockWithPath, merchantId);
//            }
//
//            if (ArrayUtil.isNotEmpty(lockWithParamIndexArr)) {
//                Object[] args = joinPoint.getArgs();
//
//                int[] indexFilterArr = Arrays.stream(lockWithParamIndexArr)
//                        .filter(i -> i >= 0 && i < args.length)
//                        .toArray();
//
//                Assert.isTrue(
//                        indexFilterArr.length == lockWithParamIndexArr.length,
//                        () -> new LockException("@Lock withParamIndexArr 索引与参数不匹配")
//                );
//
//                for (int index : lockWithParamIndexArr) {
//                    Object argument = joinPoint.getArgs()[index];
//                    lockWithPath = String.format("%s#%s", lockWithPath, argument.toString());
//                }
//
//            }
//
//            final String localPathFinal = String.format(
//                    "%s/%s",
//                    ZookeeperCommonService.APP_BASE_PATH,
//                    lockWithPath
//            );
//
//
//            mutex = zookeeperCommonService.newInterProcessMutex(localPathFinal);
//            Assert.isTrue(
//                    zookeeperCommonService.acquireLockNode(
//                            mutex,
//                            waitTime,
//                            timeUnit
//                    ),
//                    () -> new LockException("获取分布式锁超时"));
//
//            result = joinPoint.proceed();
//            return result;
//
//        } finally {
//            zookeeperCommonService.releaseLockNode(mutex);
//        }
//    }

}
