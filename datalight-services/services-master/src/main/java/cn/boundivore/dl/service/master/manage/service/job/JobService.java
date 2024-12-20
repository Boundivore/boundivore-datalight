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
package cn.boundivore.dl.service.master.manage.service.job;

import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.MasterWorkerEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.base.request.impl.master.AbstractServiceComponentRequest;
import cn.boundivore.dl.base.request.impl.master.RemoveProcedureRequest;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeVo;
import cn.boundivore.dl.base.response.impl.master.ServiceDependenciesVo;
import cn.boundivore.dl.cloud.config.async.executors.CustomThreadPoolTaskExecutor;
import cn.boundivore.dl.cloud.utils.SpringContextUtilTest;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.*;
import cn.boundivore.dl.orm.service.single.impl.*;
import cn.boundivore.dl.plugin.base.bean.MasterWorkerMeta;
import cn.boundivore.dl.plugin.base.bean.PluginClusterMeta;
import cn.boundivore.dl.plugin.base.bean.PluginConfig;
import cn.boundivore.dl.plugin.base.bean.PluginConfigResult;
import cn.boundivore.dl.service.master.cache.MySQLCache;
import cn.boundivore.dl.service.master.env.DataLightEnv;
import cn.boundivore.dl.service.master.manage.service.bean.JobMeta;
import cn.boundivore.dl.service.master.manage.service.bean.StageMeta;
import cn.boundivore.dl.service.master.manage.service.bean.StepMeta;
import cn.boundivore.dl.service.master.manage.service.bean.TaskMeta;
import cn.boundivore.dl.service.master.resolver.ResolverYamlDirectory;
import cn.boundivore.dl.service.master.resolver.yaml.YamlDirectory;
import cn.boundivore.dl.service.master.service.*;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Description: 异步任务工作时的相关操作
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/6/15
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Service
@RequiredArgsConstructor
public class JobService {

    @Value("${server.datalight.url.master-port}")
    private String masterPort;
    @Value("${server.datalight.url.worker-port}")
    private String workerPort;

    public final MasterNodeService masterNodeService;

    public final MasterComponentService masterComponentService;

    public final MasterServiceService masterServiceService;

    public final MasterConfigService masterConfigService;

    public final MasterConfigSyncService masterConfigSyncService;

    //获取自定义耗时异步任务线程池
    private final CustomThreadPoolTaskExecutor customExecutor;

    private final TDlJobServiceImpl tDlJobService;

    private final TDlStageServiceImpl tDlStageService;

    private final TDlTaskServiceImpl tDlTaskService;

    private final TDlStepServiceImpl tDlStepService;

    private final TDlJobLogServiceImpl tDlJobLogService;

    private final MasterInitProcedureService masterInitProcedureService;

    /**
     * Description: 提交异步任务到线程池
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param callable 某个（Stage 或 Task）异步任务
     */
    public <T> Future<T> submit(@NotNull Callable<T> callable) {
        return this.customExecutor.submit(callable);
    }

    /**
     * Description: 代理调用
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param taskMeta Task 元数据信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void switchComponentState(TaskMeta taskMeta) {
        JobMeta jobMeta = taskMeta.getStageMeta().getJobMeta();

        this.masterComponentService.switchComponentState(
                jobMeta.getClusterMeta().getCurrentClusterId(),
                taskMeta.getNodeId(),
                taskMeta.getServiceName(),
                taskMeta.getComponentName(),
                taskMeta.getCurrentState()
        );
    }

    /**
     * Description: 代理调用
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param stageMeta Stage 元数据信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void switchServiceState(StageMeta stageMeta) {
        JobMeta jobMeta = stageMeta.getJobMeta();

        //变更服务状态
        this.masterServiceService.switchServiceState(
                jobMeta.getClusterMeta().getCurrentClusterId(),
                stageMeta.getServiceName(),
                stageMeta.getCurrentServiceState()
        );
    }

    /**
     * Description: 代理调用
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     * @return SCStateEnum 返回服务应该处于的状态
     */
    public SCStateEnum determineServiceStateViaComponent(Long clusterId,
                                                         String serviceName) {
        return this.masterComponentService.determineServiceStateViaComponent(
                clusterId,
                serviceName
        );
    }

    /**
     * Description: 根据 Job 执行的结果，更新当前 Job 的状态到内存缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param jobMeta       Job 元数据信息
     * @param execStateEnum 执行状态
     */
    public void updateJobMemory(JobMeta jobMeta, ExecStateEnum execStateEnum) {
        jobMeta.setExecStateEnum(execStateEnum);
    }

    /**
     * Description: 根据 Job 执行的结果，更新当前 Job 的状态到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param jobMeta Job 元数据信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void updateJobDatabase(JobMeta jobMeta) {
        // 注意：执行更新数据库前，务必先更新内存，例如： this.updateJobMemory()
        // 此时会从内存中最新的元数据状态更新到数据库
        TDlJob tDlJob = this.tDlJobService.getById(jobMeta.getId());

        if (tDlJob == null) {
            tDlJob = new TDlJob();
            tDlJob.setVersion(0L).setId(jobMeta.getId());
        }

        tDlJob.setJobActionType(jobMeta.getActionTypeEnum())
                .setTag(jobMeta.getTag())
                .setClusterId(jobMeta.getClusterMeta().getCurrentClusterId())
                .setJobName(jobMeta.getName())
                .setJobState(jobMeta.getExecStateEnum())
                .setStartTime(jobMeta.getStartTime())
                .setEndTime(jobMeta.getEndTime())
                .setDuration(jobMeta.getDuration());

        Assert.isTrue(
                this.tDlJobService.saveOrUpdate(tDlJob),
                () -> new DatabaseException("保存或更新 Job 到数据库失败")
        );
    }


    /**
     * Description: 根据 Stage 执行的结果，更新当前 Stage 的状态到内存缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/7 10:48
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param stageMeta     Stage 元数据信息
     * @param execStateEnum 执行状态
     */
    public void updateStageMemory(StageMeta stageMeta, ExecStateEnum execStateEnum) {
        stageMeta.setStageStateEnum(execStateEnum);
    }


    /**
     * Description: 根据 Stage 执行的结果，更新当前 Stage 的状态到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31 10:41
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param stageMeta Stage 元数据信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void updateStageDatabase(StageMeta stageMeta) {
        // 注意：执行更新数据库前，务必先更新内存，例如： this.updateStageMemory()
        // 此时会从内存中最新的元数据状态更新到数据库
        JobMeta jobMeta = stageMeta.getJobMeta();

        TDlStage tDlStage = this.tDlStageService.getById(stageMeta.getId());

        if (tDlStage == null) {
            tDlStage = new TDlStage();
            tDlStage.setVersion(0L).setId(stageMeta.getId());
        }


        tDlStage.setNum(stageMeta.getNum())
                .setClusterId(jobMeta.getClusterMeta().getCurrentClusterId())
                .setTag(jobMeta.getTag())
                .setJobId(jobMeta.getId())
                .setStageName(stageMeta.getName())
                .setStageState(stageMeta.getStageStateEnum())
                .setServiceName(stageMeta.getServiceName())
                .setServiceState(stageMeta.getCurrentServiceState())
                .setPriority(stageMeta.getPriority())
                .setStartTime(stageMeta.getStartTime())
                .setEndTime(stageMeta.getEndTime())
                .setDuration(stageMeta.getDuration());

        Assert.isTrue(
                this.tDlStageService.saveOrUpdate(tDlStage),
                () -> new DatabaseException("保存或更新 Stage 到数据库失败")
        );
    }


    /**
     * Description: 根据 Task 执行的结果，更新当前 Task 的状态到内存缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/7 10:48
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param taskMeta      Task 元数据信息
     * @param execStateEnum 执行状态
     */
    public void updateTaskMemory(TaskMeta taskMeta, ExecStateEnum execStateEnum) {
        taskMeta.setTaskStateEnum(execStateEnum);
    }

    /**
     * Description: 根据 Task 执行的结果，更新当前 Task 的状态到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31 10:41
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param taskMeta Task 元数据信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void updateTaskDatabase(TaskMeta taskMeta) {

        //注意：执行更新数据库前，务必先更新内存，例如： this.updateTaskMemory()
        //此时会从内从中最新的元数据状态更新到数据库
        JobMeta jobMeta = taskMeta.getStageMeta().getJobMeta();
        StageMeta stageMeta = taskMeta.getStageMeta();

        TDlTask tDlTask = this.tDlTaskService.getById(taskMeta.getId());

        if (tDlTask == null) {
            tDlTask = new TDlTask();
            tDlTask.setVersion(0L).setId(taskMeta.getId());
        }

        tDlTask.setNum(taskMeta.getNum())
                .setClusterId(jobMeta.getClusterMeta().getCurrentClusterId())
                .setTag(jobMeta.getTag())
                .setJobId(jobMeta.getId())
                .setStageId(stageMeta.getId())
                .setNodeId(taskMeta.getNodeId())
                .setHostname(taskMeta.getHostname())
                .setNodeIp(taskMeta.getNodeIp())
                .setTaskName(taskMeta.getName())
                .setTaskState(taskMeta.getTaskStateEnum())
                .setActionType(taskMeta.getActionTypeEnum())
                .setServiceName(taskMeta.getServiceName())
                .setComponentName(taskMeta.getComponentName())
                .setCurrentState(taskMeta.getCurrentState())
                .setStartState(taskMeta.getStartState())
                .setFailState(taskMeta.getFailState())
                .setSuccessState(taskMeta.getSuccessState())
                .setIsWait(taskMeta.isWait())
                .setIsBlock(taskMeta.isBlock())
                .setPriority(taskMeta.getPriority())
                .setRam(taskMeta.getRam())
                .setIsFirstDeploy(taskMeta.isFirstDeployInNode())
                .setStartTime(taskMeta.getStartTime())
                .setEndTime(taskMeta.getEndTime())
                .setDuration(taskMeta.getDuration());

        Assert.isTrue(
                this.tDlTaskService.saveOrUpdate(tDlTask),
                () -> new DatabaseException("保存或更新 Task 到数据库失败")
        );
    }

    /**
     * Description: 根据 Step 执行的结果，更新当前 Step 的状态到内存缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/7 10:48
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param stepMeta      Step 元数据信息
     * @param execStateEnum 执行状态
     */
    public void updateStepMemory(StepMeta stepMeta, ExecStateEnum execStateEnum) {
        stepMeta.setExecStateEnum(execStateEnum);
    }


    /**
     * Description: 根据 Step 执行的结果，更新当前 Step 的状态到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/7 10:48
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param stepMeta Step 元数据信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void updateStepDatabase(StepMeta stepMeta) {

        //注意：执行更新数据库前，务必先更新内存，例如： this.updateStepMemory()，
        //此时会从内从中最新的元数据状态更新到数据库
        TaskMeta taskMeta = stepMeta.getTaskMeta();
        StageMeta stageMeta = taskMeta.getStageMeta();
        JobMeta jobMeta = stageMeta.getJobMeta();

        TDlStep tDlStep = this.tDlStepService.getById(stepMeta.getId());

        if (tDlStep == null) {
            tDlStep = new TDlStep();
            tDlStep.setVersion(0L).setId(stepMeta.getId());
        }

        tDlStep.setNum(stepMeta.getNum())
                .setClusterId(jobMeta.getClusterMeta().getCurrentClusterId())
                .setTag(jobMeta.getTag())
                .setJobId(jobMeta.getId())
                .setStageId(stageMeta.getId())
                .setTaskId(taskMeta.getId())
                .setStepName(stepMeta.getName())
                .setStepState(stepMeta.getExecStateEnum())
                .setStepType(stepMeta.getType())
                .setJar(stepMeta.getJar())
                .setClazz(stepMeta.getClazz())
                .setMethod(stepMeta.getMethod())
                .setShell(stepMeta.getShell())
                .setArgs(StepMeta.list2Str(stepMeta.getArgs()))
                .setInteractions(StepMeta.list2Str(stepMeta.getInteractions()))
                .setExits(String.valueOf(stepMeta.getExits()))
                .setTimeout(stepMeta.getTimeout())
                .setSleep(stepMeta.getSleep())
                .setStartTime(stepMeta.getStartTime())
                .setEndTime(stepMeta.getEndTime())
                .setDuration(stepMeta.getDuration());

        Assert.isTrue(
                this.tDlStepService.saveOrUpdate(tDlStep),
                () -> new DatabaseException("保存或更新 Step 到数据库失败")
        );
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
     */
    public boolean configSaveOrUpdateBatch(PluginConfigResult pluginConfigResult) {
        // 判断如果为有效的配置修改，则发送修改配置请求
        return this.masterConfigSyncService.saveConfigOrUpdateBatch(pluginConfigResult);
    }

    /**
     * Description: 设置服务、组件部署时所需配置文件的内容
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15 11:28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param taskMeta 任务元信息
     * @return PluginConfig 用于插件处理服务组件配置的元数据封装
     */
    public PluginConfig pluginConfig(TaskMeta taskMeta) {

        final StageMeta stageMeta = taskMeta.getStageMeta();
        final JobMeta jobMeta = stageMeta.getJobMeta();

        // 获取任务元数据中的当前服务名称
        final String currentServiceName = taskMeta.getServiceName();
        // 获取任务元数据中的当前组件名称
        final String currentComponentName = taskMeta.getComponentName();
        // 获取任务元数据中的当前节点 ID
        final Long currentNodeId = taskMeta.getNodeId();
        // 获取任务元数据中的当前节点 IP
        final String currentNodeIp = taskMeta.getNodeIp();
        // 获取任务元数据中的当前节点主机名
        final String currentNodeHostname = taskMeta.getHostname();

        // 当前节点的计数序列
        final Integer serialNum = taskMeta.getSerialNum();

        // 获取当前集群下，当前服务所依赖服务和组件的分布情况，
        // 如果为 COMPUTE 集群，则 COMPUTE 集群中所依赖的 STORAGE 类型的服务将会被关联到 MIXED 集群中
        final ServiceDependenciesVo serviceDependenciesVo = this.masterComponentService.getServiceDependencies(
                jobMeta.getClusterMeta(),
                currentServiceName
        ).getData();


        // 创建 PluginConfig 对象
        PluginConfig pluginConfig = new PluginConfig();

        // 设置环境变量
        YamlDirectory.Directory directoryYaml = ResolverYamlDirectory.DIRECTORY_YAML.getDatalight();
        pluginConfig.setUnixEnv(
                new PluginConfig.UnixEnv(
                        directoryYaml.getJavaHome(),
                        directoryYaml.getDatalightDir(),
                        directoryYaml.getServiceDir(),
                        directoryYaml.getLogDir(),
                        directoryYaml.getPidDir(),
                        directoryYaml.getDataDir()
                )
        );

        // 设置数据库源
        pluginConfig.setMysqlEnv(
                new PluginConfig.MySQLEnv(
                        MySQLCache.DB_HOST,
                        MySQLCache.DB_PORT,
                        MySQLCache.DB_NAME,
                        MySQLCache.DB_USER,
                        MySQLCache.DB_PASSWORD
                )
        );

        // 获取服务详情列表
        final List<ServiceDependenciesVo.ServiceDetailVo> serviceDetailList = serviceDependenciesVo.getServiceDetailList();

        // <ServiceName, MetaService> 处理服务详情列表，逐个构建 MetaService
        Map<String, PluginConfig.MetaService> metaServiceMap = serviceDetailList.stream()
                .map(this::buildMetaService)
                .collect(Collectors.toMap(PluginConfig.MetaService::getServiceName, i -> i));

        // 设置服务列表
        pluginConfig.setMetaServiceMap(metaServiceMap);

        // 设置当前节点信息
        pluginConfig.setCurrentNodeId(currentNodeId);
        pluginConfig.setCurrentNodeIp(currentNodeIp);
        pluginConfig.setCurrentNodeHostname(currentNodeHostname);
        pluginConfig.setCurrentNodeSerialNum(serialNum);

        // 设置当前服务的元数据
        pluginConfig.setCurrentMetaService(
                this.findMetaService(
                        metaServiceMap,
                        currentServiceName
                )
        );
        // 设置当前组件的元数据
        pluginConfig.setCurrentMetaComponent(
                this.findMetaComponent(
                        pluginConfig.getCurrentMetaService(),
                        currentComponentName,
                        currentNodeId
                )
        );

        // 设置当前集群 Master 和 Worker 列表
        pluginConfig.setMasterWorkerMetaList(
                this.findMasterWorkerList(jobMeta.getClusterMeta().getCurrentClusterId())
        );

        // 返回 PluginConfig 对象
        return pluginConfig;
    }

    /**
     * Description: 构建 MetaService 对象
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15 11:28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param serviceDetailVo 服务下组件、配置等详情
     * @return PluginConfig 用于插件处理服务组件配置的元数据封装
     */
    private PluginConfig.MetaService buildMetaService(ServiceDependenciesVo.ServiceDetailVo serviceDetailVo) {
        // 组装当前服务下的预配置内容
        Map<String, Map<String, String>> configPreMap = serviceDetailVo.getPropertyList()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                ServiceDependenciesVo.PropertyVo::getTemplatedFilePath,
                                Collectors.toMap(
                                        ServiceDependenciesVo.PropertyVo::getPlaceholder,
                                        ServiceDependenciesVo.PropertyVo::getValue
                                )
                        )
                );

        // 组装 PluginConfig.ConfDir 列表
        List<PluginConfig.ConfDir> pluginConfigConfDirList = serviceDetailVo.getConfDirList()
                .stream()
                .map(confDir -> new PluginConfig.ConfDir(
                                confDir.getServiceConfDir(),
                                confDir.getTemplatedDir()
                        )
                )
                .collect(Collectors.toList());

        // 组装 MetaComponent 列表
        Map<String, PluginConfig.MetaComponent> metaComponentMap = serviceDetailVo.getComponentDetailList()
                .stream()
                .map(componentDetail ->
                        new PluginConfig.MetaComponent()
                                .setComponentName(componentDetail.getComponentName())
                                .setComponentState(componentDetail.getComponentState())
                                .setNodeId(componentDetail.getNodeId())
                                .setNodeIp(componentDetail.getNodeIp())
                                .setHostname(componentDetail.getHostname())
                                .setSerialNum(componentDetail.getSerialNum())
                                .setRam(componentDetail.getRam())
                                .setCpuCores(componentDetail.getCpuCores())
                )
                .collect(
                        Collectors.toMap(
                                i -> i.getComponentName() + i.getNodeId(),
                                i -> i
                        )
                );

        // 创建并返回 MetaService 对象
        return new PluginConfig.MetaService()
                .setPluginClusterMeta(
                        new PluginClusterMeta(
                                serviceDetailVo.getClusterId(),
                                serviceDetailVo.getClusterName(),
                                serviceDetailVo.getClusterType()
                        )
                )
                .setServiceName(serviceDetailVo.getServiceName())
                .setServiceState(serviceDetailVo.getServiceState())
                .setConfDirList(pluginConfigConfDirList)
                .setConfigPreMap(configPreMap)
                .setMetaComponentMap(metaComponentMap);
    }

    /**
     * Description: 查找指定名称的 MetaService（当前正在操作的 Service）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15 11:28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param metaServiceMap Plugin 中使用的服务元信息
     * @param serviceName    服务名称
     * @return PluginConfig 用于插件处理服务组件配置的元数据封装
     */
    private PluginConfig.MetaService findMetaService(Map<String, PluginConfig.MetaService> metaServiceMap,
                                                     String serviceName) {
        return metaServiceMap.values()
                .stream()
                .filter(metaService -> metaService.getServiceName().equals(serviceName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Description: 查找指定组件名称和节点 ID 的 MetaComponent（当前正在操作的 Component）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/15 11:28
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param metaService   Plugin 中使用的服务元信息
     * @param componentName 组件名称
     * @param nodeId        节点 ID
     * @return PluginConfig 用于插件处理服务组件配置的元数据封装
     */
    private PluginConfig.MetaComponent findMetaComponent(PluginConfig.MetaService metaService,
                                                         String componentName,
                                                         Long nodeId) {
        Assert.notNull(
                metaService,
                () -> new BException("当前 ServiceMeta 为空")
        );

        return metaService.getMetaComponentMap()
                .values()
                .stream()
                .filter(metaComponent ->
                        metaComponent.getComponentName().equals(componentName) &&
                                metaComponent.getNodeId().equals(nodeId)
                )
                .findFirst()
                .orElse(null);
    }

    /**
     * Description: 获取 Master Worker 信息列表
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/11
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @return Master Worker 信息列表
     */
    private List<MasterWorkerMeta> findMasterWorkerList(Long clusterId) {
        AbstractNodeVo.NodeVo nodeVo = this.masterNodeService.getNodeList(clusterId).getData();
        //Master
        MasterWorkerMeta masterMeta = new MasterWorkerMeta();

        List<MasterWorkerMeta> masterWorkerMetaList = nodeVo.getNodeDetailList()
                .stream()
                .map(i -> {
                            if (i.getNodeIp().equals(DataLightEnv.MASTER_IP)) {
                                masterMeta.setNodeId(i.getNodeId());
                                masterMeta.setHostname(i.getHostname());
                                masterMeta.setNodeIp(i.getNodeIp());
                                masterMeta.setPort(masterPort);
                                masterMeta.setMasterWorkerEnum(MasterWorkerEnum.MASTER);
                            }

                            return new MasterWorkerMeta(
                                    i.getNodeId(),
                                    i.getHostname(),
                                    i.getNodeIp(),
                                    workerPort,
                                    MasterWorkerEnum.WORKER
                            );
                        }
                )
                .collect(Collectors.toList());

        // DEBUG 本地调试环境配置
        if (DataLightEnv.IS_DEBUG) {
            String masterIpTest = SpringContextUtilTest.MASTER_IP_GATEWAY_TEST;
            masterMeta.setHostname(masterIpTest);
            masterMeta.setNodeIp(masterIpTest);
            masterMeta.setNodeId(-1L);
            masterMeta.setPort(masterPort);
            masterMeta.setMasterWorkerEnum(MasterWorkerEnum.MASTER);
        }

        masterWorkerMetaList.add(0, masterMeta);

        return masterWorkerMetaList;

    }

    /**
     * Description: 判断当前节点中是否已存在某服务下的任一组件处于非 UNSELECTED 和 REMOVED 状态，
     * 如果存在，则为 非初始化状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeId      节点 ID
     * @param serviceName 服务名称
     * @return true: 需要初始化移除目录，false: 不需要初始化移除目录
     */
    public boolean isInit(Long nodeId, String serviceName) {
        List<TDlComponent> tDlComponentListByServiceName = this.masterComponentService
                .getTDlComponentListByServiceNameInNode(
                        nodeId,
                        serviceName
                );

        return tDlComponentListByServiceName.stream()
                .noneMatch(
                        i -> i.getComponentState() != SCStateEnum.SELECTED &&
                                i.getComponentState() != SCStateEnum.UNSELECTED &&
                                i.getComponentState() != SCStateEnum.REMOVED
                );

    }

    /**
     * Description: 记录当前 NodeStep 执行日志到数据库。
     * 提示：所有 NodeStep 日志可以聚合为 NodeTask 日志，所有 NodeTask 日志，可以聚合为 NodeJob 日志
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: DatabaseException
     *
     * @param stepMeta  Step 元数据信息
     * @param logStdOut 标准输出
     * @param logErrOut 标准错误输出
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void saveLog(StepMeta stepMeta,
                        String logStdOut,
                        String logErrOut) {
        TaskMeta taskMeta = stepMeta.getTaskMeta();
        StageMeta stageMeta = taskMeta.getStageMeta();
        JobMeta jobMeta = stageMeta.getJobMeta();

        TDlJobLog tDlJobLog = new TDlJobLog();

        tDlJobLog.setTag(jobMeta.getTag());
        tDlJobLog.setClusterId(jobMeta.getClusterMeta().getCurrentClusterId());

        tDlJobLog.setJobId(jobMeta.getId());
        tDlJobLog.setNodeId(taskMeta.getNodeId());
        tDlJobLog.setStageId(stageMeta.getId());
        tDlJobLog.setTaskId(taskMeta.getId());
        tDlJobLog.setStepId(stepMeta.getId());

        tDlJobLog.setLogStdout(
                String.format(
                        "[\n节点: %s\n服务: %s\n 组件: %s\n]\n 日志: \n%s",
                        taskMeta.getHostname(),
                        stageMeta.getServiceName(),
                        taskMeta.getComponentName(),
                        logStdOut
                )
        );

        tDlJobLog.setLogErrout(
                String.format(
                        "[\n节点: %s\n服务: %s\n 组件: %s\n]\n 日志: \n%s",
                        taskMeta.getHostname(),
                        stageMeta.getServiceName(),
                        taskMeta.getComponentName(),
                        logErrOut
                )
        );

        Assert.isTrue(
                this.tDlJobLogService.save(tDlJobLog),
                () -> new DatabaseException("保存服务组件任务日志失败")
        );

    }

    /**
     * Description: 记录当前 NodeStep 执行日志到数据库。
     * 提示：所有 NodeStep 日志可以聚合为 NodeTask 日志，所有 NodeTask 日志，可以聚合为 NodeJob 日志
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: DatabaseException
     *
     * @param jobMeta   Job 元数据信息
     * @param logStdOut 标准输出
     * @param logErrOut 标准错误输出
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void saveLog(JobMeta jobMeta,
                        String logStdOut,
                        String logErrOut) {
        TDlJobLog tDlJobLog = new TDlJobLog();

        tDlJobLog.setTag(jobMeta.getTag());
        tDlJobLog.setClusterId(jobMeta.getClusterMeta().getCurrentClusterId());

        tDlJobLog.setJobId(jobMeta.getId());
        tDlJobLog.setNodeId(null);
        tDlJobLog.setStageId(null);
        tDlJobLog.setTaskId(null);
        tDlJobLog.setStepId(null);

        tDlJobLog.setLogStdout(logStdOut);
        tDlJobLog.setLogErrout(logErrOut);

        Assert.isTrue(
                this.tDlJobLogService.save(tDlJobLog),
                () -> new DatabaseException("保存服务组件任务日志失败")
        );

    }

    /**
     * Description: 获取某集群，某服务下所有组件的状态
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId   集群 ID
     * @param serviceName 服务名称
     * @return 返回组件列表
     */
    public List<TDlComponent> getTDlComponentListByServiceName(Long clusterId, String serviceName) {
        return this.masterComponentService.getTDlComponentListByServiceName(clusterId, serviceName);
    }

    /**
     * Description: 根据主机 ID 列表获取主机信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/11/13
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     * @param nodeIdList 节点 ID
     * @return List<TDlNode> 节点实体列表
     */
    public List<TDlNode> getTDlNodeListById(Long clusterId, List<Long> nodeIdList){
        return this.masterNodeService.getNodeListInNodeIds(clusterId, nodeIdList);
    }

    /**
     * Description: 整个步骤完成后，清除步骤记录信息
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 集群 ID
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void clearProcedure(Long clusterId) {
        this.masterInitProcedureService.removeInitProcedure(
                new RemoveProcedureRequest(
                        clusterId
                )
        );
    }

    /**
     * Description: 更新组件重启标记
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2024/1/16
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param intention 操作意图
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void updateComponentRestartMark(Intention intention) {

        Long clusterId = intention.getClusterMeta().getCurrentClusterId();

        intention.getServiceList().forEach(
                service -> service.getComponentList().forEach(
                        component -> this.masterComponentService.updateComponentRestartMark(
                                new AbstractServiceComponentRequest.UpdateNeedRestartRequest(
                                        clusterId,
                                        service.getServiceName(),
                                        component.getNodeList()
                                                .stream()
                                                .map(Intention.Node::getNodeId)
                                                .collect(Collectors.toList()),
                                        false
                                )
                        ))
        );
    }

}
