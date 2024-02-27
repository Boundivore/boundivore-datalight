package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
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
 * Task 节点任务信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-02-27 02:42:10
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_node_task")
@ApiModel(value = "TDlNodeTask对象", description = "Task 节点任务信息表")
public class TDlNodeTask extends TBasePo<TDlNodeTask> {

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

    @ApiModelProperty("节点 ID")
    @TableField("node_id")
    private Long nodeId;

    @ApiModelProperty("节点主机名")
    @TableField("hostname")
    private String hostname;

    @ApiModelProperty("IPV4 地址 内网地址")
    @TableField("node_ip")
    private String nodeIp;

    @ApiModelProperty("Task 名称")
    @TableField("node_task_name")
    private String nodeTaskName;

    @ApiModelProperty("Task 状态 枚举值：见代码")
    @TableField("node_task_state")
    private ExecStateEnum nodeTaskState;

    @ApiModelProperty("操作类型 枚举值：见代码")
    @TableField("node_action_type")
    private NodeActionTypeEnum nodeActionType;

    @ApiModelProperty("执行开始时节点状态 枚举值：见代码")
    @TableField("node_start_state")
    private NodeStateEnum nodeStartState;

    @ApiModelProperty("执行失败时节点状态 枚举值：见代码")
    @TableField("node_fail_state")
    private NodeStateEnum nodeFailState;

    @ApiModelProperty("执行成功时节点状态 枚举值：见代码")
    @TableField("node_success_state")
    private NodeStateEnum nodeSuccessState;

    @ApiModelProperty("执行前节点状态 枚举值：见代码")
    @TableField("node_current_state")
    private NodeStateEnum nodeCurrentState;

    @ApiModelProperty("是否滚动执行")
    @TableField("is_wait")
    private Boolean isWait;

    @ApiModelProperty("SSH 端口号")
    @TableField("ssh_port")
    private String sshPort;

    @ApiModelProperty("私钥文件路径")
    @TableField("private_key_path")
    private String privateKeyPath;

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
