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
 * 角色分组信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-07 05:41:32
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_role_group")
@ApiModel(value = "TDlRoleGroup对象", description = "角色分组信息表")
public class TDlRoleGroup extends TBasePo<TDlRoleGroup> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("是否删除")
    @TableField("is_deleted")
    private Boolean isDeleted;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("角色分组编码")
    @TableField("role_group_code")
    private String roleGroupCode;

    @ApiModelProperty("角色分组名称")
    @TableField("role_group_name")
    private String roleGroupName;

    @ApiModelProperty("角色分组备注")
    @TableField("role_group_comment")
    private String roleGroupComment;


}
