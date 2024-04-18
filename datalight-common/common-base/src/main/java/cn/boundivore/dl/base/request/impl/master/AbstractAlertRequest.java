package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.enumeration.impl.AlertHandlerTypeEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Description: 告警相关请求体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/17
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAlertRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertRequest.NewAlertRuleRequest",
            description = "AbstractAlertRequest.NewAlertRuleRequest: 新建告警规则 请求体"
    )
    public final static class NewAlertRuleRequest implements IRequest {

        private static final long serialVersionUID = 4209771485171669929L;

        @ApiModelProperty(name = "ClusterId", value = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull(message = "集群 ID 不能为空")
        private Long clusterId;

        @ApiModelProperty(name = "AlertRuleName", value = "告警配置名称", required = true)
        @JsonProperty(value = "AlertRuleName", required = true)
        private String alertRuleName;

        @ApiModelProperty(name = "AlertRuleContent", value = "告警规则配置解析后的实体", required = true)
        @JsonProperty(value = "AlertRuleContent", required = true)
        @NotNull(message = "规则实体不能为空")
        private AlertRuleContentRequest alertRuleContent;

        @ApiModelProperty(name = "AlertHandlerTypeEnum", value = "告警触发时的处理类型", required = true)
        @JsonProperty(value = "AlertHandlerTypeEnum", required = true)
        @NotNull(message = "告警处理类型不能为空")
        private AlertHandlerTypeEnum alertHandlerTypeEnum;

        @ApiModelProperty(name = "HandlerId", value = "告警处理方式的配置 ID", required = true)
        @JsonProperty(value = "HandlerId", required = false)
        private Long handlerId;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertRequest.AlertRuleContentRequest",
            description = "AbstractAlertRequest.AlertRuleContentRequest: 新建告警规则内容实体 请求体"
    )
    public final static class AlertRuleContentRequest implements IRequest {
        private static final long serialVersionUID = -5838473919880577976L;

        @ApiModelProperty(name = "Groups", value = "告警规则分组", required = true)
        @JsonProperty(value = "Groups", required = true)
        @NotEmpty(message = "告警规则分组不能为空")
        private List<GroupRequest> groups;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertRequest.GroupRequest",
            description = "AbstractAlertRequest.GroupRequest: 告警规则实体中的分组 请求体"
    )
    public static class GroupRequest implements IRequest {
        private static final long serialVersionUID = -4561495605113571769L;

        @ApiModelProperty(name = "Name", value = "告警规则分组名称", required = true)
        @JsonProperty(value = "Name")
        private String name;

        @ApiModelProperty(name = "Rules", value = "告警规则", required = true)
        @JsonProperty(value = "Rules")
        private List<RuleRequest> rules;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertRequest.RuleRequest",
            description = "AbstractAlertRequest.RuleRequest: 告警规则分组中的规则 请求体"
    )
    public static class RuleRequest implements IRequest {
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
