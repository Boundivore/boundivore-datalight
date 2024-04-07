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
 * 角色绑定关系常量表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-07 02:50:46
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_role_user_relation_final")
@ApiModel(value = "TDlRoleUserRelationFinal对象", description = "角色绑定关系常量表")
public class TDlRoleUserRelationFinal extends TBasePo<TDlRoleUserRelationFinal> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("角色 ID")
    @TableField("role_id")
    private Long roleId;

    @ApiModelProperty("绑定的用户 ID")
    @TableField("user_id")
    private Long userId;


}
