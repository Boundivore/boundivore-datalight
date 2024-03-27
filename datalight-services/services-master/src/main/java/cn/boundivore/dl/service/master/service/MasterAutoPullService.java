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

import cn.boundivore.dl.base.constants.AutoPullSwitchState;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
import cn.boundivore.dl.base.request.impl.common.AbstractAutoPullRequest;
import cn.boundivore.dl.base.response.impl.master.AutoPullProcessVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    // 多线程拉起 Worker 线程池
    private final ForkJoinPool forkJoinPool = new ForkJoinPool(4);


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
    public Result<String> switchAutoPullWorker(AbstractAutoPullRequest.AutoPullWorkerRequest request) {
        AutoPullSwitchState.setCloseAutoPullWorker(
                request.getAutoPullWorker(),
                request.getCloseDuration()
        );

        return Result.success();
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
    public Result<String> switchAutoPullComponent(AbstractAutoPullRequest.AutoPullComponentRequest request) {
        AutoPullSwitchState.setCloseAutoPullComponent(
                request.getAutoPullComponent(),
                request.getCloseDuration()
        );

        // 更新自动拉起 Component 开关状态到 Worker 进程
        this.updateAutoPullComponentSwitchToWorker();

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
    public Result<AutoPullProcessVo> getAutoPullState() {
        return Result.success(
                new AutoPullProcessVo(
                        AutoPullSwitchState.AUTO_PULL_WORKER,
                        AutoPullSwitchState.AUTO_PULL_COMPONENT
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
    public void updateAutoPullComponentSwitchToWorker() {
        // <ip, WorkerMeta> 获取全部状态为 STARTED 的节点
        List<String> startedNodeIpV4List = this.masterNodeService.getNodeListByState(
                        CollUtil.newArrayList(
                                NodeStateEnum.STARTED
                        )
                )
                .stream()
                .map(TDlNode::getIpv4)
                .collect(Collectors.toList());

        this.forkJoinPool.submit(() -> {
                    startedNodeIpV4List.parallelStream()
                            .forEach(ip -> {
                                        try {
                                            // 更新组件拉起开关状态到 Worker 进程
                                            this.remoteInvokeWorkerService.iWorkerAutoPullAPI(ip)
                                                    .switchAutoPullComponent(
                                                            new AbstractAutoPullRequest.AutoPullComponentRequest(
                                                                    AutoPullSwitchState.AUTO_PULL_COMPONENT,
                                                                    AutoPullSwitchState.AUTO_CLOSE_DURATION_COMPONENT
                                                            )
                                                    );
                                        } catch (Exception e) {
                                            log.error(ExceptionUtil.stacktraceToString(e));
                                        }
                                    }
                            );
                }
        );
    }

}
