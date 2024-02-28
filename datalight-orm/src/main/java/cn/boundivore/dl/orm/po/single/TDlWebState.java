package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 前端状态信息缓存表
 * </p>
 *
 * @author Boundivore
 * @since 2024-02-28 05:17:41
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_web_state")
@ApiModel(value = "TDlWebState对象", description = "前端状态信息缓存表")
public class TDlWebState extends TBasePo<TDlWebState> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("用户 ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("缓存键 Base64")
    @TableField("web_key")
    private String webKey;

    @ApiModelProperty("缓存值 Base64")
    @TableField("web_value")
    private String webValue;


}
