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

import cn.boundivore.dl.api.master.define.IMasterDLCAPI;
import cn.boundivore.dl.base.response.impl.master.AbstractDLCVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterDLCService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterDLCController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
//@SaCheckLogin
public class MasterDLCController implements IMasterDLCAPI {

    private final MasterDLCService masterDLCService;


    @Override
    public Result<AbstractDLCVo.DLCServiceVo> dlcServiceList() throws Exception {
        return masterDLCService.dlcServiceList();
    }

    @Override
    public Result<AbstractDLCVo.DLCComponentVo> dlcComponentList() throws Exception {
        return masterDLCService.dlcComponentList();
    }
}
