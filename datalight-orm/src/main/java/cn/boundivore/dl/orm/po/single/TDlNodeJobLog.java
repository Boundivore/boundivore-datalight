package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.orm.po.TBasePo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * Job 节点工作日志信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-07-05 06:19:31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_node_job_log")
@Schema(name = "TDlNodeJobLog对象", description = "Job 节点工作日志信息表")
public class TDlNodeJobLog extends TBasePo<TDlNodeJobLog> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "同批任务唯一标识")
    @TableField("tag")
    private String tag;

    @Schema(name = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(name = "NodeJob ID")
    @TableField("node_job_id")
    private Long nodeJobId;

    @Schema(name = "NodeTask ID")
    @TableField("node_task_id")
    private Long nodeTaskId;

    @Schema(name = "NodeStep ID")
    @TableField("node_step_id")
    private Long nodeStepId;

    @Schema(name = "标准日志")
    @TableField("log_stdout")
    private String logStdout;

    @Schema(name = "错误日志")
    @TableField("log_errout")
    private String logErrout;


}
