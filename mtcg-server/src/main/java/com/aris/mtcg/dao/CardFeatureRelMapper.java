package com.aris.mtcg.dao;

import com.aris.mtcg.domain.entity.CardFeatureRelDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卡牌-特征关联 Mapper
 *
 * @author pengYuJun
 */
@Mapper
public interface CardFeatureRelMapper extends BaseMapper<CardFeatureRelDO> {}
