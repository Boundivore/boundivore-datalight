package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
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
@Schema(name = "TDlNodeTask对象", description = "Task 节点任务信息表")
public class TDlNodeTask extends TBasePo<TDlNodeTask> {

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
    @TableField("node_job_id")
    private Long nodeJobId;

    @Schema(description = "节点 ID")
    @TableField("node_id")
    private Long nodeId;

    @Schema(description = "节点主机名")
    @TableField("hostname")
    private String hostname;

    @Schema(description = "IPV4 地址 内网地址")
    @TableField("node_ip")
    private String nodeIp;

    @Schema(description = "Task 名称")
    @TableField("node_task_name")
    private String nodeTaskName;

    @Schema(description = "Task 状态 枚举值：见代码")
    @TableField("node_task_state")
    private ExecStateEnum nodeTaskState;

    @Schema(description = "操作类型 枚举值：见代码")
    @TableField("node_action_type")
    private NodeActionTypeEnum nodeActionType;

    @Schema(description = "执行开始时节点状态 枚举值：见代码")
    @TableField("node_start_state")
    private NodeStateEnum nodeStartState;

    @Schema(description = "执行失败时节点状态 枚举值：见代码")
    @TableField("node_fail_state")
    private NodeStateEnum nodeFailState;

    @Schema(description = "执行成功时节点状态 枚举值：见代码")
    @TableField("node_success_state")
    private NodeStateEnum nodeSuccessState;

    @Schema(description = "执行前节点状态 枚举值：见代码")
    @TableField("node_current_state")
    private NodeStateEnum nodeCurrentState;

    @Schema(description = "是否滚动执行")
    @TableField("is_wait")
    private Boolean isWait;

    @Schema(description = "SSH 端口号")
    @TableField("ssh_port")
    private String sshPort;

    @Schema(description = "私钥文件路径")
    @TableField("private_key_path")
    private String privateKeyPath;

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
