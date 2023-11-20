package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * Task 节点任务信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-29 05:35:20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_node_task")
@Schema(name = "TDlTaskNode对象", description = "Task 节点任务信息表")
public class TDlNodeTask extends TBasePo<TDlNodeTask> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(name = "同批任务唯一标识")
    @TableField("tag")
    private String tag;

    @Schema(name = "Job ID")
    @TableField("node_job_id")
    private Long nodeJobId;

    @Schema(name = "节点 ID")
    @TableField("node_id")
    private Long nodeId;

    @Schema(name = "节点主机名")
    @TableField("hostname")
    private String hostname;

    @Schema(name = "IPV4 地址 内网地址")
    @TableField("node_ip")
    private String nodeIp;

    @Schema(name = "Task 名称")
    @TableField("node_task_name")
    private String nodeTaskName;

    @Schema(name = "Task 状态 枚举值：见代码")
    @TableField("node_task_state")
    private ExecStateEnum nodeTaskState;

    @Schema(name = "操作类型 枚举值：见代码")
    @TableField("node_action_type")
    private NodeActionTypeEnum nodeActionType;

    @Schema(name = "执行起始时间 毫秒时间戳")
    @TableField("start_time")
    private Long startTime;

    @Schema(name = "执行结束时间 毫秒时间戳")
    @TableField("end_time")
    private Long endTime;

    @Schema(name = "耗时 毫秒时间戳")
    @TableField("duration")
    private Long duration;


}
