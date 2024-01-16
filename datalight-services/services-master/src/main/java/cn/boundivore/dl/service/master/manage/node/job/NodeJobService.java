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
package cn.boundivore.dl.service.master.manage.node.job;

import cn.boundivore.dl.base.constants.ICommonConstant;
import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.base.response.impl.master.AbstractClusterVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeInitVo;
import cn.boundivore.dl.base.response.impl.master.AbstractNodeVo;
import cn.boundivore.dl.exception.BException;
import cn.boundivore.dl.exception.DatabaseException;
import cn.boundivore.dl.orm.po.single.TDlNodeJob;
import cn.boundivore.dl.orm.po.single.TDlNodeJobLog;
import cn.boundivore.dl.orm.po.single.TDlNodeStep;
import cn.boundivore.dl.orm.po.single.TDlNodeTask;
import cn.boundivore.dl.orm.service.single.impl.TDlNodeJobLogServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlNodeJobServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlNodeStepServiceImpl;
import cn.boundivore.dl.orm.service.single.impl.TDlNodeTaskServiceImpl;
import cn.boundivore.dl.service.master.manage.node.bean.*;
import cn.boundivore.dl.service.master.service.MasterClusterService;
import cn.boundivore.dl.service.master.service.MasterNodeInitService;
import cn.boundivore.dl.service.master.service.MasterNodeService;
import cn.boundivore.dl.ssh.bean.TransferProgress;
import cn.boundivore.dl.ssh.service.SshService;
import cn.boundivore.dl.ssh.tools.SshTool;
import cn.hutool.core.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
@Slf4j
public class NodeJobService {

    //获取通用耗时异步任务线程池
    private final ThreadPoolTaskExecutor commonExecutor;

    private final MasterClusterService masterClusterService;

    private final MasterNodeInitService masterNodeInitService;
    private final MasterNodeService masterNodeService;

    private final TDlNodeJobServiceImpl tDlNodeJobService;

    private final TDlNodeTaskServiceImpl tDlTaskNodeService;

    private final TDlNodeStepServiceImpl tDlStepNodeService;

    private final TDlNodeJobLogServiceImpl tDlNodeJobLogService;

    private final SshService sshService;


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
        return this.commonExecutor.submit(callable);
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
     * @param nodeTaskMeta Task 元数据信息
     */
    public void switchNodeState(NodeTaskMeta nodeTaskMeta) {
        NodeJobMeta nodeJobMeta = nodeTaskMeta.getNodeJobMeta();

        NodeActionTypeEnum nodeActionTypeEnum = nodeJobMeta.getNodeActionTypeEnum();

        // 初始化过程中的节点，与服役过程中的节点，数据不在同一张表，因此需要判断更新哪张表的状态
        switch (nodeActionTypeEnum) {
            case SHUTDOWN:
            case RESTART:
                this.masterNodeService.switchNodeState(
                        nodeJobMeta.getClusterId(),
                        nodeTaskMeta.getNodeId(),
                        nodeTaskMeta.getCurrentState()
                );
                break;
            case DETECT:
            case CHECK:
            case DISPATCH:
            case START_WORKER:
                this.masterNodeInitService.switchNodeInitState(
                        nodeJobMeta.getClusterId(),
                        nodeTaskMeta.getNodeId(),
                        nodeTaskMeta.getCurrentState()
                );
                break;
            default:
                throw new BException(
                        String.format(
                                "非法的节点行为: %s",
                                nodeActionTypeEnum
                        )
                );
        }

    }

    /**
     * Description: 根据 NodeJob 执行的结果，更新当前 NodeJob 的状态到内存缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeJobMeta   NodeJob 元数据信息
     * @param execStateEnum 执行状态
     */
    public void updateNodeJobMemory(NodeJobMeta nodeJobMeta, ExecStateEnum execStateEnum) {
        nodeJobMeta.setExecStateEnum(execStateEnum);
    }

    /**
     * Description: 根据 NodeJob 执行的结果，更新当前 NodeJob 的状态到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeJobMeta NodeJob 元数据信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void updateNodeJobDatabase(NodeJobMeta nodeJobMeta) {
        //注意：执行更新数据库前，务必先更新内存，例如： this.updateNodeJobMemory()
        //此时会从内从中最新的元数据状态更新到数据库
        TDlNodeJob tDlNodeJob = this.tDlNodeJobService.getById(nodeJobMeta.getId());

        if (tDlNodeJob == null) {
            tDlNodeJob = new TDlNodeJob();
            tDlNodeJob.setVersion(0L).setId(nodeJobMeta.getId());
        }

        tDlNodeJob.setTag(nodeJobMeta.getTag())
                .setClusterId(nodeJobMeta.getClusterId())
                .setNodeJobName(nodeJobMeta.getName())
                .setNodeJobState(nodeJobMeta.getExecStateEnum())
                .setStartTime(nodeJobMeta.getStartTime())
                .setEndTime(nodeJobMeta.getEndTime())
                .setDuration(nodeJobMeta.getDuration());

        Assert.isTrue(
                this.tDlNodeJobService.saveOrUpdate(tDlNodeJob),
                () -> new DatabaseException("保存或更新 NodeJob 到数据库失败")
        );

    }

    /**
     * Description: 根据 NodeTask 执行的结果，更新当前 NodeTask 的状态到内存缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/7
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeTaskMeta  NodeTask 元数据信息
     * @param execStateEnum 执行状态
     */
    public void updateNodeTaskMemory(NodeTaskMeta nodeTaskMeta, ExecStateEnum execStateEnum) {
        nodeTaskMeta.setNodeTaskStateEnum(execStateEnum);
    }

    /**
     * Description: 根据 NodeTask 执行的结果，更新当前 NodeTask 的状态到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/5/31
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeTaskMeta NodeTask 元数据信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void updateNodeTaskDatabase(NodeTaskMeta nodeTaskMeta) {
        //注意：执行更新数据库前，务必先更新内存，例如： this.updateNodeTaskMemory()
        //此时会从内存中最新的元数据状态更新到数据库
        NodeJobMeta nodeJobMeta = nodeTaskMeta.getNodeJobMeta();

        TDlNodeTask tDlNodeTask = this.tDlTaskNodeService.getById(nodeTaskMeta.getId());

        if (tDlNodeTask == null) {
            tDlNodeTask = new TDlNodeTask();
            tDlNodeTask.setVersion(0L).setId(nodeTaskMeta.getId());
        }

        tDlNodeTask.setTag(nodeJobMeta.getTag())
                .setClusterId(nodeJobMeta.getClusterId())
                .setNodeJobId(nodeJobMeta.getId())
                .setNodeId(nodeTaskMeta.getNodeId())
                .setHostname(nodeTaskMeta.getHostname())
                .setNodeIp(nodeTaskMeta.getNodeIp())
                .setNodeTaskName(nodeTaskMeta.getName())
                .setNodeTaskState(nodeTaskMeta.getNodeTaskStateEnum())
                .setNodeActionType(nodeTaskMeta.getNodeActionTypeEnum())
                .setStartTime(nodeTaskMeta.getStartTime())
                .setEndTime(nodeTaskMeta.getEndTime())
                .setDuration(nodeTaskMeta.getDuration());

        Assert.isTrue(
                this.tDlTaskNodeService.saveOrUpdate(tDlNodeTask),
                () -> new DatabaseException("保存或更新 NodeTaskMeta 到数据库失败")
        );
    }

    /**
     * Description: 根据 NodeStep 执行的结果，更新当前 NodeStep 的状态到内存缓存
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/7
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeStepMeta  NodeStep 元数据信息
     * @param execStateEnum 执行状态
     */
    public void updateNodeStepMemory(NodeStepMeta nodeStepMeta, ExecStateEnum execStateEnum) {
        nodeStepMeta.setExecStateEnum(execStateEnum);
    }


    /**
     * Description: 根据 Step 执行的结果，更新当前 Step 的状态到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/6/7
     * Modification description:
     * Modified by:
     * Modification time:
     *
     * @param nodeStepMeta NodeStep 元数据信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void updateNodeStepDatabase(NodeStepMeta nodeStepMeta) {
        // 注意：执行更新数据库前，务必先更新内存，例如： this.updateNodeStepMemory()，
        // 此时会从内存中最新的元数据状态更新到数据库
        NodeTaskMeta nodeTaskMeta = nodeStepMeta.getNodeTaskMeta();
        NodeJobMeta nodeJobMeta = nodeTaskMeta.getNodeJobMeta();

        TDlNodeStep tDlNodeStep = this.tDlStepNodeService.getById(nodeStepMeta.getId());

        if (tDlNodeStep == null) {
            tDlNodeStep = new TDlNodeStep();
            tDlNodeStep.setVersion(0L).setId(nodeStepMeta.getId());
        }

        tDlNodeStep.setTag(nodeJobMeta.getTag())
                .setClusterId(nodeJobMeta.getClusterId())
                .setNodeJobId(nodeJobMeta.getId())
                .setNodeTaskId(nodeTaskMeta.getId())
                .setNodeStepName(nodeStepMeta.getName())
                .setNodeStepState(nodeStepMeta.getExecStateEnum())
                .setNodeStepType(nodeStepMeta.getType())
                .setStartTime(nodeStepMeta.getStartTime())
                .setEndTime(nodeStepMeta.getEndTime())
                .setDuration(nodeStepMeta.getDuration());

        Assert.isTrue(
                this.tDlStepNodeService.saveOrUpdate(tDlNodeStep),
                () -> new DatabaseException("保存或更新 NodeStepMeta 到数据库失败")
        );
    }

    /**
     * Description: 代理执行远程脚本
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param host           节点地址（IP或主机名）
     * @param sshPort        ssh 端口号
     * @param privateKeyPath 远程节点私钥路径
     * @param cmd            待执行脚本的绝对路径
     * @return 脚本执行结果
     */
    public SshTool.ExecResult exec(String host,
                                   int sshPort,
                                   String privateKeyPath,
                                   String cmd,
                                   long timeout,
                                   TimeUnit timeUnit) throws IOException {

        SshTool sshTool = this.sshService.sshTool();

        SSHClient sshClient = sshTool.connect(
                host,
                sshPort,
                "root",
                privateKeyPath
        );
        SshTool.ExecResult execResult = sshTool.exec(
                sshClient,
                cmd,
                timeout,
                timeUnit
        );

        sshTool.disconnect(sshClient);

        return execResult;
    }

    /**
     * Description: 在目标节点创建目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param hostname       主机名
     * @param sshPort        ssh 端口号
     * @param privateKeyPath 远程节点私钥路径
     * @param remoteDirPath  目标节点目录
     */
    public void mkdirs(String hostname,
                       int sshPort,
                       String privateKeyPath,
                       String remoteDirPath) throws IOException {

        SshTool sshTool = sshService.sshTool();

        SSHClient sshClient = sshTool.connect(
                hostname,
                sshPort,
                "root",
                privateKeyPath
        );
        sshTool.exec(
                sshClient,
                String.format(
                        "mkdir -p %s",
                        remoteDirPath
                )
        );
        sshTool.disconnect(sshClient);
    }

    /**
     * Description: 删除目标节点的文件或目录
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param hostname       主机名
     * @param sshPort        ssh 端口号
     * @param privateKeyPath 远程节点私钥路径
     * @param remotePath     文件或目录路径
     */
    public void rmr(String hostname,
                    int sshPort,
                    String privateKeyPath,
                    String remotePath) throws IOException {

        SshTool sshTool = sshService.sshTool();

        SSHClient sshClient = sshTool.connect(
                hostname,
                sshPort,
                "root",
                privateKeyPath
        );
        sshTool.exec(
                sshClient,
                String.format(
                        "rm -rf %s",
                        remotePath
                )
        );
        sshTool.disconnect(sshClient);
    }

    /**
     * Description: 扫描探测指定节点
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param host           目标节点 IP 或主机名
     * @param sshPort        SSH 端口号
     * @param privateKeyPath 私钥文件地址
     * @return true 可连通, false 不可连通
     */
    public boolean scan(String host, int sshPort, String privateKeyPath) {
        SshTool sshTool = this.sshService.sshTool();

        return sshTool.detectConnection(
                host,
                sshPort,
                "root",
                privateKeyPath
        );
    }

    /**
     * Description: 初始化节点传输进度, 此方法用于通过SSH连接到指定的主机，并初始化传输进度对象。
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/7
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws: IOException - 如果在SSH连接期间发生错误
     *
     * @param hostname       主机名或 IP 地址
     * @param sshPort        SSH端口号
     * @param privateKeyPath 私钥文件的路径
     * @param transferPath   传输路径
     * @return TransferProgress - 初始化的传输进度对象
     */
    public TransferProgress initNodeTransferProgress(String hostname,
                                                     int sshPort,
                                                     String privateKeyPath,
                                                     String transferPath) throws IOException {
        SshTool sshTool = sshService.sshTool();

        SSHClient sshClient = sshTool.connect(
                hostname,
                sshPort,
                "root",
                privateKeyPath
        );

        TransferProgress transferProgress = sshTool.initNodePushProgress(
                transferPath,
                sshClient,
                true
        );

        sshClient.disconnect();

        return transferProgress;
    }

    /**
     * Description: 推送安装包到指定节点
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param hostname       主机名
     * @param sshPort        SSH 端口号
     * @param privateKeyPath 私钥文件地址
     * @param localPath      本地文件路径
     * @param remotePath     远端文件路径
     */
    public void push(String hostname,
                     int sshPort,
                     String privateKeyPath,
                     String localPath,
                     String remotePath) throws IOException {

        this.push(null, hostname, sshPort, privateKeyPath, localPath, remotePath);
    }

    /**
     * Description: 推送安装包到指定节点
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/3
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param transferProgress 进度信息封装
     * @param hostname         主机名
     * @param sshPort          SSH 端口号
     * @param privateKeyPath   私钥文件地址
     * @param localPath        本地文件路径
     * @param remotePath       远端文件路径
     */
    public void push(TransferProgress transferProgress,
                     String hostname,
                     int sshPort,
                     String privateKeyPath,
                     String localPath,
                     String remotePath) throws IOException {

        SshTool sshTool = sshService.sshTool();

        SSHClient sshClient = sshTool.connect(
                hostname,
                sshPort,
                "root",
                privateKeyPath
        );

        sshTool.transfer(
                transferProgress,
                sshClient,
                localPath,
                remotePath,
                true
        );

        sshClient.disconnect();
    }

    /**
     * Description: 代理调用：将探测到的物理资源更新到数据库
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/5
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeTaskMeta  当前 NodeTask 的元数据信息
     * @param nodeResources 当前探测到的节点物理资源
     */
    public void updateNodeResourceInDatabase(NodeTaskMeta nodeTaskMeta,
                                             NodeResources nodeResources) throws Exception {

        this.masterNodeInitService.updateNodeResourceInDatabase(
                nodeTaskMeta.getNodeId(),
                nodeResources
        );
    }

    /**
     * Description: 获取检查节点过程中当前集群所有节点信息
     * （包括集群初始化过程和新增节点过程）
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/6
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 当前集群 ID
     * @return 包含所有节点信息的 NodeHosts 对象
     */
    public NodeHosts getAllNodeWhenCheck(Long clusterId) {

        List<Long> clusterIdList = this.getClusterIdList(clusterId);

        List<NodeHosts> nodeHostsList = clusterIdList.stream()
                .map(this::getTargetClusterNodeList)
                .collect(Collectors.toList());

        return this.mergeNodeHosts(nodeHostsList);
    }

    /**
     * Description: 获取当前集群以及当前集群可能关联的 MIXED 集群的 ID
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 当前集群 ID
     * @return 当前集群以及当前集群可能关联的 MIXED 集群的 ID 列表
     */
    private List<Long> getClusterIdList(Long clusterId) {
        List<Long> clusterIdList = new ArrayList<>();
        AbstractClusterVo.ClusterVo currentCluster = this.masterClusterService
                .getClusterById(clusterId)
                .getData();

        clusterIdList.add(currentCluster.getClusterId());

        if (currentCluster.getClusterTypeEnum() == ClusterTypeEnum.COMPUTE) {
            AbstractClusterVo.ClusterVo relativeCluster = this.masterClusterService
                    .getClusterRelative(currentCluster.getClusterId())
                    .getData();

            clusterIdList.add(relativeCluster.getClusterId());
        }

        return clusterIdList;
    }

    /**
     * Description: 将多个节点列表合并
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param nodeHostsList 其他节点列表
     */
    public NodeHosts mergeNodeHosts(List<NodeHosts> nodeHostsList) {
        Set<NodeHosts.NodeHost> nodeHostSet = new LinkedHashSet<>();
        for (NodeHosts nodeHosts : nodeHostsList) {
            nodeHostSet.addAll(nodeHosts.getNodeHostList());
        }

        return new NodeHosts(new ArrayList<>(nodeHostSet));
    }

    /**
     * Description: 获取指定集群的所有节点
     * Created by: Boundivore
     * E-mail: boundivore@foxmail.com
     * Creation time: 2023/7/20
     * Modification description:
     * Modified by:
     * Modification time:
     * Throws:
     *
     * @param clusterId 目标集群 ID
     * @return 包含所有节点信息的 NodeHosts 对象
     */
    private NodeHosts getTargetClusterNodeList(Long clusterId) {
        // 获取集群初始化检查节点列表的节点信息
        List<NodeHosts.NodeHost> nodeHostList = Stream.concat(
                        // 获取新增节点的节点信息
                        this.masterNodeInitService.initCheckList(clusterId)
                                .getData()
                                .getNodeInitDetailList()
                                .stream(),
                        // 获取已服役的节点信息
                        this.masterNodeService.getNodeList(clusterId)
                                .getData()
                                .getNodeDetailList()
                                .stream()
                )
                .map(i -> {
                    if (i instanceof AbstractNodeInitVo.NodeInitDetailVo) {
                        // 如果是集群初始化过程中的节点信息
                        AbstractNodeInitVo.NodeInitDetailVo initDetail = (AbstractNodeInitVo.NodeInitDetailVo) i;

                        return new NodeHosts.NodeHost(
                                initDetail.getHostname(),
                                initDetail.getNodeIp()
                        );
                    } else if (i instanceof AbstractNodeVo.NodeDetailVo) {
                        // 如果是新增节点的节点信息
                        AbstractNodeVo.NodeDetailVo nodeDetail = (AbstractNodeVo.NodeDetailVo) i;

                        return new NodeHosts.NodeHost(
                                nodeDetail.getHostname(),
                                nodeDetail.getNodeIp()
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.comparing(NodeHosts.NodeHost::getHostname))
                .collect(Collectors.toList());

        return new NodeHosts(nodeHostList);
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
     * @param nodeStepMeta NodeStep 元数据信息
     */
    @Transactional(
            timeout = ICommonConstant.TIMEOUT_TRANSACTION_SECONDS,
            rollbackFor = DatabaseException.class
    )
    public void saveLog(NodeStepMeta nodeStepMeta,
                        String logStdout,
                        String logErrout) {
        NodeTaskMeta nodeTaskMeta = nodeStepMeta.getNodeTaskMeta();
        NodeJobMeta nodeJobMeta = nodeTaskMeta.getNodeJobMeta();

        TDlNodeJobLog tDlNodeJobLog = new TDlNodeJobLog();

        tDlNodeJobLog.setTag(nodeJobMeta.getTag());
        tDlNodeJobLog.setClusterId(nodeJobMeta.getClusterId());

        tDlNodeJobLog.setNodeJobId(nodeJobMeta.getId());
        tDlNodeJobLog.setNodeTaskId(nodeTaskMeta.getId());
        tDlNodeJobLog.setNodeStepId(nodeStepMeta.getId());

        tDlNodeJobLog.setLogStdout(
                String.format(
                        "%s %s",
                        nodeTaskMeta.getHostname(),
                        logStdout
                )
        );

        tDlNodeJobLog.setLogErrout(
                String.format(
                        "%s %s",
                        nodeTaskMeta.getHostname(),
                        logErrout
                )
        );

        Assert.isTrue(
                this.tDlNodeJobLogService.save(tDlNodeJobLog),
                () -> new DatabaseException("保存节点任务日志失败")
        );

    }


}
