package cn.boundivore.dl.base.response.impl;

import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Description: 如果检查当前返回的响应体的列权限，当前类需要被继承
 * Created by: Boundivore
 * Creation time: 2024/4/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(
        name = "AbstractRuleDataColumnVo",
        description = "AbstractRuleDataColumnVo 数据列权限 抽象响应体"
)
public abstract class AbstractRuleDataColumnVo implements IVo {
    private static final long serialVersionUID = -6862338458911371160L;

    @ApiModelProperty(name = "RuleDataColumnDesensitizationList", value = "被列权限规则规律的字段列表")
    @JsonProperty(value = "RuleDataColumnDesensitizationList")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<RuleDataColumnDesensitization> ruleDataColumnDesensitizationList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(
            name = "AbstractRuleDataColumnVo.RuleDataColumnDesensitization",
            description = "AbstractRuleDataColumnVo.RuleDataColumnDesensitization 数据列权限 抽象响应体"
    )
    public static class RuleDataColumnDesensitization implements IVo {
        private static final long serialVersionUID = -7549302946090498756L;

        @ApiModelProperty(name = "FieldName", value = "被列权限规则规律的字段")
        @JsonProperty(value = "FieldName")
        private String fieldName;
    }
}
