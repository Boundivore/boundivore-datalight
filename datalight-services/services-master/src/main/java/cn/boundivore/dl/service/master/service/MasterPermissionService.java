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

import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.base.request.impl.master.AbstractPermissionRuleRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractRolePermissionRuleVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.service.single.impl.TDlPermissionRuleRelationServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlPermissionServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlRuleInterfaceServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

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

    private final MasterPermissionTemplatedService masterPermissionTemplatedService;

    private final TDlPermissionServiceImpl tDlPermissionService;
    private final TDlRuleInterfaceServiceImpl tDlRuleInterfaceService;
    private final TDlPermissionRuleRelationServiceImpl tDlPermissionRuleRelationService;

    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @PostConstruct
    public void init() {
        this.initPermissionFromTemplated();
    }

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
     * Description: 从权限模板中加载权限信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void initPermissionFromTemplated() {
        
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
     * @return Result<AbstractRolePermissionRuleVo.PermissionRuleInterfaceListVo> 接口权限规则列表
     */
    public Result<AbstractRolePermissionRuleVo.PermissionRuleInterfaceListVo> listPermissionRuleInterface(Long userId,
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
     * @return Result<AbstractRolePermissionRuleVo.PermissionRuleListVo> 返回更新后的内容
     */
    public Result<AbstractRolePermissionRuleVo.PermissionRuleListVo> putPermissionBatch(AbstractPermissionRuleRequest.NewPermissionAndRuleRequest request) {
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
     * @return Result<AbstractRolePermissionRuleVo.PermissionRuleDetailsVo> 返回权限详情
     */
    public Result<AbstractRolePermissionRuleVo.PermissionRuleDetailsVo> details(Long permissionId) {
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
     * @return Result<AbstractRolePermissionRuleVo.PermissionRuleListVo> 权限信息
     */
    public Result<AbstractRolePermissionRuleVo.PermissionRuleListVo> listPermissionByRoleId(Long roleId) {
        return null;
    }
}
