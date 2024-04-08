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
 * 权限角色信息映射常量模板表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-07 02:50:46
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_permission_role_relation_templated")
@ApiModel(value = "TDlPermissionRoleRelationTemplated对象", description = "权限角色信息映射常量模板表")
public class TDlPermissionRoleRelationTemplated extends TBasePo<TDlPermissionRoleRelationTemplated> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("权限 ID")
    @TableField("permission_id")
    private Long permissionId;

    @ApiModelProperty("角色 ID")
    @TableField("role_id")
    private Long roleId;


}
