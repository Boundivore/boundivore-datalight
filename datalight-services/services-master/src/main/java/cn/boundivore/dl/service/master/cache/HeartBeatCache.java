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
package cn.boundivore.dl.service.master.cache;

import cn.boundivore.dl.base.constants.Constants;
import cn.boundivore.dl.orm.po.single.TDlNode;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Description: Worker 心跳包缓存
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HeartBeatCache {


    //<Ip, WorkerHeartBeat>
    @Getter
    private ConcurrentHashMap<String, WorkerHeartBeat> heartBeatMap;

    @PostConstruct
    public void init() {
        this.heartBeatMap = new ConcurrentHashMap<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class WorkerHeartBeat {

        private String ip;

        private long lastHeartBeatTs = 0L;

        public boolean isTimeout() {
            return System.currentTimeMillis() - lastHeartBeatTs > Constants.HEART_BEAT_TIMEOUT;
        }
    }

    /**
     * Description: 判断某个节点的 Worker 是否存已经缓存了心跳信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param ip IP 地址
     */
    public boolean isContains(String ip) {
        return this.heartBeatMap.containsKey(ip);
    }

    /**
     * Description: 停止、重启节点时，应该移除心跳列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param ip IP 地址
     */
    public void removeHeartBeat(String ip) {
        this.heartBeatMap.remove(ip);
    }

    /**
     * Description: 更新心跳包
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param ip Worker 节点 IP
     */
    public void updateHeartBeat(String ip) {


        WorkerHeartBeat newWorkerBeat = this.heartBeatMap
                .getOrDefault(
                        ip,
                        new WorkerHeartBeat()
                );

        long currentTs = System.currentTimeMillis();
        long lastTs = newWorkerBeat.getLastHeartBeatTs();
        long deltaTs = lastTs == 0L ? lastTs : currentTs - lastTs;

        newWorkerBeat.setIp(ip);
        newWorkerBeat.setLastHeartBeatTs(currentTs);


        if (log.isDebugEnabled()) {
            log.debug("接收并更新心跳包, 来自 ({}), 距上次 {} ms", ip, deltaTs);
        }

        log.info("接收并更新心跳包, 来自 ({}), 距上次 {} ms", ip, deltaTs);

        this.heartBeatMap.put(ip, newWorkerBeat);

    }

    /**
     * Description: 获取心跳包过期的 Worker 列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/2
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return List<WorkerHeartBeat>
     */
    public List<WorkerHeartBeat> getTimeoutWorkerHeartBeatList() {
        return this.heartBeatMap
                .values()
                .stream()
                .filter(WorkerHeartBeat::isTimeout)
                .collect(Collectors.toList());
    }

    /**
     * Description: 清除无效心跳包
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param startedWorkerTDlNodeMap 处于启动状态的节点列表
     */
    public void clearInvalidHearBeat(Map<String, TDlNode> startedWorkerTDlNodeMap) {
        List<String> invalidHearBeatIpList = this.heartBeatMap
                .values()
                .stream()
                .map(WorkerHeartBeat::getIp)
                .filter(ip -> !startedWorkerTDlNodeMap.containsKey(ip))
                .collect(Collectors.toList());

        if (!invalidHearBeatIpList.isEmpty()) {
            log.info("清理过期心跳包: {}", invalidHearBeatIpList);
            invalidHearBeatIpList.forEach(ip -> this.heartBeatMap.remove(ip));
        }

    }

}
