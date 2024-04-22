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

import cn.boundivore.dl.base.enumeration.impl.AlertHandlerTypeEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * Description: 告警相关响应体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/17
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAlertVo {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertVo.AlertRuleVo",
            description = "AbstractAlertVo.AlertRuleVo 告警规则响应体"
    )
    public static class AlertRuleVo implements IVo {

        private static final long serialVersionUID = 110622383464524744L;

        @Schema(name = "AlertRuleId", title = "告警配置 ID", required = true)
        @JsonProperty(value = "AlertRuleId", required = true)
        private Long alertRuleId;

        @Schema(name = "AlertRuleName", title = "告警配置名称", required = true)
        @JsonProperty(value = "AlertRuleName", required = true)
        private String alertRuleName;

        @Schema(name = "AlertFilePath", title = "告警规则文件路径", required = true)
        @JsonProperty(value = "AlertFilePath", required = true)
        private String alertFilePath;

        @Schema(name = "AlertFileName", title = "告警规则文件名", required = true)
        @JsonProperty(value = "AlertFileName", required = true)
        private String alertFileName;

        @Schema(name = "AlertRuleContentBase64", title = "告警规则实际内容[Base64 格式]", required = true)
        @JsonProperty(value = "AlertRuleContentBase64", required = true)
        private String alertRuleContentBase64;

        @Schema(name = "Sha256", title = "文件指纹", required = true)
        @JsonProperty(value = "Sha256", required = true)
        private String sha256;

        @Schema(name = "AlertRuleContent", title = "告警规则配置解析后的实体", required = true)
        @JsonProperty(value = "AlertRuleContent", required = true)
        private AlertRuleContentVo alertRuleContent;

        @Schema(name = "Enabled", title = "是否启用", required = true)
        @JsonProperty(value = "Enabled", required = true)
        private Boolean enabled;

        @Schema(name = "AlertVersion", title = "当前规则文件版本号", required = true)
        @JsonProperty(value = "AlertVersion", required = true)
        private Long alertVersion;

        @Schema(name = "AlertHandlerTypeEnum", title = "告警触发后处理告警的操作类型", required = true)
        @JsonProperty(value = "AlertHandlerTypeEnum", required = true)
        private AlertHandlerTypeEnum alertHandlerTypeEnum;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertVo.AlertRuleContentVo",
            description = "AbstractAlertVo.AlertRuleContentVo: 新建告警规则内容实体 响应体"
    )
    public final static class AlertRuleContentVo implements IVo {
        private static final long serialVersionUID = -5838473919880577976L;

        @Schema(name = "Groups", title = "告警规则分组", required = true)
        @JsonProperty(value = "Groups", required = true)
        @NotEmpty(message = "告警规则分组不能为空")
        private List<AbstractAlertVo.GroupVo> groups;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertVo.GroupVo",
            description = "AbstractAlertVo.GroupVo: 告警规则实体中的分组 响应体"
    )
    public static class GroupVo implements IVo {
        private static final long serialVersionUID = -4561495605113571769L;

        @Schema(name = "Name", title = "告警规则分组名称", required = true)
        @JsonProperty(value = "Name")
        private String name;

        @Schema(name = "Rules", title = "告警规则", required = true)
        @JsonProperty(value = "Rules")
        private List<AbstractAlertVo.RuleVo> rules;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertVo.RuleVo",
            description = "AbstractAlertVo.RuleVo: 告警规则分组中的规则 响应体"
    )
    public static class RuleVo implements IVo {
        private static final long serialVersionUID = -1194530752751114766L;

        @Schema(name = "Alert", title = "告警规则名称", required = true)
        @JsonProperty(value = "Alert")
        private String alert;

        @Schema(name = "Expr", title = "告警规则判断表达式 [Base64 格式]", required = true)
        @JsonProperty(value = "Expr")
        private String expr;

        @Schema(name = "For", title = "满足 Expr 表达式多久后执行告警", required = true)
        @JsonProperty(value = "For")
        private String duration;

        @Schema(name = "Labels", title = "告警规则标签", required = true)
        @JsonProperty(value = "Labels")
        private Map<String, String> labels;

        @Schema(name = "Annotations", title = "告警规则注解", required = true)
        @JsonProperty(value = "Annotations")
        private Map<String, String> annotations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertVo.AlertSimpleListVo",
            description = "AbstractAlertVo.AlertSimpleListVo 告警概览列表响应体"
    )
    public static class AlertSimpleListVo implements IVo {

        private static final long serialVersionUID = -4638857133088137464L;

        @Schema(name = "AlertSimpleList", title = "告警信息概览列表", required = true)
        @JsonProperty(value = "AlertSimpleList", required = true)
        private List<AlertSimpleVo> alertSimpleList;


    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertVo.AlertSimpleVo",
            description = "AbstractAlertVo.AlertSimpleVo 告警概览信息响应体"
    )
    public static class AlertSimpleVo implements IVo {


        private static final long serialVersionUID = 8023120698837227077L;

        @Schema(name = "AlertRuleId", title = "告警配置 ID", required = true)
        @JsonProperty(value = "AlertRuleId", required = true)
        private Long alertRuleId;

        @Schema(name = "AlertRuleName", title = "告警配置名称", required = true)
        @JsonProperty(value = "AlertRuleName", required = true)
        private String alertRuleName;

        @Schema(name = "AlertFilePath", title = "告警配置文件路径", required = true)
        @JsonProperty(value = "AlertFilePath", required = true)
        private String alertFilePath;

        @Schema(name = "Enabled", title = "是否启用", required = true)
        @JsonProperty(value = "Enabled", required = true)
        private Boolean enabled;

        @Schema(name = "HandlerType", title = "告警触发后处理告警的操作类型", required = true)
        @JsonProperty(value = "HandlerType", required = true)
        private AlertHandlerTypeEnum handlerType;

    }


}
