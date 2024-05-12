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
 * 节点信息表
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-09 11:37:19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_node")
@Schema(name = "TDlNode对象", description = "节点信息表")
public class TDlNode extends TBasePo<TDlNode> {

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

    @Schema(name = "内存总大小 单位：Kbytes")
    @TableField("ram")
    private Long ram;

    @Schema(name = "磁盘总容量 单位：Kbytes")
    @TableField("disk")
    private Long disk;

    @Schema(name = "状态枚举，见代码")
    @TableField("node_state")
    private NodeStateEnum nodeState;

    @Schema(name = "系统版本")
    @TableField("os_version")
    private String osVersion;

    @Schema(name = "节点计数 当前为第几个增加的节点，唯一且仅递增，不可回退，同时用于部分组件唯一部署 ID")
    @TableField("serial_num")
    private Integer serialNum;

}
