package cn.boundivore.dl.base.response.impl.master;

import cn.boundivore.dl.base.enumeration.impl.PermissionTypeEnum;
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
public abstract class AbstractPermissionRuleVo {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleVo.PermissionRuleListVo",
            description = "AbstractPermissionRuleVo.PermissionRuleListVo 权限权限详情列表 响应体"
    )
    public final static class PermissionRuleListVo implements IVo {
        private static final long serialVersionUID = 68623053874861368L;

        @ApiModelProperty(required = true, name = "PermissionRuleInterfaceList", value = "接口权限信息列表")
        @JsonProperty(value = "PermissionRuleInterfaceList", required = true)
        private List<PermissionRuleInterfaceVo> permissionRuleInterfaceList = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleVo.PermissionRuleDetailsVo",
            description = "AbstractPermissionRuleVo.PermissionRuleDetailsVo 权限权限详情 响应体"
    )
    public final static class PermissionRuleDetailsVo implements IVo {
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
            name = "AbstractPermissionRuleVo.PermissionListVo",
            description = "AbstractPermissionRuleVo.PermissionListVo 权限权限详情列表 响应体"
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
            name = "AbstractPermissionRuleVo.PermissionRuleInterfaceListVo",
            description = "AbstractPermissionRuleVo.PermissionRuleInterfaceListVo 接口权限规则信息列表 响应体"
    )
    public final static class PermissionRuleInterfaceListVo implements IVo {
        private static final long serialVersionUID = -3706117875208207796L;

        @ApiModelProperty(required = true, name = "PermissionRuleInterfaceList", value = "接口权限规则信息列表 响应体")
        @JsonProperty(value = "PermissionRuleInterfaceList", required = true)
        private List<PermissionRuleInterfaceVo> permissionRuleInterfaceList;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleVo.PermissionRuleInterfaceVo",
            description = "AbstractPermissionRuleVo.PermissionRuleInterfaceVo 接口权限规则信息列表 响应体"
    )
    public final static class PermissionRuleInterfaceVo implements IVo {
        private static final long serialVersionUID = 5866369726494529388L;

        @ApiModelProperty(required = true, name = "Permission", value = "权限主体信息")
        @JsonProperty(value = "Permission", required = true)
        private PermissionVo permissionVo;

        @ApiModelProperty(required = true, name = "RuleInterface", value = "权限规则信息")
        @JsonProperty(value = "RuleInterface", required = true)
        private RuleInterfaceVo ruleInterface;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleVo.PermissionVo",
            description = "AbstractPermissionRuleVo.PermissionVo 权限信息 响应体"
    )
    public final static class PermissionVo implements IVo {
        private static final long serialVersionUID = -1954502674063728108L;

        @ApiModelProperty(required = true, name = "PermissionId", value = "主键 ID")
        @JsonProperty(value = "PermissionId", required = true)
        private Long permissionId;

        @ApiModelProperty(required = true, name = "IsDeleted", value = "是否删除")
        @JsonProperty(value = "IsDeleted", required = true)
        private Boolean isDeleted;

        @ApiModelProperty(required = true, name = "Enabled", value = "是否生效")
        @JsonProperty(value = "Enabled", required = true)
        private Boolean enabled;

        @ApiModelProperty(name = "IsPublished", value = "是否已发布", required = true)
        @JsonProperty(value = "IsPublished", required = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean isPublished;

        @ApiModelProperty(required = true, name = "MerchantId", value = "商家 ID 数据的逻辑或物理隔离唯一识别码")
        @JsonProperty(value = "MerchantId", required = true)
        private Long merchantId;

        @ApiModelProperty(required = true, name = "StaticVersion", value = "静态文件版本 导入的 Excel 静态文件的版本，只有 Excel 版本大于当前数据库记录的版本，静态文件才会被导入或更新")
        @JsonProperty(value = "StaticVersion", required = true)
        private Long staticVersion;

        @ApiModelProperty(required = true, name = "PermissionCode", value = "权限编码")
        @JsonProperty(value = "PermissionCode", required = true)
        private String permissionCode;

        @ApiModelProperty(required = true, name = "PermissionName", value = "权限名称")
        @JsonProperty(value = "PermissionName", required = true)
        private String permissionName;

        @ApiModelProperty(required = true, name = "PermissionType", value = "权限类型 枚举：PERMISSION_INTERFACE(0, 接口操作权限),PERMISSION_DATA_ROW(1, 数据行读权限),PERMISSION_DATA_COLUMN(2, 数据列读权限),PERMISSION_PAGE(3, 页面操作权限);")
        @JsonProperty(value = "PermissionType", required = true)
        private PermissionTypeEnum permissionType;

        @ApiModelProperty(required = true, name = "IsStatic", value = "是否为静态权限")
        @JsonProperty(value = "IsStatic", required = true)
        private Boolean isStatic;

        @ApiModelProperty(required = true, name = "IsGlobal", value = "是否全局控制")
        @JsonProperty(value = "IsGlobal", required = true)
        private Boolean isGlobal;

        @ApiModelProperty(name = "RejectPermissionCode", value = "互斥权限编码", required = true)
        @JsonProperty(value = "RejectPermissionCode", required = true)
        private String rejectPermissionCode;

        @ApiModelProperty(required = true, name = "PermissionWeight", value = "权限权重 优先级，取值范围：1 ~ 10")
        @JsonProperty(value = "PermissionWeight", required = true)
        private Long permissionWeight;

        @ApiModelProperty(required = true, name = "PermissionComment", value = "权限备注")
        @JsonProperty(value = "PermissionComment", required = true)
        private String permissionComment;

        @ApiModelProperty(name = "CreateTime", value = "创建时间", required = true)
        @JsonProperty(value = "CreateTime", required = true)
        private Long createTime;

        @ApiModelProperty(name = "UpdateTime", value = "修改时间", required = true)
        @JsonProperty(value = "UpdateTime", required = true)
        private Long updateTime;


    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleVo.RuleInterfaceVo",
            description = "AbstractPermissionRuleVo.RuleInterfaceVo 权限接口规则 响应体"
    )
    @EqualsAndHashCode
    public final static class RuleInterfaceVo implements IVo {
        private static final long serialVersionUID = -6011186614241048963L;

//        @ApiModelProperty(required = true, name = "RuleInterfaceId", value = "接口规则主键 ID")
//        @JsonProperty(value = "RuleInterfaceId", required = true)
//        private Long ruleInterfaceId;

        @ApiModelProperty(required = true, name = "RuleInterfaceUri", value = "接口 URI 绝对路径")
        @JsonProperty(value = "RuleInterfaceUri", required = true)
        private String ruleInterfaceUri;
    }
}
