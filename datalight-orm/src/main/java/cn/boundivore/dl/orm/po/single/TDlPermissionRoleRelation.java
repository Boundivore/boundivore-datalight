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
 * 权限角色信息映射表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-07 02:50:46
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_permission_role_relation")
@Schema(name = "TDlPermissionRoleRelation对象", description = "权限角色信息映射表")
public class TDlPermissionRoleRelation extends TBasePo<TDlPermissionRoleRelation> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "权限 ID")
    @TableField("permission_id")
    private Long permissionId;

    @Schema(description = "角色 ID")
    @TableField("role_id")
    private Long roleId;


}
