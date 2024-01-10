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
package cn.boundivore.dl.base.response.impl.master;


import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStepTypeEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;


public abstract class AbstractNodeJobVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.NodeJobIdVo",
            description = "AbstractNodeJobVo.NodeJobIdVo: NodeJobId 信息"
    )
    public static class NodeJobIdVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "NodeJobId", title = "节点异步 JobId", required = true)
        @JsonProperty(value = "NodeJobId", required = true)
        @NotNull
        private Long nodeJobId;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.NodeJobProgressVo",
            description = "AbstractNodeJobVo.NodeJobProgressVo: NodeJob 计划进度信息"
    )
    public static class NodeJobProgressVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "NodeJobId", title = "节点异步 JobId", required = true)
        @JsonProperty(value = "NodeJobId", required = true)
        @NotNull
        private Long nodeJobId;

        @Schema(name = "NodeJobPlanProgress", title = "NodeJob 制定计划的进度", required = true)
        @JsonProperty(value = "NodeJobPlanProgress", required = true)
        @NotNull
        private NodeJobPlanProgressVo nodeJobPlanProgressVo;

        @Schema(name = "NodeJobExecProgress", title = "NodeJob 执行的进度", required = true)
        @JsonProperty(value = "NodeJobExecProgress", required = true)
        @NotNull
        private NodeJobExecProgressVo nodeJobExecProgressVo;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.NodeJobPlanProgressVo",
            description = "AbstractNodeJobVo.NodeJobPlanProgressVo: NodeJob 计划进度信息"
    )
    public static class NodeJobPlanProgressVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "NodeJobId", title = "节点异步 JobId", required = true)
        @JsonProperty(value = "NodeJobId", required = true)
        @NotNull
        private Long nodeJobId;

        @Schema(name = "NodeActionType", title = "节点操作动作类型", required = true)
        @JsonProperty(value = "NodeActionType", required = true)
        @NotNull
        private NodeActionTypeEnum nodeActionTypeEnum;

        @Schema(name = "PlanTotal", title = "计划总数", required = true)
        @JsonProperty(value = "PlanTotal", required = true)
        @NotNull
        private Integer planTotal;

        @Schema(name = "PlanCurrent", title = "当前计划进度", required = true)
        @JsonProperty(value = "PlanCurrent", required = true)
        @NotNull
        private Integer planCurrent;

        @Schema(name = "PlanProgress", title = "当前计划进度百分比", required = true)
        @JsonProperty(value = "PlanProgress", required = true)
        @NotNull
        private Integer planProgress;

        @Schema(name = "PlanName", title = "当前计划名称", required = true)
        @JsonProperty(value = "PlanName", required = true)
        @NotNull
        private String planName;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.NodeJobExecProgressVo",
            description = "AbstractNodeJobVo.NodeJobExecProgressVo: NodeJob 执行进度信息"
    )
    public static class NodeJobExecProgressVo implements IVo {

        @Schema(name = "JobExecStateEnum", title = "当前 Job 执行状态", required = true)
        @JsonProperty(value = "JobExecStateEnum", required = true)
        @NotNull
        private ExecStateEnum jobExecStateEnum;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "NodeJobId", title = "节点异步 JobId", required = true)
        @JsonProperty(value = "NodeJobId", required = true)
        @NotNull
        private Long nodeJobId;

        @Schema(name = "ExecTotal", title = "执行总数", required = true)
        @JsonProperty(value = "ExecTotal", required = true)
        @NotNull
        private Integer execTotal;

        @Schema(name = "ExecCurrent", title = "当前执行进度", required = true)
        @JsonProperty(value = "ExecCurrent", required = true)
        @NotNull
        private Integer execCurrent;

        @Schema(name = "ExecProgress", title = "当前执行进度百分比", required = true)
        @JsonProperty(value = "ExecProgress", required = true)
        @NotNull
        private Integer execProgress;

        @Schema(name = "ExecProgressPerNodeList", title = "每个节点的任务进度信息", required = true)
        @JsonProperty(value = "ExecProgressPerNodeList", required = true)
        @NotNull
        private List<ExecProgressPerNodeVo> execProgressPerNodeList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.ExecProgressPerNodeVo",
            description = "AbstractNodeJobVo.ExecProgressPerNodeVo: NodeJob 中每个节点的执行进度信息"
    )
    public static class ExecProgressPerNodeVo implements IVo {
        @Schema(name = "NodeId", title = "节点 ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        @NotNull
        private Long nodeId;

        @Schema(name = "Hostname", title = "主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        @NotNull
        private String hostname;

        @Schema(name = "NodeIp", title = "节点 IP", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        @NotNull
        private String nodeIp;

        @Schema(name = "NodeTaskId", title = "节点 Task ID", required = true)
        @JsonProperty(value = "NodeTaskId", required = true)
        @NotNull
        private Long nodeTaskId;

        @Schema(name = "NodeTaskName", title = "节点 Task 名称", required = true)
        @JsonProperty(value = "NodeTaskName", required = true)
        @NotNull
        private String nodeTaskName;

        @Schema(name = "ExecTotal", title = "当前节点执行总数", required = true)
        @JsonProperty(value = "ExecTotal", required = true)
        @NotNull
        private Integer execTotal;

        @Schema(name = "ExecCurrent", title = "当前节点执行进度", required = true)
        @JsonProperty(value = "ExecCurrent", required = true)
        @NotNull
        private Integer execCurrent;

        @Schema(name = "ExecProgress", title = "当前节点执行进度百分比", required = true)
        @JsonProperty(value = "ExecProgress", required = true)
        @NotNull
        private Integer execProgress;

        @Schema(name = "ExecProgressStepList", title = "每个任务的步骤进度信息", required = true)
        @JsonProperty(value = "ExecProgressStepList", required = true)
        @NotNull
        private List<ExecProgressStepVo> execProgressStepList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.ExecProgressStepVo",
            description = "AbstractNodeJobVo.ExecProgressStepVo: NodeJob 中每个节点的 Step 执行进度信息"
    )
    public static class ExecProgressStepVo implements IVo {

        @Schema(name = "NodeStepType", title = "节点 Step 类型", required = true)
        @JsonProperty(value = "NodeStepType", required = true)
        @NotNull
        private NodeStepTypeEnum nodeStepTypeEnum;

        @Schema(name = "NodeStepId", title = "节点 NodeStep ID", required = true)
        @JsonProperty(value = "NodeStepId", required = true)
        @NotNull
        private Long nodeStepId;

        @Schema(name = "NodeStepName", title = "节点 Step 名称", required = true)
        @JsonProperty(value = "NodeStepName", required = true)
        @NotNull
        private String nodeStepName;

        @Schema(name = "StepExecState", title = "节点 Step 执行状态", required = true)
        @JsonProperty(value = "StepExecState", required = true)
        @NotNull
        private ExecStateEnum stepExecStateEnum;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.NodeJobTransferProgressVo",
            description = "AbstractNodeJobVo.NodeJobTransferProgressVo: NodeJob 全部文件分发进度"
    )
    public static class AllNodeJobTransferProgressVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 Id", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "NodeJobId", title = "节点异步 Job Id", required = true)
        @JsonProperty(value = "NodeJobId", required = true)
        @NotNull
        private Long nodeJobId;


        @Schema(name = "NodeJobTransferProgressList", title = "各个文件传输信息列表", required = true)
        @JsonProperty(value = "NodeJobTransferProgressList", required = true)
        @NotNull
        private List<NodeJobTransferProgressVo> nodeJobTransferProgressList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.NodeJobTransferProgressVo",
            description = "AbstractNodeJobVo.NodeJobTransferProgressVo: NodeJob 当前节点传输概览"
    )
    public static class NodeJobTransferProgressVo implements IVo {


        @Schema(name = "NodeTaskId", title = "节点任务 ID", required = true)
        @JsonProperty(value = "NodeTaskId", required = true)
        @NotNull
        private Long nodeTaskId;

        @Schema(name = "NodeStepId", title = "节点步骤 ID", required = true)
        @JsonProperty(value = "NodeStepId", required = true)
        @NotNull
        private Long nodeStepId;


        @Schema(name = "ExecState", title = "当前步骤执行状态", required = true)
        @JsonProperty(value = "ExecState", required = true)
        @NotNull
        private ExecStateEnum execState;

        @Schema(name = "NodeId", title = "节点 Id", required = true)
        @JsonProperty(value = "NodeId", required = true)
        @NotNull
        private Long nodeId;

        @Schema(name = "Hostname", title = "主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        @NotNull
        private String hostname;

        @Schema(name = "CurrentFileProgress", title = "当前正在传输的文件进度信息", required = true)
        @JsonProperty(value = "CurrentFileProgress", required = true)
        @NotNull
        private CurrentFileProgressVo currentFileProgressVo;

        @Schema(name = "FileBytesProgress", title = "当前正在传输的文件字节进度信息", required = true)
        @JsonProperty(value = "FileBytesProgress", required = true)
        @NotNull
        private FileBytesProgressVo fileBytesProgressVo;

        @Schema(name = "FileCountProgress", title = "当前正在传输的文件个数进度信息", required = true)
        @JsonProperty(value = "FileCountProgress", required = true)
        @NotNull
        private FileCountProgressVo fileCountProgressVo;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.FileBytesProgressVo",
            description = "AbstractNodeJobVo.FileBytesProgressVo: NodeJob 当前正在传输的文件字节进度信息"
    )
    public static class FileBytesProgressVo implements IVo {
        @Schema(name = "TotalBytes", title = "所有文件总字节数", required = true)
        @JsonProperty(value = "TotalBytes", required = true)
        @NotNull
        private Long totalBytes;

        @Schema(name = "TotalProgress", title = "已传输总进度（百分比）", required = true)
        @JsonProperty(value = "TotalProgress", required = true)
        @NotNull
        private Long totalProgress;

        @Schema(name = "TotalTransferBytes", title = "所有文件已传输总字节数", required = true)
        @JsonProperty(value = "TotalTransferBytes", required = true)
        @NotNull
        private Long totalTransferBytes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.FileCountProgressVo",
            description = "AbstractNodeJobVo.FileCountProgressVo: NodeJob 当前正在传输的文件个数进度信息"
    )
    public static class FileCountProgressVo implements IVo {
        @Schema(name = "TotalFileCount", title = "所有文件个数", required = true)
        @JsonProperty(value = "TotalFileCount", required = true)
        @NotNull
        private Long totalFileCount;

        @Schema(name = "TotalFileCountProgress", title = "已传输文件个数总进度（百分比）", required = true)
        @JsonProperty(value = "TotalFileCountProgress", required = true)
        @NotNull
        private Long totalFileCountProgress;

        @Schema(name = "TotalTransferFileCount", title = "所有文件已传输总个数", required = true)
        @JsonProperty(value = "TotalTransferFileCount", required = true)
        @NotNull
        private Long totalTransferFileCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.CurrentFileProgressVo",
            description = "AbstractNodeJobVo.CurrentFileProgressVo: NodeJob 当前正在传输的文件进度信息"
    )
    public static class CurrentFileProgressVo implements IVo {
        @Schema(name = "CurrentFilename", title = "当前正在传输的文件", required = true)
        @JsonProperty(value = "CurrentFilename", required = true)
        @NotNull
        private String currentFilename;

        @Schema(name = "CurrentFileBytes", title = "当前正在传输的文件总大小", required = true)
        @JsonProperty(value = "CurrentFileBytes", required = true)
        @NotNull
        private Long currentFileBytes;

        @Schema(name = "CurrentFileTransferBytes", title = "当前正在传输文件已传输大小", required = true)
        @JsonProperty(value = "CurrentFileTransferBytes", required = true)
        @NotNull
        private Long currentFileTransferBytes;

        @Schema(name = "CurrentFileProgress", title = "当前正在传输的文件进度", required = true)
        @JsonProperty(value = "CurrentFileProgress", required = true)
        @NotNull
        private Long currentFileProgress;

        @Schema(name = "CurrentPrintSpeed", title = "当前正在传输的文件速率", required = true)
        @JsonProperty(value = "CurrentPrintSpeed", required = true)
        @NotNull
        private String currentPrintSpeed;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.NodeJobTransferProgressDetailVo",
            description = "AbstractNodeJobVo.NodeJobTransferProgressDetailVo: NodeJob 当前节点传输详情"
    )
    public static class NodeJobTransferProgressDetailVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 Id", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "NodeJobId", title = "节点异步 Job Id", required = true)
        @JsonProperty(value = "NodeJobId", required = true)
        @NotNull
        private Long nodeJobId;

        @Schema(name = "NodeJobTransferProgressVo", title = "节点 Id", required = true)
        @JsonProperty(value = "NodeJobTransferProgressVo", required = true)
        @NotNull
        private NodeJobTransferProgressVo nodeJobTransferProgressVo;


        @Schema(name = "FileProgressList", title = "各个文件传输信息列表", required = true)
        @JsonProperty(value = "FileProgressList", required = true)
        @NotNull
        private List<FileProgressVo> fileProgressList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractNodeJobVo.FileProgressVo",
            description = "AbstractNodeJobVo.FileProgressVo: NodeJob 单个文件分发进度"
    )
    public static class FileProgressVo implements IVo {

        @Schema(name = "FileDir", title = "文件目录", required = true)
        @JsonProperty(value = "FileDir", required = true)
        @NotNull
        private String fileDir;

        @Schema(name = "Filename", title = "文件名", required = true)
        @JsonProperty(value = "Filename", required = true)
        @NotNull
        private String filename;

        @Schema(name = "FileBytes", title = "文件总字节数", required = true)
        @JsonProperty(value = "FileBytes", required = true)
        @NotNull
        private Long fileBytes;

        @Schema(name = "FileTransferBytes", title = "文件已传输字节数", required = true)
        @JsonProperty(value = "FileTransferBytes", required = true)
        @NotNull
        private Long fileTransferBytes;

        @Schema(name = "FileProgress", title = "文件传输进度（百分比）", required = true)
        @JsonProperty(value = "FileProgress", required = true)
        @NotNull
        private Long fileProgress;

        @Schema(name = "Speed", title = "文件传输速率", required = true)
        @JsonProperty(value = "Speed", required = true)
        @NotNull
        private String speed;
    }


}
