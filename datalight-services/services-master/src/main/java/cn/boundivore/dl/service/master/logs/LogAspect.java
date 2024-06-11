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
package cn.boundivore.dl.service.master.logs;

import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.result.ResultEnum;
import cn.boundivore.dl.boot.utils.ReactiveAddressUtil;
import cn.boundivore.dl.service.master.env.DataLightEnv;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Description: AOP 拦截 @Logs 注解
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    private final DataLightEnv dataLightEnv;

    @Pointcut("@annotation(cn.boundivore.dl.service.master.logs.Logs) || @within(cn.boundivore.dl.service.master.logs.Logs)")
    public void logPointCut() {
    }

    /**
     * Description: 拦截指定注解的方法，记录日志和审计信息。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: Throwable
     *
     * @param joinPoint 连接点，表示被拦截的方法
     * @return 被拦截方法的返回值
     */
    @Around("logPointCut()")
    public Object logs(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!dataLightEnv.getAuditEnable()) {
            return joinPoint.proceed();
        }

        LogTrace logTrace = new LogTrace();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        // 检查方法上是否有 @LogsIgnore 注解，如有，则忽略，并放行
        if (method.isAnnotationPresent(LogsIgnore.class)) {
            return joinPoint.proceed();
        }

        // 获取注解
        Logs logsAnnotation = this.getAnnotation(joinPoint, Logs.class);
        if (logsAnnotation == null) {
            return joinPoint.proceed();
        }

        // 判断是否需要记录 GET 或 POST 请求的日志
        if (dataLightEnv.getPostMappingEnable() && this.getAnnotation(joinPoint, PostMapping.class) == null) {
            return joinPoint.proceed();
        }
        if (dataLightEnv.getGetMappingEnable() && this.getAnnotation(joinPoint, GetMapping.class) == null) {
            return joinPoint.proceed();
        }

        String logName = this.getLogName(joinPoint, logsAnnotation);

        logTrace.setLogName(logName);
        logTrace.setLogType(logsAnnotation.logType());
        logTrace.setClassName(joinPoint.getTarget().getClass().getName());
        logTrace.setMethodName(method.getName());

        // Time & Date
        LocalDateTime now = LocalDateTime.now();
        ZoneId zoneId = ZoneId.of("GMT+8");
        ZonedDateTime zonedDateTime = now.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        logTrace.setTimeStamp(zonedDateTime.toInstant().toEpochMilli());
        logTrace.setDateFormat(zonedDateTime.format(formatter));

        // Params
        logTrace.setParams(joinPoint.getArgs());

        // Uri and Ip
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            logTrace.setUri(request.getRequestURI());
            logTrace.setIp(ReactiveAddressUtil.getRemoteIp(request));
        }

        // User
        if (StpUtil.isLogin()) {
            Long userId = Long.parseLong(StpUtil.getSession().get("userId").toString());
            logTrace.setUserId(userId);
        }

        // Proceed with the method execution
        Object result = joinPoint.proceed();

        if (result instanceof Result) {
            Result<?> r = (Result<?>) result;
            logTrace.setResultCode(r.getCode());
            logTrace.setResultEnum(ResultEnum.getByCode(r.getCode()));
            logTrace.setResult(r);

            if (logsAnnotation.isPrintResult()) {
                logTrace.setResult(r);
            }
        }

        // 将 LogTrace 对象添加到缓存中
        LogTraceCache.putLogTrace(logTrace);

        return result;
    }

    /**
     * Description: 获取指定方法或类上的注解。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: 无
     *
     * @param joinPoint       连接点，表示被拦截的方法
     * @param annotationClass 要获取的注解的类
     * @return 注解实例，如果没有找到则返回 null
     */
    private <T extends Annotation> T getAnnotation(ProceedingJoinPoint joinPoint, Class<T> annotationClass) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        T annotation = method.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        return joinPoint.getTarget().getClass().getAnnotation(annotationClass);
    }

    /**
     * Description: 获取日志名称。如果没有指定日志名称，则尝试从 @ApiOperation 注解中获取。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/11
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param joinPoint      连接点，表示被拦截的方法
     * @param logsAnnotation 方法或类上的 @Logs 注解
     * @return 日志名称
     */
    private String getLogName(ProceedingJoinPoint joinPoint, Logs logsAnnotation) {
        String logName = logsAnnotation.name();
        if (StrUtil.isBlank(logName)) {
            Class<?> targetClass = joinPoint.getTarget().getClass();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            ApiOperation apiOperation = getAnnotation(joinPoint, ApiOperation.class);

            if (apiOperation != null) {
                logName = apiOperation.notes();
            } else {
                logName = String.format("%s#%s", targetClass.getName(), method.getName());
            }
        }
        return logName;
    }
}
