package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "TDlUser对象", description = "用户基础信息表")
public class TDlUser extends TBasePo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户昵称")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty("真实姓名")
    @TableField("realname")
    private String realname;

    @ApiModelProperty("头像")
    @TableField("avatar")
    private String avatar;


}
