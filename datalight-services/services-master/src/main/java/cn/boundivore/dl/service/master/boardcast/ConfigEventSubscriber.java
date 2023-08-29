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
package cn.boundivore.dl.service.master.boardcast;

import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.cloud.utils.SpringContextUtilTest;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.mapper.custom.ComponentNodeMapper;
import cn.boundivore.dl.plugin.base.bean.PluginConfigEvent;
import cn.boundivore.dl.plugin.base.bean.PluginConfigResult;
import cn.boundivore.dl.plugin.base.bean.PluginConfigSelf;
import cn.boundivore.dl.plugin.base.config.event.IConfigEventHandler;
import cn.boundivore.dl.service.master.handler.RemoteInvokePrometheusHandler;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceManifest;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceManifest;
import cn.boundivore.dl.service.master.service.MasterClusterService;
import cn.boundivore.dl.service.master.service.MasterConfigService;
import cn.boundivore.dl.service.master.service.MasterConfigSyncService;
import cn.boundivore.dl.service.master.service.MasterServiceService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 消费订阅的消息
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConfigEventSubscriber {

    private final MasterConfigSyncService masterConfigSyncService;

    private final MasterClusterService masterClusterService;

    private final MasterServiceService masterServiceService;

    private final MasterConfigService masterConfigService;

    private final RemoteInvokePrometheusHandler remoteInvokePrometheusHandler;

    private final ComponentNodeMapper componentNodeMapper;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Builder
    private static class RelativeService {
        private Long clusterId;

        private ClusterTypeEnum clusterTypeEnum;

        private String serviceName;
    }


    /**
     * Description: 以下逻辑要充分考虑几个要素：
     * 1、当某个服务的配置文件发生变化，则首先要通知依赖该服务的其他服务 (dependencies 列表)；
     * 2、其次，要通知该服务会影响的其它服务（ relatives 列表）；
     * 3、如果当前为存储集群（也称之为混合集群 MIXED）集群，则除了要通知当前集群内的服务，还要通知计算集群中依赖该服务的服务；
     * 4、如果当前为计算集群，则只需要通知当前集群内自己的服务。
     * <p>
     * 注意：有些服务可能会彼此互相影响，因此可能会陷入循环，但某次循环退出的唯一情况是：直到彼此互相影响的服务所关联的配置文件不再发生变动，
     * 这个要依赖于 Plugin 中的实现。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param event 当前变更的配置文件
     */
    @EventListener
    @Async("commonExecutor")
    public synchronized void handleTopicEvent(ConfigEvent event) {

        final Long currentClusterId = event.getPluginConfigEvent().getClusterId();

        final String serviceName = event.getPluginConfigEvent().getServiceName();
        final PluginConfigEvent pluginConfigEvent = event.getPluginConfigEvent();

        log.info("发现服务 {} 中发生文件变动, Thread Id: {}", serviceName, Thread.currentThread().getId());

        // 读取当前服务配置中，依赖当前服务的服务列表，以及该服务可能会影响的服务列表
        List<RelativeService> relativeServiceList = Stream.concat(
                        // 读取依赖当前服务的服务名称
                        ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP.keySet()
                                .stream()
                                .map(ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP::get)
                                .filter(i -> i.getDependencies().contains(serviceName))
                                .map(YamlServiceManifest.Service::getName),

                        // 读取当前服务会影响的服务
                        ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP.get(serviceName)
                                .getRelatives()
                                .stream()
                )
                .filter(i -> !i.equals(serviceName)) // 提升容错：排除当前服务自己（防止开发者在配置列表中出现不合逻辑的配置）
                .distinct()
                .map(ResolverYamlServiceManifest.MANIFEST_SERVICE_MAP::get)
                .sorted(Comparator.comparing(YamlServiceManifest.Service::getPriority))
                .map(YamlServiceManifest.Service::getName)
                .flatMap(i -> this.relativeServiceList(currentClusterId, i).stream())
                .collect(Collectors.toList());

        // 调用受影响服务插件，联动修改受影响的服务的配置
        relativeServiceList.forEach(i -> {

                    YamlServiceDetail.Service service = ResolverYamlServiceDetail.SERVICE_MAP.get(i.getServiceName());
                    String jar = service.getConfigEventHandlerJar();
                    String clazzName = service.getConfigEventHandlerClazz();

                    this.invokeJar(
                            i.getClusterId(),
                            pluginConfigEvent,
                            i.getServiceName(),
                            jar,
                            clazzName
                    );

                }
        );

    }

    /**
     * Description: 调用插件 Jar 包，变更受影响的配置文件
     * 1. 初始化 IConfigEventHandler 实例
     * 2. 初始化 将变动的事件（包括配置文件内容） 传递到 IConfigEventHandler 中
     * 3. 询问插件稍后需要修改自身哪些配置
     * 4. 读取 3 中这些配置文件的当前内容并传递
     * 5. 插件结合 2、4 修改这些内容
     * 6. 保存修改后的内容
     * 举例：假设集群中部署了 ZOOKEEPER HDFS YARN 3个服务
     * 1. 当 ZOOKEEPER 某个 QuarumPeermain 的部署位置发生了变化（即 zoo.cfg）文件发生了变动
     * 2. 触发事件推送，检查到依赖 ZOOKEEPER 服务的相关服务，且处于非 UNSELECTED or REMOVED 状态
     * 3. 初始化插件所需的元数据信息，包括当前事件的内容（发生主动修改的、服务的、配置文件内容，称为事件 A）
     * 3. 读取 HDFS 所有配置文件列表，并传递给 HDFS 插件，获取本次涉及到修改的 HDFS 配置文件内容（插件根据事件 A 判断自己需要哪些配置文件内容）
     * 4. 主服务读取 3 中的对应配置文件，传递给插件进行修改，并得到修改后的配置文件，进行更新
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/24
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param relativeClusterId 被影响服务所在的集群 ID
     * @param pluginConfigEvent 配置文件变动的事件
     * @param serviceName       被影响的服务
     * @param jar               插件的 jar 包名称
     * @param clazzName         插件的入口类全类名
     */
    private void invokeJar(Long relativeClusterId,
                           PluginConfigEvent pluginConfigEvent,
                           String serviceName,
                           String jar,
                           String clazzName) {
        try {
            String jarParentPath = String.format(
                    "file:%s/%s/jars/%s",
                    SpringContextUtilTest.PLUGINS_PATH_DIR_LOCAL,
                    serviceName,
                    jar
            );
            log.info("Loading jar: {}", jarParentPath);

            URL url = new URL(jarParentPath);
            try (URLClassLoader ucl = new URLClassLoader(
                    new URL[]{url},
                    Thread.currentThread().getContextClassLoader())) {

                Class<?> clazz = ucl.loadClass(clazzName);

                if (IConfigEventHandler.class.isAssignableFrom(clazz)) {
                    IConfigEventHandler iConfigEventHandler = (IConfigEventHandler) clazz.getDeclaredConstructor().newInstance();
                    // 传递当前变动的事件
                    iConfigEventHandler.init(pluginConfigEvent);

                    // 读取受影响服务本次涉及修改的配置文件列表
                    List<String> relativeConfigPathList = iConfigEventHandler.getRelativeConfigPathList(
                            this.masterConfigService.getConfigPathList(
                                    relativeClusterId,
                                    serviceName
                            )
                    );

                    // 通过插件修改受影响服务的配置文件
                    PluginConfigResult pluginConfigResult = iConfigEventHandler.configByEvent(
                            // 封装受影响服务自己的配置文件信息
                            this.assemblePluginConfigEventSelf(
                                    relativeClusterId,
                                    serviceName,
                                    relativeConfigPathList
                            )
                    );

                    // 判断如果为有效的配置修改，则发送修改配置请求
                    if (!pluginConfigResult.getConfigMap().isEmpty()) {
                        Assert.isTrue(
                                this.masterConfigSyncService.saveConfigOrUpdateBatch(pluginConfigResult),
                                () -> new BException("订阅者修改配置失败")
                        );

                        // 重载 Prometheus 配置
                        this.reloadIfPrometheus(relativeClusterId, serviceName);
                    }

                } else {
                    throw new BException(
                            String.format(
                                    "该 class 未实现 %s %s %s 接口",
                                    "datalight-plugins",
                                    "plugin-base",
                                    "cn.boundivore.dl.plugin.base.config.event.IConfigEventHandler"
                            )

                    );
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: 获取受影响的服务以及所在的集群
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param currentClusterId    当前配置发生变动的集群
     * @param relativeServiceName 关联的服务
     * @return 受影响的服务以及所在的集群
     */
    private List<RelativeService> relativeServiceList(Long currentClusterId, String relativeServiceName) {
        List<RelativeService> relativeServiceList = new ArrayList<>();

        // 获取当前集群的类型
        final ClusterTypeEnum currentClusterTypeEnum = this.masterClusterService.getClusterById(currentClusterId)
                .getData()
                .getClusterTypeEnum();

        // 当前集群为 COMPUTE 或 MIXED 集群时，为当前集群自己添加受影响的服务列表
        // 获取当前集群中指定服务状态
        SCStateEnum serviceStateInCurrentCluster = this.masterServiceService.getServiceState(
                currentClusterId,
                relativeServiceName
        );

        if (serviceStateInCurrentCluster.isServiceDeployed()) {
            relativeServiceList.add(
                    new RelativeService(
                            currentClusterId,
                            currentClusterTypeEnum,
                            relativeServiceName
                    )
            );
        }

        // 当前集群为 MIXED 集群，则还需要判断被影响的 COMPUTE 集群中的被影响的服务
        if (currentClusterTypeEnum == ClusterTypeEnum.MIXED) {
            this.masterClusterService
                    .getComputeClusterListByRelativeClusterId(currentClusterId)
                    .getData()
                    .getClusterList()
                    .forEach(i -> {
                                SCStateEnum serviceStateInRelativeCluster = this.masterServiceService.getServiceState(
                                        i.getClusterId(),
                                        relativeServiceName
                                );
                                if (serviceStateInRelativeCluster.isServiceDeployed()) {
                                    relativeServiceList.add(
                                            new RelativeService(
                                                    i.getClusterId(),
                                                    i.getClusterTypeEnum(),
                                                    relativeServiceName
                                            )
                                    );
                                }

                            }
                    );


        }

        return relativeServiceList;

    }

    /**
     * Description: 返回受影响的配置文件详细信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/22
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId          当前集群 ID
     * @param configRelativePath 配置文件相对路径
     * @return 受影响的配置文件详细信息
     */
    private PluginConfigSelf assemblePluginConfigEventSelf(Long clusterId,
                                                           String serviceName,
                                                           List<String> configRelativePath) {

        // 组装配置文件分布情况
        List<PluginConfigSelf.ConfigSelfData> configSelfDataList = configRelativePath.stream()
                .flatMap(i -> this.masterConfigService.getConfigListByGroup(
                                        clusterId,
                                        serviceName,
                                        i
                                )
                                .getData()
                                .getConfigGroupList()
                                .stream()
                )
                .map(i -> {
                            List<PluginConfigSelf.ConfigSelfNode> configSelfNodeList = i.getConfigNodeList()
                                    .stream()
                                    .map(m -> new PluginConfigSelf.ConfigSelfNode(
                                                    m.getNodeId(),
                                                    m.getHostname(),
                                                    m.getNodeIp(),
                                                    m.getConfigVersion()
                                            )
                                    )
                                    .collect(Collectors.toList());

                            PluginConfigSelf.ConfigSelfData configSelfData = new PluginConfigSelf.ConfigSelfData();
                            configSelfData.setConfigPath(i.getConfigPath());
                            configSelfData.setConfigData(i.getConfigData());
                            configSelfData.setFilename(i.getFilename());
                            configSelfData.setSha256(i.getSha256());
                            configSelfData.setConfigSelfNodeList(configSelfNodeList);


                            return configSelfData;
                        }
                )
                .collect(Collectors.toList());


        //<ComponentName, List<ConfigSelfNode>>，组装组件分布情况
        Map<String, List<PluginConfigSelf.ConfigSelfComponentNode>> configSelfComponentNameMap =
                this.componentNodeMapper.selectComponentNodeNotInStatesDto(
                                clusterId,
                                serviceName,
                                CollUtil.newArrayList(
                                        SCStateEnum.UNSELECTED,
                                        SCStateEnum.REMOVED
                                )
                        )
                        .stream()
                        .map(i ->
                                new Pair<>(
                                        i.getComponentName(),
                                        new PluginConfigSelf.ConfigSelfComponentNode(
                                                i.getNodeId(),
                                                i.getHostname(),
                                                i.getIpv4()
                                        )
                                )
                        )
                        .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())));

        PluginConfigSelf pluginConfigSelf = new PluginConfigSelf();
        pluginConfigSelf.setClusterId(clusterId);
        pluginConfigSelf.setServiceName(serviceName);
        pluginConfigSelf.setConfigSelfComponentMap(configSelfComponentNameMap);
        pluginConfigSelf.setConfigSelfDataList(configSelfDataList);

        return pluginConfigSelf;
    }

    /**
     * Description: 重载 Prometheus 配置
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     */
    private void reloadIfPrometheus(Long clusterId, String serviceName) {
        if (serviceName.equals("MONITOR")) {
            this.remoteInvokePrometheusHandler.invokePrometheusReload(
                    clusterId
            );
        }
    }

}
