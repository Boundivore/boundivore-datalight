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

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.orm.po.single.TDlComponent;
import cn.boundivore.dl.service.master.converter.IStepConverter;
import cn.boundivore.dl.service.master.handler.RemoteInvokeGrafanaHandler;
import cn.boundivore.dl.service.master.handler.RemoteInvokePrometheusHandler;
import cn.boundivore.dl.service.master.manage.service.bean.*;
import cn.boundivore.dl.service.master.manage.service.stage.IStage;
import cn.boundivore.dl.service.master.manage.service.stage.impl.Stage;
import cn.boundivore.dl.service.master.manage.service.task.ITask;
import cn.boundivore.dl.service.master.manage.service.task.impl.Task;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.resolver.yaml.YamlServiceDetail;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Description: 为当前服务组装一个 Job
 * Stage 包含了该服务下的所有组件，Task 包含 "某一个组件在某一个节点" 上的一系列执行(一系列 Step)
 * EASY-FIX: 如果需要进一步提升 Meta 和 Plan 的规划生成效率，可以采用并行流进行改进，但需要确保 Stage、Task、Step 的执行顺序。
 * 可考虑先使用只包含 Id 的 StageMeta TaskMeta StepMeta 实例占位，占位后再并发处理实际逻辑，从而保证顺序
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/30
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
public class Job extends Thread {

    @Getter
    private JobMeta jobMeta;

    @Getter
    private final Intention intention;

    @Getter
    private final Plan plan;

    private final JobService jobService = SpringContextUtil.getBean(JobService.class);

    private final IStepConverter iStepConverter = SpringContextUtil.getBean(IStepConverter.class);

    private boolean isInit;

    protected final RemoteInvokePrometheusHandler remoteInvokePrometheusHandler = SpringContextUtil.getBean(RemoteInvokePrometheusHandler.class);

    protected final RemoteInvokeGrafanaHandler remoteInvokeGrafanaHandler = SpringContextUtil.getBean(RemoteInvokeGrafanaHandler.class);

    public Job(Intention intention) {
        this.intention = intention;
        this.plan = new Plan(this.intention);
    }


    /**
     * Description: 1、初始化各类异步任务 Meta 信息；2、根据初始化好的 Meta 信息，初始化执行计划
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return Job
     */
    public Job init() throws InterruptedException {
        // 初始化 JobMeta，并组装 Stage、Task
        try {
            this.initJobMeta();

            // 根据初始化的元数据信息，初始化计划信息，并组装、填充待执行异步线程
            this.plan(this.jobMeta);

            this.jobService.updateJobDatabase(this.jobMeta);

            this.plan.initExecTotal(this.jobMeta);
            this.isInit = true;
        } catch (Exception e) {
            JobCacheUtil.getInstance().releaseActiveJobId();
            log.error(ExceptionUtil.stacktraceToString(e));
            throw new BException(
                    String.format(
                            "初始化 Job 异常: %s",
                            ExceptionUtil.stacktraceToString(e)
                    )
            );
        }
        return this;
    }

    /**
     * Description: 初始化 JobMeta，并组装 Stage、Task
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    private void initJobMeta() throws InterruptedException {
        long jobMetaId = IdWorker.getId();

        Assert.isTrue(
                JobCacheUtil.getInstance().setActiveJobId(jobMetaId),
                () -> new BException(
                        String.format(
                                "安全起见，不允许同时启动多个作业对集群服务组件进行变更，已有其他活跃的任务正在运行: %s",
                                JobCacheUtil.getInstance().getActiveJobId()
                        )
                )
        );

        this.jobMeta = new JobMeta()
                .setTag(IdUtil.fastSimpleUUID())
                .setId(jobMetaId)
                .setName(intention.getActionTypeEnum().name())
                .setExecStateEnum(ExecStateEnum.SUSPEND)
                .setJobResult(new JobMeta.JobResult(false))
                // 当前集群以及所依赖的集群信息（如果为 MIXED 集群，则所依赖集群信息为 null）
                .setClusterMeta(intention.getClusterMeta())
                .setActionTypeEnum(intention.getActionTypeEnum())
                .setStageMetaMap(new LinkedHashMap<>());

        // 缓存 Job 相关信息
        JobCacheUtil.getInstance().cache(
                new JobCacheBean(
                        this.jobMeta,
                        this.plan
                )
        );

        // 生成序号，用于表明当前实例的生成顺序
        AtomicLong stageNum = new AtomicLong(0L);
        intention.getServiceList()
                .forEach(service -> {
                            final StageMeta stageMeta = this.initStageMeta(
                                    this.jobMeta,
                                    service,
                                    stageNum.incrementAndGet()
                            );
                            this.jobMeta.getStageMetaMap().put(stageMeta.getId(), stageMeta);
                        }
                );
    }


    /**
     * Description: 初始化当前 StageMeta，StageMeta 会切分不同的 Service
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param jobMeta          Job 元数据
     * @param intentionService 操作意图封装的 Service
     * @param stageNum         Stage 生成序号
     * @return StageMeta
     */
    private StageMeta initStageMeta(final JobMeta jobMeta,
                                    final Intention.Service intentionService,
                                    final long stageNum) {
        String serviceName = intentionService.getServiceName();
        Long priority = intentionService.getPriority();
        List<Intention.Component> componentList = intentionService.getComponentList();

        //初始化 StageMeta
        final StageMeta stageMeta = new StageMeta()
                .setJobMeta(jobMeta)
                .setId(IdWorker.getId())
                .setName(
                        String.format(
                                "%s:%s",
                                jobMeta.getName(),
                                intentionService.getServiceName()
                        )
                )
                .setServiceName(serviceName)
                .setCurrentServiceState(SCStateEnum.CHANGING)
                .setPriority(priority)
                .setStageStateEnum(ExecStateEnum.SUSPEND)
                .setStageResult(new StageMeta.StageResult(false))
                .setTaskMetaMap(new LinkedHashMap<>());

        stageMeta.setNum(stageNum);

        /* 如果满足以下条件，则需要为当前异步 Job 中的每一个节点的、每一个服务的 第一个 Task 添加一个 通用的 Step 操作：
            1、当前为部署操作；
            2、当前所在节点在此次操作之前，该不存在任何已存在的组件；
            3、当前为该节点、该服务的第一个 Task；
        */

        // 用于记录该服务目前在哪些节点执行过初始化步骤
        final Set<Long> haveInitServiceSet = new HashSet<>();
        ActionTypeEnum actionTypeEnum = this.jobMeta.getActionTypeEnum();
        if ((actionTypeEnum == ActionTypeEnum.DEPLOY || actionTypeEnum == ActionTypeEnum.MIGRATE)) { // 判断是否为部署操作
            // 判断当前服务在当前节点是否为第一次部署
            List<SCStateEnum> unDeployedComponentStateEnumList = CollUtil.newArrayList(
                    SCStateEnum.SELECTED,
                    SCStateEnum.UNSELECTED,
                    SCStateEnum.REMOVED
            );

            haveInitServiceSet.addAll(
                    this.jobService.getTDlComponentListByServiceName(
                                    this.jobMeta.getClusterMeta().getCurrentClusterId(),
                                    stageMeta.getServiceName()
                            )
                            .stream()
                            .filter(i -> !unDeployedComponentStateEnumList.contains(i.getComponentState()))
                            .map(TDlComponent::getNodeId)
                            .collect(Collectors.toSet())
            );
        }


        componentList.forEach(i -> {
            //根据意图中待部署的组件列表，初始化若干 TaskMeta（同一个组件，在不同节点上的部署，属于不同 TaskMeta 实例）
            List<TaskMeta> taskMetaList = this.initTaskMeta(
                    stageMeta,
                    i,
                    // 用于记录该服务目前在哪些节点执行过初始化步骤
                    haveInitServiceSet
            );

            taskMetaList.forEach(
                    taskMeta -> stageMeta.getTaskMetaMap().put(taskMeta.getId(), taskMeta)
            );
        });

        return stageMeta;
    }


    /**
     * Description: 初始化当前 TaskMeta，TaskMeta 会切分不同的 Component 或 Node
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param stageMeta          Stage 元数据信息
     * @param intentionComponent 意图中的组件信息
     * @param haveInitServiceSet 用于记录该服务目前在哪些节点执行过初始化步骤
     * @return List<TaskMeta>
     */
    private List<TaskMeta> initTaskMeta(final StageMeta stageMeta,
                                        final Intention.Component intentionComponent,
                                        final Set<Long> haveInitServiceSet) {
        String serviceName = stageMeta.getServiceName();
        String componentName = intentionComponent.getComponentName();
        Long priority = intentionComponent.getPriority();
        List<Intention.Node> nodeList = intentionComponent.getNodeList();

        YamlServiceDetail.Component component = ResolverYamlServiceDetail.COMPONENT_MAP.get(
                intentionComponent.getComponentName()
        );

        //获取异步任务中，配置文件中设定的组件的状态变化值
        SCStateEnum startState = null;
        SCStateEnum failState = null;
        SCStateEnum successState = null;

        for (YamlServiceDetail.Action action : component.getActions()) {
            if (action.getType() != this.intention.getActionTypeEnum()) continue;

            startState = action.getStartState();
            failState = action.getFailState();
            successState = action.getSuccessState();

        }

        SCStateEnum finalStartState = startState;
        SCStateEnum finalFailState = failState;
        SCStateEnum finalSuccessState = successState;

        /* 使用一个原子标记来标识是否是第一个TaskMeta
         * 用于判断当前 Task 是否需要阻塞自身执行
         * （即阻塞 "保存通用配置"，等待配置就绪后，后续任务即可直接引用已保存的配置内容）
         * 用于 MasterConfigSyncService.saveConfigOrUpdateBatch 过程进入无锁保存配置逻辑，提升部署速度
         */
        AtomicBoolean isFirstTaskMeta = new AtomicBoolean(true);

        // 生成序号，用于表明当前实例的生成顺序
        AtomicLong taskNum = new AtomicLong(0L);
        //实例化组装每一个 TaskMeta
        return nodeList.stream()
                .map(i -> {
                            TaskMeta taskMeta = new TaskMeta()
                                    .setStageMeta(stageMeta)
                                    // 配置当前 Task 是否需要等待前面的任务完成，再执行自身
                                    // 通过控制传递该参数的 true false 值可用于滚动重启等功能
                                    .setWait(this.intention.isOneByOne())
                                    // 配置当前 Task 是否需要阻塞自身执行，直到自身执行完成
                                    // 同时通过控制传递该参数的 true false 值也可用于滚动重启等功能
                                    .setBlock(isFirstTaskMeta.getAndSet(false))
                                    .setId(IdWorker.getId())
                                    .setName(String.format(
                                                    "%s:%s[%s]",
                                                    stageMeta.getName(),
                                                    componentName,
                                                    i.getHostname()
                                            )
                                    )
                                    .setHostname(i.getHostname())
                                    .setNodeIp(i.getNodeIp())
                                    .setNodeId(i.getNodeId())
                                    .setSerialNum(i.getSerialNum())

                                    .setActionTypeEnum(jobMeta.getActionTypeEnum())
                                    .setPriority(priority)

                                    .setServiceName(serviceName)
                                    .setComponentName(componentName)
                                    .setFirstDeployInNode(!haveInitServiceSet.contains(i.getNodeId()))
                                    .setStartState(finalStartState)
                                    .setFailState(finalFailState)
                                    .setSuccessState(finalSuccessState)
                                    .setCurrentState(finalStartState)

                                    .setTaskStateEnum(ExecStateEnum.SUSPEND)
                                    .setTaskResult(new TaskMeta.TaskResult(false))
                                    .setStepMetaMap(new LinkedHashMap<>());

                            taskMeta.setNum(taskNum.incrementAndGet());

                            // 当前服务、当前组件、当前节点，判断服务是否执行过初始化，如果没有，则必将执行初始化，且将其添加到已初始化记录中
                            haveInitServiceSet.add(i.getNodeId());

                            // 同一个 TaskMeta 中，会根据 ActionTypeEnum，封装一组 StepMeta
                            List<StepMeta> stepMetaList = this.initStepMeta(taskMeta);
                            stepMetaList.forEach(s -> taskMeta.getStepMetaMap().put(s.getId(), s));

                            // 更新计划执行进度
                            this.plan.planProgress();

                            return taskMeta;
                        }
                )
                .collect(Collectors.toList());
    }

    /**
     * Description: 初始化当前 StepMeta，StepMeta 为具体某个Service，某个 Component，某个 Node，某个 Action 的一个操作；
     * 因为 TaskMeta 会包含一组 StepMeta，因此返回 List<StepMeta>
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param taskMeta Task 中的元数据信息
     * @return List<StepMeta>
     */
    private List<StepMeta> initStepMeta(TaskMeta taskMeta) {

        YamlServiceDetail.Component component = ResolverYamlServiceDetail.COMPONENT_MAP.get(
                taskMeta.getComponentName()
        );

        YamlServiceDetail.Action action = CollUtil.findOne(
                component.getActions(),
                i -> i.getType() == taskMeta.getActionTypeEnum()
        );

        List<YamlServiceDetail.Step> finalSteps = new LinkedList<>(action.getSteps());
        // 针对所有节点，针对同一个服务，如果是第一个执行配置文件初始化的任务，则阻塞，其他任务则不阻塞，可提升并发速度
        // 注意：该操作仅仅是为了在这里备注，对应功能的逻辑代码的位置位于：添加 TaskMeta 的位置，通过 AtomicBoolean 控制
        ActionTypeEnum actionTypeEnum = taskMeta.getActionTypeEnum();
        if ((actionTypeEnum == ActionTypeEnum.DEPLOY || actionTypeEnum == ActionTypeEnum.MIGRATE)
                && taskMeta.isFirstDeployInNode()) {
            YamlServiceDetail.Initialize initialize = ResolverYamlServiceDetail.SERVICE_MAP
                    .get(taskMeta.getServiceName()).getInitialize();

            finalSteps.addAll(0, initialize.getSteps());
        }

        // 生成序号，用于表明当前实例的生成顺序
        AtomicLong stepNum = new AtomicLong(0L);

        return finalSteps
                .stream()
                //转换器转换部分属性值，其余属性值通过 set 方法设定
                .map(i -> {
                            StepMeta stepMeta = iStepConverter.convert2StepMeta(i)
                                    .setTaskMeta(taskMeta)
                                    .setId(IdWorker.getId())
                                    .setName(String.format(
                                                    "%s:%s",
                                                    taskMeta.getName(),
                                                    i.getName()
                                            )
                                    )
                                    .setExecStateEnum(ExecStateEnum.SUSPEND)
                                    .setStepResult(new StepMeta.StepResult(false));

                            stepMeta.setNum(stepNum.incrementAndGet());

                            return stepMeta;
                        }
                )
                .collect(Collectors.toList());
    }


    /**
     * Description: 根据元数据信息，为当前 Job 生成任务执行计划
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param jobMeta 工作元数据信息
     */
    public void plan(JobMeta jobMeta) {

        jobMeta.getStageMetaMap()
                .forEach((kStage, vStage) -> {
                            log.info(
                                    "Stage: {}, Id: {}, Tag: {}",
                                    vStage.getName(),
                                    vStage.getId(),
                                    vStage.getJobMeta().getTag()
                            );

                            IStage iStage = new Stage(vStage);

                            vStage.getTaskMetaMap()
                                    .forEach((kTask, vTask) -> {
                                                log.info(
                                                        "Task: {}, Id: {}, Tag: {}",
                                                        vTask.getName(),
                                                        vTask.getId(),
                                                        vTask.getStageMeta().getJobMeta().getTag()
                                                );

                                                ITask iTask = new Task(vTask);
                                                iStage.offerTask(iTask);

                                                //更新计划执行进度
                                                this.plan.planProgress();
                                            }
                                    );
                            this.plan.offerStage(iStage);
                        }
                );
    }

    /**
     * Description: 执行当前 Job
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/30
     * Modification description:
     * Modified by:
     * Modification time:
     */
    public void execute() {
        //记录 Job 起始时间
        this.jobMeta.setStartTime(System.currentTimeMillis());

        //更新当前 Job 执行状态到内存缓存和数据库
        this.updateJobExecutionStatus(ExecStateEnum.RUNNING);

        // 依次执行每一个 Stage 并得到 StageResult 多个 Stage 为串行
        // Stage 中的多个 Task 为并行或串行（取决于组件之间的依赖关系，同一 priority 为并发，不同 priority 为串行）
        // Task 中的多个 Step 为串行
        for (IStage stage : this.plan.getStages()) {
            try {
                StageMeta stageMeta = this.jobService.submit(stage).get();
                this.jobMeta.getJobResult().setSuccess(
                        stageMeta.getStageResult().isSuccess()
                );

                //如果中途出现任何的失败，则终止整个 Job，防止强制执行后整个集群出现过多异常，增加回滚成本
                if (!this.jobMeta.getJobResult().isSuccess()) break;

            } catch (Exception e) {
                log.error(ExceptionUtil.stacktraceToString(e));
            }
        }

        ExecStateEnum execStateEnum;
        try {
            // 如果之前任务全部成功，则重载 Prometheus 并初始化 Grafana
            if (this.jobMeta.getJobResult().isSuccess()) {
                // 重载监控初始化
                this.reloadAndInitMonitor();

                // 如果是部署服务或组件，则完成后，清除 Procedure 信息
                ActionTypeEnum actionTypeEnum = this.jobMeta.getActionTypeEnum();
                if ((actionTypeEnum == ActionTypeEnum.DEPLOY || actionTypeEnum == ActionTypeEnum.MIGRATE)) {
                    this.jobService.clearProcedure(this.jobMeta.getClusterMeta().getCurrentClusterId());
                }
            }

            // 记录 Job 结束时间(自动计算耗时)
            this.jobMeta.setEndTime(System.currentTimeMillis());

            log.info(
                    "结束 Job: {}, 耗时: {} ms",
                    jobMeta.getName(),
                    jobMeta.getDuration()
            );

            execStateEnum = this.jobMeta.getJobResult().isSuccess() ?
                    ExecStateEnum.OK :
                    ExecStateEnum.ERROR;

            this.jobService.saveLog(jobMeta, "MONITOR settings ready", null);

        } catch (Exception e) {
            String errorMsg = ExceptionUtil.stacktraceToString(e);
            log.error(errorMsg);
            this.jobService.saveLog(jobMeta, null, errorMsg);
            execStateEnum = ExecStateEnum.ERROR;
        }

        //更新当前 Job 执行状态到内存缓存和数据库
        this.updateJobExecutionStatus(execStateEnum);

        //清除所有可能残留的异步任务
        this.plan.clear();

        // 如果是重启组件，则更新组件 "重启标记 needRestart" 为 false
        if (this.jobMeta.getActionTypeEnum() == ActionTypeEnum.RESTART
                || this.jobMeta.getActionTypeEnum() == ActionTypeEnum.START
                || this.jobMeta.getActionTypeEnum() == ActionTypeEnum.STOP) {
            this.jobService.updateComponentRestartMark(this.intention);
        }
    }

    /**
     * Description: 调用 job.start() 后，将按照执行计划开始执行当前 Job
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/12 15:46
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    @Override
    public void run() {
        Assert.isTrue(
                this.isInit,
                () -> new BException("执行 Job 前需要先调用 init() 初始化任务计划")
        );

        try {
            this.execute();
        } finally {
            JobCacheUtil.getInstance().releaseActiveJobId();
        }
    }

    /**
     * Description: 更新当前 Job 执行状态到内存缓存和数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param execStateEnum 当前状态
     */
    public void updateJobExecutionStatus(ExecStateEnum execStateEnum) {
        // 更新当前作业的执行状态到内存缓存
        this.jobService.updateJobMemory(this.jobMeta, execStateEnum);
        // 更新当前作业的执行状态到数据库
        this.jobService.updateJobDatabase(this.jobMeta);
    }

    /**
     * Description: 重载 Prometheus 并初始化 Grafana
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/8/25
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    private void reloadAndInitMonitor() {
        ActionTypeEnum actionTypeEnum = this.jobMeta.getActionTypeEnum();
        if ((actionTypeEnum == ActionTypeEnum.DEPLOY || actionTypeEnum == ActionTypeEnum.MIGRATE)) {
            Long currentClusterId = this.jobMeta.getClusterMeta().getCurrentClusterId();

            // 重配置 Prometheus: 异步任务最后，重新加载 Prometheus 配置
            Optional<StageMeta> monitorStageMetaOptional = this.jobMeta.getStageMetaMap()
                    .values()
                    .stream()
                    .filter(i -> i.getServiceName().equals("MONITOR"))
                    .findFirst();

            if (monitorStageMetaOptional.isPresent()) {
                StageMeta monitorStageMeta = monitorStageMetaOptional.get();

                Optional<TaskMeta> prometheusTaskMetaOptional = monitorStageMeta.getTaskMetaMap()
                        .values()
                        .stream()
                        .filter(i -> i.getComponentName().equals("Prometheus"))
                        .findFirst();

                if (prometheusTaskMetaOptional.isPresent()) {
                    // 重新加载 Prometheus 配置
                    this.remoteInvokePrometheusHandler.invokePrometheusReload(currentClusterId);
                }

                Optional<TaskMeta> grafanaTaskMetaOptional = monitorStageMeta.getTaskMetaMap()
                        .values()
                        .stream()
                        .filter(i -> i.getComponentName().equals("Grafana"))
                        .findFirst();

                if (grafanaTaskMetaOptional.isPresent()) {
                    // 重新配置 Grafana: 异步任务最后，以幂等方式重新配置 Grafana
                    this.remoteInvokeGrafanaHandler.initGrafanaSettings(currentClusterId);
                }
            }
        }
    }

}
