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
package cn.boundivore.dl.api.master.define;

import cn.boundivore.dl.base.request.impl.master.AbstractPermissionRuleRequest;
import cn.boundivore.dl.base.request.impl.master.test.TestRuleDataColumnRequest;
import cn.boundivore.dl.base.request.impl.master.test.TestRuleDataRowRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractPermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.MASTER_URL_PREFIX;


/**
 * Description: 权限相关接口
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Api(value = "IMasterPermissionAPI", tags = {"Master 接口：权限相关"})
@FeignClient(
        name = "IMasterPermissionAPI",
        contextId = "IMasterPermissionAPI",
        path = MASTER_URL_PREFIX
)
public interface IMasterPermissionAPI {

    //测试接口权限
    @GetMapping(value = "/permission/testPermissionInterface")
    @ApiOperation(notes = "测试接口权限", value = "测试接口权限 [Finished]")
    Result<String> testPermissionInterface() throws Exception;

    //测试数据行权限
    @PostMapping(value = "/permission/testPermissionDataRow")
    @ApiOperation(notes = "测试数据行权限", value = "测试数据行权限 [Finished]")
    Result<AbstractPermissionRuleVo.PermissionListVo> testPermissionDataRow(
            @Valid
            @RequestBody
            TestRuleDataRowRequest request
    ) throws Exception;

    //测试数据列权限
    @PostMapping(value = "/permission/testPermissionDataColumn")
    @ApiOperation(notes = "测试数据列权限", value = "测试数据列权限 [Finished]")
    Result<AbstractPermissionRuleVo.PermissionListVo> testPermissionDataColumn(
            @Valid
            @RequestBody
            TestRuleDataColumnRequest request
    ) throws Exception;

    @GetMapping(value = "/permission/listPermissionRuleInterface")
    @ApiOperation(notes = "查询当前用户 接口 权限列表", value = "查询当前用户 接口 权限列表 [Finished]")
    Result<AbstractPermissionRuleVo.PermissionRuleInterfaceListVo> listPermissionRuleInterface(
            @ApiParam(name = "UserId", value = "用户 ID", example = "")
            @RequestParam(value = "UserId", required = false)
            Long userId,

            @ApiParam(name = "RuleInterfaceUri", value = "接口 URI, 传空时则拉取所有接口权限", example = "")
            @RequestParam(value = "RuleInterfaceUri", required = false)
            String ruleInterfaceUri
    ) throws Exception;

    @GetMapping(value = "/permission/listPermissionRuleDataRow")
    @ApiOperation(notes = "查询当前用户数据 行 权限列表", value = "查询当前用户数据 行 权限列表 [Finished]")
    Result<AbstractPermissionRuleVo.PermissionRuleDataRowListVo> listPermissionRuleDataRow(
            @ApiParam(name = "UserId", value = "用户 ID", example = "")
            @RequestParam(value = "UserId", required = false)
            Long userId
    ) throws Exception;

    @GetMapping(value = "/permission/listPermissionRuleDataColumn")
    @ApiOperation(notes = "查询当前用户数据 列 权限列表", value = "查询当前用户数据 列 权限列表 [Finished]")
    Result<AbstractPermissionRuleVo.PermissionRuleDataColumnListVo> listPermissionRuleDataColumn(
            @ApiParam(name = "UserId", value = "用户 ID", example = "")
            @RequestParam(value = "UserId", required = false)
            Long userId
    ) throws Exception;

    @PostMapping(value = "/permission/putPermissionBatch")
    @ApiOperation(notes = "新增、修改、删除 权限", value = "新增、修改、删除 权限 [Finished]")
    Result<AbstractPermissionRuleVo.PermissionRuleListVo> putPermissionBatch(
            @RequestBody
            @Valid
            AbstractPermissionRuleRequest.NewPermissionAndRuleRequest request
    ) throws Exception;


    @GetMapping(value = "/permission/details")
    @ApiOperation(notes = "获取当前权限详情", value = "获取当前权限详情 [Finished]")
    Result<AbstractPermissionRuleVo.PermissionRuleDetailsVo> details(
            @ApiParam(name = "PermissionId", value = "权限 Id", example = "1")
            @RequestParam(value = "PermissionId")
            Long permissionId
    ) throws Exception;

    @GetMapping(value = "/permission/listPermissionByRoleId")
    @ApiOperation(notes = "根据角色 ID 获取权限信息", value = "根据角色 ID 获取权限信息 [Finished]")
    Result<AbstractPermissionRuleVo.PermissionRuleListVo> listPermissionByRoleId(
            @ApiParam(name = "RoleId", value = "角色 ID", example = "1")
            @RequestParam(value = "RoleId")
            Long roleId
    ) throws Exception;
}
