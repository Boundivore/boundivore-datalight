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
import cn.boundivore.dl.base.enumeration.impl.ClusterStateEnum;
import cn.boundivore.dl.base.enumeration.impl.ProcedureStateEnum;
import cn.boundivore.dl.base.request.impl.master.PersistProcedureRequest;
import cn.boundivore.dl.base.request.impl.master.RemoveProcedureRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractInitProcedureVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.TBasePo;
import cn.boundivore.dl.orm.po.single.TDlCluster;
import cn.boundivore.dl.orm.po.single.TDlInitProcedure;
import cn.boundivore.dl.orm.service.single.impl.TDlClusterServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlInitProcedureServiceImpl;
import cn.boundivore.dl.service.master.converter.IInitProcedureConverter;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 集群节点初始化、服务安装等步骤记录接口服务类，需要考虑多人、多端同时操作的容错性和程序健壮
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/12/29
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class MasterInitProcedureService {

    private TDlInitProcedureServiceImpl tDlInitProcedureService;

    private TDlClusterServiceImpl tDlClusterService;

    private IInitProcedureConverter iInitProcedureConverter;


    /**
     * Description: 记录初始化步骤
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 记录状态请求体
     * @return Result<AbstractInitProcedureVo.InitProcedureVo> 返回保存后的 InitProcedure 实体
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<AbstractInitProcedureVo.InitProcedureVo> persistInitStatus(PersistProcedureRequest request) {

        // 检查进度保存合法性
        TDlInitProcedure tDlInitProcedure = this.checkProcedureIllegal(request);
        // 更新或保存本次记录
        Assert.isTrue(
                this.tDlInitProcedureService.saveOrUpdate(tDlInitProcedure),
                () -> new DatabaseException("保存或更新状态失败")
        );

        return Result.success(
                iInitProcedureConverter.convert2InitProcedureVo(tDlInitProcedure)
        );
    }

    /**
     * Description: 检查即将记录的状态的合法性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 记录状态请求体
     * @return TDlInitProcedure 准备保存或更新的数据库实体
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public TDlInitProcedure checkProcedureIllegal(PersistProcedureRequest request) {

        // 检查集群合法性
        Long clusterId = request.getClusterId();
        TDlCluster tDlCluster = this.tDlClusterService.lambdaQuery()
                .select()
                .eq(TBasePo::getId, clusterId)
                .one();

        Assert.notNull(
                tDlCluster,
                () -> new BException(
                        String.format(
                                "不存在的集群 ID: %s",
                                clusterId
                        )
                )
        );

        Assert.isTrue(
                tDlCluster.getClusterState() == ClusterStateEnum.REMOVED,
                () -> new BException("集群已移除或废弃")
        );

        // 检查 Tag 是否存在
        String tag;
        TDlInitProcedure tDlInitProcedure;
        if (StrUtil.isNotBlank(request.getTag())) {
            tag = request.getTag();
            tDlInitProcedure = this.tDlInitProcedureService.lambdaQuery()
                    .select()
                    .eq(TDlInitProcedure::getTag, tag)
                    .one();

            Assert.notNull(
                    tDlInitProcedure,
                    () -> new BException(String.format(
                            "不存在的步骤 Tag : %s",
                            tag
                    ))
            );

        } else {
            tag = IdUtil.objectId();
            tDlInitProcedure = new TDlInitProcedure();
            tDlInitProcedure.setTag(tag);
            tDlInitProcedure.setClusterId(request.getClusterId());
        }

        tDlInitProcedure.setProcedureName(request.getProcedureStateEnum().getMessage());
        tDlInitProcedure.setProcedureState(request.getProcedureStateEnum());

        return tDlInitProcedure;
    }

    /**
     * Description: 获取当前初始化步骤信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Result<AbstractInitProcedureVo.InitProcedureVo> 初始化步骤信息响应体
     */
    public Result<AbstractInitProcedureVo.InitProcedureVo> getInitProcedure(Long clusterId) {

        TDlInitProcedure tDlInitProcedure = this.tDlInitProcedureService.lambdaQuery()
                .select()
                .eq(TDlInitProcedure::getClusterId, clusterId)
                .one();

        Assert.notNull(
                tDlInitProcedure,
                () -> new DatabaseException("无对应记录")
        );

        return Result.success(
                this.iInitProcedureConverter.convert2InitProcedureVo(tDlInitProcedure)
        );
    }

    /**
     * Description: 查询是否存在记录的步骤信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Result<Boolean> 是否存在步骤信息记录
     */
    public Result<Boolean> isExistInitProcedure(Long clusterId) {
        boolean isExist = this.tDlInitProcedureService.lambdaQuery()
                .select()
                .eq(TDlInitProcedure::getClusterId, clusterId)
                .exists();
        return Result.success(isExist);
    }

    /**
     * Description: 清除指定集群的初始化步骤信息记录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 包含集群 ID 的请求体
     * @return Result<String>
     */
    public Result<String> removeInitProcedure(RemoveProcedureRequest request) {
        TDlInitProcedure tDlInitProcedure = this.tDlInitProcedureService.lambdaQuery()
                .select()
                .eq(TDlInitProcedure::getClusterId, request.getClusterId())
                .one();

        Assert.notNull(
                tDlInitProcedure,
                () -> new DatabaseException("无对应记录")
        );

        Assert.isTrue(
                this.tDlInitProcedureService.removeById(tDlInitProcedure),
                () -> new DatabaseException("操作失败")
        );

        return Result.success();
    }


    /**
     * Description: 检查当前操作是否合法
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId          集群 ID
     * @param procedureStateEnum 已经完成的步骤状态
     * @return boolean 是否合法
     */
    public Result<Boolean> checkOperationIllegal(Long clusterId, ProcedureStateEnum procedureStateEnum) {

        TDlInitProcedure tDlInitProcedure = this.tDlInitProcedureService.lambdaQuery()
                .select()
                .eq(TDlInitProcedure::getClusterId, clusterId)
                .one();

        Assert.notNull(
                tDlInitProcedure,
                () -> new DatabaseException("无对应记录")
        );

        ProcedureStateEnum currentInitProcedureStateEnum = tDlInitProcedure.getProcedureState();

        Assert.isTrue(
                currentInitProcedureStateEnum == procedureStateEnum ||
                        currentInitProcedureStateEnum.next() == procedureStateEnum,
                () -> new BException(
                        String.format(
                                "非法的操作, 当前步骤处于: %s, Name: %s, Code: %s",
                                currentInitProcedureStateEnum,
                                currentInitProcedureStateEnum.getMessage(),
                                currentInitProcedureStateEnum.getCode()
                        )
                )
        );

        return Result.success(true);
    }


}
