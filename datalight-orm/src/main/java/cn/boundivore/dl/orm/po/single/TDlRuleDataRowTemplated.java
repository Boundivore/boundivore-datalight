package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 数据行资源规则模板表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-08 11:31:21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_rule_data_row_templated")
@ApiModel(value = "TDlRuleDataRowTemplated对象", description = "数据行资源规则模板表")
public class TDlRuleDataRowTemplated extends TBasePo<TDlRuleDataRowTemplated> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("数据库名")
    @TableField("database_name")
    private String databaseName;

    @ApiModelProperty("表名")
    @TableField("table_name")
    private String tableName;

    @ApiModelProperty("列名")
    @TableField("column_name")
    private String columnName;

    @ApiModelProperty("规则表达式 =, >, <, <=, >=, <>")
    @TableField("rule_condition")
    private String ruleCondition;

    @ApiModelProperty("规则表达式对应值")
    @TableField("rule_condition_value")
    private String ruleConditionValue;


}
