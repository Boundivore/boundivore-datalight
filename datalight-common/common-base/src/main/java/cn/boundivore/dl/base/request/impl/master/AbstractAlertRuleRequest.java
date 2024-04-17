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
public abstract class AbstractAlertRuleRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractAlertRuleRequest.NewAlertRuleRequest",
            description = "AbstractAlertRuleRequest.NewAlertRuleRequest: 新建告警规则 请求体"
    )
    public final static class NewAlertRuleRequest implements IRequest {

        private static final long serialVersionUID = 4209771485171669929L;

        @ApiModelProperty(name = "AlertName", value = "告警配置名称", required = true)
        @JsonProperty(value = "AlertName", required = true)
        private String alertName;

        @ApiModelProperty(name = "AlertRuleContentBase64", value = "告警配置内容(YAML 的 Base64 形式字符串)", required = true)
        @JsonProperty(value = "AlertRuleContentBase64", required = true)
        private String alertRuleContentBase64;

        @ApiModelProperty(name = "AlertHandlerTypeEnum", value = "告警触发时的处理类型", required = true)
        @JsonProperty(value = "AlertHandlerTypeEnum", required = true)
        @NotNull(message = "告警处理类型不能为空")
        private AlertHandlerTypeEnum alertHandlerTypeEnum;

        @ApiModelProperty(name = "HandlerId", value = "告警处理方式的配置 ID", required = true)
        @JsonProperty(value = "HandlerId", required = false)
        private Long handlerId;

    }
}
