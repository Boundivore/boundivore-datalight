package cn.boundivore.dl.base.response.impl.master;

import cn.boundivore.dl.base.enumeration.impl.PermissionTypeEnum;
import cn.boundivore.dl.base.response.IVo;
import cn.boundivore.dl.base.response.impl.AbstractRuleDataColumnVo;
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

        @ApiModelProperty(required = true, name = "PermissionRuleDataRowList", value = "数据行权限信息列表")
        @JsonProperty(value = "PermissionRuleDataRowList", required = true)
        private List<PermissionRuleDataRowVo> permissionRuleDataRowList = new ArrayList<>();

        @ApiModelProperty(required = true, name = "PermissionRuleDataColumnList", value = "数据列权限信息列表")
        @JsonProperty(value = "PermissionRuleDataColumnList", required = true)
        private List<PermissionRuleDataColumnVo> permissionRuleDataColumnList = new ArrayList<>();
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

        @ApiModelProperty(required = true, name = "RuleDataRowList", value = "权限数据行规则信息列表(仅权限类型为 PERMISSION_DATA_ROW 时不为空)")
        @JsonProperty(value = "RuleDataRowList", required = true)
        private List<RuleDataRowVo> ruleDataRowList = new ArrayList<>();

        @ApiModelProperty(required = true, name = "RuleDataColumnList", value = "权限数据列规则信息列表(仅权限类型为 PERMISSION_DATA_COLUMN 时不为空)")
        @JsonProperty(value = "RuleDataColumnList", required = true)
        private List<RuleDataColumnVo> ruleDataColumnList = new ArrayList<>();

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleVo.PermissionListVo",
            description = "AbstractPermissionRuleVo.PermissionListVo 权限权限详情列表 响应体"
    )
    public final static class PermissionListVo extends AbstractRuleDataColumnVo {
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
            name = "AbstractPermissionRuleVo.PermissionRuleDataRowListVo",
            description = "AbstractPermissionRuleVo.PermissionRuleDataRowListVo 数据行权限规则信息列表 响应体"
    )
    public final static class PermissionRuleDataRowListVo implements IVo {
        private static final long serialVersionUID = -4058065069608911831L;

        @ApiModelProperty(required = true, name = "PermissionRuleDataRowList", value = "数据行权限规则信息列表 响应体")
        @JsonProperty(value = "PermissionRuleDataRowList", required = true)
        private List<PermissionRuleDataRowVo> permissionRuleDataRowList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleVo.PermissionRuleDataColumnListVo",
            description = "AbstractPermissionRuleVo.PermissionRuleDataColumnListVo 数据行列权限规则信息列表 响应体"
    )
    public final static class PermissionRuleDataColumnListVo implements IVo {
        private static final long serialVersionUID = 8454236536326081122L;

        @ApiModelProperty(required = true, name = "PermissionRuleDataColumnList", value = "数据行列权限规则信息列表 响应体")
        @JsonProperty(value = "PermissionRuleDataColumnList", required = true)
        private List<PermissionRuleDataColumnVo> permissionRuleDataColumnList;
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
            name = "AbstractPermissionRuleVo.PermissionRuleDataRowVo",
            description = "AbstractPermissionRuleVo.PermissionRuleDataRowVo 数据行权限规则信息 响应体"
    )
    public final static class PermissionRuleDataRowVo implements IVo {
        private static final long serialVersionUID = -7553975153958623991L;

        @ApiModelProperty(required = true, name = "Permission", value = "权限主体信息")
        @JsonProperty(value = "Permission", required = true)
        private PermissionVo permissionVo;

        @ApiModelProperty(required = true, name = "RuleDataRowList", value = "数据行权限规则信息列表")
        @JsonProperty(value = "RuleDataRowList", required = true)
        private List<RuleDataRowVo> ruleDataRowList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleVo.PermissionRuleDataColumnVo",
            description = "AbstractPermissionRuleVo.PermissionRuleDataColumnVo 数据列权限规则信息 响应体"
    )
    public final static class PermissionRuleDataColumnVo implements IVo {
        private static final long serialVersionUID = 5388173065393388785L;

        @ApiModelProperty(required = true, name = "Permission", value = "权限主体信息")
        @JsonProperty(value = "Permission", required = true)
        private PermissionVo permissionVo;

        @ApiModelProperty(required = true, name = "RuleDataColumnList", value = "数据列权限规则信息列表")
        @JsonProperty(value = "RuleDataColumnList", required = true)
        private List<RuleDataColumnVo> ruleDataColumnList;
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleVo.RuleInterfaceVo",
            description = "AbstractPermissionRuleVo.RuleInterfaceVo 权限数据行规则 响应体"
    )
    @EqualsAndHashCode
    public final static class RuleDataRowVo implements IVo {
        private static final long serialVersionUID = -337484444964226991L;

//        @ApiModelProperty(required = true, name = "RuleDataRowId", value = "数据行规则主键 ID")
//        @JsonProperty(value = "RuleDataRowId", required = true)
//        private Long ruleDataRowId;

        @ApiModelProperty(required = true, name = "DatabaseName", value = "数据库名")
        @JsonProperty(value = "DatabaseName", required = true)
        private String databaseName;

        @ApiModelProperty(required = true, name = "TableName", value = "表名")
        @JsonProperty(value = "TableName", required = true)
        private String tableName;

        @ApiModelProperty(required = true, name = "ColumnName", value = "列名")
        @JsonProperty(value = "ColumnName", required = true)
        private String columnName;

        @ApiModelProperty(required = true, name = "RuleCondition", value = "规则表达式 (EQ=, GT>, LT<, LE<=, GE>=, NE!=)")
        @JsonProperty(value = "RuleCondition", required = true)
        private String ruleCondition;

        @ApiModelProperty(required = true, name = "RuleConditionValue", value = "规则表达式对应值")
        @JsonProperty(value = "RuleConditionValue", required = true)
        private String ruleConditionValue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @EqualsAndHashCode
    @Schema(
            name = "AbstractPermissionRuleVo.RuleInterfaceVo",
            description = "AbstractPermissionRuleVo.RuleInterfaceVo 权限数据列规则 响应体"
    )
    public final static class RuleDataColumnVo implements IVo {
        private static final long serialVersionUID = 3782615655343164685L;

//        @ApiModelProperty(required = true, name = "RuleDataColumnId", value = "数据列规则主键 ID")
//        @JsonProperty(value = "RuleDataColumnId", required = true)
//        private Long ruleDataColumnId;

        @ApiModelProperty(required = true, name = "DatabaseName", value = "数据库名")
        @JsonProperty(value = "DatabaseName", required = true)
        private String databaseName;

        @ApiModelProperty(required = true, name = "TableName", value = "表名")
        @JsonProperty(value = "TableName", required = true)
        private String tableName;

        @ApiModelProperty(required = true, name = "ColumnName", value = "列名")
        @JsonProperty(value = "ColumnName", required = true)
        private String columnName;

        @ApiModelProperty(required = true, name = "IsAllow", value = "是否允许访问")
        @JsonProperty(value = "IsAllow", required = true)
        private Boolean isAllow;
    }
}
