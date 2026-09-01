package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "TDlRuleInterface对象", description = "接口资源规则表")
public class TDlRuleInterface extends TBasePo<TDlRuleInterface> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "接口 URI 绝对路径")
    @TableField("rule_interface_uri")
    private String ruleInterfaceUri;

    @Schema(description = "接口 HTTP METHOD GET, POST")
    @TableField("rule_interface_method")
    private String ruleInterfaceMethod;

}
