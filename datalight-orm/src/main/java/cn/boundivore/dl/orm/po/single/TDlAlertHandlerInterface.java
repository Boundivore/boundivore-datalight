package cn.boundivore.dl.orm.po.single;

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
 * 处理告警接口配置信息表
 * </p>
 *
 * @author Boundivore
 * @since 2024-04-22 11:51:04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_dl_alert_handler_interface")
@Schema(name = "TDlAlertHandlerInterface对象", description = "处理告警接口配置信息表")
public class TDlAlertHandlerInterface extends TBasePo<TDlAlertHandlerInterface> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "接收告警的地址")
    @TableField("interface_uri")
    private String interfaceUri;


}
