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
import cn.boundivore.dl.base.request.impl.master.AbstractWebStateRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractWebStateVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlWebState;
import cn.boundivore.dl.orm.service.single.impl.TDlWebStateServiceImpl;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: Web 状态信息相关操作逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/2/28
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterWebStateService {

    private final TDlWebStateServiceImpl tDlWebStateService;


    /**
     * Description: 保存（覆盖）前端状态信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 缓存数据请求体
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> saveWebState(AbstractWebStateRequest.SaveStateRequest request) {
        TDlWebState tDlWebState = this.tDlWebStateService.lambdaQuery()
                .select()
                .eq(TDlWebState::getClusterId, request.getClusterId())
                .eq(TDlWebState::getUserId, request.getUserId())
                .eq(TDlWebState::getWebKey, request.getWebKey())
                .one();

        if (tDlWebState == null) {
            tDlWebState = new TDlWebState();
            tDlWebState.setVersion(0L);
            tDlWebState.setClusterId(request.getClusterId());
            tDlWebState.setUserId(request.getUserId());
            tDlWebState.setWebKey(request.getWebKey());
        }

        tDlWebState.setWebValue(request.getWebValue());

        Assert.isTrue(
                this.tDlWebStateService.saveOrUpdate(tDlWebState),
                () -> new DatabaseException("保存前端缓存信息失败")
        );

        return Result.success();
    }

    /**
     * Description: 返回前端缓存数据请求体
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @param userId    用户 ID
     * @param webKey    缓存键
     * @return 键值对集合
     */
    public Result<AbstractWebStateVo.WebStateMapVo> getWebStateMap(Long clusterId,
                                                                   Long userId,
                                                                   String webKey) {

        LambdaQueryChainWrapper<TDlWebState> tDlWebStateWrapper = this.tDlWebStateService.lambdaQuery().select();

        if (clusterId != null) {
            tDlWebStateWrapper = tDlWebStateWrapper.eq(TDlWebState::getClusterId, clusterId);
        }

        if (userId != null) {
            tDlWebStateWrapper = tDlWebStateWrapper.eq(TDlWebState::getUserId, userId);
        }

        if (userId != null) {
            tDlWebStateWrapper = tDlWebStateWrapper.eq(TDlWebState::getWebKey, webKey);
        }

        List<TDlWebState> tDlWebStateList = tDlWebStateWrapper.list();
        Assert.notEmpty(
                tDlWebStateList,
                () -> new BException("未找到缓存信息")
        );

        Map<String, String> kvMap = tDlWebStateList.stream()
                .collect(
                        Collectors.toMap(
                                TDlWebState::getWebKey,
                                TDlWebState::getWebValue
                        )
                );

        AbstractWebStateVo.WebStateMapVo webStateMapVo = new AbstractWebStateVo.WebStateMapVo();
        webStateMapVo.setClusterId(clusterId);
        webStateMapVo.setUserId(userId);
        webStateMapVo.setKvMap(kvMap);

        return Result.success(webStateMapVo);
    }

    /**
     * Description: 根据 Key 移除指定前端缓存状态 KV
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 移除指定前端缓存状态 KV 请求体
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> removeByKey(AbstractWebStateRequest.RemoveStateRequest request) {
        TDlWebState tDlWebState = this.tDlWebStateService.lambdaQuery()
                .select()
                .eq(TDlWebState::getWebKey, request.getWebKey())
                .one();

        if (tDlWebState == null) {
            return Result.success();
        }

        Assert.isTrue(
                this.tDlWebStateService.removeById(tDlWebState),
                () -> new DatabaseException("移除指定前端缓存 KV 失败")
        );

        return Result.success();
    }

    /**
     * Description: 根据 ClusterId 清空所有关联的状态缓存信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/2/28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 移除指定前端缓存状态 KV 请求体
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> clearByClusterId(AbstractWebStateRequest.ClearStateRequest request) {
        List<TDlWebState> tDlWebStateList = this.tDlWebStateService.lambdaQuery()
                .select()
                .eq(TDlWebState::getClusterId, request.getClusterId())
                .list();

        if (tDlWebStateList.isEmpty()) {
            return Result.success();
        }

        Assert.isTrue(
                this.tDlWebStateService.removeBatchByIds(tDlWebStateList),
                () -> new DatabaseException("清空指定集群前端缓存信息失败")
        );

        return Result.success();
    }
}
