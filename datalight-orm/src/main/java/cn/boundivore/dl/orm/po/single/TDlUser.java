package cn.boundivore.dl.orm.po.single;

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
 * 用户基础信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-04-17 04:46:30
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_user")
@Schema(name = "TDlUser对象", description = "用户基础信息表")
public class TDlUser extends TBasePo<TDlUser> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "用户昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(name = "真实姓名")
    @TableField("realname")
    private String realname;

    @Schema(name = "头像")
    @TableField("avatar")
    private String avatar;


}
