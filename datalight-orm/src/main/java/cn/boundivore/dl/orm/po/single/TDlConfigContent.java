package cn.boundivore.dl.orm.po.single;

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
 * 配置文件内容信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-07-31 10:51:47
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_config_content")
@ApiModel(value = "TDlConfigContent对象", description = "配置文件内容信息表")
public class TDlConfigContent extends TBasePo<TDlConfigContent> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @ApiModelProperty("配置文件名称")
    @TableField("filename")
    private String filename;

    @ApiModelProperty("配置文件内容 配置文件内容的 Base64")
    @TableField("config_data")
    private String configData;

    @ApiModelProperty("文件内容摘要 256 位摘要算法，极低碰撞概率，用于比较文件内容是否相同(文件内容+文件绝对路径）")
    @TableField("sha256")
    private String sha256;


}
