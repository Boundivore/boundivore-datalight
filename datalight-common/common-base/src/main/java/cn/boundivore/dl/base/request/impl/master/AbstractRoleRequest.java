package cn.boundivore.dl.base.request.impl.master;

import cn.boundivore.dl.base.enumeration.impl.RoleTypeEnum;
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
 * Description: 角色相关请求体
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2024/4/11
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public abstract class AbstractRoleRequest {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRoleRequest.NewRoleRequest",
            description = "AbstractRoleRequest.NewRoleRequest: 新建角色 请求体"
    )
    public final static class NewRoleRequest implements IRequest {

        private static final long serialVersionUID = -7752686548270223945L;

        @ApiModelProperty(name = "RoleName", value = "角色名称", required = true)
        @JsonProperty(value = "RoleName", required = true)
        @NotNull(message = "角色名称不能为空")
        private String roleName;

        @ApiModelProperty(name = "Enabled", value = "是否启用标记", required = true)
        @JsonProperty(value = "Enabled", required = true)
        @NotNull(message = "是否启用标记不能为空")
        private Boolean enabled;

        @ApiModelProperty(name = "RoleType", value = "角色类型", required = true)
        @JsonProperty(value = "RoleType", required = true)
        @NotNull(message = "角色类型不能为空")
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
            name = "AbstractRoleRequest.RoleIdListRequest",
            description = "AbstractRoleRequest.RoleIdListRequest: 角色 ID 列表 请求体"
    )
    public final static class RoleIdListRequest implements IRequest {

        private static final long serialVersionUID = 3152313825607294067L;

        @ApiModelProperty(name = "RoleIdList", value = "角色 ID 列表", required = true)
        @JsonProperty(value = "RoleIdList", required = true)
        @NotEmpty(message = "角色 ID 列表不能为空")
        private List<Long> roleIdList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRoleRequest.RoleUserIdRequest",
            description = "AbstractRoleRequest.RoleUserIdRequest: 角色与用户 ID 信息 请求体"
    )
    public final static class RoleUserIdRequest implements IRequest {

        private static final long serialVersionUID = -7519956203389684421L;

        @ApiModelProperty(name = "RoleId", value = "角色 ID", required = true)
        @JsonProperty(value = "RoleId", required = true)
        @NotNull(message = "角色 ID 不能为空")
        private Long roleId;

        @ApiModelProperty(name = "UserId", value = "用户 ID", required = true)
        @JsonProperty(value = "UserId", required = true)
        @NotNull(message = "用户 ID 不能为空")
        private Long userId;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Schema(
            name = "AbstractRoleRequest.RoleUserIdRequest",
            description = "AbstractRoleRequest.RoleUserIdRequest: 角色与用户 ID 信息列表 请求体"
    )
    public final static class RoleUserIdListRequest implements IRequest {

        private static final long serialVersionUID = -7519956203389684421L;

        @ApiModelProperty(name = "RoleUserList", value = "角色用户映射列表", required = true)
        @JsonProperty(value = "RoleUserList", required = true)
        @NotEmpty(message = "角色用户映射列表不能为空")
        private List<RoleUserIdRequest> roleUserList;


    }

}
