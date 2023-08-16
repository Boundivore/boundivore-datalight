package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.ClusterStateEnum;
import cn.boundivore.dl.base.enumeration.impl.ClusterTypeEnum;
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
 * 集群信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-28 03:26:06
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_cluster")
@ApiModel(value = "TDlCluster对象", description = "集群信息表")
public class TDlCluster extends TBasePo<TDlCluster> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("服务组件版本 当前服务组件套装的版本：DataLightComponents")
    @TableField("dlc_version")
    private String dlcVersion;

    @ApiModelProperty("主机名")
    @TableField("cluster_name")
    private String clusterName;

    @ApiModelProperty("集群类型 存储、计算、混合，枚举见代码")
    @TableField("cluster_type")
    private ClusterTypeEnum clusterType;

    @ApiModelProperty("集群状态 枚举值，见代码")
    @TableField("cluster_state")
    private ClusterStateEnum clusterState;

    @ApiModelProperty("集群描述")
    @TableField("cluster_desc")
    private String clusterDesc;

    @ApiModelProperty("关联集群 ID 只有计算集群可以关联存储或混合集群")
    @TableField("relative_cluster_id")
    private String relativeClusterId;


}
