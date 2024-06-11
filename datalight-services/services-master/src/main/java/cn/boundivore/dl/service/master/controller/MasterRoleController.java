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

import cn.boundivore.dl.api.master.define.IMasterRoleAPI;
import cn.boundivore.dl.base.enumeration.impl.LogTypeEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractRoleRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractRolePermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.boot.logs.Logs;
import cn.boundivore.dl.service.master.service.MasterRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterRoleController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/12
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
@Logs(logType = LogTypeEnum.MASTER, isPrintResult = true)
public class MasterRoleController implements IMasterRoleAPI {

    private final MasterRoleService masterRoleService;


    @Override
    public Result<AbstractRolePermissionRuleVo.RoleVo> newRole(AbstractRoleRequest.NewRoleRequest request) throws Exception {
        return this.masterRoleService.newRole(request);
    }

    @Override
    public Result<AbstractRolePermissionRuleVo.RoleVo> switchRoleEnabled(AbstractRoleRequest.SwitchRoleEnabledRequest request) throws Exception {
        return this.masterRoleService.switchRoleEnabled(request);
    }

    @Override
    public Result<AbstractRolePermissionRuleVo.RoleVo> getRoleById(Long roleId) throws Exception {
        return this.masterRoleService.getRoleById(roleId);
    }

    @Override
    public Result<AbstractRolePermissionRuleVo.RoleListVo> getRoleList() throws Exception {
        return this.masterRoleService.getRoleList();
    }

    @Override
    public Result<AbstractRolePermissionRuleVo.RoleListVo> getRoleListByUserId(Long userId) throws Exception {
        return this.masterRoleService.getRoleListByUserId(userId);
    }

    @Override
    public Result<String> removeRoleBatchByIdList(AbstractRoleRequest.RoleIdListRequest request) throws Exception {
        return this.masterRoleService.removeRoleBatchByIdList(request);
    }
}
