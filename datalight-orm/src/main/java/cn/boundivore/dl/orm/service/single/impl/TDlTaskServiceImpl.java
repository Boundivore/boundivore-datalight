package cn.boundivore.dl.orm.service.single.impl;

import cn.boundivore.dl.orm.po.single.TDlTask;
import cn.boundivore.dl.orm.mapper.single.TDlTaskMapper;
import cn.boundivore.dl.orm.service.single.ITDlTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Task 信息表 服务实现类
 * </p>
 *
 * @author Boundivore
 * @since 2023-06-09 11:37:19
 */
@Service
public class TDlTaskServiceImpl extends ServiceImpl<TDlTaskMapper, TDlTask> implements ITDlTaskService {

}
