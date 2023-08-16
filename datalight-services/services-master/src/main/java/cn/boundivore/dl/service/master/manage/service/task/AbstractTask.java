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
package cn.boundivore.dl.service.master.manage.service.task;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.ExecTypeEnum;
import cn.boundivore.dl.base.request.impl.worker.ExecRequest;
import cn.boundivore.dl.base.result.Result;
import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.cloud.utils.SpringContextUtilTest;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.plugin.base.bean.PluginConfigResult;
import cn.boundivore.dl.plugin.base.config.IConfig;
import cn.boundivore.dl.service.master.manage.service.bean.*;
import cn.boundivore.dl.service.master.manage.service.job.JobService;
import cn.boundivore.dl.service.master.resolver.ResolverYamlServiceDetail;
import cn.boundivore.dl.service.master.service.RemoteInvokeWorkerService;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Description: 包装异步 Task 的执行逻辑，Task 线程运行性质：同服务、同组件、同节点
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/4/23
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@Getter
public abstract class AbstractTask implements ITask {
    //任务执行必要的元数据信息
    protected final TaskMeta taskMeta;

    protected final JobService jobService = SpringContextUtil.getBean(JobService.class);

    protected final RemoteInvokeWorkerService remoteInvokeWorkerService = SpringContextUtil.getBean(RemoteInvokeWorkerService.class);

    public AbstractTask(@NotNull TaskMeta taskMeta) {
        this.taskMeta = taskMeta;
        this.jobService.updateTaskDatabase(this.taskMeta);
    }

    @Override
    public TaskMeta getTaskMeta() {
        return this.taskMeta;
    }

    /**
     * Description: 执行任务逻辑
     * Created by: Boundivore
     * Creation time: 2023/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return TaskMeta 任务元数据信息
     */
    @Override
    public TaskMeta call() throws Exception {
        try {
            //记录 Task 起始时间
            this.taskMeta.setStartTime(System.currentTimeMillis());

            //更新当前 Task 执行状态到内存缓存和数据库
            this.updateTaskExecutionStatus(ExecStateEnum.RUNNING);

            // 判断并移除历史遗留目录
            this.initServiceEnv();

            //设置 Task 开始执行时，当前组件状态到 TaskMeta 内存缓存中
            this.taskMeta.setCurrentState(this.taskMeta.getStartState());

            //执行前：变更当前组件起始状态到数据库
            this.jobService.switchComponentState(this.taskMeta);

            this.run();
            this.success();
        } catch (BException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            this.fail();
        } finally {
            //执行后：变更当前组件起始状态到数据库
            this.jobService.switchComponentState(this.taskMeta);

            //记录 Task 结束时间(自动计算耗时)
            this.taskMeta.setEndTime(System.currentTimeMillis());
            log.info("Task: TaskName: {}, Action: {}, Duration: {} ms",
                    taskMeta.getName(),
                    taskMeta.getActionTypeEnum(),
                    taskMeta.getDuration()
            );

            ExecStateEnum execStateEnum = taskMeta.getTaskResult().isSuccess() ?
                    ExecStateEnum.OK :
                    ExecStateEnum.ERROR;

            //更新当前 Task 执行状态到内存缓存和数据库
            this.updateTaskExecutionStatus(execStateEnum);
        }

        return this.taskMeta;
    }

    @Override
    public TaskMeta.TaskResult success() {
        this.taskMeta.getTaskResult().setSuccess(true);
        this.taskMeta.setCurrentState(taskMeta.getSuccessState());
        return this.taskMeta.getTaskResult();
    }

    @Override
    public TaskMeta.TaskResult fail() {
        this.taskMeta.getTaskResult().setSuccess(false);
        this.taskMeta.setCurrentState(taskMeta.getFailState());
        return this.taskMeta.getTaskResult();
    }

    /**
     * Description: 执行 Step 类型为 COMMAND 的操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param stepMeta 步骤元数据信息
     * @return boolean 成功返回 true 失败返回 false
     */
    protected String command(StepMeta stepMeta) throws BException {
        Result<String> result = remoteInvokeWorkerService.iWorkerExecAPI(taskMeta.getNodeIp())
                .exec(
                        new ExecRequest(
                                ExecTypeEnum.COMMAND,
                                stepMeta.getName(),
                                stepMeta.getShell(),
                                stepMeta.getExits(),
                                stepMeta.getTimeout(),
                                stepMeta.getArgs().toArray(new String[0]),
                                stepMeta.getInteractions().toArray(new String[0]),
                                true
                        )
                );

        return result.getMessage();
    }

    /**
     * Description: 执行 Step 类型为 SCRIPT 的操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param stepMeta 步骤元数据信息
     * @return boolean 成功返回 true 失败返回 false
     */
    protected String script(StepMeta stepMeta) throws BException {
        stepMeta.setShell(
                this.absoluteCommandPath(stepMeta)
        );
        return this.command(stepMeta);
    }

    /**
     * Description: 获取服务脚本的绝对路径
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param stepMeta 当前步骤的元数据信息
     * @return 服务插件下脚本的绝对路径
     */
    protected String absoluteCommandPath(StepMeta stepMeta) {
        return String.format(
                "%s/%s/scripts/%s",
                //TODO FOR TEST
                SpringContextUtilTest.PLUGINS_PATH_DIR_REMOTE,
                this.taskMeta.getServiceName(),
                stepMeta.getShell()
        );
    }

    /**
     * Description: 执行 Step 类型为 JAR 的操作, 该 Jar 必须存放于项目根目录下的 plugins 目录下
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31 10:26
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: 执行成功时无返回，执行异常时抛出 BException 异常。
     *
     * @param stepMeta 步骤元数据信息
     * @return boolean 成功返回 true 失败返回 false
     */
    protected String jar(StepMeta stepMeta) throws BException {
        try {
            String jarParentPath = String.format(
                    "file:%s/%s/jars/%s",
                    SpringContextUtilTest.PLUGINS_PATH_DIR_LOCAL,
                    this.taskMeta.getServiceName(),
                    stepMeta.getJar()
            );
            log.info("Loading jar: {}", jarParentPath);

            URL url = new URL(jarParentPath);

            try (URLClassLoader ucl = new URLClassLoader(
                    new URL[]{url},
                    Thread.currentThread().getContextClassLoader())) {

                Class<?> clazz = ucl.loadClass(stepMeta.getClazz());

                if (IConfig.class.isAssignableFrom(clazz)) {
                    IConfig iConfig = (IConfig) clazz.getDeclaredConstructor().newInstance();
                    iConfig.init(this.jobService.pluginConfig(this.taskMeta));

                    //得到配置文件修改后的返回结果，准备入库
                    PluginConfigResult selfPluginConfigResult = iConfig.configSelf();

                    Assert.isTrue(
                            this.jobService.configSaveOrUpdateBatch(selfPluginConfigResult),
                            () -> new BException(
                                    String.format(
                                            "%s 配置文件修改失败",
                                            this.taskMeta.getComponentName()
                                    )
                            )
                    );

                } else {
                    throw new BException(
                            String.format(
                                    "该 class 未实现 %s %s %s 接口",
                                    "datalight-plugins",
                                    "plugin-base",
                                    "cn.boundivore.dl.plugin.base.exec.IExec"
                            )

                    );
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return String.format(
                    "插件运行成功: %s",
                    jarParentPath
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Description: 更新当前 Task 执行状态到内存缓存和数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/4
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param execStateEnum 当前异步任务的执行状态
     */
    private void updateTaskExecutionStatus(ExecStateEnum execStateEnum) {
        // 更新当前作业的执行状态到内存缓存
        this.jobService.updateTaskMemory(this.taskMeta, execStateEnum);
        // 更新当前作业的执行状态到数据库
        this.jobService.updateTaskDatabase(this.taskMeta);
    }

    /**
     * Description: 移除并初始化当前节点服务相关目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/27
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     */
    protected void initServiceEnv() {
        // 判断当前是否需要删除原有目录
        StageMeta stageMeta = this.taskMeta.getStageMeta();
        JobMeta jobMeta = stageMeta.getJobMeta();
        ClusterMeta clusterMeta = jobMeta.getClusterMeta();

        String serviceName =  this.taskMeta.getServiceName();
        String tgzFilename = ResolverYamlServiceDetail.SERVICE_MAP.get(serviceName).getTgz();

        // 如果该节点该服务没有任何已部署的组件，则清除历史遗留目录
        if (this.jobService.isInit(
                clusterMeta.getCurrentClusterId(),
                this.taskMeta.getNodeId(),
                this.taskMeta.getServiceName())) {

            // 清除相关目录
            String removeCmd = this.absoluteRemovedCommandPath();
            boolean removeResult = this.invokeWorkerExecCmd(
                    "移除服务遗留目录",
                    removeCmd,
                    serviceName,
                    tgzFilename
            );
            Assert.isTrue(
                    removeResult,
                    () -> new BException(
                            String.format(
                                    "移除服务遗留目录失败: %s, %s",
                                    this.taskMeta.getServiceName(),
                                    this.taskMeta.getHostname()
                            )
                    )
            );

            // 创建相关目录
            String createCmd = this.absoluteCreateEnvCommandPath();
            boolean createResult = this.invokeWorkerExecCmd(
                    "初始化服务目录",
                    createCmd,
                    serviceName,
                    tgzFilename
            );
            Assert.isTrue(
                    createResult,
                    () -> new BException(
                            String.format(
                                    "初始化服务目录失败: %s, %s",
                                    this.taskMeta.getServiceName(),
                                    this.taskMeta.getHostname()
                            )
                    )
            );

        }
    }

    /**
     * Description: 获取清除目录的脚本路径
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 服务插件下脚本的绝对路径
     */
    protected String absoluteRemovedCommandPath() {
        return String.format(
                "%s/%s",
                //TODO FOR TEST
                SpringContextUtilTest.SCRIPTS_PATH_DIR_REMOTE,
                "service-remove-dir.sh"
        );
    }

    /**
     * Description: 获取初始化目录的脚本路径
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/21
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @return 服务插件下脚本的绝对路径
     */
    protected String absoluteCreateEnvCommandPath() {
        return String.format(
                "%s/%s",
                //TODO FOR TEST
                SpringContextUtilTest.SCRIPTS_PATH_DIR_REMOTE,
                "service-init-env.sh"
        );
    }

    /**
     * Description: 执行 Step 类型为 COMMAND 的操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     * TODO 参数中应包含全部应该清除的目录，用户自定义目录等
     * @param shell 清除脚本的绝对路径，shell
     * @return boolean 成功返回 true 失败返回 false
     */
    protected boolean invokeWorkerExecCmd(String cmdName,
                                          String shell,
                                          String serviceName,
                                          String targzFilename) throws BException {

        Result<String> result = this.remoteInvokeWorkerService.iWorkerExecAPI(this.taskMeta.getNodeIp())
                .exec(
                        new ExecRequest(
                                ExecTypeEnum.COMMAND,
                                cmdName,
                                shell,
                                0,
                                30 * 3000L,
                                new String[]{serviceName, targzFilename},
                                new String[0],
                                true
                        )
                );

        return result.isSuccess();
    }

}
