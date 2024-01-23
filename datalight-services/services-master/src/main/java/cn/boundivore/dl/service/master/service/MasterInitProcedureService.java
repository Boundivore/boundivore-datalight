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
import cn.boundivore.dl.base.request.impl.master.AbstractProcedureRequest;
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
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
@Slf4j
public class MasterInitProcedureService {

    private final TDlInitProcedureServiceImpl tDlInitProcedureService;

    private final TDlClusterServiceImpl tDlClusterService;

    private final IInitProcedureConverter iInitProcedureConverter;

    private final ObjectMapper objectMapper = new ObjectMapper();


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
    public Result<AbstractInitProcedureVo.InitProcedureVo> persistInitStatus(AbstractProcedureRequest.PersistProcedureRequest request) {

        // 检查进度保存合法性
        TDlInitProcedure tDlInitProcedure = this.checkProcedureIllegal(request);
        // 更新或保存本次记录
        Assert.isTrue(
                this.tDlInitProcedureService.saveOrUpdate(tDlInitProcedure),
                () -> new DatabaseException("保存或更新状态失败")
        );

        AbstractInitProcedureVo.InitProcedureVo initProcedureVo = iInitProcedureConverter.convert2InitProcedureVo(
                tDlInitProcedure
        );
        initProcedureVo.setNodeInfoList(
                this.base642NodeInfoList(
                        tDlInitProcedure.getNodeInfoListBase64()
                )
        );


        return Result.success(initProcedureVo);
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
    public TDlInitProcedure checkProcedureIllegal(AbstractProcedureRequest.PersistProcedureRequest request) {

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
                tDlCluster.getClusterState() != ClusterStateEnum.REMOVED,
                () -> new BException("集群已移除或废弃")
        );

        // 获取当前集群已记录的步骤信息
        TDlInitProcedure tDlInitProcedure = this.tDlInitProcedureService.lambdaQuery()
                .select()
                .eq(TDlInitProcedure::getClusterId, request.getClusterId())
                .one();

        if (tDlInitProcedure == null) {
            tDlInitProcedure = new TDlInitProcedure();
        }

        tDlInitProcedure.setClusterId(request.getClusterId());
        tDlInitProcedure.setProcedureName(request.getProcedureStateEnum().getMessage());
        tDlInitProcedure.setProcedureState(request.getProcedureStateEnum());
        tDlInitProcedure.setNodeJobId(request.getNodeJobId());
        if (CollUtil.isNotEmpty(request.getNodeInfoList())) {
            tDlInitProcedure.setNodeInfoListBase64(this.nodeInfoList2Base64(request.getNodeInfoList()));
        } else {
            tDlInitProcedure.setNodeInfoListBase64(null);
        }

        return tDlInitProcedure;
    }

    /**
     * Description: 将节点信息转换为 Base64 存储在 MySQL
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeInfoList 节点信息列表
     * @return base64 字符串
     */
    public String nodeInfoList2Base64(List<AbstractProcedureRequest.NodeInfoListRequest> nodeInfoList) {
        if (CollUtil.isNotEmpty(nodeInfoList)) {
            try {
                // 将节点信息列表转换为JSON字符串
                String json = objectMapper.writeValueAsString(nodeInfoList);
                // 使用 Base64 编码 JSON 字符串
                return Base64.encode(json);
            } catch (JsonProcessingException e) {
                log.error(ExceptionUtil.stacktraceToString(e));
                Assert.isTrue(
                        true,
                        () -> new BException("节点信息列表序列化失败")
                );
            }
        }

        return null;
    }


    /**
     * Description: 将 MySQL 中的节点信息转换为 List<AbstractProcedureRequest.NodeInfoListRequest>
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param base64 节点信息列表 Base64
     * @return base64 字符串
     */
    public List<AbstractInitProcedureVo.NodeInfoListVo> base642NodeInfoList(String base64) {
        if (StrUtil.isNotBlank(base64)) {
            try {
                return objectMapper.readerFor(new TypeReference<List<AbstractInitProcedureVo.NodeInfoListVo>>() {
                        })
                        .readValue(Base64.decode(base64));
            } catch (Exception e) {
                log.error(ExceptionUtil.stacktraceToString(e));
                Assert.isTrue(
                        true,
                        () -> new BException("节点信息列表反序列化失败")
                );
            }
        }

        return CollUtil.newArrayList();
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

        AbstractInitProcedureVo.InitProcedureVo initProcedureVo = iInitProcedureConverter.convert2InitProcedureVo(
                tDlInitProcedure
        );
        initProcedureVo.setNodeInfoList(
                this.base642NodeInfoList(
                        tDlInitProcedure.getNodeInfoListBase64()
                )
        );

        return Result.success(
                initProcedureVo
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
     * Description: 查询所有存在未完成步骤的集群列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return List<Long> 集群 ID 列表
     */
    public Set<Long> getClusterIdListWithInProcedure() {
        return this.tDlInitProcedureService.lambdaQuery()
                .select()
                .list()
                .stream()
                .map(TDlInitProcedure::getClusterId)
                .collect(Collectors.toSet());
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
     * @return Result<String> 成功或失败
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
                () -> new DatabaseException("无对应步骤记录")
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


    /**
     * Description: 记录节点初始化信息步骤
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId          集群 ID
     * @param nodeJobId          节点作业 ID
     * @param procedureStateEnum 步骤状态枚举
     * @param nodeInfoList       节点初始信息列表
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void persistNodeInitProcedure(Long clusterId,
                                         Long nodeJobId,
                                         ProcedureStateEnum procedureStateEnum,
                                         List<AbstractProcedureRequest.NodeInfoListRequest> nodeInfoList) {
        // 记录步骤信息
        boolean isPersistProcedureSuccess = this.persistInitStatus(
                new AbstractProcedureRequest.PersistProcedureRequest(
                        clusterId,
                        procedureStateEnum,
                        nodeJobId,
                        nodeInfoList,
                        null
                )
        ).isSuccess();

        Assert.isTrue(
                isPersistProcedureSuccess,
                () -> new BException("保存节点步骤信息失败")
        );
    }

    /**
     * Description: 记录节点初始化信息步骤
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId          集群 ID
     * @param procedureStateEnum 步骤状态枚举
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void persistServiceComponentProcedure(Long clusterId,
                                                 Long jobId,
                                                 ProcedureStateEnum procedureStateEnum) {
        // 记录步骤信息
        boolean isPersistProcedureSuccess = this.persistInitStatus(
                new AbstractProcedureRequest.PersistProcedureRequest(
                        clusterId,
                        procedureStateEnum,
                        null,
                        null,
                        jobId
                )
        ).isSuccess();

        Assert.isTrue(
                isPersistProcedureSuccess,
                () -> new BException("保存服务组件步骤信息失败")
        );
    }

}
