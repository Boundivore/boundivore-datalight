package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.enumeration.impl.AlertHandlerTypeEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
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

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull(message = "集群 ID 不能为空")
        private Long clusterId;

        @Schema(name = "AlertRuleName", title = "告警配置名称", required = true)
        @JsonProperty(value = "AlertRuleName", required = true)
        private String alertRuleName;

        @Schema(name = "AlertRuleContent", title = "告警规则配置解析后的实体", required = true)
        @JsonProperty(value = "AlertRuleContent", required = true)
        @NotNull(message = "规则实体不能为空")
        private AlertRuleContentRequest alertRuleContent;
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

        @Schema(name = "Groups", title = "告警规则分组", required = true)
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

        @Schema(name = "Name", title = "告警规则分组名称", required = true)
        @JsonProperty(value = "Name")
        private String name;

        @Schema(name = "Rules", title = "告警规则", required = true)
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
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertRequest.AlertIdListRequest",
            description = "AbstractAlertRequest.AlertIdListRequest: 告警规则 ID 列表 请求体"
    )
    public static class AlertIdListRequest implements IRequest {
        private static final long serialVersionUID = -1194530752751114766L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId")
        @NotNull(message = "集群 ID 不能为空")
        private Long clusterId;

        @Schema(name = "AlertIdList", title = "告警 ID 列表", required = true)
        @JsonProperty(value = "AlertIdList")
        @NotEmpty(message = "操作列表不能为空")
        private List<Long> alertIdList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertRequest.AlertSwitchEnabledListRequest",
            description = "AbstractAlertRequest.AlertSwitchEnabledListRequest: 告警规则启用停用列表 请求体"
    )
    public static class AlertSwitchEnabledListRequest implements IRequest {

        private static final long serialVersionUID = -3298132549174214148L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId")
        @NotNull(message = "集群 ID 不能为空")
        private Long clusterId;

        @Schema(name = "AlertSwitchEnabledList", title = "告警规则启用停用列表", required = true)
        @JsonProperty(value = "AlertSwitchEnabledList")
        @NotEmpty(message = "操作列表不能为空")
        private List<AlertSwitchEnabledRequest> alertSwitchEnabledList;


    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertRequest.AlertSwitchEnabledRequest",
            description = "AbstractAlertRequest.AlertSwitchEnabledRequest: 告警规则启用停用 请求体"
    )
    public static class AlertSwitchEnabledRequest implements IRequest {
        private static final long serialVersionUID = 7547167603044591531L;

        @Schema(name = "Enabled", title = "是否启用", required = true)
        @JsonProperty(value = "Enabled")
        private Boolean enabled;

        @Schema(name = "AlertId", title = "告警 ID", required = true)
        @JsonProperty(value = "AlertId")
        private Long alertId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(
            name = "AbstractAlertRequest.UpdateAlertRuleRequest",
            description = "AbstractAlertRequest.UpdateAlertRuleRequest 更新告警规则 请求体"
    )
    public static class UpdateAlertRuleRequest implements IRequest {

        private static final long serialVersionUID = 3848878212398955891L;

        @Schema(name = "ClusterId", title = "集群 ID", required = true)
        @JsonProperty(value = "ClusterId", required = true)
        @NotNull(message = "集群 ID 不能为空")
        private Long clusterId;

        @Schema(name = "AlertRuleId", title = "告警配置 ID", required = true)
        @JsonProperty(value = "AlertRuleId", required = true)
        @NotNull(message = "告警配置 ID 不能为空")
        private Long alertRuleId;

        @Schema(name = "AlertRuleName", title = "告警配置名称", required = true)
        @JsonProperty(value = "AlertRuleName", required = true)
        @NotNull(message = "告警名称不能为空")
        private String alertRuleName;

        @Schema(name = "Enabled", title = "是否启用", required = true)
        @JsonProperty(value = "Enabled", required = true)
        @NotNull(message = "是否启用不能为空")
        private Boolean enabled;

        @Schema(name = "AlertRuleContent", title = "告警规则配置解析后的实体", required = true)
        @JsonProperty(value = "AlertRuleContent", required = true)
        @NotNull(message = "规则实体不能为空")
        private AlertRuleContentRequest alertRuleContent;
    }

}
