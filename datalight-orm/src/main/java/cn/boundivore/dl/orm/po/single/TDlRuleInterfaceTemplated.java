package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 接口资源规则常量模板表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-07 02:50:46
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_rule_interface_templated")
@ApiModel(value = "TDlRuleInterfaceTemplated对象", description = "接口资源规则常量模板表")
public class TDlRuleInterfaceTemplated extends TBasePo<TDlRuleInterfaceTemplated> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("接口 URI 绝对路径")
    @TableField("rule_interface_uri")
    private String ruleInterfaceUri;

    @ApiModelProperty("接口 HTTP METHOD GET, POST")
    @TableField("rule_interface_method")
    private String ruleInterfaceMethod;

}
