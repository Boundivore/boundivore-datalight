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
 * 角色绑定关系表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-07 02:50:46
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_role_user_relation")
@ApiModel(value = "TDlRoleUserRelation对象", description = "角色绑定关系表")
public class TDlRoleUserRelation extends TBasePo<TDlRoleUserRelation> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("角色 ID")
    @TableField("role_id")
    private Long roleId;

    @ApiModelProperty("绑定的用户 ID")
    @TableField("user_id")
    private Long userId;


}
