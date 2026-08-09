package com.aris.mtcg.dao;

import com.aris.mtcg.domain.entity.ProductDO;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品 DAO
 *
 * @author pengYuJun
 */
@Mapper
public interface ProductMapper extends BaseMapper<ProductDO> {
}
