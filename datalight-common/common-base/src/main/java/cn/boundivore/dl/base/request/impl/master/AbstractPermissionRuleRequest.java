package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.enumeration.impl.RuleDataRowConditionEnum;
import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 权限相关请求体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/1/25
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractPermissionRuleRequest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleRequest.NewPermissionAndRuleRequest",
            description = "AbstractPermissionRuleRequest.NewPermissionAndRuleRequest: 新建、更新、删除批量权限信息 请求体"
    )
    public final static class NewPermissionAndRuleRequest implements IRequest {

        private static final long serialVersionUID = 5587915222636038824L;

        @ApiModelProperty(name = "RoleId", value = "角色 ID", required = true)
        @JsonProperty(value = "RoleId", required = true)
        private Long roleId;

        @ApiModelProperty(name = "IsClearInterface", value = "是否清空指定角色下所有接口权限", required = true)
        @JsonProperty(value = "IsClearInterface", required = true)
        private Boolean isClearInterface = false;

        @ApiModelProperty(name = "IsClearDataRow", value = "是否清空指定角色下所有数据行权限", required = true)
        @JsonProperty(value = "IsClearDataRow", required = true)
        private Boolean isClearDataRow = false;

        @ApiModelProperty(name = "IsClearDataColumn", value = "是否清空指定角色下所有数据列权限", required = true)
        @JsonProperty(value = "IsClearDataColumn", required = true)
        private Boolean isClearDataColumn = false;

        @ApiModelProperty(name = "PermissionInterfaceList", value = "接口权限列表", required = true)
        @JsonProperty(value = "PermissionInterfaceList", required = true)
        @Valid
        private List<NewPermissionInterfaceRequest> permissionInterfaceList = new ArrayList<>();

        @ApiModelProperty(name = "PermissionDataRowList", value = "数据行权限列表", required = true)
        @JsonProperty(value = "PermissionDataRowList", required = true)
        @Valid
        private List<NewPermissionDataRowRequest> permissionDataRowList = new ArrayList<>();

        @ApiModelProperty(name = "PermissionDataColumnList", value = "数据列权限列表", required = true)
        @JsonProperty(value = "PermissionDataColumnList", required = true)
        @Valid
        private List<NewPermissionDataColumnRequest> permissionDataColumnList = new ArrayList<>();

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleRequest.NewPermissionInterfaceRequest",
            description = "AbstractPermissionRuleRequest.NewPermissionInterfaceRequest: 新建接口权限信息 请求体"
    )
    public final static class NewPermissionInterfaceRequest implements IRequest {

        private static final long serialVersionUID = 6616348577672629228L;

        @ApiModelProperty(name = "PermissionFinalId", value = "权限类目常量 ID", required = true)
        @JsonProperty(value = "PermissionFinalId", required = true)
        @NotNull(message = "权限类目常量 ID 不能为空")
        private Long permissionFinalId;

        @ApiModelProperty(required = true, name = "NewRuleInterface", value = "权限接口规则")
        @JsonProperty(required = true, value = "NewRuleInterface")
        @NotNull(message = "权限接口规则")
        @Valid
        private NewRuleInterfaceRequest newRuleInterface;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleRequest.NewPermissionDataRowRequest",
            description = "AbstractPermissionRuleRequest.NewPermissionDataRowRequest: 新建数据行权限信息 请求体"
    )
    public final static class NewPermissionDataRowRequest implements IRequest {

        private static final long serialVersionUID = 1207049110943994771L;

        @ApiModelProperty(name = "PermissionFinalId", value = "权限类目常量 ID", required = true)
        @JsonProperty(value = "PermissionFinalId", required = true)
        @NotNull(message = "权限类目常量 ID 不能为空")
        private Long permissionFinalId;

        @ApiModelProperty(required = true, name = "NewRuleDataRowList", value = "权限数据行规则列表")
        @JsonProperty(required = true, value = "NewRuleDataRowList")
        @NotEmpty(message = "权限数据行规则列表不能为空")
        @Valid
        private List<NewRuleDataRowRequest> newRuleDataRowList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleRequest.NewPermissionDataColumnRequest",
            description = "AbstractPermissionRuleRequest.NewPermissionDataColumnRequest: 新建数据列权限信息 请求体"
    )
    public final static class NewPermissionDataColumnRequest implements IRequest {

        private static final long serialVersionUID = -3913677829463036436L;

        @ApiModelProperty(name = "PermissionFinalId", value = "权限类目常量 ID", required = true)
        @JsonProperty(value = "PermissionFinalId", required = true)
        @NotNull(message = "权限类目常量 ID 不能为空")
        private Long permissionFinalId;

        @ApiModelProperty(required = true, name = "NewRuleDataColumnList", value = "权限数据列规则列表")
        @JsonProperty(required = true, value = "NewRuleDataColumnList")
        @NotEmpty(message = "权限数据列规则列表不能为空")
        @Valid
        private List<NewRuleDataColumnRequest> newRuleDataColumnList;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleRequest.NewRuleInterfaceRequest",
            description = "AbstractPermissionRuleRequest.NewRuleInterfaceRequest: 新建权限接口规则 请求体"
    )
    public final static class NewRuleInterfaceRequest implements IRequest {

        private static final long serialVersionUID = -8485629232571382032L;

        @ApiModelProperty(name = "RuleInterfaceUri", value = "权限接口规则接口地址", required = true)
        @JsonProperty(value = "RuleInterfaceUri", required = true)
        @NotBlank(message = "权限接口规则接口地址不能为空")
        private String ruleInterfaceUri;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleRequest.NewRuleDataRowRequest",
            description = "AbstractPermissionRuleRequest.NewRuleDataRowRequest: 新建权限数据行规则 请求体"
    )
    public final static class NewRuleDataRowRequest implements IRequest {

        private static final long serialVersionUID = 6419579957141578327L;

        @ApiModelProperty(name = "DatabaseName", value = "数据库名", required = true)
        @JsonProperty(value = "DatabaseName", required = true)
        @NotBlank(message = "数据库名不能为空")
        private String databaseName;

        @ApiModelProperty(name = "TableName", value = "表名", required = true)
        @JsonProperty(value = "TableName", required = true)
        @NotBlank(message = "数据表名不能为空")
        private String tableName;

        @ApiModelProperty(name = "ColumnName", value = "列名", required = true)
        @JsonProperty(value = "ColumnName", required = true)
        @NotBlank(message = "数据列名不能为空")
        private String columnName;

        @ApiModelProperty(name = "RuleCondition", value = "数据行规则表达式", required = true)
        @JsonProperty(value = "RuleCondition", required = true)
        @NotNull(message = "规则表达式不能为空")
        private RuleDataRowConditionEnum ruleCondition;

        @ApiModelProperty(name = "RuleConditionValue", value = "数据行规则表达式对应值", required = true)
        @JsonProperty(value = "RuleConditionValue", required = true)
        @NotBlank(message = "规则表达式对应值不能为空")
        private String ruleConditionValue;

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleRequest.NewRuleDataColumnRequest",
            description = "AbstractPermissionRuleRequest.NewRuleDataColumnRequest: 新建权限数据列规则 请求体"
    )
    public final static class NewRuleDataColumnRequest implements IRequest {

        private static final long serialVersionUID = -1972113350151951308L;

        @ApiModelProperty(name = "DatabaseName", value = "数据库名", required = true)
        @JsonProperty(value = "DatabaseName", required = true)
        @NotBlank(message = "数据库名不能为空")
        private String databaseName;

        @ApiModelProperty(name = "TableName", value = "表名", required = true)
        @JsonProperty(value = "TableName", required = true)
        @NotBlank(message = "数据表名不能为空")
        private String tableName;

        @ApiModelProperty(name = "ColumnName", value = "列名", required = true)
        @JsonProperty(value = "ColumnName", required = true)
        @NotBlank(message = "数据列名不能为空")
        private String columnName;

    }
}
