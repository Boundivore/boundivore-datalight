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

import cn.boundivore.dl.base.enumeration.impl.GrafanaUserRoleEnum;
import cn.boundivore.dl.base.enumeration.impl.GrafanaUserTypeEnum;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.utils.JsonUtil;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.bean.GrafanaUser;
import cn.boundivore.dl.service.master.service.RemoteInvokeGrafanaService;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
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

    private final RemoteInvokeGrafanaService remoteInvokeGrafanaService;

    public void initGrafanaSettings(String prometheusHost, String prometheusPort) {
        try {
            String orgName = "datalight";

            Map<GrafanaUserTypeEnum, GrafanaUser> grafanaUserMap = Stream.of(
                    new AbstractMap.SimpleEntry<>(
                            GrafanaUserTypeEnum.ADMIN,
                            GrafanaUser.getGrafanaUser(
                                    orgName,
                                    GrafanaUserTypeEnum.ADMIN
                            )
                    ),
                    new AbstractMap.SimpleEntry<>(
                            GrafanaUserTypeEnum.ADMIN_ORG,
                            GrafanaUser.getGrafanaUser(
                                    orgName,
                                    GrafanaUserTypeEnum.ADMIN_ORG
                            )
                    ),
                    new AbstractMap.SimpleEntry<>(
                            GrafanaUserTypeEnum.EDITOR_ORG,
                            GrafanaUser.getGrafanaUser(
                                    orgName,
                                    GrafanaUserTypeEnum.EDITOR_ORG
                            )
                    )
            ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


            // 1、修改 Grafana 主账号（userId1）密码
            Result<String> changeUserPasswordResult = this.remoteInvokeGrafanaService.changeUserPassword(
                    grafanaUserMap.get(GrafanaUserTypeEnum.ADMIN).getLoginPassword(),
                    grafanaUserMap.get(GrafanaUserTypeEnum.ADMIN).getLoginPassword()
            );
            log.info("changeUserPassword: {}", changeUserPasswordResult);

            // 2、为当前集群创建 Org，并获取 orgId
            try {
                Result<String> createOrgResult = this.remoteInvokeGrafanaService.createOrg(orgName);
                log.info("createOrg: {}", createOrgResult);
            } catch (Exception ignore) {
            }

            Result<String> getOrgByNameResult = this.remoteInvokeGrafanaService.getOrgByName(orgName);
            log.info("getOrgByName: {}", getOrgByNameResult);
            String orgId = JsonUtil.getMapObj(getOrgByNameResult.getData()).get("id").toString();

            // 3、为当前集群 Org 创建用户（Admin），并获取该用户的 userId2
            GrafanaUser grafanaUser2 = grafanaUserMap.get(GrafanaUserTypeEnum.ADMIN_ORG);
            try {
                Result<String> createUser2Result = this.remoteInvokeGrafanaService.createUsers(
                        grafanaUser2.getLoginName(),
                        grafanaUser2.getLoginName(),
                        grafanaUser2.getLoginPassword()
                );
                log.info("createUser2: {}", createUser2Result);

            } catch (Exception ignore) {
            }


            // 4、将 userId2 加入到 orgId 中
            try {
                Result<String> addUser2InOrgResult = this.remoteInvokeGrafanaService.addUserInOrg(orgId, grafanaUser2.getLoginName(), GrafanaUserRoleEnum.Admin.name());
                log.info("addUser2InOrg: {}", addUser2InOrgResult);
            } catch (Exception ignore) {
            }


            // 5、将 userId2 从主 org 中移除
            try {
                Result<String> getUser2ByLoginNameResult = this.remoteInvokeGrafanaService.getUserByLoginName(grafanaUser2.getLoginName());
                log.info("getUser2ByLoginName: {}", getUser2ByLoginNameResult);
                String userId2 = JsonUtil.getMapObj(getUser2ByLoginNameResult.getData()).get("id").toString();
                Result<String> deleteUser2FromOrgResult = this.remoteInvokeGrafanaService.deleteUserFromOrg("1", userId2);
                log.info("deleteUser2FromOrg: {}", deleteUser2FromOrgResult);
            } catch (Exception ignore) {
            }

            // 6、为当前集群 Org 创建用户（Editor），并获取该用户的 userId3
            GrafanaUser grafanaUser3 = grafanaUserMap.get(GrafanaUserTypeEnum.EDITOR_ORG);
            try {
                Result<String> createUser3Result = this.remoteInvokeGrafanaService.createUsers(
                        grafanaUser3.getLoginName(),
                        grafanaUser3.getLoginName(),
                        grafanaUser3.getLoginPassword()
                );
                log.info("createUser3: {}", createUser3Result);

            } catch (Exception ignore) {
            }

            // 7、将 userId3 加入到 orgId 中
            try {
                Result<String> addUser3InOrgResult = this.remoteInvokeGrafanaService.addUserInOrg(orgId, grafanaUser3.getLoginName(), GrafanaUserRoleEnum.Editor.name());
                log.info("addUser3InOrg: {}", addUser3InOrgResult);
            } catch (Exception ignore) {
            }

            // 8、将 userId3 从主 org 中移除
            try {
                Result<String> getUser3ByLoginNameResult = this.remoteInvokeGrafanaService.getUserByLoginName(grafanaUser2.getLoginName());
                log.info("getUser3ByLoginName: {}", getUser3ByLoginNameResult);
                String userId3 = JsonUtil.getMapObj(getUser3ByLoginNameResult.getData()).get("id").toString();
                Result<String> deleteUser3FromOrgResult = this.remoteInvokeGrafanaService.deleteUserFromOrg("1", userId3);
                log.info("deleteUser3FromOrg: {}", deleteUser3FromOrgResult);
            } catch (Exception ignore) {
            }

            // 9、使用 userId2 的账号密码创建数据源，名称为 MONITOR-Prometheus，且为默认
            try {
                Result<String> deleteDataSourceResult = this.remoteInvokeGrafanaService.deleteDataSource("MONITOR-Prometheus");
                log.info("deleteDataSource: {}", deleteDataSourceResult);

                Result<String> createDataSourcesResult = this.remoteInvokeGrafanaService.createDataSources(
                        orgId,
                        "MONITOR-Prometheus",
                        prometheusHost,
                        prometheusPort,
                        grafanaUser2.getLoginName(),
                        grafanaUser2.getLoginPassword()
                );
                log.info("createDataSources: {}", createDataSourcesResult);
            } catch (Exception ignore) {
            }


        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            throw new BException(e.getMessage());
        }
    }
}
