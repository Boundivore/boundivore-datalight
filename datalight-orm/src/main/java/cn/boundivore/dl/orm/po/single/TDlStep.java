package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.StepTypeEnum;
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
 * Step 信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-02-27 11:32:07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_step")
@ApiModel(value = "TDlStep对象", description = "Step 信息表")
public class TDlStep extends TBasePo<TDlStep> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("同批任务唯一标识")
    @TableField("tag")
    private String tag;

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

    @ApiModelProperty("Jar 包名称")
    @TableField("jar")
    private String jar;

    @ApiModelProperty("class 名称")
    @TableField("clazz")
    private String clazz;

    @ApiModelProperty("脚本名称")
    @TableField("shell")
    private String shell;

    @ApiModelProperty("脚本参数")
    @TableField("args")
    private String args;

    @ApiModelProperty("交互参数")
    @TableField("interactions")
    private String interactions;

    @ApiModelProperty("期望退出码")
    @TableField("exits")
    private String exits;

    @ApiModelProperty("脚本超时时间 单位：毫秒")
    @TableField("timeout")
    private Long timeout;

    @ApiModelProperty("脚本睡眠时间 脚本执行后的等待时间，单位：毫秒")
    @TableField("sleep")
    private Long sleep;

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
