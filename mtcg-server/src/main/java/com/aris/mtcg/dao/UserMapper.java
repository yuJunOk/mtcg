package com.aris.mtcg.dao;

import com.aris.mtcg.domain.entity.UserDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 DAO
 *
 * @author pengYuJun
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
