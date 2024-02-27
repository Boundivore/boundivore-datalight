package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ActionTypeEnum;
import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
import cn.boundivore.dl.base.enumeration.impl.SCStateEnum;
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
 * Task 信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-02-27 11:32:07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_task")
@ApiModel(value = "TDlTask对象", description = "Task 信息表")
public class TDlTask extends TBasePo<TDlTask> {

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
    @TableField("job_id")
    private Long jobId;

    @ApiModelProperty("Stage ID")
    @TableField("stage_id")
    private Long stageId;

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
    @TableField("task_name")
    private String taskName;

    @ApiModelProperty("Task 状态 枚举值：见代码")
    @TableField("task_state")
    private ExecStateEnum taskState;

    @ApiModelProperty("操作类型 枚举值：见代码")
    @TableField("action_type")
    private ActionTypeEnum actionType;

    @ApiModelProperty("服务名称 全大写英文命名法，可以唯一 标识服务")
    @TableField("service_name")
    private String serviceName;

    @ApiModelProperty("组件名称 帕斯卡命名法，可以唯一组件")
    @TableField("component_name")
    private String componentName;

    @ApiModelProperty("当前组件状态")
    @TableField("current_state")
    private SCStateEnum currentState;

    @ApiModelProperty("执行时组件状态")
    @TableField("start_state")
    private SCStateEnum startState;

    @ApiModelProperty("失败时组件状态")
    @TableField("fail_state")
    private SCStateEnum failState;

    @ApiModelProperty("成功时组件状态")
    @TableField("success_state")
    private SCStateEnum successState;

    @ApiModelProperty("是否阻塞执行")
    @TableField("is_wait")
    private Boolean isWait;

    @ApiModelProperty("是否阻塞自身")
    @TableField("is_block")
    private Boolean isBlock;

    @ApiModelProperty("优先级")
    @TableField("priority")
    private Long priority;

    @ApiModelProperty("内存大小")
    @TableField("ram")
    private Long ram;

    @ApiModelProperty("是否第一次部署 是否所在节点第一次部署该服务")
    @TableField("is_first_deploy")
    private Boolean isFirstDeploy;

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
