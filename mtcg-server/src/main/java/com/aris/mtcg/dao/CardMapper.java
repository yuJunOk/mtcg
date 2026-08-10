package com.aris.mtcg.dao;

import com.aris.mtcg.domain.entity.CardDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卡牌 DAO
 *
 * @author pengYuJun
 */
@Mapper
public interface CardMapper extends BaseMapper<CardDO> {}
