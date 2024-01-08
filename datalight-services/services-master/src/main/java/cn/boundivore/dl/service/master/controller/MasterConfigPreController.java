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

import cn.boundivore.dl.api.master.define.IMasterConfigPreAPI;
import cn.boundivore.dl.base.request.impl.master.ConfigPreSaveRequest;
import cn.boundivore.dl.base.response.impl.master.ConfigPreVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.service.master.service.MasterConfigPreService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description: MasterConfigPreController
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/3/30
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@RestController
@RequiredArgsConstructor
//@SaCheckLogin
public class MasterConfigPreController implements IMasterConfigPreAPI {

    private final MasterConfigPreService masterConfigPreService;


    @Override
    public Result<ConfigPreVo> configPreList(Long clusterId) throws Exception {
        return this.masterConfigPreService.configPreList(clusterId);
    }

    @Override
    public Result<String> configPreSave(ConfigPreSaveRequest request) throws Exception {
        return this.masterConfigPreService.configPreSave(request);
    }
}
