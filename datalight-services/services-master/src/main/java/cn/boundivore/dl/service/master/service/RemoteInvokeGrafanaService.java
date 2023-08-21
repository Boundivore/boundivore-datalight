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

import cn.boundivore.dl.api.third.define.IThirdGrafanaAPI;
import cn.boundivore.dl.base.constants.IUrlPrefixConstants;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.cloud.feign.RequestOptionsGenerator;
import cn.hutool.core.codec.Base64;
import feign.Feign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Description: 通过 Feign 远程调用指定节点上的 IThirdGrafanaAPI 接口的一系列封装
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/5
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RemoteInvokeGrafanaService {

    private final Feign.Builder feignBuilder;

    /**
     * Description: Feign 远程调用指定节点的 IThirdGrafanaAPI 的接口
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     * add("Accept", "application/json");
     * add("Content-Type", "application/json");
     * add("Authorization", basicAuthValue(user, password));
     *
     * @param grafanaIp       Grafana IP 地址
     * @param grafanaPort     Grafana 端口号
     * @param grafanaUser     Grafana 用户名
     * @param grafanaPassword Grafana 密码
     * @return IWorkerExecAPI 可调用 API 实例
     */
    public IThirdGrafanaAPI iThirdGrafanaAPI(String grafanaIp,
                                             String grafanaPort,
                                             String grafanaUser,
                                             String grafanaPassword) {
        return feignBuilder
                .options(RequestOptionsGenerator.getRequestOptions(
                                2 * 1000L,
                                5 * 1000L
                        )
                )
                .requestInterceptor(
                        template -> template
                                .header(
                                        "Accept",
                                        "application/json"
                                )
                                .header(
                                        "Content-Type",
                                        "application/json"
                                )
                                .header(
                                        "Authorization",
                                        basicAuthValue(grafanaUser, grafanaPassword)
                                )
                )
                .target(
                        IThirdGrafanaAPI.class,
                        String.format(
                                "http://%s:%s%s",
                                grafanaIp,
                                grafanaPort,
                                IUrlPrefixConstants.NONE_PREFIX
                        )
                );
    }

    private String basicAuthValue(String user, String password) {
        return String.format("Basic %s", Base64.encode(user + ":" + password));
    }

    /**
     * Description: 获取账号信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param orgName          组织名称
     * @return @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> createOrg(IThirdGrafanaAPI iThirdGrafanaAPI, String orgName) {
        return iThirdGrafanaAPI.createOrg(
                new HashMap<String, Object>() {
                    {
                        put("name", orgName);
                    }
                }
        );
    }


    /**
     * Description: 创建用户
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param userName         用户名
     * @param loginName        登录名
     * @param password         密码
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> createUsers(IThirdGrafanaAPI iThirdGrafanaAPI,
                                      String userName,
                                      String loginName,
                                      String password) {
        return iThirdGrafanaAPI.createUsers(
                new HashMap<String, Object>() {
                    {
                        put("name", userName);
                        put("login", loginName);
                        put("password", password);
                    }
                }
        );
    }

    /**
     * Description: 添加用户到指定组织
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param orgId            组织 ID
     * @param loginName        用户登录名
     * @param role             用户在组织中的角色
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> addUserInOrg(IThirdGrafanaAPI iThirdGrafanaAPI,
                                       String orgId,
                                       String loginName,
                                       String role) {
        return iThirdGrafanaAPI.addUserInOrg(
                orgId,
                new HashMap<String, Object>() {
                    {
                        put("loginOrEmail", loginName);
                        put("role", role);
                    }
                }
        );
    }

    /**
     * Description: 从指定组织中删除用户
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param orgId            组织 ID
     * @param userId           用户 ID
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> deleteUserFromOrg(IThirdGrafanaAPI iThirdGrafanaAPI,
                                            String orgId,
                                            String userId) {
        return iThirdGrafanaAPI.deleteUserFromOrg(
                orgId,
                userId
        );
    }

    /**
     * Description: 创建 DataSources
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI  Grafana API 接口集合
     * @param orgId             组织 ID
     * @param prometheusBaseUri Prometheus 请求地址
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> createDataSources(IThirdGrafanaAPI iThirdGrafanaAPI,
                                            String orgId,
                                            String prometheusBaseUri) {
        return iThirdGrafanaAPI.createDataSources(
                new HashMap<String, Object>() {
                    {
                        put("id", null);
                        put("orgId", orgId);
                        put("name", "Prometheus");
                        put("type", "prometheus");
                        put("typeLogoUrl", "");
                        put("access", "proxy");
                        put("url", prometheusBaseUri);
                        put("user", "admin");
                        put("password", "admin");
                        put("database", "");
                        put("basicAuth", false);
                        put("basicAuthUser", "");
                        put("basicAuthPassword", "");
                        put("withCredentials", false);
                        put("isDefault", true);
                        put("jsonData", new HashMap<String, Object>());
                        put("secureJsonFields", new HashMap<String, Object>());
                        put("version", 1);
                        put("readOnly", false);
                    }
                }
        );
    }

    /**
     * Description: 保存或更新 Dashboard
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param dashboard        dashboard 完整文件
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> createOrUpdateDashboard(IThirdGrafanaAPI iThirdGrafanaAPI,
                                                  String dashboard) {
        return iThirdGrafanaAPI.createDataSources(
                new HashMap<String, Object>() {
                    {
                        put("dashboard", dashboard);
                        put("folderUid", null);
                        put("message", "");
                        put("overwrite", true);
                    }
                }
        );
    }

    /**
     * Description: 更新指定组织下的用户
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param orgId            组织 ID
     * @param userId           用户 ID
     * @param loginName        登录名
     * @param role             角色
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> updateUserInOrg(IThirdGrafanaAPI iThirdGrafanaAPI,
                                          String orgId,
                                          String userId,
                                          String loginName,
                                          String role) {
        return iThirdGrafanaAPI.updateUserInOrg(
                orgId,
                userId,
                new HashMap<String, Object>() {
                    {
                        put("loginOrEmail", loginName);
                        put("role", role);
                    }
                }
        );
    }

    /**
     * Description: 获取指定组织下的用户
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param orgId            组织 ID
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> getUserInOrg(IThirdGrafanaAPI iThirdGrafanaAPI,
                                       String orgId) {
        return iThirdGrafanaAPI.getUserInOrg(
                orgId
        );
    }

    /**
     * Description: 根据名称获取组织信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param orgName          组织 ID
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> getOrgByName(IThirdGrafanaAPI iThirdGrafanaAPI,
                                       String orgName) {
        return iThirdGrafanaAPI.getOrgByName(
                orgName
        );
    }

    /**
     * Description: 根据登录名获取用户信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param loginOrEmail     登录名
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> getUserByLoginName(IThirdGrafanaAPI iThirdGrafanaAPI,
                                             String loginOrEmail) {
        return iThirdGrafanaAPI.getUserByLoginName(
                loginOrEmail
        );
    }

    /**
     * Description: 获取所有用户
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> searchAllUsers(IThirdGrafanaAPI iThirdGrafanaAPI) {
        return iThirdGrafanaAPI.searchAllUsers(
                "1000",
                "1"
        );
    }

    /**
     * Description: 获取所有组织
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> searchAllOrgs(IThirdGrafanaAPI iThirdGrafanaAPI) {
        return iThirdGrafanaAPI.searchAllOrgs();
    }

    /**
     * Description: 根据 ID 删除指定用户
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param userId           用户 ID
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> deleteUserById(IThirdGrafanaAPI iThirdGrafanaAPI,
                                         String userId) {
        return iThirdGrafanaAPI.deleteUserById(userId);
    }

    /**
     * Description: 根据 ID 删除指定组织
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> deleteOrgById(IThirdGrafanaAPI iThirdGrafanaAPI,
                                        String orgId) {
        return iThirdGrafanaAPI.deleteOrgById(orgId);
    }


    /**
     * Description: 变更用户名密码
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @param oldPassword      旧密码
     * @param newPassword      新密码
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> changeUserPassword(IThirdGrafanaAPI iThirdGrafanaAPI,
                                             String oldPassword,
                                             String newPassword) {
        return iThirdGrafanaAPI.changeUserPassword(
                new HashMap<String, Object>() {
                    {
                        put("oldPassword", oldPassword);
                        put("newPassword", newPassword);
                    }
                }
        );
    }

    /**
     * Description: 获取 Grafana 状态信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param iThirdGrafanaAPI Grafana API 接口集合
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> getStats(IThirdGrafanaAPI iThirdGrafanaAPI) {
        return iThirdGrafanaAPI.getStats();
    }
}
