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
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.common.AlertWebhookPayloadRequest;
import cn.boundivore.dl.base.request.impl.master.AbstractAlertRequest;
import cn.boundivore.dl.base.request.impl.master.ConfigSaveRequest;
import cn.boundivore.dl.base.request.impl.worker.ConfigFileRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractAlertVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeVo;
import cn.boundivore.dl.base.response.impl.master.ConfigListByGroupVo;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.base.utils.YamlDeserializer;
import cn.boundivore.dl.base.utils.YamlSerializer;
import cn.boundivore.dl.boot.lock.LocalLock;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlAlert;
import cn.boundivore.dl.orm.po.single.TDlAlertHandlerRelation;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerMailServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertHandlerRelationServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlAlertServiceImpl;
import cn.boundivore.dl.plugin.base.bean.config.YamlPrometheusConfig;
import cn.boundivore.dl.plugin.base.bean.config.YamlPrometheusRulesConfig;
import cn.boundivore.dl.service.master.converter.IAlertRuleConverter;
import cn.boundivore.dl.service.master.handler.RemoteInvokePrometheusHandler;
import cn.boundivore.dl.service.master.resolver.ResolverYamlDirectory;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    private static final String MONITOR_SERVICE_NAME = "MONITOR";
    public static final String ALERT_RULE_FILE_FORMAT = "RULE-CUSTOM-%s";
    public static final String ALERT_RULE_FILE_PATH_FORMAT = "%s/MONITOR/prometheus/rules/custom/%s";
    public static final String PROMETHEUS_YML_FILE_PATH_FORMAT = "%s/MONITOR/prometheus/prometheus.yml";

    private final RemoteInvokeWorkerService remoteInvokeWorkerService;

    private final MasterComponentService masterComponentService;

    private final MasterNodeService masterNodeService;

    private final MasterManageService masterManageService;

    private final MasterConfigService masterConfigService;

    private final MasterConfigSyncService masterConfigSyncService;

    private final RemoteInvokePrometheusHandler remoteInvokePrometheusHandler;

    private final IAlertRuleConverter iAlertRuleConverter;


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

    /**
     * Description: 新建告警规则
     * 注：参数中 expr 表达式可能包含多种操作运算符和特殊字符，因此，此处传递时应该以 Base64 进行，且入库时，需要再对整体字符串进行 Base64 编码
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/18
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 新建规则请求体
     * @return Result<AbstractAlertVo.AlertRuleVo> 新建的告警规则信息
     */
    // 新增告警配置
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @LocalLock
    public Result<AbstractAlertVo.AlertRuleVo> newAlertRule(AbstractAlertRequest.NewAlertRuleRequest request) throws JsonProcessingException {

        // 检查请求参数是否合法
        this.checkNewAlertRuleRequest(request);

        // 解析参数到 YamlBean
        YamlPrometheusRulesConfig yamlPrometheusRulesConfig = this.iAlertRuleConverter.convert2YamlPrometheusRulesConfig(
                request.getAlertRuleContent()
        );

        log.info("解析完毕:\n{}\n", YamlDeserializer.toString(yamlPrometheusRulesConfig));

        String ruleFileName = String.format(
                ALERT_RULE_FILE_FORMAT,
                request.getAlertRuleName()
        );

        String ruleFilePath = String.format(
                ALERT_RULE_FILE_PATH_FORMAT,
                ResolverYamlDirectory.DIRECTORY_YAML.getDatalight().getServiceDir(),
                ruleFileName
        );

        log.info("告警规则文件路径: {}", ruleFilePath);


        // 保存数据库
        TDlAlert tDlAlert = new TDlAlert();
        tDlAlert.setVersion(0L);
        tDlAlert.setAlertName(request.getAlertRuleName());
        tDlAlert.setAlertFileName(ruleFileName);
        tDlAlert.setAlertFilePath(ruleFilePath);
        tDlAlert.setAlertRuleContent(
                Base64.encode(
                        YamlDeserializer.toString(yamlPrometheusRulesConfig),
                        CharsetUtil.UTF_8
                )
        );
        tDlAlert.setEnabled(true);
        tDlAlert.setAlertVersion(1L);
        tDlAlert.setHandlerType(request.getAlertHandlerTypeEnum());

        Assert.isTrue(
                tDlAlertService.save(tDlAlert),
                () -> new DatabaseException("保存告警规则到数据库失败")
        );

        // 关系保存到数据库
        TDlAlertHandlerRelation tDlAlertHandlerRelation = new TDlAlertHandlerRelation();
        tDlAlertHandlerRelation.setAlertId(tDlAlert.getId());
        tDlAlertHandlerRelation.setHandlerId(request.getHandlerId());

        Assert.isTrue(
                this.tDlAlertHandlerRelationService.save(tDlAlertHandlerRelation),
                () -> new DatabaseException("保存告警规则关联关系到数据库失败")
        );

        // 将文件写入到指定节点的指定目录
        // 如果希望这部分告警规则的配置文件，别列入到配置文件管理中，则可以通过调用共用配置文件服务的函数来实现
        final AbstractNodeVo.NodeDetailVo nodeDetailVo = this.writeAlertRuleFile2Target(
                request.getClusterId(),
                tDlAlert.getAlertFileName(),
                tDlAlert.getAlertFilePath(),
                tDlAlert.getAlertVersion(),
                tDlAlert.getAlertRuleContent()
        );


        // 读取 prometheus.yml 文件
        String prometheusFilePath = String.format(
                PROMETHEUS_YML_FILE_PATH_FORMAT,
                ResolverYamlDirectory.DIRECTORY_YAML.getDatalight().getServiceDir()
        );

        ConfigListByGroupVo.ConfigGroupVo monitorServiceConfigGroupVo = this.masterConfigService
                .getConfigListByGroup(
                        request.getClusterId(),
                        MONITOR_SERVICE_NAME,
                        prometheusFilePath
                )
                .getData()
                .getConfigGroupList()
                .stream()
                .filter(i -> {
                            List<String> nodeIpList = i.getConfigNodeList()
                                    .stream()
                                    .map(ConfigListByGroupVo.ConfigNodeVo::getNodeIp)
                                    .collect(Collectors.toList());

                            return nodeIpList.contains(nodeDetailVo.getNodeIp());
                        }
                )
                .findFirst()
                .orElseThrow(
                        () -> new BException("当前集群中无法找到 MONITOR-Prometheus 配置文件")
                );

        String decodeConfigContent = Base64.decodeStr(
                monitorServiceConfigGroupVo.getConfigData(),
                CharsetUtil.UTF_8
        );

        // 解析 prometheus.yml
        YamlPrometheusConfig yamlPrometheusConfig = YamlSerializer.toObject(
                decodeConfigContent,
                YamlPrometheusConfig.class
        );
        // 添加文件绝对路径到 rules 数组
        yamlPrometheusConfig.getRuleFiles().add(tDlAlert.getAlertFilePath());

        // 反序列化为 Base64 字符串，并修改配置文件
        String newPrometheusYmlConfigBase64 = Base64.encode(
                YamlDeserializer.toString(yamlPrometheusConfig),
                CharsetUtil.UTF_8
        );
        String newPrometheusYmlSha256 = SecureUtil.sha256(newPrometheusYmlConfigBase64);


        // 保存 prometheus.yml 配置
        ConfigSaveRequest configSaveRequest = new ConfigSaveRequest();
        configSaveRequest.setClusterId(request.getClusterId());
        configSaveRequest.setServiceName(MONITOR_SERVICE_NAME);

        boolean isSuccess = this.masterConfigSyncService.saveConfigOrUpdateBatch(
                new ConfigSaveRequest(
                        request.getClusterId(),
                        MONITOR_SERVICE_NAME,
                        CollUtil.newArrayList(
                                new ConfigSaveRequest.ConfigRequest(
                                        nodeDetailVo.getNodeId(),
                                        "prometheus.yml",
                                        newPrometheusYmlConfigBase64,
                                        newPrometheusYmlSha256,
                                        prometheusFilePath
                                )
                        )
                )
        );

        Assert.isTrue(
                isSuccess,
                () -> new BException("修改 prometheus.yml 配置文件失败")
        );

        // 重载 Prometheus 配置，更新告警规则
        this.remoteInvokePrometheusHandler.invokePrometheusReload(nodeDetailVo.getHostname());


        // 组装返回报文
        AbstractAlertVo.AlertRuleVo alertRuleVo = new AbstractAlertVo.AlertRuleVo();
        AbstractAlertVo.AlertRuleContentVo alertRuleContentVo = this.iAlertRuleConverter.convert2AlertRuleContentVo(
                yamlPrometheusRulesConfig
        );
        alertRuleVo.setAlertRuleId(tDlAlert.getId());
        alertRuleVo.setAlertRuleName(tDlAlert.getAlertName());
        alertRuleVo.setAlertFilePath(tDlAlert.getAlertFilePath());
        alertRuleVo.setAlertFileName(tDlAlert.getAlertFileName());
        alertRuleVo.setAlertRuleContentBase64(tDlAlert.getAlertRuleContent());
        alertRuleVo.setEnabled(tDlAlert.getEnabled());
        alertRuleVo.setAlertVersion(tDlAlert.getAlertVersion());
        alertRuleVo.setHandlerType(tDlAlert.getHandlerType());

        alertRuleVo.setAlertRuleContent(alertRuleContentVo);

        return Result.success(alertRuleVo);
    }

    /**
     * Description: 检查新增的是否合理告警规则请求是否合理
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/18
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 新增的告警规则请求
     */
    private void checkNewAlertRuleRequest(AbstractAlertRequest.NewAlertRuleRequest request) {
        // 检查是否存在同名告警名称
        Assert.isFalse(
                this.tDlAlertService.lambdaQuery()
                        .select()
                        .eq(TDlAlert::getAlertName, request.getAlertRuleName())
                        .exists(),
                () -> new BException("存在同名规则配置")
        );

        // 检查关联配置信息是否存在
        switch (request.getAlertHandlerTypeEnum()) {
            case ALERT_INTERFACE:
                Assert.notNull(
                        null,
                        () -> new BException("无法找到[接口]告警处理方式的配置 ID")
                );
                break;
            case ALERT_MAIL:
                Assert.notNull(
                        this.tDlAlertHandlerMailService.getById(request.getHandlerId()),
                        () -> new BException("无法找到[邮件]告警处理方式的配置 ID")
                );
                break;
            case ALERT_WEICHAT:
                Assert.notNull(
                        null,
                        () -> new BException("无法找到[微信]告警处理方式的配置 ID")
                );
                break;
            case ALERT_FEISHU:
                Assert.notNull(
                        null,
                        () -> new BException("无法找到[飞书]告警处理方式的配置 ID")
                );
                break;
            case ALERT_DINGDING:
                Assert.notNull(
                        null,
                        () -> new BException("无法找到[钉钉]告警处理方式的配置 ID")
                );
                break;
            default:
                Assert.isTrue(
                        request.getHandlerId() == null,
                        () -> new BException(
                                String.format(
                                        "%s 类型的告警处理不应存在处理配置信息 %s, 请置空",
                                        request.getAlertHandlerTypeEnum(),
                                        request.getHandlerId()
                                )
                        )
                );
                break;
        }
    }

    /**
     * Description: 将告警配置文件写入到指定节点的指定路径
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/18
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId     集群 ID
     * @param filename      文件名称
     * @param filePath      文件绝对路径
     * @param configVersion 文件当前静态版本
     * @param contentBase64 文件内容 Base64
     * @return String 节点 IP
     */
    private AbstractNodeVo.NodeDetailVo writeAlertRuleFile2Target(Long clusterId,
                                                                  String filename,
                                                                  String filePath,
                                                                  Long configVersion,
                                                                  String contentBase64) {
        Assert.notEmpty(
                contentBase64,
                "配置文件 Base64 内容不能为空"
        );

        TDlComponent tDlComponent = this.findPrometheusComponent(clusterId);
        AbstractNodeVo.NodeDetailVo nodeDetailVo = this.masterNodeService.getNodeDetailById(tDlComponent.getNodeId())
                .getData();

        ConfigFileRequest configFileRequest = new ConfigFileRequest(
                filePath,
                configVersion,
                filename,
                contentBase64,
                SecureUtil.sha256(contentBase64)
        );

        Result<String> result = this.remoteInvokeWorkerService.iWorkerConfigAPI(nodeDetailVo.getNodeIp())
                .config(configFileRequest);

        Assert.isTrue(
                result.isSuccess(),
                () -> new BException(
                        String.format(
                                "写入文件到节点自定路径失败: %s, %s",
                                result.getMessage(),
                                result.getData()
                        )
                )
        );

        return nodeDetailVo;
    }

    /**
     * Description: 查找指定集群中的 Prometheus 所在位置
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/18
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return TDlComponent Prometheus 组件数据库实例
     */
    private TDlComponent findPrometheusComponent(Long clusterId) {
        return this.masterComponentService.getTDlComponentByComponentName(
                        clusterId,
                        "MONITOR",
                        "Prometheus"
                )
                .stream()
                .filter(i -> i.getComponentState() != SCStateEnum.REMOVED
                        && i.getComponentState() != SCStateEnum.UNSELECTED
                        && i.getComponentState() != SCStateEnum.SELECTED)
                .findFirst()
                .orElseThrow(
                        () -> new BException("当前集群中无法找到 MONITOR-Prometheus 实例")
                );
    }


    // 删除告警配置

    // 获取告警配置列表

    // 获取告警配置详情

    // 查看历史版本

    // 启用\禁用告警配置


}


