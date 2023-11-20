package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ExecStateEnum;
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
 * Stage 信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-09 11:37:19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_stage")
@Schema(name = "TDlStage对象", description = "Stage 信息表")
public class TDlStage extends TBasePo<TDlStage> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "同批任务唯一标识")
    @TableField("tag")
    private String tag;

    @Schema(name = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(name = "Job ID")
    @TableField("job_id")
    private Long jobId;

    @Schema(name = "Stage 名称")
    @TableField("stage_name")
    private String stageName;

    @Schema(name = "Stage 状态 枚举值：见代码")
    @TableField("stage_state")
    private ExecStateEnum stageState;

    @Schema(name = "服务名称 全大写英文命名法，可以唯一 标识服务")
    @TableField("service_name")
    private String serviceName;

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
