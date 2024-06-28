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
package cn.boundivore.dl.service.master.service;

import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.response.impl.master.AbstractAuditVo;
import cn.boundivore.dl.base.response.impl.master.AbstractUserVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.page.MyPage;
import cn.boundivore.dl.orm.po.single.TDlLogs;
import cn.boundivore.dl.orm.service.single.impl.TDlLogsServiceImpl;
import cn.boundivore.dl.service.master.utils.PageUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 审计相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/6/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterAuditService {

    private final TDlLogsServiceImpl tDlLogsService;

    private final MasterUserService masterUserService;

    /**
     * Description: 条件查询审计信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param currentPage 当前页码
     * @param pageSize    每页条目数
     * @param principal   用户主体
     * @param userId      用户 ID
     * @param opName      操作名称
     * @param startTs     操作起始时间
     * @param endTs       操作结束时间
     * @param uri         操作路径
     * @param Ip          操作 IP
     * @param logType     操作日志类型
     * @return Result<AbstractAuditVo.AuditLogSimpleListVo> 审计日志缩略信息列表
     */
    public Result<AbstractAuditVo.AuditLogSimpleListVo> getAuditLogSimpleList(Long currentPage,
                                                                              Long pageSize,
                                                                              String principal,
                                                                              Long userId,
                                                                              String opName,
                                                                              Long startTs,
                                                                              Long endTs,
                                                                              String uri,
                                                                              String Ip,
                                                                              LogTypeEnum logType) {




        // 获取用户详细信息列表
        List<AbstractUserVo.UserInfoVo> userInfoList = this.masterUserService.getUserDetailList()
                .getData()
                .getUserInfoList();

        // 用户信息临时缓存，降低数据库查询（此处不必要使用 JOIN 关联用户信息）
        // 将 userInfoList 转换为 <UserId, UserInfoVo> 的 Map
        Map<Long, AbstractUserVo.UserInfoVo> userIdMap = userInfoList.stream()
                .collect(Collectors.toMap(
                                AbstractUserVo.UserInfoVo::getUserId,
                                Function.identity()
                        )
                );

        // 将 userInfoList 转换为 <Principal, UserInfoVo> 的 Map
        Map<String, AbstractUserVo.UserInfoVo> userPrincipalMap = userInfoList.stream()
                .collect(Collectors.toMap(
                                AbstractUserVo.UserInfoVo::getPrincipal,
                                Function.identity()
                        )
                );

        // 日志查询 wrapper
        List<String> excludeColumnList = CollUtil.newArrayList(
                "params",
                "result"
        );
        LambdaQueryChainWrapper<TDlLogs> tableWrapper = this.tDlLogsService
                .lambdaQuery()
                .select(
                        TDlLogs.class,
                        column -> !excludeColumnList.contains(column.getColumn())
                );

        // 添加查询条件
        if (StrUtil.isNotBlank(principal) && userPrincipalMap.containsKey(principal)) {
            tableWrapper = tableWrapper.eq(
                    TDlLogs::getUserId,
                    userPrincipalMap.get(principal).getUserId()
            );
        }

        if (userId != null) {
            tableWrapper = tableWrapper.eq(
                    TDlLogs::getUserId,
                    userId
            );
        }

        if (StrUtil.isNotBlank(opName)) {
            tableWrapper = tableWrapper.like(
                    TDlLogs::getLogName,
                    "%" + opName + "%"
            );
        }

        if (startTs != null) {
            tableWrapper = tableWrapper.ge(
                    TDlLogs::getTimestamp,
                    startTs
            );
        }

        if (endTs != null) {
            tableWrapper = tableWrapper.le(
                    TDlLogs::getTimestamp,
                    endTs
            );
        }

        if (StrUtil.isNotBlank(uri)) {
            tableWrapper = tableWrapper.like(
                    TDlLogs::getUri,
                    "%" + uri + "%"
            );
        }

        if (StrUtil.isNotBlank(Ip)) {
            tableWrapper = tableWrapper.eq(
                    TDlLogs::getIp,
                    Ip
            );
        }

        if (logType != null) {
            tableWrapper = tableWrapper.eq(
                    TDlLogs::getLogType,
                    logType
            );
        }

        // 分页查询，并转换结果
        MyPage<TDlLogs> page = MyPage.of(currentPage, pageSize);
        List<TDlLogs> tDlLogsList = tableWrapper
                .page(page)
                .getRecords();

        List<AbstractAuditVo.AuditLogSimpleVo> auditLogSimpleVoList = tDlLogsList.stream()
                .map(log -> {
                    AbstractUserVo.UserInfoVo userInfo = userIdMap.get(log.getUserId());
                    return new AbstractAuditVo.AuditLogSimpleVo(
                            log.getId(),
                            log.getLogName(),
                            log.getUserId(),
                            userInfo != null ? userInfo.getPrincipal() : null,
                            log.getTimestamp(),
                            log.getDateFormat(),
                            log.getLogType(),
                            log.getClassName(),
                            log.getMethodName(),
                            log.getIp(),
                            log.getUri(),
                            log.getResultCode(),
                            log.getResultEnum()
                    );
                })
                .collect(Collectors.toList());


        return Result.successWithPage(
                new AbstractAuditVo.AuditLogSimpleListVo(
                        auditLogSimpleVoList
                ),
                PageUtil.iPage2Page(page)

        );
    }

    /**
     * Description: 根据审计日志 ID 获取审计日志详情
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param auditLogId 审计日志主键 ID
     * @return Result<AbstractAuditVo.AuditLogDetailVo> 审计日志详情
     */
    public Result<AbstractAuditVo.AuditLogDetailVo> getAuditLogDetail(Long auditLogId) {
        // 查询日志详情
        TDlLogs tDlLogs = this.tDlLogsService.getById(auditLogId);
        Assert.notNull(
                tDlLogs,
                () -> new BException("不存在的审计日志 ID")
        );

        String paramsBase64 = Base64.encode(tDlLogs.getParams());
        String resultBase64 = Base64.encode(tDlLogs.getResult());

        // 构建日志详情对象
        AbstractAuditVo.AuditLogDetailVo auditLogDetailVo = new AbstractAuditVo.AuditLogDetailVo()
                .setAuditLogId(tDlLogs.getId())
                .setOpName(tDlLogs.getLogName())
                .setUserId(tDlLogs.getUserId())

                .setTimestamp(tDlLogs.getTimestamp())
                .setDateFormat(tDlLogs.getDateFormat())
                .setLogType(tDlLogs.getLogType())
                .setClassName(tDlLogs.getClassName())
                .setMethodName(tDlLogs.getMethodName())
                .setIp(tDlLogs.getIp())
                .setUri(tDlLogs.getUri())
                .setResultCode(tDlLogs.getResultCode())
                .setResultEnum(tDlLogs.getResultEnum())
                .setParamsBase64(paramsBase64)
                .setResultBase64(resultBase64);

        if(tDlLogs.getUserId() != null){
            // 获取用户信息
            String principal = this.masterUserService.getUserDetailById(tDlLogs.getUserId())
                    .getData()
                    .getPrincipal();

            auditLogDetailVo.setPrincipal(principal);
        }

        return Result.success(auditLogDetailVo);
    }
}
