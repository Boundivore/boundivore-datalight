package cn.boundivore.dl.orm.po.single;

import cn.boundivore.dl.base.enumeration.impl.NodeStateEnum;
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
 * 节点初始化信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-28 03:26:07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_node_init")
@Schema(name = "TDlNodeInit对象", description = "节点初始化信息表")
public class TDlNodeInit extends TBasePo<TDlNodeInit> {

    private static final long serialVersionUID = 1L;

    @Schema(name = "集群 ID")
    @TableField("cluster_id")
    private Long clusterId;

    @Schema(name = "主机名")
    @TableField("hostname")
    private String hostname;

    @Schema(name = "IPV4 地址")
    @TableField("ipv4")
    private String ipv4;

    @Schema(name = "IPV6 地址")
    @TableField("ipv6")
    private String ipv6;

    @Schema(name = "SSH 端口 默认为 22 端口，可自定义修改")
    @TableField("ssh_port")
    private Long sshPort;

    @Schema(name = "CPU 架构")
    @TableField("cpu_arch")
    private String cpuArch;

    @Schema(name = "CPU 核心数 单位：个")
    @TableField("cpu_cores")
    private Long cpuCores;

    @Schema(name = "内存总大小 单位：K-bytes")
    @TableField("ram")
    private Long ram;

    @Schema(name = "磁盘总容量 单位：K-bytes")
    @TableField("disk")
    private Long disk;

    @Schema(name = "系统版本")
    @TableField("os_version")
    private String osVersion;

    @Schema(name = "当前初始化状态")
    @TableField("node_init_state")
    private NodeStateEnum nodeInitState;
}
