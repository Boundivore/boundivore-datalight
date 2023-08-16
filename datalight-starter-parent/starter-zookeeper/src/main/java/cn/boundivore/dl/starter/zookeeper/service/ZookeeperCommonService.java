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
package cn.boundivore.dl.starter.zookeeper.service;

import cn.boundivore.dl.starter.zookeeper.listener.ZkConnectionStateListener;
import cn.boundivore.dl.starter.zookeeper.properties.ZookeeperServerProperties;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Description: ZookeeperCommonService
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
public class ZookeeperCommonService {

    public static final String BASE_PATH = "/DATALIGHT/LOCK";

    public static String APP_BASE_PATH;

    private CuratorFramework client;

    @Resource
    private ZookeeperServerProperties zookeeperServerProperties;

    /**
     * Description: Init CuratorFramework client
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     */
    @SneakyThrows
    @PostConstruct
    public void init() {

        if (zookeeperServerProperties == null
                || zookeeperServerProperties.getNamespace() == null
                || zookeeperServerProperties.getConnectString() == null
                || zookeeperServerProperties.getConnectionTimeout() <= 0
                || zookeeperServerProperties.getSessionTimeout() <= 0) {
            return;
        }

        APP_BASE_PATH = String.format("%s/%s",
                BASE_PATH,
                zookeeperServerProperties.getNamespace()
        );

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);

        this.client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperServerProperties.getConnectString())
                .sessionTimeoutMs(zookeeperServerProperties.getSessionTimeout() * 1000)
                .connectionTimeoutMs(zookeeperServerProperties.getConnectionTimeout() * 1000)
                .retryPolicy(retryPolicy)
                .namespace(zookeeperServerProperties.getNamespace())
                .build();

        this.client.start();

        try {
            this.client.blockUntilConnected();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }

        initStateLister();

        createForeverNode(APP_BASE_PATH);

        log.info("Zookeeper's client init succeed");
    }

    /**
     * Description: Set the zookeeper listener.
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     */
    public void initStateLister() {
        Assert.notNull(client, "Could not init CuratorFramework client");
        client.getConnectionStateListenable().addListener(
                new ZkConnectionStateListener()
        );
    }


    /**
     * Description: checkNodeExists
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param path
     * @return boolean
     */
    @SneakyThrows
    public boolean checkNodeExists(String path) {
        return client.checkExists().forPath(path) != null;
    }

    /**
     * Description: createForeverNode
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param path format: ${functionName}_${variable}
     * @return String operation result if any
     */
    @SneakyThrows
    public String createForeverNode(String path) {
        if (!checkNodeExists(path)) {
            return client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path);
        }
        return null;
    }

    /**
     * Description: createTempNode
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param path
     * @return String operation result if any
     */
    @SneakyThrows
    public String createTempNode(String path) {
        if (!checkNodeExists(path)) {
            return client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path);
        }

        return null;
    }

    /**
     * Description: newInterProcessMutex
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param path
     * @return boolean
     */
    @SneakyThrows
    public InterProcessMutex newInterProcessMutex(String path) {
        return new InterProcessMutex(client, path);
    }

    /**
     * Description: acquireLockNode
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param mutex
     * @param waitTime
     * @param timeUnit
     * @return boolean
     */
    @SneakyThrows
    public boolean acquireLockNode(InterProcessMutex mutex, long waitTime, TimeUnit timeUnit) {
        return mutex.acquire(waitTime, timeUnit);
    }

    /**
     * Description: releaseLockNodeNode
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     */
    @SneakyThrows
    public void releaseLockNode(InterProcessMutex mutex) {
        if (mutex != null && mutex.isAcquiredInThisProcess()) {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @SneakyThrows
                    @Override
                    public void afterCompletion(int status) {
                        mutex.release();
                    }
                });

            } else {
                mutex.release();
            }
        }
    }

    /**
     * Description: setNodeData
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param dataNodePath
     * @return jsonData
     */
    @SneakyThrows
    public void setNodeData(String dataNodePath, String jsonData) {
        client.setData().forPath(dataNodePath, jsonData.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Description: getNodeData
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param dataNodePath
     * @return String
     */
    @SneakyThrows
    public String getNodeData(String dataNodePath) {
        byte[] bytes = client.getData().forPath(dataNodePath);
        return StrUtil.str(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Description: deleteNodeData
     * Created by: Boundivore
     * Creation time: 2023/5/22
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param path
     * @return String
     */
    @SneakyThrows
    public void deleteNodeData(String path) {
        client.delete().forPath(path);
    }

}
