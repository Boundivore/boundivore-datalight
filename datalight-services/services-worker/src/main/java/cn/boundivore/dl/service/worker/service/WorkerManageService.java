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
package cn.boundivore.dl.service.worker.service;

import cn.boundivore.dl.base.constants.Constants;
import cn.boundivore.dl.base.request.impl.master.HeartBeatRequest;
import cn.boundivore.dl.base.request.impl.worker.MasterMetaRequest;
import cn.boundivore.dl.base.request.impl.worker.ServiceMetaRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.utils.ReactiveAddressUtil;
import cn.boundivore.dl.service.worker.cache.MetaCache;
import cn.boundivore.dl.service.worker.converter.IMasterMetaConverter;
import cn.boundivore.dl.service.worker.converter.IServiceMetaConverter;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Description: Worker 管理相关工作，包括接收 Master 位置暴露，Master 主从切换消息等
 * Created by: Boundivore
 * E-mail: boundivore@formail.com
 * Creation time: 2023/8/1
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerManageService {

    private final MetaCache metaCache;

    private final IMasterMetaConverter iMasterMetaConverter;

    private final IServiceMetaConverter iServiceMetaConverter;

    private final RemoteInvokeMasterService remoteInvokeMasterService;

    /**
     * Description: 更新 Master 位置信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 主要包含 Master 所在节点的信息
     * @return 更新成功或更新失败
     */
    public Result<String> updateMasterMeta(MasterMetaRequest request) {
        // Master 信息更新到 Worker 内存
        log.info("接收到 Master({}) 元数据信息，更新内存缓存", request.getIp());
        this.metaCache.updateMasterMeta(
                this.iMasterMetaConverter.convert2MasterMeta(request)
        );

        this.sendHeartBeat();

        return Result.success();
    }

    /**
     * Description: 更新服务的元数据信息，用于后续的服务下组件进程的自动拉起
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/14
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 服务元数据信息
     * @return 更新成功或更新失败
     */
    public Result<String> updateServiceMeta(ServiceMetaRequest request) {
        // Service 信息更新到 Worker 内存
        this.metaCache.updateMasterMeta(
                this.iServiceMetaConverter.convert2ServiceMeta(request)
        );
        return Result.success();
    }

    /**
     * Description: 定期向 Master 发送心跳包
     * EASY TO FIX: 可以通过动态修改 Trigger 动态改变定时任务的周期策略
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Scheduled(initialDelay = 10 * 1000, fixedDelay = Constants.HEART_BEAT_TIMEOUT / 3)
    private void sendHeartBeat() {
        MetaCache.MasterMeta masterMeta = this.metaCache.getMasterMeta();
        if (masterMeta != null) {
            try {
                // 随机延迟 100 ~ 4100 毫秒，防止出现集中访问 Master 造成性能波动
                long delaySeconds = RandomUtil.randomLong(4 * 1000L) + 100;
                ThreadUtil.safeSleep(delaySeconds);

                if (log.isDebugEnabled()) {
                    log.debug("{} ms 延迟结束, 向 {} 发送心跳包", delaySeconds, masterMeta.getIp());
                }

                this.remoteInvokeMasterService.iMasterManageAPI(
                        masterMeta.getIp()
                ).heartBeat(new HeartBeatRequest(
                        ReactiveAddressUtil.getInternalIPAddress()
                ));
            } catch (Exception e) {
                log.error("心跳包发送失败: {}", ExceptionUtil.getMessage(e));
            }
        }
    }

    /**
     * Description: 根据缓存信息，周期性检查并拉起应该处于启动状态的 Component
     * EASY TO FIX: 可以通过动态修改 Trigger 动态改变定时任务的周期策略
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 30 * 1000)
    private void checkAndPullComponent() {
        MetaCache.ServiceMeta serviceMeta = this.metaCache.getServiceMeta();
        if (serviceMeta != null) {
            log.info("检查并拉起服务");

        }

    }

}
