package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.PermissionTypeEnum;
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
 * 权限信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-07 02:50:46
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_permission")
@Schema(name = "TDlPermission对象", description = "权限信息表")
public class TDlPermission extends TBasePo<TDlPermission> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "权限规则 ID")
    @TableField("rule_id")
    private Long ruleId;

    @Schema(description = "是否删除")
    @TableField("is_deleted")
    private Boolean isDeleted;

    @Schema(description = "是否生效")
    @TableField("enabled")
    private Boolean enabled;

    @Schema(description = "权限编码")
    @TableField("permission_code")
    private String permissionCode;

    @Schema(description = "权限名称")
    @TableField("permission_name")
    private String permissionName;

    @Schema(description = "权限类型 枚举：PERMISSION_INTERFACE(0, 接口操作权限),PERMISSION_DATA_ROW(1, 数据行读写权限),PERMISSION_DATA_COLUMN(2, 数据列读权限),PERMISSION_PAGE(3, 页面操作权限);")
    @TableField("permission_type")
    private PermissionTypeEnum permissionType;

    @Schema(description = "互斥权限编码")
    @TableField("reject_permission_code")
    private String rejectPermissionCode;

    @Schema(description = "权限权重 优先级，取值范围：1 ~ 10")
    @TableField("permission_weight")
    private Long permissionWeight;

    @Schema(description = "权限备注")
    @TableField("permission_comment")
    private String permissionComment;


}
