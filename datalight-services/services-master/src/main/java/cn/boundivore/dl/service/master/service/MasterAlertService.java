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
import cn.boundivore.dl.base.enumeration.impl.AlertHandlerTypeEnum;
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
import cn.boundivore.dl.orm.po.TBasePo;
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
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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

    private static final String ERR_MSG_TEMPLATE = "暂时无法找到 [%s] 告警处理方式的配置 ID";
    private static final String ERR_MSG_ID_REQUIRED = "类型为 %s 时告警处理配置 ID (HandlerId) 不能为空";

    private static final String MONITOR_SERVICE_NAME = "MONITOR";
    public static final String ALERT_RULE_FILE_FORMAT = "RULE-CUSTOM-%s.yaml";
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
     * 注：参数中 expr 表达式可能包含多种操作运算符和特殊字符，因此，此处传递时应该以 Base64 进行，
     * 且入库时，需要再对整体字符串进行 Base64 编码
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
        // 解码 Expr Base64 格式的表达式的值
        yamlPrometheusRulesConfig.getGroups()
                .forEach(ruleGroup ->
                        ruleGroup.getRules().forEach(rule -> {
                                    String exprDecodeBase64 = Base64.decodeStr(
                                            rule.getExpr(),
                                            CharsetUtil.UTF_8
                                    );
                                    rule.setExpr(exprDecodeBase64);
                                }
                        )
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
        tDlAlert.setClusterId(request.getClusterId());
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


        // 获取 Prometheus 所在节点的详细信息
        TDlComponent tDlComponent = this.findPrometheusComponent(request.getClusterId());
        AbstractNodeVo.NodeDetailVo nodeDetailVo = this.masterNodeService.getNodeDetailById(tDlComponent.getNodeId())
                .getData();

        // 将文件写入到指定节点的指定目录, 如果希望这部分告警规则的配置文件，被列入到配置文件管理中，则可以通过调用共用配置文件服务的函数来实现
        this.writeAlertRuleFile2Target(
                nodeDetailVo,
                tDlAlert.getAlertFileName(),
                tDlAlert.getAlertFilePath(),
                tDlAlert.getAlertVersion(),
                tDlAlert.getAlertRuleContent()
        );

        String prometheusFilePath = String.format(
                PROMETHEUS_YML_FILE_PATH_FORMAT,
                ResolverYamlDirectory.DIRECTORY_YAML.getDatalight().getServiceDir()
        );

        // 读取 prometheus.yml 文件, 解析 Prometheus YamlJavaBean
        YamlPrometheusConfig yamlPrometheusConfig = this.parseYamlPrometheusConfig(
                request.getClusterId(),
                prometheusFilePath,
                nodeDetailVo
        );
        // 添加文件绝对路径到 rules 数组
        yamlPrometheusConfig.getRuleFiles().add(tDlAlert.getAlertFilePath());


        // 远程写入 prometheus.yml 配置文件
        this.writePrometheusYml(
                request.getClusterId(),
                nodeDetailVo,
                prometheusFilePath,
                yamlPrometheusConfig
        );


        // 重载 Prometheus 配置，更新告警规则
        this.remoteInvokePrometheusHandler.invokePrometheusReload(nodeDetailVo.getHostname());

        // 组装返回报文
        AbstractAlertVo.AlertRuleVo alertRuleVo = this.createAlertRuleVo(yamlPrometheusRulesConfig, tDlAlert);

        return Result.success(alertRuleVo);
    }

    /**
     * Description: 解析 Prometheus 文件
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId    集群 ID
     * @param nodeDetailVo Prometheus 所在节点的详细信息
     * @return YamlPrometheusConfig Prometheus YamlJavaBean
     */
    public YamlPrometheusConfig parseYamlPrometheusConfig(Long clusterId,
                                                          String prometheusFilePath,
                                                          AbstractNodeVo.NodeDetailVo nodeDetailVo) throws JsonProcessingException {


        ConfigListByGroupVo.ConfigGroupVo monitorServiceConfigGroupVo = this.masterConfigService
                .getConfigListByGroup(
                        clusterId,
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

        return yamlPrometheusConfig;
    }

    /**
     * Description: 远程写入 Prometheus 配置文件到对端本地目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId            集群 ID
     * @param nodeDetailVo         Prometheus 所在节点的详细信息
     * @param prometheusFilePath   prometheus.yml 配置文件路径
     * @param yamlPrometheusConfig YamlJavaBaean
     */
    public void writePrometheusYml(Long clusterId,
                                   AbstractNodeVo.NodeDetailVo nodeDetailVo,
                                   String prometheusFilePath,
                                   YamlPrometheusConfig yamlPrometheusConfig) throws JsonProcessingException {
        // 反序列化为 Base64 字符串，并修改配置文件
        String newPrometheusYmlConfigBase64 = Base64.encode(
                YamlDeserializer.toString(yamlPrometheusConfig),
                CharsetUtil.UTF_8
        );
        String newPrometheusYmlSha256 = SecureUtil.sha256(newPrometheusYmlConfigBase64);


        // 保存 prometheus.yml 配置
        ConfigSaveRequest configSaveRequest = new ConfigSaveRequest();
        configSaveRequest.setClusterId(clusterId);
        configSaveRequest.setServiceName(MONITOR_SERVICE_NAME);

        boolean isSuccess = this.masterConfigSyncService.saveConfigOrUpdateBatch(
                new ConfigSaveRequest(
                        clusterId,
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
    }

    /**
     * Description: 创建告警规则详情响应体
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param yamlPrometheusRulesConfig 告警规则 YamlJavaBean
     * @param tDlAlert                  告警规则数据库实体
     * @return AbstractAlertVo.AlertRuleVo 告警规则详情响应体
     */
    public AbstractAlertVo.AlertRuleVo createAlertRuleVo(YamlPrometheusRulesConfig yamlPrometheusRulesConfig, TDlAlert tDlAlert) {

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
        alertRuleVo.setAlertHandlerTypeEnum(tDlAlert.getHandlerType());
        alertRuleVo.setAlertRuleContent(alertRuleContentVo);

        return alertRuleVo;
    }


    /**
     * Description: 检查新增的告警规则请求是否合理
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
        this.checkForDuplicateAlertName(request.getAlertRuleName());
    }

    /**
     * Description: 检查是否存在同名的告警规则
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/18
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alertName 告警名称
     */
    private void checkForDuplicateAlertName(String alertName) {
        boolean exists = this.tDlAlertService.lambdaQuery()
                .select()
                .eq(TDlAlert::getAlertName, alertName)
                .exists();
        Assert.isFalse(exists, () -> new BException("存在同名规则配置"));
    }

    /**
     * Description: 检查关联的处理器配置是否存在
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/18
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alertHandlerTypeEnum 告警触发后的处理类型
     * @param handlerId            处理配置信息 ID
     */
    private void checkHandlerExistence(AlertHandlerTypeEnum alertHandlerTypeEnum, Long handlerId) {

        switch (alertHandlerTypeEnum) {
            case ALERT_INTERFACE:
                this.checkNotNullHandler(alertHandlerTypeEnum, handlerId, "接口");
                break;
            case ALERT_MAIL:
                this.checkNotNullHandler(alertHandlerTypeEnum, handlerId, "邮件");
                break;
            case ALERT_WEICHAT:
                this.checkNotNullHandler(alertHandlerTypeEnum, handlerId, "微信");
                break;
            case ALERT_FEISHU:
                this.checkNotNullHandler(alertHandlerTypeEnum, handlerId, "飞书");
                break;
            case ALERT_DINGDING:
                this.checkNotNullHandler(alertHandlerTypeEnum, handlerId, "钉钉");
                break;
            default:
                Assert.isTrue(
                        handlerId == null,
                        () -> new BException(
                                String.format(
                                        "%s 类型的告警处理不应存在处理配置信息 %s, 请置空",
                                        alertHandlerTypeEnum,
                                        handlerId
                                )
                        )
                );
                break;
        }
    }


    /**
     * Description: 检查指定类型的处理器配置 ID 是否正确
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/18
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alertHandlerTypeEnum 告警处理类型
     * @param handlerId            处理配置信息 ID
     * @param targetName           告警处理目标
     */
    private void checkNotNullHandler(AlertHandlerTypeEnum alertHandlerTypeEnum,
                                     Long handlerId,
                                     String targetName) {

        IService<?> service = this.getServiceByHandlerType(alertHandlerTypeEnum);

        Assert.notNull(
                service != null ? service.getById(handlerId) : null,
                () -> new BException(
                        String.format(
                                ERR_MSG_TEMPLATE,
                                targetName
                        )
                )
        );

        Assert.notNull(
                handlerId,
                () -> new BException(
                        String.format(
                                ERR_MSG_ID_REQUIRED,
                                targetName
                        )
                )
        );
    }


    /**
     * Description: 根据告警处理类型获取相应的数据库操作服务实例
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/18
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param type 告警处理类型
     * @return IService<?> 对应的服务实例
     */
    private IService<?> getServiceByHandlerType(AlertHandlerTypeEnum type) {
        switch (type) {
            case ALERT_INTERFACE:
                return null;
            case ALERT_MAIL:
                return this.tDlAlertHandlerMailService;
            case ALERT_WEICHAT:
                return null;
            case ALERT_FEISHU:
                return null;
            case ALERT_DINGDING:
                return null;
            default:
                return null;
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
     * @param nodeDetailVo  Prometheus 所在节点的详细信息
     * @param filename      文件名称
     * @param filePath      文件绝对路径
     * @param configVersion 文件当前静态版本
     * @param contentBase64 文件内容 Base64
     */
    private void writeAlertRuleFile2Target(AbstractNodeVo.NodeDetailVo nodeDetailVo,
                                           String filename,
                                           String filePath,
                                           Long configVersion,
                                           String contentBase64) {
        Assert.notEmpty(
                contentBase64,
                "配置文件 Base64 内容不能为空"
        );

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


    /**
     * Description: 删除告警配置
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警 ID 列表请求体
     * @return Result<String> 成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> removeAlertRule(AbstractAlertRequest.AlertIdListRequest request) throws JsonProcessingException {
        // 检查 ID 是否全部存在
        this.checkAlertIdExists(request.getClusterId(), request.getAlertIdList());

        // 检查告警处理配置关联表是否存在关联，如果存在，则移除关联
        List<TDlAlertHandlerRelation> tDlAlertHandlerRelationList = this.tDlAlertHandlerRelationService.lambdaQuery()
                .select()
                .in(TDlAlertHandlerRelation::getAlertId, request.getAlertIdList())
                .list();

        // 移除关联关系
        Assert.isTrue(
                this.tDlAlertHandlerRelationService.removeBatchByIds(tDlAlertHandlerRelationList),
                () -> new DatabaseException("移除告警关联关系失败")
        );

        // 执行删除操作
        List<TDlAlert> tDlAlertList = this.tDlAlertService.listByIds(request.getAlertIdList());
        Assert.isTrue(
                this.tDlAlertService.removeBatchByIds(tDlAlertList),
                () -> new DatabaseException("移除告警信息失败")
        );


        // 获取 Prometheus 所在节点的详细信息
        TDlComponent tDlComponent = this.findPrometheusComponent(request.getClusterId());
        AbstractNodeVo.NodeDetailVo nodeDetailVo = this.masterNodeService.getNodeDetailById(tDlComponent.getNodeId())
                .getData();

        String prometheusFilePath = String.format(
                PROMETHEUS_YML_FILE_PATH_FORMAT,
                ResolverYamlDirectory.DIRECTORY_YAML.getDatalight().getServiceDir()
        );

        // 读取 prometheus.yml 文件， 解析 Prometheus YamlJavaBean
        YamlPrometheusConfig yamlPrometheusConfig = this.parseYamlPrometheusConfig(
                request.getClusterId(),
                prometheusFilePath,
                nodeDetailVo
        );

        // 将告警文件绝对路径从 rules 数组中移除，删除 prometheus.yml 中的告警配置项
        tDlAlertList.forEach(i -> yamlPrometheusConfig.getRuleFiles().remove(i.getAlertFilePath()));

        // 远程写入 prometheus.yml 配置文件
        this.writePrometheusYml(
                request.getClusterId(),
                nodeDetailVo,
                prometheusFilePath,
                yamlPrometheusConfig
        );


        // 重载 Prometheus 配置，更新告警规则
        this.remoteInvokePrometheusHandler.invokePrometheusReload(nodeDetailVo.getHostname());

        return Result.success();
    }

    /**
     * Description: 检查告警 ID 是否存在
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param alertIdList 告警 ID 列表
     */
    public void checkAlertIdExists(Long clusterId, List<Long> alertIdList) {
        Assert.notEmpty(
                alertIdList,
                () -> new BException("告警 ID 列表不能为空")
        );

        List<Long> distinctAlertIdList = alertIdList
                .stream()
                .distinct()
                .collect(Collectors.toList());

        List<TDlAlert> tDlAlertList = this.tDlAlertService.lambdaQuery()
                .select()
                .eq(TDlAlert::getClusterId, clusterId)
                .in(
                        TBasePo::getId,
                        distinctAlertIdList
                )
                .list();

        Assert.isTrue(
                tDlAlertList.size() == distinctAlertIdList.size(),
                () -> new BException("给定集群中告警 ID 列表中不允许出现不存在的 ID")
        );
    }


    /**
     * Description: 获取告警配置信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Result<AbstractAlertVo.AlertSimpleListVo> 告警配置信息列表
     */
    public Result<AbstractAlertVo.AlertSimpleListVo> getAlertSimpleList(Long clusterId) {
        AbstractAlertVo.AlertSimpleListVo alertSimpleListVo = new AbstractAlertVo.AlertSimpleListVo();

        List<TDlAlert> tDlAlertList = this.tDlAlertService.lambdaQuery()
                .select()
                .eq(TDlAlert::getClusterId, clusterId)
                .list();

        List<AbstractAlertVo.AlertSimpleVo> alertSimpleVoList = tDlAlertList.stream()
                .map(i -> new AbstractAlertVo.AlertSimpleVo(
                                i.getId(),
                                i.getAlertName(),
                                i.getEnabled(),
                                i.getHandlerType()
                        )
                )
                .collect(Collectors.toList());

        alertSimpleListVo.setAlertSimpleList(alertSimpleVoList);


        return Result.success(alertSimpleListVo);
    }

    /**
     * Description: 根据 ID 获取告警配置详情
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param alertId 告警配置 ID
     * @return Result<AbstractAlertVo.AlertRuleVo> 告警配置详情
     */
    public Result<AbstractAlertVo.AlertRuleVo> getAlertDetailById(Long alertId) throws JsonProcessingException {
        TDlAlert tDlAlert = this.tDlAlertService.getById(alertId);
        Assert.notNull(
                tDlAlert,
                () -> new BException("无法找到对应信息")
        );

        // 解析 Base64
        String ruleYamlStr = Base64.decodeStr(
                tDlAlert.getAlertRuleContent(),
                CharsetUtil.UTF_8
        );
        // 解析到 Yaml Bean
        YamlPrometheusRulesConfig yamlPrometheusRulesConfig = YamlSerializer.toObject(
                ruleYamlStr,
                YamlPrometheusRulesConfig.class
        );

        // 组装返回报文
        AbstractAlertVo.AlertRuleVo alertRuleVo = this.createAlertRuleVo(
                yamlPrometheusRulesConfig,
                tDlAlert
        );

        return Result.success(alertRuleVo);

    }


    /**
     * Description: 启用、禁用告警配置
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 启用停用响应体
     * @return Result<String> 操作成功或失败
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public Result<String> switchAlertEnabled(AbstractAlertRequest.AlertSwitchEnabledListRequest request) {

        Assert.notEmpty(
                request.getAlertSwitchEnabledList(),
                () -> new BException("操作列表不能为空")
        );

        // 读取数据库，并将实体转为映射关系: <AlertId, TDlAlert>
        Map<Long, TDlAlert> tDlAlertMap = this.tDlAlertService.lambdaQuery()
                .select()
                .eq(TDlAlert::getClusterId, request.getClusterId())
                .in(
                        TBasePo::getId,
                        request.getAlertSwitchEnabledList()
                                .stream()
                                .map(AbstractAlertRequest.AlertSwitchEnabledRequest::getAlertId)
                                .collect(Collectors.toList())
                )
                .list()
                .stream()
                .collect(Collectors.toMap(TDlAlert::getId, alert -> alert));

        // 检查集群 ID 与告警 ID 是否匹配
        Assert.isTrue(
                request.getAlertSwitchEnabledList().size() == tDlAlertMap.size(),
                () -> new BException("操作列表信息不一致，同批次只能操作同集群的告警列表")
        );

        // 更新数据库实体的 Enabled 值
        request.getAlertSwitchEnabledList().forEach(i -> {
            tDlAlertMap.get(i.getAlertId()).setEnabled(i.getEnabled());
        });

        // 更新到数据库
        Assert.isTrue(
                this.tDlAlertService.updateBatchById(tDlAlertMap.values()),
                () -> new DatabaseException("更新启用停用到数据库失败")
        );

        return Result.success();
    }


    /**
     * Description: 修改告警规则信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/4/19
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param request 告警配置信息更新请求体
     * @return Result<AbstractAlertVo.AlertRuleVo> 修改后的告警内容
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    @LocalLock
    public Result<AbstractAlertVo.AlertRuleVo> updateAlertRule(AbstractAlertRequest.UpdateAlertRuleRequest request) throws JsonProcessingException {

        // 检查请求合法性
        this.checkUpdateAlertRuleRequest(request);

        // 解析参数到 YamlBean
        YamlPrometheusRulesConfig yamlPrometheusRulesConfig = this.iAlertRuleConverter.convert2YamlPrometheusRulesConfig(
                request.getAlertRuleContent()
        );
        // 解码 Expr Base64 格式的表达式的值
        yamlPrometheusRulesConfig.getGroups()
                .forEach(ruleGroup ->
                        ruleGroup.getRules().forEach(rule -> {
                                    String exprDecodeBase64 = Base64.decodeStr(
                                            rule.getExpr(),
                                            CharsetUtil.UTF_8
                                    );
                                    rule.setExpr(exprDecodeBase64);
                                }
                        )
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


        // 更新数据库
        TDlAlert tDlAlert = this.tDlAlertService.getById(request.getAlertRuleId());
        tDlAlert.setClusterId(request.getClusterId());
        tDlAlert.setAlertName(request.getAlertRuleName());
        tDlAlert.setAlertFileName(ruleFileName);
        tDlAlert.setAlertFilePath(ruleFilePath);
        tDlAlert.setAlertRuleContent(
                Base64.encode(
                        YamlDeserializer.toString(yamlPrometheusRulesConfig),
                        CharsetUtil.UTF_8
                )
        );
        tDlAlert.setEnabled(request.getEnabled());
        tDlAlert.setAlertVersion(tDlAlert.getAlertVersion() + 1L);
        tDlAlert.setHandlerType(request.getAlertHandlerTypeEnum());

        Assert.isTrue(
                tDlAlertService.updateById(tDlAlert),
                () -> new DatabaseException("更新告警规则到数据库失败")
        );

        // 获取 Prometheus 所在节点的详细信息
        TDlComponent tDlComponent = this.findPrometheusComponent(request.getClusterId());
        AbstractNodeVo.NodeDetailVo nodeDetailVo = this.masterNodeService.getNodeDetailById(tDlComponent.getNodeId())
                .getData();

        // 将文件写入到指定节点的指定目录, 如果希望这部分告警规则的配置文件，被列入到配置文件管理中，则可以通过调用共用配置文件服务的函数来实现
        this.writeAlertRuleFile2Target(
                nodeDetailVo,
                tDlAlert.getAlertFileName(),
                tDlAlert.getAlertFilePath(),
                tDlAlert.getAlertVersion(),
                tDlAlert.getAlertRuleContent()
        );

        // 重载 Prometheus 配置，更新告警规则
        this.remoteInvokePrometheusHandler.invokePrometheusReload(nodeDetailVo.getHostname());

        // 组装返回报文
        AbstractAlertVo.AlertRuleVo alertRuleVo = this.createAlertRuleVo(yamlPrometheusRulesConfig, tDlAlert);

        return Result.success(alertRuleVo);
    }


    /**
     * Description: 检查新增的告警规则请求是否合理
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
    private void checkUpdateAlertRuleRequest(AbstractAlertRequest.UpdateAlertRuleRequest request) {
        // 检查是否变更了名称（不允许变更告警名称)
        TDlAlert tDlAlert = this.tDlAlertService.lambdaQuery()
                .select()
                .eq(TDlAlert::getClusterId, request.getClusterId())
                .eq(TBasePo::getId, request.getAlertRuleId())
                .one();

        Assert.notNull(
                tDlAlert,
                () -> new BException("目标集群中未找到指定告警信息配置")
        );

        Assert.isTrue(
                tDlAlert.getAlertName().equals(request.getAlertRuleName()),
                () -> new BException("不允许修改告警配置名称")
        );
    }


    // 设定告警与处理手段的绑定关系

}


