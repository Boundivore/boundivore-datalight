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
 * Job 工作日志信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-07-05 06:19:31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_job_log")
@ApiModel(value = "TDlJobLog对象", description = "Job 工作日志信息表")
public class TDlJobLog extends TBasePo<TDlJobLog> {

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

    @ApiModelProperty("Step ID")
    @TableField("step_id")
    private Long stepId;

    @ApiModelProperty("标准日志")
    @TableField("log_stdout")
    private String logStdout;

    @ApiModelProperty("错误日志")
    @TableField("log_errout")
    private String logErrout;

}
