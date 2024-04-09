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
package cn.boundivore.dl.service.master.controller;

import cn.boundivore.dl.api.master.define.IMasterPermissionAPI;
import cn.boundivore.dl.base.request.impl.master.AbstractPermissionRuleRequest;
import cn.boundivore.dl.base.request.impl.master.test.TestRuleDataColumnRequest;
import cn.boundivore.dl.base.request.impl.master.test.TestRuleDataRowRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractPermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterPermissionService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterPermissionController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/9s
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@SaCheckLogin
public class MasterPermissionController implements IMasterPermissionAPI {

    private final MasterPermissionService masterPermissionService;


    @Override
    public Result<String> testPermissionInterface() throws Exception {
        return this.masterPermissionService.testPermissionInterface();
    }

    @Override
    public Result<AbstractPermissionRuleVo.PermissionListVo> testPermissionDataRow(TestRuleDataRowRequest request) throws Exception {
        return this.masterPermissionService.testPermissionDataRow(request);
    }

    @Override
    public Result<AbstractPermissionRuleVo.PermissionListVo> testPermissionDataColumn(TestRuleDataColumnRequest request) throws Exception {
        return this.masterPermissionService.testPermissionDataColumn(request);
    }

    @Override
    public Result<AbstractPermissionRuleVo.PermissionRuleInterfaceListVo> listPermissionRuleInterface(Long userId, String ruleInterfaceUri) throws Exception {
        return this.masterPermissionService.listPermissionRuleInterface(userId, ruleInterfaceUri);
    }

    @Override
    public Result<AbstractPermissionRuleVo.PermissionRuleDataRowListVo> listPermissionRuleDataRow(Long userId) throws Exception {
        return this.masterPermissionService.listPermissionRuleDataRow(userId);
    }

    @Override
    public Result<AbstractPermissionRuleVo.PermissionRuleDataColumnListVo> listPermissionRuleDataColumn(Long userId) throws Exception {
        return this.masterPermissionService.listPermissionRuleDataColumn(userId);
    }

    @Override
    public Result<AbstractPermissionRuleVo.PermissionRuleListVo> putPermissionBatch(AbstractPermissionRuleRequest.NewPermissionAndRuleRequest request) throws Exception {
        return this.masterPermissionService.putPermissionBatch(request);
    }

    @Override
    public Result<AbstractPermissionRuleVo.PermissionRuleDetailsVo> details(Long permissionId) throws Exception {
        return this.masterPermissionService.details(permissionId);
    }

    @Override
    public Result<AbstractPermissionRuleVo.PermissionRuleListVo> listPermissionByRoleId(Long roleId) throws Exception {
        return this.masterPermissionService.listPermissionByRoleId(roleId);
    }
}
