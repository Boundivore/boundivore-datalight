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
package cn.boundivore.dl.service.master.manage.node.task;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.boot.utils.ReactiveAddressUtil;
import cn.boundivore.dl.cloud.utils.SpringContextUtil;
import cn.boundivore.dl.cloud.utils.SpringContextUtilTest;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.service.master.manage.node.bean.NodeResources;
import cn.boundivore.dl.service.master.manage.node.bean.NodeStepMeta;
import cn.boundivore.dl.service.master.manage.node.bean.NodeTaskMeta;
import cn.boundivore.dl.service.master.manage.node.job.NodeJobService;
import cn.boundivore.dl.ssh.bean.TransferProgress;
import cn.boundivore.dl.ssh.tools.SshTool;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Description: 包装异步 NodeTask 的执行逻辑，NodeTask 线程运行性质：同集群、同节点
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
public abstract class AbstractNodeTask implements INodeTask {
    //任务执行必要的元数据信息
    protected final NodeTaskMeta nodeTaskMeta;

    protected final NodeJobService nodeJobService = SpringContextUtil.getBean(NodeJobService.class);

    public AbstractNodeTask(@NotNull NodeTaskMeta nodeTaskMeta) {
        this.nodeTaskMeta = nodeTaskMeta;
        this.nodeJobService.updateNodeTaskDatabase(this.nodeTaskMeta);
    }

    @Override
    public NodeTaskMeta getNodeTaskMeta() {
        return this.nodeTaskMeta;
    }

    /**
     * Description: 执行任务逻辑
     * Created by: Boundivore
     * Creation time: 2023/4/23
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @return NodeTaskMeta
     */
    @Override
    public NodeTaskMeta call() throws Exception {
        try {
            //更新当前 Task 执行状态到内存缓存和数据库
            this.updateTaskExecutionStatus(ExecStateEnum.RUNNING);

            //记录 NodeTask 起始时间
            this.nodeTaskMeta.setStartTime(System.currentTimeMillis());

            //设置 NodeTask 开始执行时，当前组件状态到 NodeTaskMeta 内存缓存中
            this.nodeTaskMeta.setCurrentState(this.nodeTaskMeta.getStartState());

            //执行前：变更当前组件起始状态到数据库
            this.nodeJobService.switchNodeState(this.nodeTaskMeta);

            this.run();
            this.success();
        } catch (BException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            this.fail();
        } finally {
            //执行后：变更当前节点状态到数据库
            this.nodeJobService.switchNodeState(this.nodeTaskMeta);

            //记录 Task 结束时间(自动计算耗时)
            this.nodeTaskMeta.setEndTime(System.currentTimeMillis());
            log.info("Task: NodeTaskName: {}, NodeAction: {}, Duration: {} ms",
                    nodeTaskMeta.getName(),
                    nodeTaskMeta.getNodeActionTypeEnum(),
                    nodeTaskMeta.getDuration()
            );

            ExecStateEnum execStateEnum = nodeTaskMeta.getNodeTaskResult().isSuccess() ?
                    ExecStateEnum.OK :
                    ExecStateEnum.ERROR;

            //更新当前 Task 执行状态到内存缓存和数据库
            this.updateTaskExecutionStatus(execStateEnum);

        }

        return this.nodeTaskMeta;
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
     * @param execStateEnum 当前状态
     */
    private void updateTaskExecutionStatus(ExecStateEnum execStateEnum) {
        // 更新当前作业的执行状态到内存缓存
        this.nodeJobService.updateNodeTaskMemory(this.nodeTaskMeta, execStateEnum);
        // 更新当前作业的执行状态到数据库
        this.nodeJobService.updateNodeTaskDatabase(this.nodeTaskMeta);
    }

    @Override
    public NodeTaskMeta.NodeTaskResult success() {
        this.nodeTaskMeta.getNodeTaskResult().setSuccess(true);
        this.nodeTaskMeta.setCurrentState(nodeTaskMeta.getSuccessState());
        return this.nodeTaskMeta.getNodeTaskResult();
    }

    @Override
    public NodeTaskMeta.NodeTaskResult fail() {
        this.nodeTaskMeta.getNodeTaskResult().setSuccess(false);
        this.nodeTaskMeta.setCurrentState(nodeTaskMeta.getFailState());
        return this.nodeTaskMeta.getNodeTaskResult();
    }

    /**
     * Description: 执行 NodeStep 类型为 COMMAND 的操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeStepMeta 步骤元数据信息
     * @return String 返回脚本执行结果
     */
    protected String command(NodeStepMeta nodeStepMeta) throws Exception {
        SshTool.ExecResult execResult = this.exec(nodeStepMeta);

        Assert.isTrue(
                execResult.getExitCode() == 0,
                () -> new BException(
                        String.format(
                                "SSH 执行远程指令失败: %s",
                                execResult.getOutput()
                        )
                )
        );

        return execResult.getOutput();
    }

    /**
     * Description: 执行 NodeStep 类型为 SCRIPT 的操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeStepMeta 步骤元数据信息
     * @return String 返回脚本执行结果
     */
    protected String script(NodeStepMeta nodeStepMeta) throws Exception {
        nodeStepMeta.setShell(
                this.absoluteScriptPath(nodeStepMeta.getShell())
        );

        return this.command(nodeStepMeta);
    }

    /**
     * Description: 为脚本拼接上..../scripts 目录的绝对路径
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param scriptName 脚本名称
     * @return 脚本的绝对路径
     */
    protected String absoluteScriptPath(String scriptName) throws Exception {
        return String.format(
                "%s/%s",
                SpringContextUtilTest.NODE_SCRIPTS_DIR_REMOTE,
                scriptName
        );
    }

    /**
     * Description: 执行命令并返回脚本执行结果字符串
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeStepMeta 节点步骤的元数据信息
     * @return 脚本执行结果
     */
    protected SshTool.ExecResult exec(NodeStepMeta nodeStepMeta) throws Exception {
        log.info("Step 执行: " + nodeStepMeta.getShell() + " 节点: " + nodeTaskMeta.getHostname());

        StringBuilder intactCmd = new StringBuilder(nodeStepMeta.getShell() + " ");
        nodeStepMeta.getArgs().forEach(
                i -> intactCmd.append(i).append(" ")
        );

        return nodeJobService.exec(
                this.nodeTaskMeta.getHostname(),
                this.nodeTaskMeta.getSshPort(),
                this.nodeTaskMeta.getPrivateKeyPath(),
                intactCmd.toString().trim(),
                nodeStepMeta.getTimeout(),
                TimeUnit.SECONDS
        );
    }

    /**
     * Description: 扫描指定节点的连通性
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeStepMeta 步骤元数据信息
     * @return String 返回脚本执行结果
     */
    protected String scan(NodeStepMeta nodeStepMeta) throws Exception {
        Assert.isTrue(
                this.nodeJobService.scan(
                        nodeTaskMeta.getHostname(),
                        nodeTaskMeta.getSshPort(),
                        nodeTaskMeta.getPrivateKeyPath()
                ),
                () -> new BException(
                        String.format(
                                "节点 %s 无法通过 SSH 连接",
                                nodeTaskMeta.getHostname()
                        )
                )
        );

        return "成功建立 SSH 连接";
    }

    /**
     * Description: 探测节点的物理资源
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: Exception 连接失败抛出异常
     *
     * @param nodeStepMeta 步骤元数据信息
     * @return String 返回脚本执行结果
     */

    protected String scanResources(NodeStepMeta nodeStepMeta) throws Exception {
        // 在目标节点创建脚本目录
        this.nodeJobService.mkdirs(
                nodeTaskMeta.getHostname(),
                nodeTaskMeta.getSshPort(),
                nodeTaskMeta.getPrivateKeyPath(),
                SpringContextUtilTest.NODE_DIR_REMOTE
        );
        // 推送脚本到目标节点
        this.nodeJobService.push(
                nodeTaskMeta.getHostname(),
                nodeTaskMeta.getSshPort(),
                nodeTaskMeta.getPrivateKeyPath(),
                SpringContextUtilTest.NODE_DIR_LOCAL,
                SpringContextUtilTest.NODE_DIR_REMOTE
        );


        // 在目标节点创建脚本目录
        this.nodeJobService.mkdirs(
                nodeTaskMeta.getHostname(),
                nodeTaskMeta.getSshPort(),
                nodeTaskMeta.getPrivateKeyPath(),
                SpringContextUtilTest.CONF_ENV_DIR_REMOTE
        );
        // 推送相关环境变量到目标节点
        this.nodeJobService.push(
                nodeTaskMeta.getHostname(),
                nodeTaskMeta.getSshPort(),
                nodeTaskMeta.getPrivateKeyPath(),
                SpringContextUtilTest.CONF_ENV_DIR_LOCAL,
                SpringContextUtilTest.CONF_ENV_DIR_REMOTE
        );

        nodeStepMeta.setShell(
                this.absoluteScriptPath(nodeStepMeta.getShell())
        );

        // 执行脚本
        SshTool.ExecResult execResult = this.exec(nodeStepMeta);

        Assert.isTrue(
                execResult.getExitCode() == 0,
                () -> new BException(
                        String.format(
                                "扫描物理资源信息失败: %s",
                                execResult.getOutput()
                        )
                )
        );

        //解析脚本的返回内容
        Assert.notBlank(execResult.getOutput(), () -> new BException("扫描物理资源信息失败"));

        // 解析脚本输出并映射到JavaBean
        NodeResources nodeResources = NodeResources.fromString(execResult);

        //更新到数据库
        this.nodeJobService.updateNodeResourceInDatabase(this.nodeTaskMeta, nodeResources);

        return execResult.getOutput();
    }

    /**
     * Description: 检查节点环境是否正常
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeStepMeta 步骤元数据信息
     * @return String 返回脚本执行结果
     */
    protected String checkEnv(NodeStepMeta nodeStepMeta) throws Exception {
        // 获取 Master 节点 IP
        String masterIp = ReactiveAddressUtil.getInternalIPAddress();
        //TODO TEST
        masterIp = SpringContextUtilTest.MASTER_IP_TEST;

        // 输出 hosts 配置到 ./node/conf/auto-hosts.conf
        Long clusterId = this.nodeTaskMeta.getNodeJobMeta().getClusterId();

        //如果是计算集群，则需要考虑包含存储集群的节点，以便后续可以访问
        String hostsStr = this.nodeJobService.getAllNodeWhenCheck(clusterId).toString();

        File autoHostsConfFile = FileUtil.file(
                String.format(
                        "%s/%s",
                        SpringContextUtilTest.NODE_CONF_DIR_LOCAL,
                        "auto-hosts.conf"
                )
        );
        FileUtil.writeString(hostsStr, autoHostsConfFile, CharsetUtil.UTF_8);

        nodeStepMeta.setShell(
                this.absoluteScriptPath(nodeStepMeta.getShell())
        );

        // 拼接当前节点的信息作为参数
        nodeStepMeta.getArgs().add(masterIp);
        nodeStepMeta.getArgs().add(this.nodeTaskMeta.getHostname());

        // 执行脚本
        SshTool.ExecResult execResult = this.exec(nodeStepMeta);

        // 解析脚本的返回内容
        Assert.isTrue(
                execResult.getExitCode() == 0,
                () -> new BException(
                        String.format(
                                "\n检查节点环境失败: %s\n",
                                execResult.getOutput()
                        )
                )
        );

        return execResult.getOutput();
    }

    /**
     * Description: 执行 NodeStep 类型为 PUSH 的操作
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31 10:26
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeStepMeta 步骤元数据信息
     * @return String 返回脚本执行结果
     */
    protected String push(NodeStepMeta nodeStepMeta) throws Exception {
        //TODO TEST
        String localPath = SpringContextUtilTest.APP_PARENT_DIR_LOCAL_LOCAL;
        String remotePath = SpringContextUtilTest.APP_PARENT_DIR_REMOTE_LOCAL;

        String hostname = nodeTaskMeta.getHostname();
        Integer sshPort = nodeTaskMeta.getSshPort();
        String privateKeyPath = nodeTaskMeta.getPrivateKeyPath();

        TransferProgress transferProgress = this.nodeJobService.initNodeTransferProgress(
                hostname,
                sshPort,
                privateKeyPath,
                localPath
        );
        nodeStepMeta.setTransferProgress(transferProgress);


        if (this.isMasterNode(this.nodeTaskMeta.getNodeIp())) {

            //如果要推送的目标地址为当前节点，则跳过推送，并将总进度更新为 100
            transferProgress.setTotalProgress(100);
            transferProgress.getTotalTransferBytes().set(transferProgress.getTotalBytes());

            transferProgress.setTotalFileCountProgress(100);
            transferProgress.getTotalTransferFileCount().set(transferProgress.getTotalFileCount());

            //各个文件进度更新为 100
            transferProgress.getFileProgressMap().forEach((filePath, fileProgress) -> {
                        fileProgress.setFileProgress(100);
                        fileProgress.getFileTransferBytes().set(fileProgress.getFileBytes());
                    }
            );
        } else {
            this.nodeJobService.push(
                    transferProgress,
                    hostname,
                    sshPort,
                    privateKeyPath,
                    localPath,
                    remotePath
            );
        }

        return "成功推送安装包";
    }

    /**
     * Description: 判断目标节点是否为当前 Master 节点
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param targetNodeIp 目标节点 IP
     * @return 目标节点为当前 Master 节点，返回 true ，反之返回 false
     */
    private boolean isMasterNode(String targetNodeIp) {
        // 获取 Master 节点 IP
        String masterIp = ReactiveAddressUtil.getInternalIPAddress();

        //TODO TEST
        masterIp = SpringContextUtilTest.MASTER_IP_TEST;

        return targetNodeIp.equals(masterIp);
    }


}
