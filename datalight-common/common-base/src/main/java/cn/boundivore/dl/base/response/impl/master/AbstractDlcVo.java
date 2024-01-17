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


import cn.boundivore.dl.base.enumeration.impl.ServiceTypeEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


public abstract class AbstractDlcVo {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractDlcVo.DlcServiceVo",
            description = "AbstractDlcVo.DlcServiceVo 服务列表信息"
    )
    public final static class DlcServiceVo implements IVo {

        @Schema(name = "DlcVersion", title = "服务组件包版本", required = true)
        @JsonProperty(value = "DlcVersion", required = true)
        private String dlcVersion;

        @Schema(name = "DlcServiceSummaryList", title = "服务概览列表", required = true)
        @JsonProperty(value = "DlcServiceSummaryList", required = true)
        private List<DlcServiceSummaryVo> dlcServiceSummaryList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractDlcVo.DlcServiceSummaryVo",
            description = "AbstractDlcVo.DlcServiceSummaryVo 服务信息"
    )
    public final static class DlcServiceSummaryVo implements IVo {

        @Schema(name = "ServiceName", title = "服务名称", required = true)
        @JsonProperty(value = "ServiceName", required = true)
        private String serviceName;

        @Schema(name = "ServiceType", title = "服务类型", required = true)
        @JsonProperty(value = "ServiceType", required = true)
        private ServiceTypeEnum serviceTypeEnum;

        @Schema(name = "Priority", title = "服务部署优先级", required = true)
        @JsonProperty(value = "Priority", required = true)
        private Long priority;

        @Schema(name = "Desc", title = "服务描述", required = true)
        @JsonProperty(value = "Desc", required = true)
        private String desc;

        @Schema(name = "Tgz", title = "服务安装包名", required = true)
        @JsonProperty(value = "Tgz", required = true)
        private String tgz;

        @Schema(name = "Version", title = "服务版本", required = true)
        @JsonProperty(value = "Version", required = true)
        private String version;

        @Schema(name = "DependencyList", title = "需要依赖的服务列表", required = true)
        @JsonProperty(value = "DependencyList", required = true)
        private List<String> dependencyList;

        @Schema(name = "RelativeList", title = "受当前服务影响的其他服务", required = true)
        @JsonProperty(value = "RelativeList", required = true)
        private List<String> relativeList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractDlcVo.DlcComponentVo",
            description = "AbstractDlcVo.DlcComponentVo 服务与组件详细信息列表"
    )
    public final static class DlcComponentVo implements IVo {

        @Schema(name = "DlcVersion", title = "服务组件包版本", required = true)
        @JsonProperty(value = "DlcVersion", required = true)
        private String dlcVersion;

        @Schema(name = "DlcServiceComponentSummaryList", title = "服务列表", required = true)
        @JsonProperty(value = "DlcServiceComponentSummaryList", required = true)
        private List<DlcServiceComponentSummaryVo> dlcServiceComponentSummaryList;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractDlcVo.DlcServiceComponentSummaryVo",
            description = "AbstractDlcVo.DlcServiceComponentSummaryVo 服务组件信息"
    )
    public final static class DlcServiceComponentSummaryVo implements IVo {

        @Schema(name = "DlcServiceSummary", title = "服务名称", required = true)
        @JsonProperty(value = "DlcServiceSummary", required = true)
        private DlcServiceSummaryVo dlcServiceSummaryVo;

        @Schema(name = "DlcComponentSummaryList", title = "组件概览列表", required = true)
        @JsonProperty(value = "DlcComponentSummaryList", required = true)
        private List<DlcComponentSummaryVo> dlcComponentSummaryList;


    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractDlcVo.DlcComponentSummaryVo",
            description = "AbstractDlcVo.DlcComponentSummaryVo 组件信息"
    )
    public final static class DlcComponentSummaryVo implements IVo {

        @Schema(name = "ComponentName", title = "组件名称", required = true)
        @JsonProperty(value = "ComponentName", required = true)
        private String componentName;

        @Schema(name = "Priority", title = "优先级", required = true)
        @JsonProperty(value = "Priority", required = true)
        private long priority;

        @Schema(name = "Max", title = "最大安装数量,-1为不限制", required = true)
        @JsonProperty(value = "Max", required = true)
        private long max;

        @Schema(name = "Min", title = "最小安装数量", required = true)
        @JsonProperty(value = "Min", required = true)
        private long min;

        @Schema(name = "MutexesList", title = "与当前组件互斥的组件名列表（即不允许出现在同一节点）", required = true)
        @JsonProperty("MutexesList")
        private List<String> mutexesList;


    }

}
