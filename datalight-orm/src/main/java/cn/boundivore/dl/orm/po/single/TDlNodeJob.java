package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
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
 * Job 节点工作信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-29 05:35:20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_node_job")
@ApiModel(value = "TDlJobNode对象", description = "Job 节点工作信息表")
public class TDlNodeJob extends TBasePo<TDlNodeJob> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("同批任务唯一标识")
    @TableField("tag")
    private String tag;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("Job 名称")
    @TableField("node_job_name")
    private String nodeJobName;

    @ApiModelProperty("Job 状态 枚举值：见代码")
    @TableField("node_job_state")
    private ExecStateEnum nodeJobState;

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
