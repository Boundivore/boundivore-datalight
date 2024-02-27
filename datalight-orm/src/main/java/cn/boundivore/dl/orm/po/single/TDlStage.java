package cn.boundivore.dl.orm.po.single;

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
 * Stage 信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-02-27 11:32:07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_stage")
@ApiModel(value = "TDlStage对象", description = "Stage 信息表")
public class TDlStage extends TBasePo<TDlStage> {

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

    @ApiModelProperty("Stage 名称")
    @TableField("stage_name")
    private String stageName;

    @ApiModelProperty("Stage 状态 枚举值：见代码")
    @TableField("stage_state")
    private ExecStateEnum stageState;

    @ApiModelProperty("服务名称 全大写英文命名法，可以唯一 标识服务")
    @TableField("service_name")
    private String serviceName;

    @ApiModelProperty("服务当前状态 枚举值：见代码")
    @TableField("service_state")
    private SCStateEnum serviceState;

    @ApiModelProperty("优先级")
    @TableField("priority")
    private Long priority;

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
