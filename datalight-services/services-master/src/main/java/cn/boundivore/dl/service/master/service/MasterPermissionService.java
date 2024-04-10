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

import cn.boundivore.dl.base.request.impl.master.AbstractPermissionRuleRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractPermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: 权限管理相关
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/7
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterPermissionService {

    /**
     * Description: 测试接口权限
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 通过则返回成功
     */
    public Result<String> testPermissionInterface() {
        return Result.success();
    }


    /**
     * Description: 根据用户 ID 以及接口 URI 获取接口权限规则列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param userId           用户 ID
     * @param ruleInterfaceUri 规则接口 URI
     * @return Result<AbstractPermissionRuleVo.PermissionRuleInterfaceListVo> 接口权限规则列表
     */
    public Result<AbstractPermissionRuleVo.PermissionRuleInterfaceListVo> listPermissionRuleInterface(Long userId,
                                                                                                      String ruleInterfaceUri) {
        return null;
    }

    /**
     * Description: 更新权限规则列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 待更新权限请求体
     * @return Result<AbstractPermissionRuleVo.PermissionRuleListVo> 返回更新后的内容
     */
    public Result<AbstractPermissionRuleVo.PermissionRuleListVo> putPermissionBatch(AbstractPermissionRuleRequest.NewPermissionAndRuleRequest request) {
        return null;
    }

    /**
     * Description: 根据权限 ID 获取权限详情信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/9
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param permissionId 权限 ID
     * @return Result<AbstractPermissionRuleVo.PermissionRuleDetailsVo> 返回权限详情
     */
    public Result<AbstractPermissionRuleVo.PermissionRuleDetailsVo> details(Long permissionId) {
        return null;
    }

    /**
     * Description: 根据角色 ID 后去权限信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/10
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param roleId 角色 ID
     * @return Result<AbstractPermissionRuleVo.PermissionRuleListVo> 权限信息
     */
    public Result<AbstractPermissionRuleVo.PermissionRuleListVo> listPermissionByRoleId(Long roleId) {
        return null;
    }
}
