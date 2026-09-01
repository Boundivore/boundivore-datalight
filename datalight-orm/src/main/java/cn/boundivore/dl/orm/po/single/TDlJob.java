package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
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
 * Job 信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-02-27 11:32:07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_job")
@Schema(name = "TDlJob对象", description = "Job 信息表")
public class TDlJob extends TBasePo<TDlJob> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(description = "同批任务唯一标识")
    @TableField("tag")
    private String tag;

    @Schema(description = "Job 行为类型")
    @TableField("job_action_type")
    private ActionTypeEnum jobActionType;

    @Schema(description = "Job 名称")
    @TableField("job_name")
    private String jobName;

    @Schema(description = "Job 状态 枚举值：见代码")
    @TableField("job_state")
    private ExecStateEnum jobState;

    @Schema(description = "执行起始时间 毫秒时间戳")
    @TableField("start_time")
    private Long startTime;

    @Schema(description = "执行结束时间 毫秒时间戳")
    @TableField("end_time")
    private Long endTime;

    @Schema(description = "耗时 毫秒时间戳")
    @TableField("duration")
    private Long duration;


}
