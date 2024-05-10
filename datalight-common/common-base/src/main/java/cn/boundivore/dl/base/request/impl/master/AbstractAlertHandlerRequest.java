package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.enumeration.impl.AlertHandlerTypeEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: 告警处理方式相关请求体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/22
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractAlertHandlerRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertHandlerRequest.NewAlertHandlerInterfaceRequest",
            description = "AbstractAlertHandlerRequest.NewAlertHandlerInterfaceRequest: 新建告警接口处理方式 请求体"
    )
    public final static class NewAlertHandlerInterfaceRequest implements IRequest {

        private static final long serialVersionUID = -4330420182299664430L;

        @Schema(name = "InterfaceUri", title = "告警外部调用接口地址", required = true)
        @JsonProperty(value = "InterfaceUri", required = true)
        @NotNull(message = "接口地址不能为空")
        private String interfaceUri;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertHandlerRequest.NewAlertHandlerMailRequest",
            description = "AbstractAlertHandlerRequest.NewAlertHandlerMailRequest: 新建告警邮件处理方式 请求体"
    )
    public final static class NewAlertHandlerMailRequest implements IRequest {

        private static final long serialVersionUID = 1104669842717705459L;

        @Schema(name = "MailAccount", title = "接收告警邮箱", required = true)
        @JsonProperty(value = "MailAccount", required = true)
        @NotNull(message = "接收告警邮箱不能为空")
        private String mailAccount;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertHandlerRequest.UpdateAlertHandlerInterfaceRequest",
            description = "AbstractAlertHandlerRequest.UpdateAlertHandlerInterfaceRequest: 更新告警接口处理方式 请求体"
    )
    public final static class UpdateAlertHandlerInterfaceRequest implements IRequest {

        private static final long serialVersionUID = -4330420182299664430L;

        @Schema(name = "HandlerId", title = "处理方式 ID", required = true)
        @JsonProperty(value = "HandlerId", required = true)
        @NotNull(message = "处理方式 ID 不能为空")
        private Long handlerId;

        @Schema(name = "InterfaceUri", title = "告警外部调用接口地址", required = true)
        @JsonProperty(value = "InterfaceUri", required = true)
        @NotNull(message = "接口地址不能为空")
        private String interfaceUri;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertHandlerRequest.UpdateAlertHandlerMailRequest",
            description = "AbstractAlertHandlerRequest.UpdateAlertHandlerMailRequest: 更新告警邮件处理方式 请求体"
    )
    public final static class UpdateAlertHandlerMailRequest implements IRequest {

        private static final long serialVersionUID = 1104669842717705459L;

        @Schema(name = "HandlerId", title = "处理方式 ID", required = true)
        @JsonProperty(value = "HandlerId", required = true)
        @NotNull(message = "处理方式 ID 不能为空")
        private Long handlerId;

        @Schema(name = "MailAccount", title = "接收告警邮箱", required = true)
        @JsonProperty(value = "MailAccount", required = true)
        @NotNull(message = "接收告警邮箱不能为空")
        private String mailAccount;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertHandlerRequest.AlertHandlerRelationListRequest",
            description = "AbstractAlertHandlerRequest.AlertHandlerRelationListRequest: 告警与处理方式绑定关系列表 请求体"
    )
    public final static class AlertHandlerRelationListRequest implements IRequest {

        private static final long serialVersionUID = 1104669842717705459L;

        @Schema(name = "HandlerId", title = "处理方式 ID", required = true)
        @JsonProperty(value = "HandlerId", required = true)
        @NotEmpty(message = "绑定关系列表不能为空")
        private List<AlertHandlerRelationRequest> alertHandlerRelationList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertHandlerRequest.AlertHandlerRelationRequest",
            description = "AbstractAlertHandlerRequest.AlertHandlerRelationRequest: 告警与处理方式绑定关系 请求体"
    )
    public final static class AlertHandlerRelationRequest implements IRequest {

        private static final long serialVersionUID = 1104669842717705459L;

        @Schema(name = "HandlerId", title = "告警处理方式 ID", required = true)
        @JsonProperty(value = "HandlerId", required = true)
        @NotNull(message = "告警处理方式 ID 不能为空")
        private Long handlerId;

        @Schema(name = "AlertHandlerTypeEnum", title = "告警处理方式类型枚举", required = true)
        @JsonProperty(value = "AlertHandlerTypeEnum", required = true)
        @NotNull(message = "告警处理方式类型不能为空")
        private AlertHandlerTypeEnum alertHandlerTypeEnum;

        @Schema(name = "AlertId", title = "告警规则 ID", required = true)
        @JsonProperty(value = "AlertId", required = true)
        @NotNull(message = "告警规则 ID 不能为空")
        private Long alertId;

        @Schema(name = "IsBinding", title = "是否绑定", required = true)
        @JsonProperty(value = "IsBinding", required = true)
        @NotNull(message = "是否绑定标记不能为空")
        private Boolean isBinding;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertHandlerRequest.AlertHandlerIdTypeListRequest",
            description = "AbstractAlertHandlerRequest.AlertHandlerIdTypeListRequest: 告警处理方式列表 请求体"
    )
    public final static class AlertHandlerIdTypeListRequest implements IRequest {

        private static final long serialVersionUID = 8962944832002331696L;

        @Schema(name = "AlertHandlerIdTypeList", title = "告警处理方式列表", required = true)
        @JsonProperty(value = "AlertHandlerIdTypeList", required = true)
        @NotEmpty(message = "告警处理方式 ID 列表不能为空")
        private List<AlertHandlerIdTypeRequest> alertHandlerIdTypeList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertHandlerRequest.AlertHandlerIdTypeRequest",
            description = "AbstractAlertHandlerRequest.AlertHandlerIdTypeRequest: 告警处理方式 请求体"
    )
    public final static class AlertHandlerIdTypeRequest implements IRequest {

        private static final long serialVersionUID = 481731623663092215L;

        @Schema(name = "AlertHandlerType", title = "告警处理类型", required = true)
        @JsonProperty(value = "AlertHandlerType", required = true)
        @NotEmpty(message = "告警处理类型不能为空")
        private AlertHandlerTypeEnum alertHandlerTypeEnum;

        @Schema(name = "AlertHandlerIdList", title = "告警处理方式 ID 列表", required = true)
        @JsonProperty(value = "AlertHandlerIdList", required = true)
        @NotEmpty(message = "告警处理方式 ID 列表不能为空")
        private List<Long> alertHandlerIdList;

    }

}
