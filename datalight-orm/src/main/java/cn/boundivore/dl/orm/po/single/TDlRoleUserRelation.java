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
@Schema(name = "TDlRoleUserRelation对象", description = "角色绑定关系表")
public class TDlRoleUserRelation extends TBasePo<TDlRoleUserRelation> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色 ID")
    @TableField("role_id")
    private Long roleId;

    @Schema(description = "绑定的用户 ID")
    @TableField("user_id")
    private Long userId;


}
