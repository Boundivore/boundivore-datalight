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
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Description: 用户相关信息响应体 Vo
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

        @ApiModelProperty(name = "AlertRuleContent", value = "告警规则配置解析后的实体", required = true)
        @JsonProperty(value = "AlertRuleContent", required = true)
        private AlertRuleContentVo alertRuleContent;

        @Schema(name = "Enabled", title = "是否启用", required = true)
        @JsonProperty(value = "Enabled", required = true)
        private Boolean enabled;

        @Schema(name = "AlertVersion", title = "当前规则文件版本号", required = true)
        @JsonProperty(value = "AlertVersion", required = true)
        private Long alertVersion;

        @Schema(name = "HandlerType", title = "告警触发后处理告警的操作类型", required = true)
        @JsonProperty(value = "HandlerType", required = true)
        private AlertHandlerTypeEnum handlerType;

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

        @ApiModelProperty(name = "Groups", value = "告警规则分组", required = true)
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

        @ApiModelProperty(name = "Name", value = "告警规则分组名称", required = true)
        @JsonProperty(value = "Name")
        private String name;

        @ApiModelProperty(name = "Rules", value = "告警规则", required = true)
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

        @ApiModelProperty(name = "Alert", value = "告警规则名称", required = true)
        @JsonProperty(value = "Alert")
        private String alert;

        @ApiModelProperty(name = "Expr", value = "告警规则判断表达式 [Base64 格式]", required = true)
        @JsonProperty(value = "Expr")
        private String expr;

        @ApiModelProperty(name = "For", value = "满足 Expr 表达式多久后执行告警", required = true)
        @JsonProperty(value = "For")
        private String duration;

        @ApiModelProperty(name = "Labels", value = "告警规则标签", required = true)
        @JsonProperty(value = "Labels")
        private Map<String, String> labels;

        @ApiModelProperty(name = "Annotations", value = "告警规则注解", required = true)
        @JsonProperty(value = "Annotations")
        private Map<String, String> annotations;
    }


}
