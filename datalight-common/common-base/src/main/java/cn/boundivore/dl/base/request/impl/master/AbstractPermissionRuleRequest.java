package cn.boundivore.dl.base.request.impl.master;

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

        @ApiModelProperty(name = "PermissionInterfaceList", value = "接口权限列表", required = true)
        @JsonProperty(value = "PermissionInterfaceList", required = true)
        @Valid
        private List<NewPermissionInterfaceRequest> permissionInterfaceList = new ArrayList<>();

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
}
