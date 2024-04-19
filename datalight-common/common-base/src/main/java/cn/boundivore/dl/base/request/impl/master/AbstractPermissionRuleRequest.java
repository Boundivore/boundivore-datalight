package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.request.IRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
            name = "AbstractPermissionRuleRequest.PermissionRoleIdRequest",
            description = "AbstractPermissionRuleRequest.PermissionRoleIdRequest: 权限与角色 ID 信息 请求体"
    )
    public final static class PermissionRoleIdRequest implements IRequest {

        private static final long serialVersionUID = 3576061572492713349L;

        @Schema(name = "PermissionId", title = "权限 ID", required = true)
        @JsonProperty(value = "PermissionId", required = true)
        @NotNull(message = "权限 ID 不能为空")
        private Long permissionId;

        @Schema(name = "RoleId", title = "角色 ID", required = true)
        @JsonProperty(value = "RoleId", required = true)
        @NotNull(message = "角色 ID 不能为空")
        private Long roleId;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractPermissionRuleRequest.PermissionRoleIdListRequest",
            description = "AbstractPermissionRuleRequest.PermissionRoleIdListRequest: 权限与角色 ID 信息列表 请求体"
    )
    public final static class PermissionRoleIdListRequest implements IRequest {

        private static final long serialVersionUID = 2368545681642644579L;

        @Schema(name = "PermissionRoleIdList", title = "权限角色映射列表", required = true)
        @JsonProperty(value = "PermissionRoleIdList", required = true)
        @NotEmpty(message = "权限角色映射列表不能为空")
        private List<PermissionRoleIdRequest> permissionRoleIdList;


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

        @Schema(name = "PermissionId", title = "权限类目常量 ID", required = true)
        @JsonProperty(value = "PermissionId", required = true)
        @NotNull(message = "权限类目常量 ID 不能为空")
        private Long permissionId;


    }

}
