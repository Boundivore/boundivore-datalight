package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.AutoPullSwitchTypeEnum;
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
 * 进程自动拉起状态表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-01 11:31:39
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_auto_pull_switch")
@Schema(name = "TDlAutoPullSwitch对象", description = "进程自动拉起状态表")
public class TDlAutoPullSwitch extends TBasePo<TDlAutoPullSwitch> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(description = "进程自动拉起开关类型 枚举，AUTO_PULL_WORKER：Worker 进程自动拉起开关类型；AUTO_PULL_COMPONENT：Component 进程自动拉起开关类型")
    @TableField("auto_pull_switch_type")
    private AutoPullSwitchTypeEnum autoPullSwitchType;

    @Schema(description = "开关状态 开启或关闭，0：关闭，1：开启")
    @TableField("off_on")
    private Boolean offOn;

    @Schema(description = "关闭起始时间 关闭自动拉起开关的起始时间")
    @TableField("close_begin_time")
    private Long closeBeginTime;

    @Schema(description = "关闭结束时间 关闭自动拉起开关的结束时间")
    @TableField("close_end_time")
    private Long closeEndTime;


}
