package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.StepTypeEnum;
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
@Schema(name = "TDlStep对象", description = "Step 信息表")
public class TDlStep extends TBasePo<TDlStep> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "生成序号")
    @TableField("num")
    private Long num;

    @Schema(description = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(description = "同批任务唯一标识")
    @TableField("tag")
    private String tag;

    @Schema(description = "Job ID")
    @TableField("job_id")
    private Long jobId;

    @Schema(description = "Stage ID")
    @TableField("stage_id")
    private Long stageId;

    @Schema(description = "Task ID")
    @TableField("task_id")
    private Long taskId;

    @Schema(description = "Step 名称")
    @TableField("step_name")
    private String stepName;

    @Schema(description = "Step 状态 枚举值：见代码")
    @TableField("step_state")
    private ExecStateEnum stepState;

    @Schema(description = "Step 类型 枚举值：见代码")
    @TableField("step_type")
    private StepTypeEnum stepType;

    @Schema(description = "Jar 包名称")
    @TableField("jar")
    private String jar;

    @Schema(description = "class 名称")
    @TableField("clazz")
    private String clazz;

    @Schema(description = "method 名称")
    @TableField("method")
    private String method;

    @Schema(description = "脚本名称")
    @TableField("shell")
    private String shell;

    @Schema(description = "脚本参数")
    @TableField("args")
    private String args;

    @Schema(description = "交互参数")
    @TableField("interactions")
    private String interactions;

    @Schema(description = "期望退出码")
    @TableField("exits")
    private String exits;

    @Schema(description = "脚本超时时间 单位：毫秒")
    @TableField("timeout")
    private Long timeout;

    @Schema(description = "脚本睡眠时间 脚本执行后的等待时间，单位：毫秒")
    @TableField("sleep")
    private Long sleep;

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
