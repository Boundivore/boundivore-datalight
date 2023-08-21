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
package cn.boundivore.dl.api.third.define;

import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.NONE_PREFIX;


/**
 * Description: Grafana 节点执行指定脚本代码
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IThirdGrafanaAPI", tags = {"Grafana 接口：直接调用 Grafana 接口"})
@FeignClient(
        name = "IThirdGrafanaAPI",
        contextId = "IThirdGrafanaAPI",
        path = NONE_PREFIX
)
public interface IThirdGrafanaAPI {

    @PostMapping(value = "/api/orgs")
    @ApiOperation(notes = "创建组织", value = "创建组织")
    Result<String> createOrg(
            @RequestBody
            Map<Object, Object> request
    );

    @PostMapping(value = "/api/admin/users")
    @ApiOperation(notes = "创建用户", value = "创建用户")
    Result<String> createUsers(
            @RequestBody
            Map<Object, Object> request
    );

    @PostMapping(value = "/api/orgs/{orgId}/users")
    @ApiOperation(notes = "向指定组织中添加用户", value = "向指定组织中添加用户")
    Result<String> addUserInOrg(
            @PathVariable("orgId")
            String orgId,

            @RequestBody
            Map<Object, Object> request
    );

    @DeleteMapping(value = "/api/orgs/{orgId}/users/{userId}")
    @ApiOperation(notes = "从指定组织中删除用户", value = "从指定组织中删除用户")
    Result<String> deleteUserFromOrg(
            @PathVariable("orgId")
            String orgId,

            @PathVariable("userId")
            String userId
    );

    @GetMapping(value = "/api/datasources/name/{name}")
    @ApiOperation(notes = "根据 UID 获取数据源信息", value = "根据 UID 获取数据源信息")
    Result<String> getDatasourceByName(
            @PathVariable("name")
            String name
    );


    @PostMapping(value = "/api/datasources")
    @ApiOperation(notes = "创建 DataSources", value = "创建 DataSources")
    Result<String> createDataSources(
            @RequestBody
            Map<Object, Object> request
    );

    @PostMapping(value = "/api/dashboards/db")
    @ApiOperation(notes = "创建或更新 Dashboard", value = "创建或更新 Dashboard")
    Result<String> createOrUpdateDashboard(
            @RequestBody
            Map<Object, Object> request
    );

    @PatchMapping(value = "/api/orgs/{orgId}/users/{userId}")
    @ApiOperation(notes = "更新指定组织下的用户", value = "更新指定组织下的用户")
    Result<String> updateUserInOrg(
            @PathVariable("orgId")
            String orgId,

            @PathVariable("userId")
            String userId,

            @RequestBody
            Map<Object, Object> request
    );

    @GetMapping(value = "/api/orgs/{orgId}/users")
    @ApiOperation(notes = "获取指定组织下的用户信息", value = "获取指定组织下的用户信息")
    Result<String> getUserInOrg(
            @PathVariable("orgId")
            String orgId
    );

    @GetMapping(value = "/api/orgs/name/{orgName}")
    @ApiOperation(notes = "根据名称获取组织信息", value = "根据名称获取组织信息")
    Result<String> getOrgByName(
            @PathVariable("orgName")
            String orgName
    );

    @GetMapping(value = "/api/users/lookup")
    @ApiOperation(notes = "根据登录账号获取用户信息", value = "根据登录账号获取用户信息")
    Result<String> getUserByLoginName(
            @RequestParam("loginOrEmail")
            String loginOrEmail
    );

    @GetMapping(value = "/api/users")
    @ApiOperation(notes = "获取所有用户", value = "获取所有用户")
    Result<String> searchAllUsers(
            @RequestParam("perpage")
            String perpage,

            @RequestParam("page")
            String page
    );

    @GetMapping(value = "/api/orgs")
    @ApiOperation(notes = "获取所有组织", value = "获取所有组织")
    Result<String> searchAllOrgs();

    @DeleteMapping(value = "/api/admin/users/{userId}")
    @ApiOperation(notes = "根据 ID 删除指定用户", value = "根据 ID 删除指定用户")
    Result<String> deleteUserById(
            @PathVariable("userId")
            String userId
    );

    @DeleteMapping(value = "/api/orgs/{orgId}")
    @ApiOperation(notes = "根据 ID 删除指定组织", value = "根据 ID 删除指定组织")
    Result<String> deleteOrgById(
            @PathVariable("orgId")
            String orgId
    );

    @PutMapping(value = "/api/user/password")
    @ApiOperation(notes = "变更用户名密码", value = "变更用户名密码")
    Result<String> changeUserPassword(
            @RequestBody
            Map<Object, Object> request
    );

    @GetMapping(value = "/api/admin/stats")
    @ApiOperation(notes = "获取 Grafana 状态信息", value = "获取 Grafana 状态信息")
    Result<String> getStats();
}
