package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "TDlWebState对象", description = "前端状态信息缓存表")
public class TDlWebState extends TBasePo<TDlWebState> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(description = "用户 ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "缓存键 Base64")
    @TableField("web_key")
    private String webKey;

    @Schema(description = "缓存值 Base64")
    @TableField("web_value")
    private String webValue;


}
