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
 * 角色信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-07 02:50:46
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_role")
@ApiModel(value = "TDlRole对象", description = "角色信息表")
public class TDlRole extends TBasePo<TDlRole> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("是否删除")
    @TableField("is_deleted")
    private Boolean isDeleted;

    @ApiModelProperty("是否允许编辑")
    @TableField("edit_enabled")
    private Boolean editEnabled;

    @ApiModelProperty("角色名称")
    @TableField("role_name")
    private String roleName;

    @ApiModelProperty("角色编码")
    @TableField("role_code")
    private String roleCode;

    @ApiModelProperty("角色分组 ID")
    @TableField("role_group_id")
    private Long roleGroupId;

    @ApiModelProperty("角色分组编码")
    @TableField("role_group_code")
    private String roleGroupCode;

    @ApiModelProperty("角色分组名称")
    @TableField("role_group_name")
    private String roleGroupName;

    @ApiModelProperty("角色启用或停用（0禁用，1启用） 默认值为 1")
    @TableField("enabled")
    private Boolean enabled;

    @ApiModelProperty("角色类型 （ROLE_DYNAMIC 自定义角色，ROLE_STATIC 静态默认自动生成的角色）")
    @TableField("role_type")
    private String roleType;

    @ApiModelProperty("角色备注")
    @TableField("role_comment")
    private String roleComment;


}
