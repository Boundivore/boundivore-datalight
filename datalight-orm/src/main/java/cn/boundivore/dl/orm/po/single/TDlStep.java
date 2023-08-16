package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.StepTypeEnum;
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
 * Step 信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-09 11:37:19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_step")
@ApiModel(value = "TDlStep对象", description = "Step 信息表")
public class TDlStep extends TBasePo<TDlStep> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("同批任务唯一标识")
    @TableField("tag")
    private String tag;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("Job ID")
    @TableField("job_id")
    private Long jobId;

    @ApiModelProperty("Stage ID")
    @TableField("stage_id")
    private Long stageId;

    @ApiModelProperty("Task ID")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty("Step 名称")
    @TableField("step_name")
    private String stepName;

    @ApiModelProperty("Step 状态 枚举值：见代码")
    @TableField("step_state")
    private ExecStateEnum stepState;

    @ApiModelProperty("Step 类型 枚举值：见代码")
    @TableField("step_type")
    private StepTypeEnum stepType;

    @ApiModelProperty("执行起始时间 毫秒时间戳")
    @TableField("start_time")
    private Long startTime;

    @ApiModelProperty("执行结束时间 毫秒时间戳")
    @TableField("end_time")
    private Long endTime;

    @ApiModelProperty("耗时 毫秒时间戳")
    @TableField("duration")
    private Long duration;


}
