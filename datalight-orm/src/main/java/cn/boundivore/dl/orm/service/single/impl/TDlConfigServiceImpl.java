package cn.boundivore.dl.orm.service.single.impl;

import cn.boundivore.dl.orm.po.single.TDlConfig;
import cn.boundivore.dl.orm.mapper.single.TDlConfigMapper;
import cn.boundivore.dl.orm.service.single.ITDlConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 配置信息表 服务实现类
 * </p>
 *
 * @author Boundivore
 * @since 2023-07-31 10:51:47
 */
@Service
public class TDlConfigServiceImpl extends ServiceImpl<TDlConfigMapper, TDlConfig> implements ITDlConfigService {

}
