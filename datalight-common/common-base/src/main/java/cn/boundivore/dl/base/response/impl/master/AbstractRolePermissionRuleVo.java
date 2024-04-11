package cn.boundivore.dl.base.response.impl.master;

import cn.boundivore.dl.base.enumeration.impl.PermissionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.RoleTypeEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 权限相关响应体集合
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
public abstract class AbstractRolePermissionRuleVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleVo.PermissionListVo",
            description = "AbstractRolePermissionRuleVo.PermissionListVo 权限详情列表 响应体"
    )
    public final static class PermissionListVo implements IVo {
        private static final long serialVersionUID = -2032227646790245253L;

        @ApiModelProperty(required = true, name = "PermissionList", value = "权限主体列表 响应体")
        @JsonProperty(value = "PermissionList", required = true)
        private List<PermissionVo> permissionList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleVo.PermissionVo",
            description = "AbstractRolePermissionRuleVo.PermissionVo 权限信息 响应体"
    )
    public final static class PermissionVo implements IVo {
        private static final long serialVersionUID = -1954502674063728108L;

        @ApiModelProperty(required = true, name = "PermissionId", value = "权限主键 ID")
        @JsonProperty(value = "PermissionId", required = true)
        private Long permissionId;

        @ApiModelProperty(required = true, name = "RuleId", value = "规则主键 ID")
        @JsonProperty(value = "RuleId", required = true)
        private Long ruleId;

        @ApiModelProperty(required = true, name = "IsDeleted", value = "是否删除")
        @JsonProperty(value = "IsDeleted", required = true)
        private Boolean isDeleted;

        @ApiModelProperty(required = true, name = "Enabled", value = "是否生效")
        @JsonProperty(value = "Enabled", required = true)
        private Boolean enabled;

        @ApiModelProperty(required = true, name = "PermissionCode", value = "权限编码")
        @JsonProperty(value = "PermissionCode", required = true)
        private String permissionCode;

        @ApiModelProperty(required = true, name = "PermissionName", value = "权限名称")
        @JsonProperty(value = "PermissionName", required = true)
        private String permissionName;

        @ApiModelProperty(required = true, name = "PermissionType", value = "权限类型 枚举：PERMISSION_INTERFACE(0, 接口操作权限),PERMISSION_DATA_ROW(1, 数据行读权限),PERMISSION_DATA_COLUMN(2, 数据列读权限),PERMISSION_PAGE(3, 页面操作权限);")
        @JsonProperty(value = "PermissionType", required = true)
        private PermissionTypeEnum permissionType;

        @ApiModelProperty(name = "RejectPermissionCode", value = "互斥权限编码", required = true)
        @JsonProperty(value = "RejectPermissionCode", required = true)
        private String rejectPermissionCode;

        @ApiModelProperty(required = true, name = "PermissionWeight", value = "权限权重 优先级，取值范围：1 ~ 10")
        @JsonProperty(value = "PermissionWeight", required = true)
        private Long permissionWeight;

        @ApiModelProperty(required = true, name = "PermissionComment", value = "权限备注")
        @JsonProperty(value = "PermissionComment", required = true)
        private String permissionComment;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleVo.PermissionRuleInterfaceListVo",
            description = "AbstractRolePermissionRuleVo.PermissionRuleInterfaceListVo 接口权限详情列表 响应体"
    )
    public final static class PermissionRuleInterfaceListVo implements IVo {
        private static final long serialVersionUID = 68623053874861368L;

        @ApiModelProperty(required = true, name = "PermissionRuleInterfaceDetailList", value = "接口权限信息列表")
        @JsonProperty(value = "PermissionRuleInterfaceDetailList", required = true)
        private List<PermissionRuleInterfaceDetailVo> permissionRuleInterfaceList = new ArrayList<>();
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleVo.PermissionRuleInterfaceDetailVo",
            description = "AbstractRolePermissionRuleVo.PermissionRuleInterfaceDetailVo 权限与接口规则详情 响应体"
    )
    public final static class PermissionRuleInterfaceDetailVo implements IVo {
        private static final long serialVersionUID = 3594602803678213609L;

        @ApiModelProperty(required = true, name = "Permission", value = "权限主体信息")
        @JsonProperty(value = "Permission", required = true)
        private PermissionVo permissionVo;

        @ApiModelProperty(required = true, name = "RuleInterface", value = "权限规则信息(仅权限类型为 PERMISSION_INTERFACE 时不为空)")
        @JsonProperty(value = "RuleInterface", required = true)
        private RuleInterfaceVo ruleInterface;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleVo.RuleInterfaceVo",
            description = "AbstractRolePermissionRuleVo.RuleInterfaceVo 接口规则 响应体"
    )
    @EqualsAndHashCode
    public final static class RuleInterfaceVo implements IVo {
        private static final long serialVersionUID = -6011186614241048963L;

        @ApiModelProperty(required = true, name = "RuleId", value = "规则主键 ID")
        @JsonProperty(value = "RuleId", required = true)
        private Long ruleId;

        @ApiModelProperty(required = true, name = "RuleInterfaceUri", value = "接口 URI 绝对路径")
        @JsonProperty(value = "RuleInterfaceUri", required = true)
        private String ruleInterfaceUri;

        @ApiModelProperty(required = true, name = "RuleInterfaceMethod", value = "Http 请求方式")
        @JsonProperty(value = "RuleInterfaceMethod", required = true)
        private String ruleInterfaceMethod;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleVo.RoleVo",
            description = "AbstractRolePermissionRuleVo.RoleVo 角色信息 响应体"
    )
    @EqualsAndHashCode
    public final static class RoleVo implements IVo {

        private static final long serialVersionUID = -5290148403914121307L;

        @ApiModelProperty(required = true, name = "RoleId", value = "角色 ID")
        @JsonProperty(value = "RoleId", required = true)
        private Long roleId;

        @ApiModelProperty(name = "EditEnabled", value = "是否可编辑", required = true)
        @JsonProperty(value = "EditEnabled", required = true)
        private Boolean editEnabled;

        @ApiModelProperty(name = "RoleName", value = "角色名称", required = true)
        @JsonProperty(value = "RoleName", required = true)
        private String roleName;

        @ApiModelProperty(name = "RoleCode", value = "角色编码", required = true)
        @JsonProperty(value = "RoleCode", required = true)
        private String roleCode;

        @ApiModelProperty(name = "Enabled", value = "是否启用标记", required = true)
        @JsonProperty(value = "Enabled", required = true)
        private Boolean enabled;

        @ApiModelProperty(name = "RoleType", value = "角色类型", required = true)
        @JsonProperty(value = "RoleType", required = true)
        private RoleTypeEnum roleType;

        @ApiModelProperty(name = "RoleComment", value = "角色备注", required = false)
        @JsonProperty(value = "RoleComment", required = false)
        private String roleComment;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleVo.RoleListVo",
            description = "AbstractRolePermissionRuleVo.RoleListVo 角色信息列表 响应体"
    )
    @EqualsAndHashCode
    public final static class RoleListVo implements IVo {

        private static final long serialVersionUID = 243959052209910553L;

        @ApiModelProperty(name = "RoleList", value = "角色信息列表", required = true)
        @JsonProperty(value = "RoleList", required = true)
        private List<RoleVo> roleList;

    }


}
