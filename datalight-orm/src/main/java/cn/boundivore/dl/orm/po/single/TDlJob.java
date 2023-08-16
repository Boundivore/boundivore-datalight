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
 * Job 信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-09 11:37:19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_job")
@ApiModel(value = "TDlJob对象", description = "Job 信息表")
public class TDlJob extends TBasePo<TDlJob> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("同批任务唯一标识")
    @TableField("tag")
    private String tag;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("Job 名称")
    @TableField("job_name")
    private String jobName;

    @ApiModelProperty("Job 状态 枚举值：见代码")
    @TableField("job_state")
    private ExecStateEnum jobState;

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
