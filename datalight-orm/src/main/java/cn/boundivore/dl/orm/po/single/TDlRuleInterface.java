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
 * 接口资源规则表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-07 02:50:46
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_rule_interface")
@ApiModel(value = "TDlRuleInterface对象", description = "接口资源规则表")
public class TDlRuleInterface extends TBasePo<TDlRuleInterface> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("接口 URI 绝对路径")
    @TableField("rule_interface_uri")
    private String ruleInterfaceUri;

}
