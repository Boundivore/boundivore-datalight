package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
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
 * Job 节点工作信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-02-27 02:42:10
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_node_job")
@ApiModel(value = "TDlNodeJob对象", description = "Job 节点工作信息表")
public class TDlNodeJob extends TBasePo<TDlNodeJob> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("同批任务唯一标识")
    @TableField("tag")
    private String tag;

    @ApiModelProperty("Job 名称")
    @TableField("node_job_name")
    private String nodeJobName;

    @ApiModelProperty("Job 状态 枚举值：见代码")
    @TableField("node_job_state")
    private ExecStateEnum nodeJobState;

    @ApiModelProperty("节点操作类型 枚举值：见代码")
    @TableField("node_action_type")
    private NodeActionTypeEnum nodeActionType;

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
