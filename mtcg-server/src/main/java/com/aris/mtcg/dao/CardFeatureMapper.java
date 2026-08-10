package com.aris.mtcg.dao;

import com.aris.mtcg.domain.entity.CardFeatureDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卡牌特征 DAO
 *
 * @author pengYuJun
 */
@Mapper
public interface CardFeatureMapper extends BaseMapper<CardFeatureDO> {}
