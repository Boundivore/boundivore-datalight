package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStepTypeEnum;
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
 * Step 节点步骤信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-02-27 02:42:10
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_node_step")
@ApiModel(value = "TDlNodeStep对象", description = "Step 节点步骤信息表")
public class TDlNodeStep extends TBasePo<TDlNodeStep> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("生成序号")
    @TableField("num")
    private Long num;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("同批任务唯一标识")
    @TableField("tag")
    private String tag;

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

    @ApiModelProperty("执行脚本")
    @TableField("shell")
    private String shell;

    @ApiModelProperty("执行脚本参数")
    @TableField("args")
    private String args;

    @ApiModelProperty("执行脚本交互参数")
    @TableField("interactions")
    private String interactions;

    @ApiModelProperty("期望退出码")
    @TableField("exits")
    private String exits;

    @ApiModelProperty("超时时间 单位：毫秒")
    @TableField("timeout")
    private Long timeout;

    @ApiModelProperty("执行后等待时间 单位：毫秒")
    @TableField("sleep")
    private Long sleep;

    @ApiModelProperty("总待传输字节数")
    @TableField("total_bytes")
    private Long totalBytes;

    @ApiModelProperty("传输字节总进度")
    @TableField("total_progress")
    private Long totalProgress;

    @ApiModelProperty("已传输字节数")
    @TableField("total_transfer_bytes")
    private Long totalTransferBytes;

    @ApiModelProperty("总待传输文件数")
    @TableField("total_file_count")
    private Long totalFileCount;

    @ApiModelProperty("传输文件个数总进度")
    @TableField("total_file_count_progress")
    private Long totalFileCountProgress;

    @ApiModelProperty("已传输文件数")
    @TableField("total_transfer_file_count")
    private Long totalTransferFileCount;

    @ApiModelProperty("当前正在传输的文件名")
    @TableField("current_transfer_file_name")
    private String currentTransferFileName;

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
