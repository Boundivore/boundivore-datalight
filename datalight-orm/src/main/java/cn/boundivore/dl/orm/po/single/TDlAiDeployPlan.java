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
package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * AI 会话式部署计划表 服务实现类
 * </p>
 *
 * @author Boundivore
 * @since 2026-09-02 11:20:00
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_ai_deploy_plan")
@Schema(name = "TDlAiDeployPlan对象", description = "AI 会话式部署计划表")
public class TDlAiDeployPlan extends TBasePo<TDlAiDeployPlan> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "集群 ID 计划确认后创建出的集群")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(name = "集群名称")
    @TableField("cluster_name")
    private String clusterName;

    @Schema(name = "会话 ID 用于回溯这份计划是哪次对话产出的")
    @TableField("session_id")
    private String sessionId;

    @Schema(name = "提交人用户 ID")
    @TableField("user_id")
    private Long userId;

    @Schema(name = "计划状态 状态枚举，见代码")
    @TableField("plan_state")
    private String planState;

    @Schema(name = "计划正文 JSON 原样保留 AI 产出的完整计划，便于追溯与复盘")
    @TableField("plan_content")
    private String planContent;

    @Schema(name = "计划摘要 一句话说明拓扑为什么这样排")
    @TableField("description")
    private String description;
}
