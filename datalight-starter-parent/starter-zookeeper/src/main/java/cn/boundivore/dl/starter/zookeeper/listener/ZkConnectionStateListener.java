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
package cn.boundivore.dl.starter.zookeeper.listener;

import cn.boundivore.dl.starter.zookeeper.service.ZookeeperCommonService;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;

/**
 * Description: ZkConnectionStateListener
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class ZkConnectionStateListener implements ConnectionStateListener {

    public ZkConnectionStateListener() {
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        if (newState == ConnectionState.LOST) {
            log.error("Lost session with zookeeper");
            while (true) {
                try {
                    log.warn("Retry to connect to zookeeper");
                    if (client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                        client.create()
                                .creatingParentsIfNeeded()
                                .withMode(CreateMode.EPHEMERAL)
                                .forPath(ZookeeperCommonService.APP_BASE_PATH);
                        break;
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                ThreadUtil.safeSleep(20);
            }
        } else if (newState == ConnectionState.CONNECTED) {
            log.warn("Connected with zookeeper");
        } else if (newState == ConnectionState.RECONNECTED) {
            log.warn("Reconnected with zookeeper");
        } else if (newState == ConnectionState.SUSPENDED) {
            log.warn("Connection SUSPENDED to zookeeper.");
        }
    }


}
