package com.aris.mtcg.dao;

import com.aris.mtcg.domain.entity.UserDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 DAO
 *
 * <p>注意：user 是 PostgreSQL 保留字，表名须加双引号。
 *
 * @author pengYuJun
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {}
