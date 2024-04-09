package cn.boundivore.dl.base.request.impl.master;


import cn.boundivore.dl.base.enumeration.impl.RuleDataRowConditionEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: AbstractRuleDataRowRequest
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
        name = "AbstractRuleDataRowRequest",
        description = "AbstractRuleDataRowRequest 数据行权限列表 请求体"
)
public abstract class AbstractRuleDataRowRequest implements IRequest {

    private static final long serialVersionUID = -5804645169554834543L;

    @ApiModelProperty(required = true, name = "RuleDataRowList", value = "数据行权限列表")
    @JsonProperty(value = "RuleDataRowList", required = true)
    protected List<RuleDataRowRequest> ruleDataRowList;

    @JsonIgnore
    protected String lastSql = "";

    @Getter
    @Setter
    @Accessors(chain = true)
    @EqualsAndHashCode
    @Schema(
            name = "AbstractRuleDataRowRequest.RuleDataRowRequest",
            description = "AbstractRuleDataRowRequest.RuleDataRowRequest 数据行权限 请求体"
    )
    public static class RuleDataRowRequest implements IRequest {
        private static final long serialVersionUID = -2454443011889595171L;

        @ApiModelProperty(required = true, name = "DatabaseName", value = "数据库名称")
        @JsonProperty(value = "DatabaseName", required = true)
        @NotBlank(message = "数据库名称不能为空")
        protected String databaseName;

        @ApiModelProperty(required = true, name = "TableName", value = "表名")
        @JsonProperty(value = "TableName", required = true)
        @NotBlank(message = "表名不能为空")
        protected String tableName;

        @ApiModelProperty(required = true, name = "ColumnName", value = "列名")
        @JsonProperty(value = "ColumnName", required = true)
        @NotBlank(message = "列名不能为空")
        protected String columnName;

        @ApiModelProperty(required = true, name = "Condition", value = "规则表达式 (EQ=, GT>, LT<, LE<=, GE>=, NE!=)")
        @JsonProperty(value = "Condition", required = true)
        @NotNull(message = "规则表达式不能为空")
        protected RuleDataRowConditionEnum condition;

        @ApiModelProperty(required = true, name = "ConditionValue", value = "规则表达式对应值")
        @JsonProperty(value = "ConditionValue", required = true)
        @NotBlank(message = "规则表达式对应值不能为空")
        protected String conditionValue;
    }
}
