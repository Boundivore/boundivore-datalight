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
import cn.boundivore.dl.base.enumeration.impl.GrafanaRoleEnum;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.utils.JsonUtil;
import cn.boundivore.dl.cloud.feign.RequestOptionsGenerator;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.bean.GrafanaUser;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
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

    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String AUTHORIZATION = "Authorization";
    private static final String APPLICATION_JSON = "application/json";
    private static final long CONNECT_TIMEOUT = 2 * 1000L;
    private static final long READ_TIMEOUT = 5 * 1000L;

    private final Feign.Builder feignBuilder;

    // Grafana IP 地址
    private String grafanaIp;
    // Grafana 端口号
    private String grafanaPort;
    // Grafana 用户名
    private String grafanaUser;
    // Grafana 密码
    private String grafanaPassword;

    private IThirdGrafanaAPI iThirdGrafanaAPI;

    private final static String ADMIN_USER = "admin";
    private final static String ADMIN_PASSWORD = "admin";

    private final static String ADMIN_SUB_USER = "admin-%s";
    private final static String ADMIN_SUB_PASSWORD = "admin-%s";

    private final static String EDITOR_SUB_USER = "%s";
    private final static String EDITOR_SUB_PASSWORD = "%s";

    public void init(String grafanaIp,
                     String grafanaPort,
                     String grafanaUser,
                     String grafanaPassword) {

        this.grafanaIp = grafanaIp;
        this.grafanaPort = grafanaPort;
        this.grafanaUser = grafanaUser;
        this.grafanaPassword = grafanaPassword;

        this.iThirdGrafanaAPI = this.iThirdGrafanaAPI();
    }

    private void checkInit() {
        Assert.notNull(
                iThirdGrafanaAPI,
                () -> new BException("请先初始化 Grafana API 客户端")
        );
    }

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
     * @return IWorkerExecAPI 可调用 API 实例
     */
    private IThirdGrafanaAPI iThirdGrafanaAPI() {
        return feignBuilder
                .options(
                        RequestOptionsGenerator.getRequestOptions(
                                CONNECT_TIMEOUT,
                                READ_TIMEOUT
                        )
                )
                .requestInterceptor(
                        template -> template
                                .header(ACCEPT, APPLICATION_JSON)
                                .header(CONTENT_TYPE, APPLICATION_JSON)
                                .header(AUTHORIZATION, basicAuthValue(this.grafanaUser, this.grafanaPassword))
                )
                .target(
                        IThirdGrafanaAPI.class,
                        String.format(
                                "http://%s:%s%s",
                                this.grafanaIp,
                                this.grafanaPort,
                                IUrlPrefixConstants.NONE_PREFIX
                        )
                );
    }

    private String basicAuthValue(String user, String password) {
        return String.format(
                "Basic %s",
                Base64.encode(
                        String.format(
                                "%s:%s",
                                user,
                                password
                        )
                )
        );
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
     * @param orgName 组织名称
     * @return @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> createOrg(String orgName) {
        this.checkInit();
        return this.iThirdGrafanaAPI.createOrg(
                MapUtil.of(
                        new Object[][]{
                                {"name", orgName}
                        }
                )
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
     * @param userName  用户名
     * @param loginName 登录名
     * @param password  密码
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> createUsers(String userName,
                                      String loginName,
                                      String password) {
        this.checkInit();
        return this.iThirdGrafanaAPI.createUsers(
                MapUtil.of(
                        new Object[][]{
                                {"name", userName},
                                {"login", loginName},
                                {"password", password}
                        }
                )
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
     * @param orgId     组织 ID
     * @param loginName 用户登录名
     * @param role      用户在组织中的角色
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> addUserInOrg(String orgId,
                                       String loginName,
                                       String role) {
        this.checkInit();
        return this.iThirdGrafanaAPI.addUserInOrg(
                orgId,
                MapUtil.of(
                        new Object[][]{
                                {"loginOrEmail", loginName},
                                {"role", role}
                        }
                )
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
     * @param orgId  组织 ID
     * @param userId 用户 ID
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> deleteUserFromOrg(String orgId,
                                            String userId) {
        this.checkInit();
        return this.iThirdGrafanaAPI.deleteUserFromOrg(
                orgId,
                userId
        );
    }

    /**
     * Description: 根据 UID 获取数据源信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param name 数据源名称
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> getDatasourceByName(String name) {
        this.checkInit();
        return this.iThirdGrafanaAPI.getDatasourceByName(
                name
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
     * @param orgId          组织 ID
     * @param prometheusHost Prometheus 节点地址
     * @param prometheusPort Prometheus 端口号
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> createDataSources(String orgId,
                                            String prometheusHost,
                                            String prometheusPort) {
        this.checkInit();
        return this.iThirdGrafanaAPI.createDataSources(
                MapUtil.of(
                        new Object[][]{
                                {"id", null},
                                {"orgId", orgId},
                                {"name", "MONITOR-Prometheus"},
                                {"label", "MONITOR-Prometheus"},
                                {"type", "prometheus"},
                                {"typeLogoUrl", ""},
                                {"access", "proxy"},
                                {"url", String.format("http://%s:%s", prometheusHost, prometheusPort)},
                                {"user", "admin"},
                                {"password", "admin"},
                                {"database", ""},
                                {"basicAuth", false},
                                {"basicAuthUser", ""},
                                {"basicAuthPassword", ""},
                                {"withCredentials", false},
                                {"isDefault", true},
                                {"jsonData", new HashMap<Object, Object>()},
                                {"secureJsonFields", new HashMap<Object, Object>()},
                                {"version", 1},
                                {"readOnly", false}
                        }
                )
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
     * @param dashboard dashboard 完整文件
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> createOrUpdateDashboard(String dashboard) {
        this.checkInit();
        return this.iThirdGrafanaAPI.createOrUpdateDashboard(
                MapUtil.of(
                        new Object[][]{
                                {"dashboard", JsonUtil.getMapObj(dashboard)},
                                {"folderUid", ""},
                                {"message", ""},
                                {"overwrite", true}
                        }
                )
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
     * @param orgId     组织 ID
     * @param userId    用户 ID
     * @param loginName 登录名
     * @param role      角色
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> updateUserInOrg(String orgId,
                                          String userId,
                                          String loginName,
                                          String role) {
        this.checkInit();
        return this.iThirdGrafanaAPI.updateUserInOrg(
                orgId,
                userId,
                MapUtil.of(
                        new Object[][]{
                                {"loginOrEmail", loginName},
                                {"role", role}
                        }
                )
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
     * @param orgId 组织 ID
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> getUserInOrg(String orgId) {
        this.checkInit();
        return this.iThirdGrafanaAPI.getUserInOrg(
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
     * @param orgName 组织 ID
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> getOrgByName(String orgName) {
        this.checkInit();
        return this.iThirdGrafanaAPI.getOrgByName(
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
     * @param loginOrEmail 登录名
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> getUserByLoginName(String loginOrEmail) {
        this.checkInit();
        return this.iThirdGrafanaAPI.getUserByLoginName(
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
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> searchAllUsers() {
        this.checkInit();
        return this.iThirdGrafanaAPI.searchAllUsers(
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
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> searchAllOrgs() {
        this.checkInit();
        return this.iThirdGrafanaAPI.searchAllOrgs();
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
     * @param userId 用户 ID
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> deleteUserById(String userId) {
        this.checkInit();
        return this.iThirdGrafanaAPI.deleteUserById(userId);
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
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> deleteOrgById(String orgId) {
        this.checkInit();
        return this.iThirdGrafanaAPI.deleteOrgById(orgId);
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
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> changeUserPassword(String oldPassword,
                                             String newPassword) {
        this.checkInit();
        return this.iThirdGrafanaAPI.changeUserPassword(
                MapUtil.of(
                        new Object[][]{
                                {"oldPassword", oldPassword},
                                {"newPassword", newPassword}
                        }
                )
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
     * @return Result<String> Grafana 响应体存在于 Result data 中
     */
    public Result<String> getStats() {
        this.checkInit();
        return this.iThirdGrafanaAPI.getStats();
    }

    /**
     * Description:
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
     * Creation time: 2023/8/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param
     * @return
     */
    public void initGrafanaSettings() {
        try {
            GrafanaUser.getGrafanaUser("", GrafanaRoleEnum.ADMIN);
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            throw new BException(e.getMessage());
        }
    }
}
