package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStepTypeEnum;
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
 * Step 节点步骤信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-29 05:35:20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_node_step")
@ApiModel(value = "TDlStepNode对象", description = "Step 节点步骤信息表")
public class TDlNodeStep extends TBasePo<TDlNodeStep> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("同批任务唯一标识")
    @TableField("tag")
    private String tag;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("Job ID")
    @TableField("node_job_id")
    private Long nodeJobId;

    @ApiModelProperty("Task ID")
    @TableField("node_task_id")
    private Long nodeTaskId;

    @ApiModelProperty("Step 名称")
    @TableField("node_step_name")
    private String nodeStepName;

    @ApiModelProperty("Step 状态 枚举值：见代码")
    @TableField("node_step_state")
    private ExecStateEnum nodeStepState;

    @ApiModelProperty("Step 类型 枚举值：见代码")
    @TableField("node_step_type")
    private NodeStepTypeEnum nodeStepType;

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
