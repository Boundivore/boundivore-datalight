package cn.boundivore.dl.base.response.impl.master;

import cn.boundivore.dl.base.enumeration.impl.PermissionTypeEnum;
import cn.boundivore.dl.base.response.IVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 权限模板相关响应体集合
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/9
 * Modification description:
 * Modified by:
 * Modification time:
 * Throws:
 */
public abstract class AbstractRolePermissionRuleTemplatedVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleTemplatedVo.PermissionTemplatedListVo",
            description = "AbstractRolePermissionRuleTemplatedVo.PermissionTemplatedListVo 权限模板详情列表 响应体"
    )
    public final static class PermissionTemplatedListVo implements IVo {
        private static final long serialVersionUID = -2032227646790245253L;

        @ApiModelProperty(required = true, name = "PermissionTemplatedList", value = "权限主体列表 响应体")
        @JsonProperty(value = "PermissionTemplatedList", required = true)
        private List<PermissionTemplatedVo> permissionTemplatedList;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleTemplatedVo.PermissionTemplatedListVo",
            description = "AbstractRolePermissionRuleTemplatedVo.PermissionTemplatedListVo 权限详情列表 响应体"
    )
    public final static class PermissionListTemplatedVo implements IVo {
        private static final long serialVersionUID = -2032227646790245253L;

        @ApiModelProperty(required = true, name = "PermissionTemplatedList", value = "权限主体列表 响应体")
        @JsonProperty(value = "PermissionTemplatedList", required = true)
        private List<PermissionTemplatedVo> permissionTemplatedList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleTemplatedVo.PermissionTemplatedVo",
            description = "AbstractRolePermissionRuleTemplatedVo.PermissionTemplatedVo 权限信息 响应体"
    )
    public final static class PermissionTemplatedVo implements IVo {
        private static final long serialVersionUID = -1954502674063728108L;

        @ApiModelProperty(required = true, name = "PermissionTemplatedId", value = "主键 ID")
        @JsonProperty(value = "PermissionTemplatedId", required = true)
        private Long permissionTemplatedId;

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
            name = "AbstractRolePermissionRuleTemplatedVo.PermissionRuleInterfaceTemplatedListVo",
            description = "AbstractRolePermissionRuleTemplatedVo.PermissionRuleInterfaceTemplatedListVo 接口权限规则信息列表 响应体"
    )
    public final static class PermissionRuleInterfaceListVo implements IVo {
        private static final long serialVersionUID = -3706117875208207796L;

        @ApiModelProperty(required = true, name = "PermissionRuleInterfaceList", value = "接口权限规则信息列表 响应体")
        @JsonProperty(value = "PermissionRuleInterfaceTemplatedList", required = true)
        private List<PermissionRuleInterfaceTemplatedVo> permissionRuleInterfaceTemplatedList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleTemplatedVo.PermissionRuleTemplatedListVo",
            description = "AbstractRolePermissionRuleTemplatedVo.PermissionRuleTemplatedListVo 权限权限详情列表 响应体"
    )
    public final static class PermissionRuleTemplatedListVo implements IVo {
        private static final long serialVersionUID = 68623053874861368L;

        @ApiModelProperty(required = true, name = "PermissionRuleInterfaceTemplatedList", value = "接口权限信息列表")
        @JsonProperty(value = "PermissionRuleInterfaceTemplatedList", required = true)
        private List<PermissionRuleInterfaceTemplatedVo> permissionRuleInterfaceTemplatedList = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleTemplatedVo.PermissionRuleInterfaceTemplatedVo",
            description = "AbstractRolePermissionRuleTemplatedVo.PermissionRuleInterfaceTemplatedVo 接口权限规则信息列表 响应体"
    )
    public final static class PermissionRuleInterfaceTemplatedVo implements IVo {
        private static final long serialVersionUID = 5866369726494529388L;

        @ApiModelProperty(required = true, name = "PermissionTemplated", value = "权限主体信息")
        @JsonProperty(value = "PermissionTemplated", required = true)
        private PermissionTemplatedVo permissionTemplatedVo;

        @ApiModelProperty(required = true, name = "RuleInterfaceTemplated", value = "权限规则信息")
        @JsonProperty(value = "RuleInterfaceTemplated", required = true)
        private RuleInterfaceTemplatedVo ruleInterfaceTemplated;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleTemplatedVo.PermissionRuleDetailsVo",
            description = "AbstractRolePermissionRuleTemplatedVo.PermissionRuleDetailsVo 权限权限详情 响应体"
    )
    public final static class PermissionRuleDetailsTemplatedVo implements IVo {
        private static final long serialVersionUID = 3594602803678213609L;

        @ApiModelProperty(required = true, name = "Permission", value = "权限主体信息")
        @JsonProperty(value = "Permission", required = true)
        private PermissionTemplatedVo permissionTemplatedVo;

        @ApiModelProperty(required = true, name = "RuleInterfaceTemplated", value = "权限规则信息(仅权限类型为 PERMISSION_INTERFACE 时不为空)")
        @JsonProperty(value = "RuleInterfaceTemplated", required = true)
        private RuleInterfaceTemplatedVo ruleInterfaceTemplated;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRolePermissionRuleTemplatedVo.RuleInterfaceTemplatedVo",
            description = "AbstractRolePermissionRuleTemplatedVo.RuleInterfaceTemplatedVo 权限接口规则 响应体"
    )
    @EqualsAndHashCode
    public final static class RuleInterfaceTemplatedVo implements IVo {
        private static final long serialVersionUID = -6011186614241048963L;

        @ApiModelProperty(required = true, name = "RuleInterfaceId", value = "接口规则主键 ID")
        @JsonProperty(value = "RuleInterfaceId", required = true)
        private Long ruleInterfaceId;

        @ApiModelProperty(required = true, name = "RuleInterfaceUri", value = "接口 URI 绝对路径")
        @JsonProperty(value = "RuleInterfaceUri", required = true)
        private String ruleInterfaceUri;
    }
}
