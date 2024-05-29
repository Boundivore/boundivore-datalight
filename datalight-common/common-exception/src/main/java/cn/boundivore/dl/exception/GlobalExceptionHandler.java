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
package cn.boundivore.dl.exception;

import cn.boundivore.dl.base.result.ErrorMessage;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.result.ResultEnum;
import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.zip.DataFormatException;

/**
 * Description: GlobalExceptionHandler
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/6
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("Http Request method error:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_REQUEST_METHOD
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("Request parameter missing:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_REQUEST_PARAM_MISSING
        );
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> typeMismatchExceptionHandler(TypeMismatchException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("The parameter type of the request is wrong:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_REQUEST_PARAM_MISSING
        );
    }

    @ExceptionHandler(DataFormatException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> dataFormatExceptionHandler(DataFormatException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("Data formatting error:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_DATA_FORMAT
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("Illegal input error:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_ILLEGAL_ARGUMENT
        );
    }

    @ExceptionHandler(BException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> businessExceptionHandler(BException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("BusinessException:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_BUSINESS_EXCEPTION
        );
    }

    @ExceptionHandler(BashException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> bashExceptionHandler(BashException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("BashException:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_EXECUTOR_EXCEPTION
        );
    }

    @ExceptionHandler({UserNotLoginException.class, AuthDeniedException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> authExceptionHandler(Exception e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("User Auth exception:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_AUTH_EXCEPTION
        );
    }

    @ExceptionHandler({PermissionInterfaceDeniedException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> permissionInterfaceExceptionHandler(Exception e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("Interface permission exception:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_INTERFACE_UNAUTHORIZED
        );
    }


    @ExceptionHandler(LoginFailException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> loginFailExceptionHandler(LoginFailException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("User login fail exception:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_USER_PWD_NOT_MATCH_EXCEPTION
        );
    }


    @ExceptionHandler(DatabaseException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> databaseExceptionHandler(DatabaseException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error("DatabaseException:[{}]", error);
        return Result.fail(
                ResultEnum.FAIL_DATABASE_EXCEPTION
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error(error);
        return Result.fail(
                ResultEnum.FAIL_404
        );
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> validExceptionHandler(BindException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error(error);
        return Result.fail(
                ResultEnum.FAIL_ILLEGAL_ARGUMENT,
                new ErrorMessage(
                        String.format(
                                "请检查传递的参数，部分参数不匹配: %s",
                                e.getAllErrors().get(0).getDefaultMessage()
                        )
                )
        );
    }

    @ExceptionHandler(LockException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> lockExceptionHandler(LockException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error(error);
        return Result.fail(
                ResultEnum.FAIL_LOCK_EXCEPTION
        );
    }

    @ExceptionHandler(InnerRemoteInvokeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> innerInvokeException(InnerRemoteInvokeException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error(error);
        return Result.fail(
                ResultEnum.FAIL_REMOTE_INVOKE_EXCEPTION
        );
    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> fileUploadException(FileUploadException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error(error);
        return Result.fail(
                ResultEnum.FAIL_FILE_UPLOAD_EXCEPTION
        );
    }

    @ExceptionHandler(FileDownloadException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> fileDownloadException(FileDownloadException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error(error);
        return Result.fail(
                ResultEnum.FAIL_FILE_DOWNLOAD_EXCEPTION
        );
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> notLoginException(NotLoginException e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error(error);

        ResultEnum resultEnum = null;
        switch (e.getType()) {
            case NotLoginException.NOT_TOKEN:
                resultEnum = ResultEnum.FAIL_NOT_TOKEN_EXCEPTION;
                break;
            case NotLoginException.INVALID_TOKEN:
                resultEnum = ResultEnum.FAIL_INVALID_TOKEN_EXCEPTION;
                break;
            case NotLoginException.TOKEN_TIMEOUT:
                resultEnum = ResultEnum.FAIL_TOKEN_TIMEOUT_EXCEPTION;
                break;
            case NotLoginException.BE_REPLACED:
                resultEnum = ResultEnum.FAIL_BE_REPLACED_EXCEPTION;
                break;
            case NotLoginException.KICK_OUT:
                resultEnum = ResultEnum.FAIL_KICK_OUT_EXCEPTION;
                break;
            case NotLoginException.TOKEN_FREEZE:
                resultEnum = ResultEnum.FAIL_TOKEN_FREEZE_EXCEPTION;
                break;
            case NotLoginException.NO_PREFIX:
                resultEnum = ResultEnum.FAIL_NO_PREFIX_EXCEPTION;
                break;
            default:
                resultEnum = ResultEnum.FAIL_UNKNOWN_TOKEN_EXCEPTION;
                break;
        }

        return Result.fail(
                resultEnum,
                new ErrorMessage(resultEnum.getMessageCN())
        );
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Result<?> unknownExceptionHandler(Exception e) {
        val error = ExceptionUtil.stacktraceToString(e);
        log.error(error);
        return Result.fail(
                ResultEnum.FAIL_UNKNOWN
        );
    }

}
