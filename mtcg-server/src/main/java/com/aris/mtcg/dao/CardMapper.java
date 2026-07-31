package com.aris.mtcg.dao;

import com.mybatisflex.core.BaseMapper;
import com.aris.mtcg.domain.entity.CardDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卡牌 DAO
 *
 * @author pengYuJun
 */
@Mapper
public interface CardMapper extends BaseMapper<CardDO> {
}
