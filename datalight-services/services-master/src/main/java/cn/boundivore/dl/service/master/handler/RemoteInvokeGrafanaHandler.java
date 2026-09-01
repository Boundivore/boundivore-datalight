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
package cn.boundivore.dl.service.master.handler;

import cn.boundivore.dl.base.constants.PortConstants;
import cn.boundivore.dl.base.enumeration.impl.GrafanaUserRoleEnum;
import cn.boundivore.dl.base.enumeration.impl.GrafanaUserTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.utils.JsonUtil;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.po.single.TDlNode;
import cn.boundivore.dl.service.master.bean.GrafanaUser;
import cn.boundivore.dl.service.master.env.DataLightEnv;
import cn.boundivore.dl.service.master.service.MasterComponentService;
import cn.boundivore.dl.service.master.service.MasterNodeService;
import cn.boundivore.dl.service.master.service.RemoteInvokeGrafanaService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: Grafana 综合调用逻辑
 * 1、修改 Grafana 主账号（userId1）密码
 * 2、为当前集群创建 Org，并获取 orgId
 * 3、为当前集群 Org 创建用户（Admin），并获取该用户的 userId2
 * 4、将 userId2 加入到 orgId 中
 * 5、将 userId2 从主 org 中移除
 * 6、为当前集群 Org 创建用户（Editor），并获取该用户的 userId3
 * 7、将 userId3 加入到 orgId 中
 * 8、将 userId3 从主 org 中移除
 * 9、使用 userId2 的账号密码创建数据源，名称为 MONITOR-Prometheus，且为默认
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/8/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteInvokeGrafanaHandler {

    public final static String GRAFANA_BASE_ORG_NAME = "datalight";

    public static final GrafanaUser ADMIN_USER = GrafanaUser.getGrafanaUser(
            GRAFANA_BASE_ORG_NAME,
            GrafanaUserTypeEnum.ADMIN
    );
    public static final GrafanaUser ADMIN_DATALIGHT_USER = GrafanaUser.getGrafanaUser(
            GRAFANA_BASE_ORG_NAME,
            GrafanaUserTypeEnum.ADMIN_DATALIGHT
    );

    public static final String ADMIN_NEW_TOKEN = RemoteInvokeGrafanaService.basicAuthToken(
            ADMIN_USER.getLoginName(),
            ADMIN_USER.getNewLoginPassword()
    );
    public static final String ADMIN_OLD_TOKEN = RemoteInvokeGrafanaService.basicAuthToken(
            ADMIN_USER.getLoginName(),
            ADMIN_USER.getOldLoginPassword()
    );
    public static final String ADMIN_DATALIGHT_NEW_TOKEN = RemoteInvokeGrafanaService.basicAuthToken(
            ADMIN_DATALIGHT_USER.getLoginName(),
            ADMIN_DATALIGHT_USER.getNewLoginPassword()
    );


    private final RemoteInvokeGrafanaService remoteInvokeGrafanaService;

    private final MasterComponentService masterComponentService;

    private final MasterNodeService masterNodeService;


    /**
     * Description: 准备执行 Grafana 基础配置
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     */
    public void initGrafanaSettings(Long clusterId) {
        try {
            Map<GrafanaUserTypeEnum, GrafanaUser> grafanaUserMap = Stream.of(
                    new AbstractMap.SimpleEntry<>(
                            GrafanaUserTypeEnum.ADMIN,
                            GrafanaUser.getGrafanaUser(
                                    GRAFANA_BASE_ORG_NAME,
                                    GrafanaUserTypeEnum.ADMIN
                            )
                    ),
                    new AbstractMap.SimpleEntry<>(
                            GrafanaUserTypeEnum.ADMIN_DATALIGHT,
                            GrafanaUser.getGrafanaUser(
                                    GRAFANA_BASE_ORG_NAME,
                                    GrafanaUserTypeEnum.ADMIN_DATALIGHT
                            )
                    ),
                    new AbstractMap.SimpleEntry<>(
                            GrafanaUserTypeEnum.EDITOR_DATALIGHT,
                            GrafanaUser.getGrafanaUser(
                                    GRAFANA_BASE_ORG_NAME,
                                    GrafanaUserTypeEnum.EDITOR_DATALIGHT
                            )
                    )
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            // 获取 Grafana 位置
            TDlComponent grafanaServerTDlComponent = this.masterComponentService
                    .getTDlComponentListByServiceName(
                            clusterId,
                            "MONITOR"
                    )
                    .stream()
                    .filter(i -> i.getComponentName().equals("Grafana"))
                    .collect(Collectors.toList())
                    .get(0);

            // 如果 Grafana 已启动
            if (grafanaServerTDlComponent != null && grafanaServerTDlComponent.getComponentState() == SCStateEnum.STARTED) {
                TDlNode tDlNodeGrafana = this.masterNodeService.getNodeListInNodeIds(
                                clusterId,
                                CollUtil.newArrayList(grafanaServerTDlComponent.getNodeId())
                        )
                        .get(0);

                // 检查是否是第一次部署：尝试获取 bds org，如果存在则说明已初始化过
                boolean isFirstDeploy = isGrafanaFirstDeploy(
                        tDlNodeGrafana.getIpv4(),
                        PortConstants.getExporterPort(
                                grafanaServerTDlComponent.getServiceName(),
                                grafanaServerTDlComponent.getComponentName()
                        )
                );

                // 如果是第一次部署，则执行 Grafana 基础配置
                if (isFirstDeploy) {
                    try {
                        this.configGrafanaBase(
                                clusterId,
                                grafanaUserMap,
                                tDlNodeGrafana.getIpv4(),
                                PortConstants.getExporterPort(
                                        grafanaServerTDlComponent.getServiceName(),
                                        grafanaServerTDlComponent.getComponentName()
                                )
                        );
                    } catch (Exception e) {
                        String errorLog = ExceptionUtil.stacktraceToString(e);
                        log.error(errorLog);
                        throw new BException(errorLog);
                    }
                } else {
                    log.info("Grafana 已初始化过，跳过基础配置");
                }

                // 加载所有 Dashboard
                this.initAllDashboard();
            }

        } catch (Exception e) {
            String errorLog = ExceptionUtil.stacktraceToString(e);
            log.error(errorLog);
            throw new BException(errorLog);
        }
    }

    /**
     * 检查 Grafana 是否是第一次部署
     * 通过检查 bds org 是否存在来判断
     *
     * @param nodeIp Grafana IP
     * @param port   Grafana 端口号
     * @return true 表示第一次部署，false 表示已初始化过
     */
    private boolean isGrafanaFirstDeploy(String nodeIp, String port) {
        try {
            // 配置 Grafana API Feign 客户端
            this.remoteInvokeGrafanaService.init(nodeIp, port);

            // 尝试获取 datalight org
            Result<String> getOrgByNameResult = this.remoteInvokeGrafanaService.getOrgByName(
                    ADMIN_NEW_TOKEN,
                    GRAFANA_BASE_ORG_NAME
            );

            // 如果成功获取到 org，说明已初始化过
            if (getOrgByNameResult.isSuccess() && getOrgByNameResult.getData() != null) {
                log.info("Grafana datalight org 已存在，判定为非第一次部署");
                return false;
            }
        } catch (Exception e) {
            // 如果获取失败，说明 org 不存在，这是第一次部署
            log.debug("Grafana datalight org 不存在或获取失败，判定为第一次部署", e);
        }

        return true;
    }

    /**
     * Description: 执行 Grafana 基础配置
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/23
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId      当前集群 ID
     * @param grafanaUserMap Grafana 基础用户信息 Map
     * @param nodeIp         Grafana IP
     * @param port           Grafana 端口号
     */
    public void configGrafanaBase(Long clusterId,
                                  Map<GrafanaUserTypeEnum, GrafanaUser> grafanaUserMap,
                                  String nodeIp,
                                  String port) {

        // 配置 Grafana API Feign 客户端
        this.remoteInvokeGrafanaService.init(
                nodeIp,
                port

        );

        // 1、修改 Grafana 主账号（userId1）密码
        try {
            GrafanaUser grafanaUser1 = grafanaUserMap.get(GrafanaUserTypeEnum.ADMIN);
            Result<String> changeUserPasswordResult = this.remoteInvokeGrafanaService.changeUserPassword(
                    ADMIN_OLD_TOKEN,
                    grafanaUser1.getOldLoginPassword(),
                    grafanaUser1.getNewLoginPassword()
            );
            log.info("changeUserPassword: {}", changeUserPasswordResult);
        } catch (Exception e) {
            // 初始化步骤可重复执行，已存在等情况属预期，不中断流程；但失败原因必须留痕，否则网络或鉴权故障无从排查
            log.warn("Grafana 初始化步骤未成功，已跳过: {}", e.getMessage());
        }

        // 2、为当前集群创建 Org，并获取 orgId
        try {
            Result<String> createOrgResult = this.remoteInvokeGrafanaService.createOrg(
                    ADMIN_NEW_TOKEN,
                    GRAFANA_BASE_ORG_NAME
            );
            log.info("createOrg: {}", createOrgResult);
        } catch (Exception e) {
            // 初始化步骤可重复执行，已存在等情况属预期，不中断流程；但失败原因必须留痕，否则网络或鉴权故障无从排查
            log.warn("Grafana 初始化步骤未成功，已跳过: {}", e.getMessage());
        }

        Result<String> getOrgByNameResult = this.remoteInvokeGrafanaService.getOrgByName(
                ADMIN_NEW_TOKEN,
                GRAFANA_BASE_ORG_NAME
        );
        log.info("getOrgByName: {}", getOrgByNameResult);
        String orgId = JsonUtil.getMapObj(getOrgByNameResult.getData()).get("id").toString();

        // 3、为当前集群 Org 创建用户（Admin），并获取该用户的 userId2
        GrafanaUser grafanaUser2 = grafanaUserMap.get(GrafanaUserTypeEnum.ADMIN_DATALIGHT);
        try {
            Result<String> createUser2Result = this.remoteInvokeGrafanaService.createUsers(
                    ADMIN_NEW_TOKEN,
                    grafanaUser2.getLoginName(),
                    grafanaUser2.getLoginName(),
                    grafanaUser2.getNewLoginPassword()
            );
            log.info("createUser2: {}", createUser2Result);

        } catch (Exception e) {
            // 初始化步骤可重复执行，已存在等情况属预期，不中断流程；但失败原因必须留痕，否则网络或鉴权故障无从排查
            log.warn("Grafana 初始化步骤未成功，已跳过: {}", e.getMessage());
        }


        // 4、将 userId2 加入到 orgId 中
        try {
            Result<String> addUser2InOrgResult = this.remoteInvokeGrafanaService.addUserInOrg(
                    ADMIN_NEW_TOKEN,
                    orgId,
                    grafanaUser2.getLoginName(),
                    GrafanaUserRoleEnum.Admin.name()
            );
            log.info("addUser2InOrg: {}", addUser2InOrgResult);
        } catch (Exception e) {
            // 初始化步骤可重复执行，已存在等情况属预期，不中断流程；但失败原因必须留痕，否则网络或鉴权故障无从排查
            log.warn("Grafana 初始化步骤未成功，已跳过: {}", e.getMessage());
        }


        // 5、将 userId2 从主 MainOrg 中移除
        try {
            Result<String> getUser2ByLoginNameResult = this.remoteInvokeGrafanaService.getUserByLoginName(
                    ADMIN_NEW_TOKEN,
                    grafanaUser2.getLoginName()
            );
            log.info("getUser2ByLoginName: {}", getUser2ByLoginNameResult);
            String userId2 = JsonUtil.getMapObj(getUser2ByLoginNameResult.getData()).get("id").toString();
            Result<String> deleteUser2FromOrgResult = this.remoteInvokeGrafanaService.deleteUserFromOrg(ADMIN_NEW_TOKEN,
                    "1",
                    userId2
            );
            log.info("deleteUser2FromOrg: {}", deleteUser2FromOrgResult);
        } catch (Exception e) {
            // 初始化步骤可重复执行，已存在等情况属预期，不中断流程；但失败原因必须留痕，否则网络或鉴权故障无从排查
            log.warn("Grafana 初始化步骤未成功，已跳过: {}", e.getMessage());
        }

        // 6、为当前集群 Org 创建用户（Editor），并获取该用户的 userId3
        GrafanaUser grafanaUser3 = grafanaUserMap.get(GrafanaUserTypeEnum.EDITOR_DATALIGHT);
        try {
            Result<String> createUser3Result = this.remoteInvokeGrafanaService.createUsers(
                    ADMIN_NEW_TOKEN,
                    grafanaUser3.getLoginName(),
                    grafanaUser3.getLoginName(),
                    grafanaUser3.getNewLoginPassword()
            );
            log.info("createUser3: {}", createUser3Result);

        } catch (Exception e) {
            // 初始化步骤可重复执行，已存在等情况属预期，不中断流程；但失败原因必须留痕，否则网络或鉴权故障无从排查
            log.warn("Grafana 初始化步骤未成功，已跳过: {}", e.getMessage());
        }

        // 7、将 userId3 加入到 orgId 中
        try {
            Result<String> addUser3InOrgResult = this.remoteInvokeGrafanaService.addUserInOrg(
                    ADMIN_NEW_TOKEN,
                    orgId,
                    grafanaUser3.getLoginName(),
                    GrafanaUserRoleEnum.Editor.name()
            );
            log.info("addUser3InOrg: {}", addUser3InOrgResult);
        } catch (Exception e) {
            // 初始化步骤可重复执行，已存在等情况属预期，不中断流程；但失败原因必须留痕，否则网络或鉴权故障无从排查
            log.warn("Grafana 初始化步骤未成功，已跳过: {}", e.getMessage());
        }

        // 8、将 userId3 从主 MainOrg 中移除
        try {
            Result<String> getUser3ByLoginNameResult = this.remoteInvokeGrafanaService.getUserByLoginName(
                    ADMIN_NEW_TOKEN,
                    grafanaUser3.getLoginName()
            );
            log.info("getUser3ByLoginName: {}", getUser3ByLoginNameResult);
            String userId3 = JsonUtil.getMapObj(getUser3ByLoginNameResult.getData()).get("id").toString();
            Result<String> deleteUser3FromOrgResult = this.remoteInvokeGrafanaService.deleteUserFromOrg(
                    ADMIN_NEW_TOKEN,
                    "1",
                    userId3
            );
            log.info("deleteUser3FromOrg: {}", deleteUser3FromOrgResult);
        } catch (Exception e) {
            // 初始化步骤可重复执行，已存在等情况属预期，不中断流程；但失败原因必须留痕，否则网络或鉴权故障无从排查
            log.warn("Grafana 初始化步骤未成功，已跳过: {}", e.getMessage());
        }

        // 9、删除已存在的 MONITOR-Prometheus datasource
        try {
            Result<String> deleteDataSourceResult = this.remoteInvokeGrafanaService.deleteDataSource(
                    ADMIN_NEW_TOKEN,
                    "MONITOR-Prometheus"
            );
            log.info("deleteDataSource: {}", deleteDataSourceResult);
        } catch (Exception e) {
            // 初始化步骤可重复执行，已存在等情况属预期，不中断流程；但失败原因必须留痕，否则网络或鉴权故障无从排查
            log.warn("Grafana 初始化步骤未成功，已跳过: {}", e.getMessage());
        }


        // 10、使用 userId 的账号密码创建数据源，名称为 MONITOR-Prometheus，且为默认
        try {
            List<TDlComponent> tDlComponentList = this.masterComponentService.getTDlComponentListByServiceName(
                            clusterId,
                            "MONITOR"
                    )
                    .stream()
                    .filter(i -> i.getComponentName().equals("Prometheus") && i.getComponentState() != SCStateEnum.REMOVED)
                    .collect(Collectors.toList());
            TDlComponent tDlComponent = CollUtil.getFirst(tDlComponentList);
            Map<Long, TDlNode> nodeMap = this.masterNodeService.getNodeMap(CollUtil.newArrayList(tDlComponent.getNodeId()));
            TDlNode tDlNode = nodeMap.get(tDlComponent.getNodeId());

            Result<String> createDataSourcesResult = this.remoteInvokeGrafanaService.createDataSources(
                    ADMIN_DATALIGHT_NEW_TOKEN,
                    orgId,
                    "MONITOR-Prometheus",
                    tDlNode.getHostname(),
                    PortConstants.EXPORTER_PORT_MAP.get("MONITOR-Prometheus"),
                    grafanaUser2.getLoginName(),
                    grafanaUser2.getNewLoginPassword()
            );
            log.info("createDataSources: {}", createDataSourcesResult);
        } catch (Exception e) {
            // 初始化步骤可重复执行，已存在等情况属预期，不中断流程；但失败原因必须留痕，否则网络或鉴权故障无从排查
            log.warn("Grafana 初始化步骤未成功，已跳过: {}", e.getMessage());
        }
    }

    /**
     * Description: 初始化所有 Dashboard
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    public void initAllDashboard() {
        // dashboard 目录
        String dashboardDir = String.format(
                "%s/MONITOR/dashboard",
                DataLightEnv.PLUGINS_DIR_LOCAL
        );

        File[] files = FileUtil.file(dashboardDir).listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        Arrays.stream(files)
                .forEach(i -> {
                            try {
                                String dashboard = FileUtil.readString(
                                        i,
                                        CharsetUtil.CHARSET_UTF_8
                                );
                                Result<String> result = this.remoteInvokeGrafanaService.createOrUpdateDashboard(
                                        ADMIN_DATALIGHT_NEW_TOKEN,
                                        dashboard
                                );

                                log.info("加载 Dashboard {} : {}", result.isSuccess(), i.getName());
                            } catch (IORuntimeException e) {
                                log.error(ExceptionUtil.stacktraceToString(e));
                            }

                        }
                );
    }
}
