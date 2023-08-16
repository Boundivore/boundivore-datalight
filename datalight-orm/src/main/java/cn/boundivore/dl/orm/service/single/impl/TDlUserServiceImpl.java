package cn.boundivore.dl.orm.service.single.impl;

import cn.boundivore.dl.orm.mapper.single.TDlUserMapper;
import cn.boundivore.dl.orm.po.single.TDlUser;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.boundivore.dl.orm.service.single.ITDlUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户基础信息表 服务实现类
 * </p>
 *
 * @author liujingze
 * @since 2023-04-17 04:46:30
 */
@Service
public class TDlUserServiceImpl extends ServiceImpl<TDlUserMapper, TDlUser> implements ITDlUserService {

}
