package cn.boundivore.dl.service.master.bean;

import cn.boundivore.dl.orm.po.single.TDlConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 封装配置文件与配置文件内容是否发生变化标识
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2025/5/20
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigUpdateBean {
    private TDlConfig tDlConfig;
    private boolean isUpdate = true;
}
