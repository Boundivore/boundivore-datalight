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

import cn.boundivore.dl.base.request.impl.master.ConfigSaveByGroupRequest;
import cn.boundivore.dl.base.request.impl.master.ConfigSaveRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.plugin.base.bean.PluginConfigResult;
import cn.boundivore.dl.service.master.bean.ConfigContentPersistedMaps;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 同步或异步修改配置文件逻辑控制服务
 * Created by: Boundivore
 * E-mail: boundivore@formail.com
 * Creation time: 2023/8/1
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MasterConfigSyncService {

    private final MasterConfigService masterConfigService;

    /**
     * Description: 同步操作：根据 Jar 包返回的修改后的配置文件内容，保存到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19 13:41
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request     配置文件保存请求
     * @return 成功返回 true，失败返回  false
     */
    public boolean saveConfigOrUpdateBatch(ConfigSaveRequest request) {
        // 判断如果为有效的配置修改，则发送修改配置请求
        ConfigContentPersistedMaps configContentPersisted = this.masterConfigService.getConfigContentPersisted(
                request
        );

        if (configContentPersisted.isAllPersisted()) {
            log.info("{} 配置已全部就绪，准备关联", request.getServiceName());
            return this.masterConfigService.saveConfigOrUpdateBatch(
                    request,
                    configContentPersisted.getGroupTDlConfigContentMap()
            ).isSuccess();
        } else {
            synchronized (this) {
                log.info("{} 配置未全部就绪，准备同步初始化", request.getServiceName());
                return this.masterConfigService.saveConfigOrUpdateBatch(
                        request,
                        configContentPersisted.getGroupTDlConfigContentMap()
                ).isSuccess();
            }
        }
    }

    /**
     * Description: 根据 Jar 包返回的修改后的配置文件内容，保存到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19 13:41
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param pluginConfigResult 插件对配置文件修改后的最终结果
     * @return boolean 是否成功批量保存配置文件
     */
    public boolean saveConfigOrUpdateBatch(PluginConfigResult pluginConfigResult) {
        // 判断如果为有效的配置修改，则发送修改配置请求
        if (this.masterConfigService.isPluginConfigResultValid(pluginConfigResult)) {
            ConfigSaveRequest request = this.pluginConfigResult2Request(
                    pluginConfigResult
            );
            return this.saveConfigOrUpdateBatch(request);
        }

        return true;
    }

    /**
     * Description: 同步操作：按照配置文件分组，修改配置文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19 13:41
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 将要修改的配置文件分组
     * @return Result<String> 同步分组保存配置文件结果
     */
    public Result<String> saveConfigByGroupSync(ConfigSaveByGroupRequest request) {
        synchronized (this) {
            return this.masterConfigService.saveConfigByGroup(request);
        }
    }

    /**
     * Description: 将 PluginConfigResult 转化为 ConfigSaveRequest
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/19 13:41
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param pluginConfigResult 插件对配置文件修改后的最终结果
     * @return ConfigSaveRequest 转换后的配置文件修改请求
     */
    public ConfigSaveRequest pluginConfigResult2Request(PluginConfigResult pluginConfigResult) {

        Assert.notNull(
                pluginConfigResult,
                () -> new BException("PluginConfigResult 不允许为 null, 如无配置修改，请在插件中返回实例，且配置集合为空集合")
        );
        Assert.notNull(
                pluginConfigResult.getConfigMap(),
                () -> new BException("LinkedHashMap<ConfigKey, ConfigValue> 不允许为 null")
        );

        final Long clusterId = pluginConfigResult.getClusterId();
        final String serviceName = pluginConfigResult.getServiceName();

        //初始化请求体
        ConfigSaveRequest configSaveRequest = new ConfigSaveRequest();
        configSaveRequest.setClusterId(clusterId);
        configSaveRequest.setServiceName(serviceName);
        configSaveRequest.setConfigList(CollUtil.newArrayList());

        pluginConfigResult.getConfigMap().forEach((k, v) -> {
            List<ConfigSaveRequest.ConfigRequest> configList = configSaveRequest.getConfigList();

            ConfigSaveRequest.ConfigRequest config = new ConfigSaveRequest.ConfigRequest()
                    .setNodeId(k.getNodeId())
                    .setFilename(v.getFilename())
                    .setConfigData(v.getConfigData())
                    .setSha256(v.getSha256())
                    .setConfigPath(k.getConfigPath());

            configList.add(config);
        });

        return configSaveRequest;

    }
}
