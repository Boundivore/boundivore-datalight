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
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.response.impl.master.AbstractRolePermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterPermissionService;
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
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = true)
public class MasterPermissionController implements IMasterPermissionAPI {

    private final MasterPermissionService masterPermissionService;


    @Override
    public Result<String> testPermissionInterface() throws Exception {
        return this.masterPermissionService.testPermissionInterface();
    }

    @Override
    public Result<AbstractRolePermissionRuleVo.PermissionRuleInterfaceDetailVo> getPermissionById(Long permissionId) throws Exception {
        return this.masterPermissionService.getPermissionById(permissionId);
    }

    @Override
    public Result<AbstractRolePermissionRuleVo.PermissionListVo> getPermissionList() throws Exception {
        return this.masterPermissionService.getPermissionList();
    }

    @Override
    public Result<AbstractRolePermissionRuleVo.PermissionListVo> getPermissionListByUserId(Long userId) throws Exception {
        return this.masterPermissionService.getPermissionListByUserId(userId);
    }

    @Override
    public Result<AbstractRolePermissionRuleVo.PermissionListVo> getPermissionListByRoleId(Long roleId) throws Exception {
        return this.masterPermissionService.getPermissionListByRoleId(roleId);
    }


}
