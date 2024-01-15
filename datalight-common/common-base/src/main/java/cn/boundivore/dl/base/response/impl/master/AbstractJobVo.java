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


import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.StepTypeEnum;
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

public abstract class AbstractJobVo {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractJobVo.JobIdVo",
            description = "AbstractJobVo.JobIdVo: JobId 信息"
    )
    public static class JobIdVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "JobId", title = "异步 Job Id", required = true)
        @JsonProperty(value = "JobId", required = true)
        @NotNull
        private Long jobId;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractJobVo.JobProgressVo",
            description = "AbstractJobVo.JobProgressVo: Job 计划进度信息"
    )
    public static class JobProgressVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "JobId", title = "异步 JobId", required = true)
        @JsonProperty(value = "JobId", required = true)
        @NotNull
        private Long JobId;

        @Schema(name = "JobPlanProgress", title = "Job 制定计划的进度", required = true)
        @JsonProperty(value = "JobPlanProgress", required = true)
        @NotNull
        private JobPlanProgressVo JobPlanProgressVo;

        @Schema(name = "JobExecProgress", title = "Job 执行的进度", required = true)
        @JsonProperty(value = "JobExecProgress", required = true)
        @NotNull
        private JobExecProgressVo JobExecProgressVo;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractJobVo.JobPlanProgressVo",
            description = "AbstractJobVo.JobPlanProgressVo: Job 计划进度信息"
    )
    public static class JobPlanProgressVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "JobId", title = "节点异步 JobId", required = true)
        @JsonProperty(value = "JobId", required = true)
        @NotNull
        private Long JobId;

        @Schema(name = "ActionType", title = "Job 操作动作类型", required = true)
        @JsonProperty(value = "ActionType", required = true)
        @NotNull
        private ActionTypeEnum actionTypeEnum;

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
            name = "AbstractJobVo.JobExecProgressVo",
            description = "AbstractJobVo.JobExecProgressVo: Job 执行进度信息"
    )
    public static class JobExecProgressVo implements IVo {

        @Schema(name = "JobExecStateEnum", title = "当前 Job 执行状态", required = true)
        @JsonProperty(value = "JobExecStateEnum", required = true)
        @NotNull
        private ExecStateEnum jobExecStateEnum;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "JobId", title = "节点异步 JobId", required = true)
        @JsonProperty(value = "JobId", required = true)
        @NotNull
        private Long JobId;

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
            name = "AbstractJobVo.ExecProgressPerNodeVo",
            description = "AbstractJobVo.ExecProgressPerNodeVo: Job 中每个节点的执行进度信息"
    )
    public static class ExecProgressPerNodeVo implements IVo {

        @Schema(name = "NodeId", title = "ID", required = true)
        @JsonProperty(value = "NodeId", required = true)
        @NotNull
        private Long nodeId;

        @Schema(name = "Hostname", title = "主机名", required = true)
        @JsonProperty(value = "Hostname", required = true)
        @NotNull
        private String hostname;

        @Schema(name = "NodeIp", title = "IP", required = true)
        @JsonProperty(value = "NodeIp", required = true)
        @NotNull
        private String nodeIp;

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
            name = "AbstractJobVo.ExecProgressStepVo",
            description = "AbstractJobVo.ExecProgressStepVo: Job 中每个节点的 Step 执行进度信息"
    )
    public static class ExecProgressStepVo implements IVo {

        @Schema(name = "StepType", title = "Step 类型", required = true)
        @JsonProperty(value = "StepType", required = true)
        @NotNull
        private StepTypeEnum stepTypeEnum;

        @Schema(name = "StepId", title = "Step ID", required = true)
        @JsonProperty(value = "StepId", required = true)
        @NotNull
        private Long stepId;

        @Schema(name = "StepName", title = "Step 名称", required = true)
        @JsonProperty(value = "StepName", required = true)
        @NotNull
        private String stepName;

        @Schema(name = "StepExecState", title = "Step 执行状态", required = true)
        @JsonProperty(value = "StepExecState", required = true)
        @NotNull
        private ExecStateEnum stepExecStateEnum;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractJobVo.JobLogListVo",
            description = "AbstractJobVo.JobLogListVo: Job 的日志信息列表"
    )
    public static class JobLogListVo implements IVo {

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull
        private Long clusterId;

        @Schema(name = "Tag", title = "同批任务唯一标识", required = true)
        @JsonProperty(value = "Tag", required = true)
        @NotNull
        private String tag;

        @Schema(name = "JobLogList", title = "作业日志列表", required = true)
        @JsonProperty(value = "JobLogList", required = true)
        @NotNull
        private List<JobLogVo> jobLogList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractJobVo.JobLogVo",
            description = "AbstractJobVo.JobLogVo: Job 的日志信息"
    )
    public static class JobLogVo implements IVo {

        @Schema(name = "JobId", title = "作业 ID", required = true)
        @JsonProperty(value = "JobId", required = true)
        @NotNull
        private Long jobId;

        @Schema(name = "StageId", title = "阶段 ID", required = true)
        @JsonProperty(value = "StageId", required = true)
        @NotNull
        private Long stageId;

        @Schema(name = "TaskId", title = "任务 ID", required = true)
        @JsonProperty(value = "TaskId", required = true)
        @NotNull
        private Long taskId;

        @Schema(name = "StepId", title = "步骤 ID", required = true)
        @JsonProperty(value = "StepId", required = true)
        @NotNull
        private Long stepId;

        @Schema(name = "LogStdOut", title = "标准日志", required = true)
        @JsonProperty(value = "LogStdOut", required = true)
        @NotNull
        private String logStdOut;

        @Schema(name = "LogErrOut", title = "错误日志", required = true)
        @JsonProperty(value = "LogErrOut", required = true)
        @NotNull
        private String logErrOut;

    }
}
