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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Description: AI 会话式部署计划相关 Vo 集合
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2026/9/2
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAiPlanVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAiPlanVo.PlanSubmitVo",
            description = "AbstractAiPlanVo.PlanSubmitVo 部署计划提交结果"
    )
    public static class PlanSubmitVo implements Serializable {

        private static final long serialVersionUID = 8471302884174471325L;

        @Schema(name = "ClusterId", title = "计划确认后创建出的集群 ID，页面据此跳转到部署流程")
        @JsonProperty(value = "ClusterId")
        private Long clusterId;

        @Schema(name = "PlanId", title = "计划 ID")
        @JsonProperty(value = "PlanId")
        private Long planId;
    }
}
