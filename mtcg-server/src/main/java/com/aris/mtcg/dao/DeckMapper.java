package com.aris.mtcg.dao;

import com.aris.mtcg.domain.entity.DeckDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 卡组 Mapper
 *
 * @author pengYuJun
 */
@Mapper
public interface DeckMapper extends BaseMapper<DeckDO> {}
