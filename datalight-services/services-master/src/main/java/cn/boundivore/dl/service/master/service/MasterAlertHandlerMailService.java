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

import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.base.request.impl.master.AbstractAlertHandlerRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractAlertHandlerVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.lock.LocalLock;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlAlertHandlerMail;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerMailServiceImpl;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 告警邮件处理方式处理相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterAlertHandlerMailService {

    private final TDlAlertHandlerMailServiceImpl tDlAlertHandlerMailService;

    /**
     * Description: 新增邮件告警处理方式
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警邮件处理方式新建请求体
     * @return Result<AbstractAlertHandlerVo.AlertHandlerMailVo> 告警邮件处理方式响应体
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @LocalLock
    public Result<AbstractAlertHandlerVo.AlertHandlerMailVo> newAlertHandlerMail(AbstractAlertHandlerRequest.NewAlertHandlerMailRequest request) {
        // 检查是否有重复邮箱地址
        boolean existsMailAccount = this.tDlAlertHandlerMailService.lambdaQuery()
                .select()
                .eq(TDlAlertHandlerMail::getMailAccount, request.getMailAccount())
                .exists();
        Assert.isFalse(
                existsMailAccount,
                () -> new BException("邮箱地址已经存在")
        );

        TDlAlertHandlerMail tDlAlertHandlerMail = new TDlAlertHandlerMail();
        tDlAlertHandlerMail.setVersion(0L);
        tDlAlertHandlerMail.setMailAccount(request.getMailAccount());

        // 保存到数据库
        Assert.isTrue(
                this.tDlAlertHandlerMailService.save(tDlAlertHandlerMail),
                () -> new DatabaseException("保存到数据库失败")
        );

        return Result.success(
                new AbstractAlertHandlerVo.AlertHandlerMailVo(
                        tDlAlertHandlerMail.getId(),
                        tDlAlertHandlerMail.getMailAccount()
                )
        );
    }

    /**
     * Description: 根据 ID 获取告警邮件处理方式详情
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param handlerId 告警处理方式 ID
     * @return Result<AbstractAlertHandlerVo.AlertHandlerMailVo> 告警邮件处理方式响应体
     */
    public Result<AbstractAlertHandlerVo.AlertHandlerMailVo> getAlertHandlerMailDetailsById(Long handlerId) {
        TDlAlertHandlerMail tDlAlertHandlerMail = this.tDlAlertHandlerMailService.getById(handlerId);
        Assert.notNull(
                tDlAlertHandlerMail,
                () -> new BException("请求的 ID 不存在")
        );


        return Result.success(
                new AbstractAlertHandlerVo.AlertHandlerMailVo(
                        tDlAlertHandlerMail.getId(),
                        tDlAlertHandlerMail.getMailAccount()
                )
        );
    }

    /**
     * Description: 更新告警邮件处理方式
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警邮件处理方式更新请求体
     * @return Result<AbstractAlertHandlerVo.AlertHandlerMailVo> 告警邮件处理方式响应体
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @LocalLock
    public Result<AbstractAlertHandlerVo.AlertHandlerMailVo> updateAlertHandlerMail(AbstractAlertHandlerRequest.UpdateAlertHandlerMailRequest request) {

        TDlAlertHandlerMail tDlAlertHandlerMail = this.tDlAlertHandlerMailService.getById(request.getHandlerId());

        Assert.notNull(
                tDlAlertHandlerMail,
                () -> new BException("请求的 ID 不存在")
        );

        tDlAlertHandlerMail.setMailAccount(request.getMailAccount());

        Assert.isTrue(
                this.tDlAlertHandlerMailService.updateById(tDlAlertHandlerMail),
                () -> new DatabaseException("更新到数据库失败")
        );

        return Result.success(
                new AbstractAlertHandlerVo.AlertHandlerMailVo(
                        tDlAlertHandlerMail.getId(),
                        tDlAlertHandlerMail.getMailAccount()
                )
        );
    }

    /**
     * Description: 获取告警邮箱处理方式列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<AbstractAlertHandlerVo.AlertHandlerMailListVo> 告警邮箱处理方式列表响应体
     */
    public Result<AbstractAlertHandlerVo.AlertHandlerMailListVo> getAlertHandlerMailList() {
        List<TDlAlertHandlerMail> tDlAlertHandlerMailList = this.tDlAlertHandlerMailService.list();

        return Result.success(
                new AbstractAlertHandlerVo.AlertHandlerMailListVo(
                        tDlAlertHandlerMailList.stream()
                                .map(i -> new AbstractAlertHandlerVo.AlertHandlerMailVo(
                                        i.getId(),
                                        i.getMailAccount()
                                ))
                                .collect(Collectors.toList())
                )
        );

    }

    /**
     * Description: 根据 ID 列表获取告警邮件处理方式列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警处理方式 ID 列表
     * @return Result<AbstractAlertHandlerVo.AlertHandlerMailListVo> 邮件处理方式详情列表
     */
    public Result<AbstractAlertHandlerVo.AlertHandlerMailListVo> getAlertHandlerMailListIdList(AbstractAlertHandlerRequest.AlertHandlerIdListRequest request) {
        return Result.success(
                new AbstractAlertHandlerVo.AlertHandlerMailListVo(
                        this.tDlAlertHandlerMailService.lambdaQuery()
                                .select()
                                .in(TBasePo::getId, request.getHandlerIdList())
                                .list()
                                .stream()
                                .map(i -> new AbstractAlertHandlerVo.AlertHandlerMailVo(
                                                i.getId(),
                                                i.getMailAccount()
                                        )
                                )
                                .collect(Collectors.toList())
                )
        );
    }
}
