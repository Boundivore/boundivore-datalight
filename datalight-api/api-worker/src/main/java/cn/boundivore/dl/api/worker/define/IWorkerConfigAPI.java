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
package cn.boundivore.dl.api.worker.define;

import cn.boundivore.dl.base.request.impl.worker.ConfigDiffRequest;
import cn.boundivore.dl.base.request.impl.worker.ConfigFileRequest;
import cn.boundivore.dl.base.response.impl.common.ConfigHistoryVersionVo;
import cn.boundivore.dl.base.response.impl.worker.ConfigDifferVo;
import cn.boundivore.dl.base.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import static cn.boundivore.dl.base.constants.IUrlPrefixConstants.WORKER_URL_PREFIX;

/**
 * Description: 对 Worker 所在节点的配置文件的相关操作
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/15
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Tag(name = "Worker 接口：配置文件相关", description = "IWorkerConfigAPI")
@FeignClient(
        name = "IWorkerConfigAPI",
        contextId = "IWorkerConfigAPI",
        path = WORKER_URL_PREFIX
)
public interface IWorkerConfigAPI {

    @PostMapping(value = "/conf/modify")
    @Operation(summary = "修改配置文件", description = "修改配置文件")
    Result<String> config(
            @RequestBody
            @Valid
            ConfigFileRequest request
    );

    @GetMapping(value = "/config/getConfigVersionInfo")
    @Operation(summary = "获取配置文件历史信息", description = "获取配置文件历史信息")
    Result<ConfigHistoryVersionVo> getConfigVersionInfo(
            @Parameter(name = "CurrentConfigVersion", description = "当前配置文件版本")
            @RequestParam(value = "CurrentConfigVersion", required = true)
            Long currentConfigVersion,

            @Parameter(name = "CurrentConfigId", description = "当前生效的配置文件 ID")
            @RequestParam(value = "CurrentConfigId", required = true)
            Long currentConfigId,

            @Parameter(name = "Filename", description = "配置文件名称")
            @RequestParam(value = "Filename", required = true)
            String filename,

            @Parameter(name = "ConfigPath", description = "配置文件路径")
            @RequestParam(value = "ConfigPath", required = true)
            String configPath
    ) throws Exception;

    @GetMapping(value = "/config/getConfigVersionDetail")
    @Operation(summary = "获取配置文件历史详细信息", description = "获取配置文件历史详细信息")
    Result<ConfigHistoryVersionVo.ConfigVersionDetailVo> getConfigVersionDetail(
            @Parameter(name = "Filename", description = "配置文件名称")
            @RequestParam(value = "Filename", required = true)
            String filename,

            @Parameter(name = "ConfigPath", description = "配置文件路径")
            @RequestParam(value = "ConfigPath", required = true)
            String configPath,

            @Parameter(name = "HistoryConfigVersion", description = "历史配置文件版本")
            @RequestParam(value = "HistoryConfigVersion", required = true)
            Long historyConfigVersion
    ) throws Exception;

    @PostMapping(value = "/config/diff")
    @Operation(summary = "比较配置文件差异", description = "比较配置文件差异")
    Result<ConfigDifferVo> configDiff(
            @RequestBody
            @Valid
            ConfigDiffRequest request
    );


}
