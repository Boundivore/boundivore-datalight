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
package cn.boundivore.dl.boot.logs;

import cn.boundivore.dl.base.result.ErrorMessage;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.utils.ReactiveAddressUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.boundivore.dl.base.result.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import static cn.boundivore.dl.base.constants.ICommonConstant.DATETIME_FORMAT;

/**
 * Description: AOP intercepts the method annotated with @Log
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/10
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    private SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);

    @Pointcut(value = "@annotation(cn.boundivore.dl.boot.logs.Logs)")
    public void logPointCut() {
    }

    @Around(value = "logPointCut()")
    public Object logs(ProceedingJoinPoint joinPoint) {
        try {
            LogTrace logTrace = new LogTrace();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

            Method method = methodSignature.getMethod();

            Logs logsAnnotation = method.getAnnotation(Logs.class);
            assert logsAnnotation != null;

            //Name & Type
            logTrace.setLogName(logsAnnotation.name());
            logTrace.setLogType(logsAnnotation.logType());

            //Class.Method
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = method.getName();
            String classMethodName = String.format("%s#%s()", className, methodName);
            logTrace.setClassMethod(classMethodName);

            //Time&Date
            Date date = new Date();
            String dateFormat = sdf.format(date);

            logTrace.setTimeStamp(date.getTime());
            logTrace.setDateFormat(dateFormat);

            //Params
            Object[] params = joinPoint.getArgs();
            logTrace.setParams(params);


            //Uri
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            assert attributes != null;
            HttpServletRequest request = attributes.getRequest();

            String uri = request.getRequestURI();
            logTrace.setUri(uri);

            //Ip
            logTrace.setIp(ReactiveAddressUtil.getRemoteIp(request));

            //User & Mobile
            String token = request.getHeader("Authorization");
            if (StrUtil.isNotBlank(token)) {
//                String realToken = token.replace(IAuthConstants.JWT_TOKEN_PREFIX, "");
//                JWSObject jwsObject = JWSObject.parse(realToken);
//                String authJson = jwsObject.getPayload().toString();
            }

            Object result = joinPoint.proceed();
            if (result instanceof Result) {
                Result<?> r = (Result<?>) result;
                logTrace.setResultCode(r.getCode());
                logTrace.setResultEnum(ResultEnum.getByCode(r.getCode()));

                if (logsAnnotation.isPrintResult()) {
                    logTrace.setResult(r);
                }

            }

            //TODO Export log to ElasticSearch or HBase or Kafka in Async


            return result;
        } catch (Throwable e) {
            val error = ExceptionUtil.stacktraceToString(e);
            log.error(error);
            return Result.fail(ResultEnum.FAIL_UNKNOWN, new ErrorMessage(ExceptionUtil.getSimpleMessage(e)));
        }
    }
}
