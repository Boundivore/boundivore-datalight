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
import cn.boundivore.dl.orm.po.single.TDlAlertHandlerInterface;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerInterfaceServiceImpl;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 告警接口处理方式处理相关逻辑
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
public class MasterAlertHandlerInterfaceService {

    private final TDlAlertHandlerInterfaceServiceImpl tDlAlertHandlerInterfaceService;

    /**
     * Description: 新增接口告警处理方式
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 新建告警接口处理方式请求体
     * @return Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> 新建的接口告警详情
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @LocalLock
    public Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> newAlertHandlerInterface(AbstractAlertHandlerRequest.NewAlertHandlerInterfaceRequest request) {
        // 检查是否有重复 URI
        boolean existsUri = this.tDlAlertHandlerInterfaceService.lambdaQuery()
                .select()
                .eq(TDlAlertHandlerInterface::getInterfaceUri, request.getInterfaceUri())
                .exists();
        Assert.isFalse(
                existsUri,
                () -> new BException("接口地址已经存在")
        );

        TDlAlertHandlerInterface tDlAlertHandlerInterface = new TDlAlertHandlerInterface();
        tDlAlertHandlerInterface.setVersion(0L);
        tDlAlertHandlerInterface.setInterfaceUri(request.getInterfaceUri());

        // 保存到数据库
        Assert.isTrue(
                this.tDlAlertHandlerInterfaceService.save(tDlAlertHandlerInterface),
                () -> new DatabaseException("保存到数据库失败")
        );

        return Result.success(
                new AbstractAlertHandlerVo.AlertHandlerInterfaceVo(
                        tDlAlertHandlerInterface.getId(),
                        tDlAlertHandlerInterface.getInterfaceUri()
                )
        );
    }

    /**
     * Description: 根据 ID 获取告警接口处理方式详情
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param handlerId 告警处理方式 ID
     * @return Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> 告警接口处理方式响应体
     */
    public Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> getAlertHandlerInterfaceDetailsById(Long handlerId) {
        TDlAlertHandlerInterface tDlAlertHandlerInterface = this.tDlAlertHandlerInterfaceService.getById(handlerId);

        Assert.notNull(
                tDlAlertHandlerInterface,
                () -> new BException("请求的 ID 不存在")
        );


        return Result.success(
                new AbstractAlertHandlerVo.AlertHandlerInterfaceVo(
                        tDlAlertHandlerInterface.getId(),
                        tDlAlertHandlerInterface.getInterfaceUri()
                )
        );
    }


    /**
     * Description: 更新告警接口处理方式
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警接口处理方式更新请求体
     * @return Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> 告警接口处理方式响应体
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @LocalLock
    public Result<AbstractAlertHandlerVo.AlertHandlerInterfaceVo> updateAlertHandlerInterface(AbstractAlertHandlerRequest.UpdateAlertHandlerInterfaceRequest request) {
        TDlAlertHandlerInterface tDlAlertHandlerInterface = this.tDlAlertHandlerInterfaceService.getById(request.getHandlerId());

        Assert.notNull(
                tDlAlertHandlerInterface,
                () -> new BException("请求的 ID 不存在")
        );

        tDlAlertHandlerInterface.setInterfaceUri(request.getInterfaceUri());

        Assert.isTrue(
                this.tDlAlertHandlerInterfaceService.updateById(tDlAlertHandlerInterface),
                () -> new DatabaseException("更新到数据库失败")
        );

        return Result.success(
                new AbstractAlertHandlerVo.AlertHandlerInterfaceVo(
                        tDlAlertHandlerInterface.getId(),
                        tDlAlertHandlerInterface.getInterfaceUri()
                )
        );
    }

    /**
     * Description: 获取告警接口处理方式列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<AbstractAlertHandlerVo.AlertHandlerMailListVo> 告警接口处理方式列表响应体
     */
    public Result<AbstractAlertHandlerVo.AlertHandlerInterfaceListVo> getAlertHandlerInterfaceList() {
        List<TDlAlertHandlerInterface> tDlAlertHandlerInterfaceList = this.tDlAlertHandlerInterfaceService.list();

        return Result.success(
                new AbstractAlertHandlerVo.AlertHandlerInterfaceListVo(
                        tDlAlertHandlerInterfaceList.stream()
                                .map(i -> new AbstractAlertHandlerVo.AlertHandlerInterfaceVo(
                                                i.getId(),
                                                i.getInterfaceUri()
                                        )
                                )
                                .collect(Collectors.toList())
                )
        );

    }
}
