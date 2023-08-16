package cn.boundivore.dl.orm.mapper.single;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.boundivore.dl.orm.po.single.TDlUserAuth;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户认证信息表 Mapper 接口
 * </p>
 *
 * @author Boundivore
 * @since 2023-04-17 04:46:30
 */
@Mapper
public interface TDlUserAuthMapper extends BaseMapper<TDlUserAuth> {

}
