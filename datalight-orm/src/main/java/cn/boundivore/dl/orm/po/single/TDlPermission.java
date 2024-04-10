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
@ApiModel(value = "TDlPermission对象", description = "权限信息表")
public class TDlPermission extends TBasePo<TDlPermission> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("是否删除")
    @TableField("is_deleted")
    private Boolean isDeleted;

    @ApiModelProperty("是否生效")
    @TableField("enabled")
    private Boolean enabled;

    @ApiModelProperty("静态文件版本 导入的 Excel 静态文件的版本，只有 Excel 版本大于当前数据库记录的版本，静态文件才会被导入或更新")
    @TableField("static_version")
    private Integer staticVersion;

    @ApiModelProperty("权限编码")
    @TableField("permission_code")
    private String permissionCode;

    @ApiModelProperty("权限名称")
    @TableField("permission_name")
    private String permissionName;

    @ApiModelProperty("权限类型 枚举：PERMISSION_INTERFACE(0, 接口操作权限),PERMISSION_DATA_ROW(1, 数据行读写权限),PERMISSION_DATA_COLUMN(2, 数据列读权限),PERMISSION_PAGE(3, 页面操作权限);")
    @TableField("permission_type")
    private String permissionType;

    @ApiModelProperty("是否为静态权限 1 为静态权限，0 为动态权限")
    @TableField("is_static")
    private Boolean isStatic;

    @ApiModelProperty("是否全局控制")
    @TableField("is_global")
    private Boolean isGlobal;

    @ApiModelProperty("互斥权限编码")
    @TableField("reject_permission_code")
    private String rejectPermissionCode;

    @ApiModelProperty("权限权重 优先级，取值范围：1 ~ 10")
    @TableField("permission_weight")
    private Long permissionWeight;

    @ApiModelProperty("权限备注")
    @TableField("permission_comment")
    private String permissionComment;


}
