package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.IdentityTypeEnum;
import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户认证信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-04-17 04:46:30
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_user_auth")
@Schema(name = "TDlUserAuth对象", description = "用户认证信息表")
public class TDlUserAuth extends TBasePo<TDlUserAuth> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "用户 ID 用户基础表主键 ID")
    @TableField("user_id")
    private Long userId;

    @Schema(name = "账户类型 枚举：EMAIL, PHONE, USERNAME")
    @TableField("identity_type")
    private IdentityTypeEnum identityType;

    @Schema(name = "认证主体 登录的账户名")
    @TableField("principal")
    private String principal;

    @Schema(name = "认证凭证 认证凭证，密码 或 Token")
    @TableField("credential")
    private String credential;


}
