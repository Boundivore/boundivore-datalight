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
import cn.boundivore.dl.base.enumeration.impl.AlertHandlerTypeEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractAlertHandlerRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractAlertHandlerVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.lock.LocalLock;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlAlert;
import cn.boundivore.dl.orm.po.single.TDlAlertHandlerInterface;
import cn.boundivore.dl.orm.po.single.TDlAlertHandlerMail;
import cn.boundivore.dl.orm.po.single.TDlAlertHandlerRelation;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerInterfaceServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerMailServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerRelationServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: 告警处理方式相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MasterAlertHandlerService {

    private final TDlAlertServiceImpl tDlAlertService;

    private final TDlAlertHandlerRelationServiceImpl tDlAlertHandlerRelationService;

    private final TDlAlertHandlerInterfaceServiceImpl tDlAlertHandlerInterfaceService;

    private final TDlAlertHandlerMailServiceImpl tDlAlertHandlerMailService;


    /**
     * Description: 绑定或解绑告警与告警处理方式
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 绑定关系请求体
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @LocalLock
    public Result<String> bindAlertAndAlertHandler(AbstractAlertHandlerRequest.AlertHandlerRelationListRequest request) {
        // 检查处理方式是否合理
        List<AlertHandlerTypeEnum> noNeedalertHandlerTypeEnumList = CollUtil.newArrayList(
                AlertHandlerTypeEnum.ALERT_IGNORE
        );

        List<AlertHandlerTypeEnum> unsupportedHandlerTypeEnumList = CollUtil.newArrayList(
                AlertHandlerTypeEnum.ALERT_WEICHAT,
                AlertHandlerTypeEnum.ALERT_DINGDING,
                AlertHandlerTypeEnum.ALERT_FEISHU
        );

        request.getAlertHandlerRelationList()
                .forEach(i -> {
                            Assert.isFalse(
                                    noNeedalertHandlerTypeEnumList.contains(i.getAlertHandlerTypeEnum()),
                                    () -> new IllegalArgumentException(
                                            String.format(
                                                    "%s 类型无需绑定告警处理方式",
                                                    i.getAlertHandlerTypeEnum()
                                            )
                                    )
                            );

                            Assert.isFalse(
                                    unsupportedHandlerTypeEnumList.contains(i.getAlertHandlerTypeEnum()),
                                    () -> new IllegalArgumentException(
                                            String.format(
                                                    "%s 类型暂未开通支持",
                                                    i.getAlertHandlerTypeEnum()
                                            )
                                    )
                            );
                        }
                );

        // 获取需要绑定和解绑的列表
        List<AbstractAlertHandlerRequest.AlertHandlerRelationRequest> bindingList = request.getAlertHandlerRelationList().stream()
                .filter(AbstractAlertHandlerRequest.AlertHandlerRelationRequest::getIsBinding)
                .collect(Collectors.toList());
        List<AbstractAlertHandlerRequest.AlertHandlerRelationRequest> unbindingList = request.getAlertHandlerRelationList().stream()
                .filter(i -> !i.getIsBinding())
                .collect(Collectors.toList());

        // 如果请求体中的列表为空,抛出异常
        Assert.notEmpty(
                request.getAlertHandlerRelationList(),
                () -> new BException("请求体中的列表不能为空")
        );

        // 获取需要检查的 ID 列表
        List<Long> handlerIdList = bindingList.stream()
                .map(AbstractAlertHandlerRequest.AlertHandlerRelationRequest::getHandlerId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> alertIdList = bindingList.stream()
                .map(AbstractAlertHandlerRequest.AlertHandlerRelationRequest::getAlertId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询处理方式和告警是否存在
        Map<Long, Boolean> handlerIdExistsMap = this.getHandlerIdExistsMap(handlerIdList);
        Map<Long, Boolean> alertIdExistsMap = this.getAlertIdExistsMap(alertIdList);

        // 检查绑定列表中的 ID 是否存在
        bindingList.forEach(i -> {
                    Assert.isTrue(
                            handlerIdExistsMap.get(i.getHandlerId()),
                            () -> new BException(
                                    String.format(
                                            "Handler Id: %s 不存在",
                                            i.getHandlerId()
                                    )
                            )
                    );
                    Assert.isTrue(
                            alertIdExistsMap.get(i.getAlertId()),
                            () -> new BException(
                                    String.format(
                                            "Alert Id: %s 不存在",
                                            i.getAlertId()
                                    )
                            )
                    );
                }
        );

        // 解绑
        if (!CollUtil.isEmpty(unbindingList)) {
            List<Long> unbindingRelationIds = unbindingList.stream()
                    .flatMap(i -> tDlAlertHandlerRelationService.lambdaQuery()
                            .eq(TDlAlertHandlerRelation::getHandlerId, i.getHandlerId())
                            .eq(TDlAlertHandlerRelation::getAlertId, i.getAlertId())
                            .list().stream()
                            .map(TDlAlertHandlerRelation::getId))
                    .collect(Collectors.toList());

            Assert.isTrue(
                    tDlAlertHandlerRelationService.removeBatchByIds(unbindingRelationIds),
                    () -> new DatabaseException("解绑过程操作失败")
            );
        }

        // 绑定
        if (!CollUtil.isEmpty(bindingList)) {
            List<TDlAlertHandlerRelation> bindingRelations = bindingList.stream()
                    .map(i -> {
                        TDlAlertHandlerRelation relation = new TDlAlertHandlerRelation();
                        relation.setVersion(0L);
                        relation.setHandlerId(i.getHandlerId());
                        relation.setAlertId(i.getAlertId());
                        relation.setHandlerType(i.getAlertHandlerTypeEnum());
                        return relation;
                    })
                    .collect(Collectors.toList());

            Assert.isTrue(
                    tDlAlertHandlerRelationService.saveBatch(bindingRelations),
                    () -> new DatabaseException("绑定过程操作失败")
            );
        }


        return Result.success();
    }

    /**
     * Description: 获取处理方式 ID 是否存在的映射
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param handlerIdList 处理方式 ID 列表
     * @return Map<Long, Boolean> 处理方式 ID 是否存在的映射
     */
    private Map<Long, Boolean> getHandlerIdExistsMap(List<Long> handlerIdList) {
        // 如果没有需要检查的 ID 列表,直接返回空 Map
        if (CollUtil.isEmpty(handlerIdList)) {
            return new HashMap<>();
        }

        // 批量查询处理方式是否存在
        List<TDlAlertHandlerInterface> handlerInterfaces = tDlAlertHandlerInterfaceService.lambdaQuery()
                .select()
                .in(TBasePo::getId, handlerIdList)
                .list();
        List<TDlAlertHandlerMail> handlerMails = tDlAlertHandlerMailService.lambdaQuery()
                .select()
                .in(TBasePo::getId, handlerIdList)
                .list();

        // 构建 Map
        return handlerIdList.stream()
                .collect(
                        Collectors.toMap(
                                id -> id,
                                id -> handlerInterfaces
                                        .stream()
                                        .anyMatch(
                                                i -> i.getId().equals(id))
                                        || handlerMails.stream().anyMatch(i -> i.getId().equals(id))
                        )
                );
    }

    /**
     * Description: 获取告警 ID 是否存在的映射
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alertIdList 告警 ID 列表
     * @return Map<Long, Boolean> 告警 ID 是否存在的映射
     */
    private Map<Long, Boolean> getAlertIdExistsMap(List<Long> alertIdList) {
        // 如果没有需要检查的 ID 列表,直接返回空 Map
        if (CollUtil.isEmpty(alertIdList)) {
            return new HashMap<>();
        }

        // 批量查询告警是否存在
        List<TDlAlert> alerts = tDlAlertService.lambdaQuery()
                .select()
                .in(TBasePo::getId, alertIdList)
                .list();

        // 构建 Map
        return alertIdList.stream()
                .collect(
                        Collectors.toMap(
                                id -> id,
                                id -> alerts.stream()
                                        .anyMatch(i -> i.getId().equals(id))
                        )
                );
    }


    /**
     * Description: 批量删除告警处理方式
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警处理方式列表
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @LocalLock
    public Result<String> removeBatchAlertHandler(AbstractAlertHandlerRequest.AlertHandlerIdTypeListRequest request) {
        request.getAlertHandlerIdTypeList()
                .forEach(
                        i -> {
                            // 检查 ID 是否合法
                            this.checkRemoveHandlerList(
                                    i.getAlertHandlerTypeEnum(),
                                    i.getAlertHandlerIdList()
                            );

                            // 执行删除
                            this.removeHandlerBatch(
                                    i.getAlertHandlerTypeEnum(),
                                    i.getAlertHandlerIdList()
                            );
                        }
                );

        return Result.success();
    }

    /**
     * Description: 批量删除告警处理方式
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alertHandlerTypeEnum 告警处理类型
     * @param alertHandlerList     告警处理类型列表
     */
    private void removeHandlerBatch(AlertHandlerTypeEnum alertHandlerTypeEnum, List<Long> alertHandlerList) {
        switch (alertHandlerTypeEnum) {
            case ALERT_INTERFACE:
                Assert.isTrue(
                        this.tDlAlertHandlerInterfaceService.removeBatchByIds(alertHandlerList),
                        () -> new DatabaseException("删除数据失败")
                );

                break;
            case ALERT_MAIL:
                Assert.isTrue(
                        this.tDlAlertHandlerMailService.removeBatchByIds(alertHandlerList),
                        () -> new DatabaseException("删除数据失败")
                );
                break;
            default:
                throw new BException(
                        String.format(
                                "暂未支持的告警处理方式: %s",
                                alertHandlerTypeEnum.getMessage()
                        )
                );
        }
    }

    /**
     * Description: 检查 ID 是否存在
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alertHandlerTypeEnum 告警处理类型
     * @param alertHandlerList     告警处理类型列表
     */
    private void checkRemoveHandlerList(AlertHandlerTypeEnum alertHandlerTypeEnum, List<Long> alertHandlerList) {
        List<Long> tDlAlertHandlerIdFromDbList;

        switch (alertHandlerTypeEnum) {
            case ALERT_INTERFACE:
                tDlAlertHandlerIdFromDbList = this.tDlAlertHandlerInterfaceService.lambdaQuery()
                        .select()
                        .in(TBasePo::getId, alertHandlerList)
                        .list()
                        .stream()
                        .map(TBasePo::getId)
                        .collect(Collectors.toList());
                break;
            case ALERT_MAIL:
                tDlAlertHandlerIdFromDbList = this.tDlAlertHandlerMailService.lambdaQuery()
                        .select()
                        .in(TBasePo::getId, alertHandlerList)
                        .list()
                        .stream()
                        .map(TBasePo::getId)
                        .collect(Collectors.toList());
                break;
            default:
                throw new BException(
                        String.format(
                                "暂未支持的告警处理方式: %s",
                                alertHandlerTypeEnum.getMessage()
                        )
                );
        }

        // 检查是否存在绑定关系，如果存在则不允许删除
        List<Long> tDlAlertHandlerRelationHandlerIdList = this.tDlAlertHandlerRelationService.lambdaQuery()
                .select()
                .in(TDlAlertHandlerRelation::getHandlerId, tDlAlertHandlerIdFromDbList)
                .list()
                .stream()
                .map(TDlAlertHandlerRelation::getHandlerId)
                .collect(Collectors.toList());

        Assert.isTrue(
                tDlAlertHandlerRelationHandlerIdList.isEmpty(),
                () -> new BException(
                        String.format(
                                "删除前，请先解绑: %s",
                                tDlAlertHandlerRelationHandlerIdList
                        )
                )
        );


        // 检查待移除的告警处理方式是否存在
        this.checkRemoveHandlerListExist(
                alertHandlerList,
                tDlAlertHandlerIdFromDbList
        );
    }

    /**
     * Description: 检查待移除的 ID 是否全部存在
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param listFromRequestList 请求报文中的 ID 列表
     * @param listFromDbList      从数据库中获取的 ID 列表
     */
    private void checkRemoveHandlerListExist(List<Long> listFromRequestList, List<Long> listFromDbList) {

        List<Long> differences = listFromRequestList.stream()
                .filter(element -> !listFromDbList.contains(element))
                .collect(Collectors.toList());

        Assert.isTrue(
                differences.isEmpty(),
                () -> new BException(
                        String.format(
                                "部分 ID 不存在: %s",
                                differences
                        )
                )

        );
    }

    /**
     * Description: 根据告警 ID 获取告警与处理方式的绑定关系
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alertId 告警 ID
     * @return Result<AbstractAlertHandlerVo.AlertHandlerListVo> 告警处理方式列表
     */
    public Result<AbstractAlertHandlerVo.AlertHandlerListVo> getBindingAlertHandlerByAlertId(Long alertId) {
        return Result.success(
                new AbstractAlertHandlerVo.AlertHandlerListVo(
                        alertId,
                        this.getAlertHandlerIdTypeListByAlertId(alertId)
                )
        );
    }

    /**
     * Description: 根据处理方式 ID 获取告警与处理方式的绑定关系
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param handlerId 处理方式 ID
     * @return Result<AbstractAlertHandlerVo.AlertHandlerListVo> 告警处理方式列表
     */
    public Result<AbstractAlertHandlerVo.HandlerAndAlertIdListVo> getBindingAlertHandlerByHandlerId(Long handlerId) {

        AbstractAlertHandlerVo.HandlerAndAlertIdListVo handlerAndAlertIdListVo = new AbstractAlertHandlerVo.HandlerAndAlertIdListVo();
        handlerAndAlertIdListVo.setHandlerId(handlerId);
        handlerAndAlertIdListVo.setHandlerAndAlertIdList(
                this.tDlAlertHandlerRelationService.lambdaQuery()
                        .select()
                        .eq(TDlAlertHandlerRelation::getHandlerId, handlerId)
                        .list()
                        .stream()
                        .map(i -> new AbstractAlertHandlerVo.HandlerAndAlertIdVo(
                                        i.getHandlerType(),
                                        i.getAlertId()
                                )
                        )
                        .collect(Collectors.toList())
        );


        return Result.success(handlerAndAlertIdListVo);
    }

    /**
     * Description: 根据告警 ID 获取存在绑定的告警处理方式列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/5/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alertId 告警 ID
     * @return List<AbstractAlertVo.AlertHandlerIdTypeListVo> 告警处理方式 ID 与类型列表
     */
    public List<AbstractAlertHandlerVo.AlertHandlerVo> getAlertHandlerIdTypeListByAlertId(Long alertId) {
        // 直接查询并按处理类型分组，避免中间变量的使用
        Map<AlertHandlerTypeEnum, List<Long>> handlerTypeMap = this.tDlAlertHandlerRelationService.lambdaQuery()
                .select(TDlAlertHandlerRelation::getHandlerType, TDlAlertHandlerRelation::getHandlerId)
                .eq(TDlAlertHandlerRelation::getAlertId, alertId)
                .list()
                .stream()
                .collect(Collectors.groupingBy(
                        TDlAlertHandlerRelation::getHandlerType,
                        LinkedHashMap::new,
                        Collectors.mapping(TDlAlertHandlerRelation::getHandlerId, Collectors.toList())
                ));

        // 构造结果列表
        return handlerTypeMap.entrySet()
                .stream()
                .map(entry -> new AbstractAlertHandlerVo.AlertHandlerVo(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .collect(Collectors.toList());
    }


}
