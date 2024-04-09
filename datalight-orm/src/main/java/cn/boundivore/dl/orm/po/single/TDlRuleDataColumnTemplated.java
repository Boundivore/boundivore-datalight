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
 * 数据列资源规则模板表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-08 11:31:21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_rule_data_column_templated")
@ApiModel(value = "TDlRuleDataColumnTemplated对象", description = "数据列资源规则模板表")
public class TDlRuleDataColumnTemplated extends TBasePo<TDlRuleDataColumnTemplated> {

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

    @ApiModelProperty("列规则 1 允许访问，0 拒绝访问")
    @TableField("is_allow")
    private Boolean isAllow;


}
