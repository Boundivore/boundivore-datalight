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
 *
 * </p>
 *
 * @author Boundivore
 * @since 2023-04-17 04:46:30
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_login_event")
@Schema(name = "TDlLoginEvent对象", description = "")
public class TDlLoginEvent extends TBasePo<TDlLoginEvent> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "用户 ID")
    @TableField("user_id")
    private Long userId;

    @Schema(name = "最近一次登录时间")
    @TableField("last_login")
    private Long lastLogin;


}
