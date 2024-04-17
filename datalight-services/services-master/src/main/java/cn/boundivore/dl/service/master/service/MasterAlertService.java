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

import cn.boundivore.dl.base.request.impl.common.AlertWebhookPayloadRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerMailServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerRelationServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertServiceImpl;
import cn.boundivore.dl.service.master.handler.RemoteInvokePrometheusHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Description: 告警相关逻辑
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/7/13
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasterAlertService {

    private final MasterManageService masterManageService;

    private final RemoteInvokePrometheusHandler remoteInvokePrometheusHandler;


    private final TDlAlertServiceImpl tDlAlertService;

    private final TDlAlertHandlerRelationServiceImpl tDlAlertHandlerRelationService;

    private final TDlAlertHandlerMailServiceImpl tDlAlertHandlerMailService;

    private final MasterAlertNoticeService masterAlertNoticeService;



    /**
     * Description: 接收 AlertManager 告警钩子函数
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request AlertManager 告警封装
     * @return Result<String> 调用成功或失败
     */
    public Result<String> alertHook(AlertWebhookPayloadRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("调用告警钩子接口成功: {}", request);
        }

        log.info("收到告警: {}", request);

        // 根据告警检查是否需要自动拉起服务组件
//        this.masterManageService.checkAndPullServiceComponent(request.getAlerts());

        return Result.success();
    }

    // 新增告警配置
    public Result<String> newAlertRule() {

        // 解析参数

        // 保存数据库

        // 创建文件夹
        // 写入到节点文件
        // 更改权限
        // 解析 prometheus.yml
        // 添加文件绝对路径到 rules 数组

        // 重载 Prometheus 配置，更新告警规则
        this.remoteInvokePrometheusHandler.invokePrometheusReload(1L);
        return Result.success();
    }

    // 删除告警配置

    // 获取告警配置列表

    // 获取告警配置详情

    // 查看历史版本

    // 启用\禁用告警配置


}


