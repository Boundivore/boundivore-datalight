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

import cn.boundivore.dl.base.constants.AutoPullComponentState;
import cn.boundivore.dl.base.constants.AutoPullWorkerState;
import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.base.enumeration.impl.AutoPullSwitchTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.request.impl.common.AbstractAutoPullRequest;
import cn.boundivore.dl.base.response.impl.master.AutoPullProcessVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlAutoPullSwitch;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.orm.service.single.impl.TDlAutoPullSwitchServiceImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * Description: 进程自动拉起开关状态切换
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/3/21
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterAutoPullService {

    private final MasterNodeService masterNodeService;

    private final RemoteInvokeWorkerService remoteInvokeWorkerService;

    private final TDlAutoPullSwitchServiceImpl tDlAutoPullSwitchService;

    // 多线程拉起 Worker 线程池
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(4);

    /**
     * Description: 初始化时从数据库恢复开关状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @PostConstruct
    public void init() {
        this.initAutoPullWorkerFromDB();
        this.initAutoPullComponentFromDB();
    }

    /**
     * Description: 从数据库中恢复 Worker 进程自动拉起开关的状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    private void initAutoPullWorkerFromDB() {
        this.tDlAutoPullSwitchService.lambdaQuery()
                .select();

    }

    /**
     * Description: 从数据库中恢复 组件 进程自动拉起开关的状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    private void initAutoPullComponentFromDB() {

    }


    /**
     * Description: 将自动拉起 Worker 进程的开关切换至目标状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 将开关切换至目标状态
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> switchAutoPullWorker(AbstractAutoPullRequest.AutoPullWorkerRequest request) {

        // 检查请求体是否合理
        this.checkSwitchRequest(
                request.getAutoPullWorker(),
                request.getCloseDuration()
        );

        // 创建开关状态缓存
        AutoPullWorkerState.CacheBean cacheBean = new AutoPullWorkerState.CacheBean();
        cacheBean.setClusterId(request.getClusterId());
        cacheBean.updatePullWorker(request.getAutoPullWorker(), request.getCloseDuration());
        // 设置缓存
        AutoPullWorkerState.putAutoPullWorkerState(cacheBean);

        // 保存数据库
        TDlAutoPullSwitch tDlAutoPullSwitch = new TDlAutoPullSwitch();
        tDlAutoPullSwitch.setClusterId(cacheBean.getClusterId());
        tDlAutoPullSwitch.setAutoPullSwitchType(AutoPullSwitchTypeEnum.AUTO_PULL_WORKER);
        tDlAutoPullSwitch.setOffOn(cacheBean.isAutoPullWorker());
        tDlAutoPullSwitch.setCloseBeginTime(cacheBean.getCloseAutoPullBeginTimeWorker());
        tDlAutoPullSwitch.setCloseEndTime(cacheBean.getCloseAutoPullEndTimeWorker());

        Assert.isTrue(
                this.tDlAutoPullSwitchService.saveOrUpdate(tDlAutoPullSwitch),
                () -> new DatabaseException("保存或更新 Worker 自动拉起开关状态异常")
        );

        return Result.success();
    }

    /**
     * Description: 检查请求参数是否合理
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param autoPullState 自动拉起开关状态
     * @param closeDuration 自动拉起开关关闭状态持续时间
     */
    public void checkSwitchRequest(Boolean autoPullState, Long closeDuration) {
        if (autoPullState) {
            Assert.isTrue(
                    closeDuration == 0L,
                    () -> new IllegalArgumentException("开启自动拉起时，关闭状态持续时间请传递 0")
            );
        } else {
            Assert.isTrue(
                    closeDuration >= 60 * 1000L,
                    () -> new IllegalArgumentException("关闭自动拉起时，开关关闭状态持续时间需 >= 60 * 1000 ms")
            );
        }
    }

    /**
     * Description: 将自动拉起 Component 进程的开关切换至目标状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 将开关切换至目标状态
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> switchAutoPullComponent(AbstractAutoPullRequest.AutoPullComponentRequest request) throws ExecutionException, InterruptedException {
        // 检查请求体是否合理
        this.checkSwitchRequest(
                request.getAutoPullComponent(),
                request.getCloseDuration()
        );

        // 创建开关状态缓存
        AutoPullComponentState.CacheBean cacheBean = new AutoPullComponentState.CacheBean();
        cacheBean.setClusterId(request.getClusterId());
        cacheBean.updatePullComponent(request.getAutoPullComponent(), request.getCloseDuration());
        // 设置缓存
        AutoPullComponentState.putAutoPullComponentState(cacheBean);

        // 保存数据库
        TDlAutoPullSwitch tDlAutoPullSwitch = new TDlAutoPullSwitch();
        tDlAutoPullSwitch.setClusterId(cacheBean.getClusterId());
        tDlAutoPullSwitch.setAutoPullSwitchType(AutoPullSwitchTypeEnum.AUTO_PULL_COMPONENT);
        tDlAutoPullSwitch.setOffOn(cacheBean.isAutoPullComponent());
        tDlAutoPullSwitch.setCloseBeginTime(cacheBean.getCloseAutoPullBeginTimeComponent());
        tDlAutoPullSwitch.setCloseEndTime(cacheBean.getCloseAutoPullEndTimeComponent());


        Assert.isTrue(
                this.tDlAutoPullSwitchService.saveOrUpdate(tDlAutoPullSwitch),
                () -> new DatabaseException("保存或更新组件自动拉起开关状态异常")
        );

        return Result.success();
    }

    /**
     * Description: 返回进程自动拉起开关状态(包括 Worker 和 Component)
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Result<AutoPullProcessVo> 返回自动拉起开关状态
     */
    public Result<AutoPullProcessVo> getAutoPullState(Long clusterId) {
        AutoPullWorkerState.CacheBean autoPullWorkerState = AutoPullWorkerState.getAutoPullWorkerState(clusterId);
        AutoPullComponentState.CacheBean autoPullComponentState = AutoPullComponentState.getAutoPullComponentState(clusterId);

        return Result.success(
                new AutoPullProcessVo(
                        clusterId,
                        autoPullWorkerState.isAutoPullWorker(),
                        autoPullWorkerState.getCloseAutoPullBeginTimeWorker(),
                        autoPullWorkerState.getCloseAutoPullEndTimeWorker(),
                        autoPullComponentState.isAutoPullComponent(),
                        autoPullComponentState.getCloseAutoPullBeginTimeComponent(),
                        autoPullComponentState.getCloseAutoPullEndTimeComponent()
                )
        );
    }

    /**
     * Description: 更新自动拉起 Component 进程开关的状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/3/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public void updateAutoPullComponentSwitchToWorker(Long clusterId) throws ExecutionException, InterruptedException {
        // <ip, WorkerMeta> 获取全部状态为 STARTED 的节点
        List<String> startedNodeIpV4List = this.masterNodeService.getNodeListByState(
                        CollUtil.newArrayList(
                                NodeStateEnum.STARTED
                        )
                )
                .stream()
                .filter(i -> Objects.equals(i.getClusterId(), clusterId))
                .map(TDlNode::getIpv4)
                .collect(Collectors.toList());

        // 获取缓存
        AutoPullComponentState.CacheBean autoPullComponentState = AutoPullComponentState.getAutoPullComponentState(clusterId);

        this.forkJoinPool.submit(() -> {
                    startedNodeIpV4List.parallelStream()
                            .forEach(ip -> {
                                        try {
                                            // 更新组件拉起开关状态到 Worker 进程
                                            this.remoteInvokeWorkerService.iWorkerAutoPullAPI(ip)
                                                    .switchAutoPullComponent(
                                                            new AbstractAutoPullRequest.AutoPullComponentRequest(
                                                                    autoPullComponentState.getClusterId(),
                                                                    autoPullComponentState.isAutoPullComponent(),
                                                                    autoPullComponentState.getCloseAutoPullDurationComponent()
                                                            )
                                                    );
                                        } catch (Exception e) {
                                            log.error(ExceptionUtil.stacktraceToString(e));
                                        }
                                    }
                            );
                }
        ).get();
    }

}
