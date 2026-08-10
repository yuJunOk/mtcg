package com.aris.mtcg.service;

import com.aris.mtcg.domain.dto.ProductCreateDTO;
import com.aris.mtcg.domain.dto.ProductQueryDTO;
import com.aris.mtcg.domain.dto.ProductUpdateDTO;
import com.aris.mtcg.domain.vo.CardVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.domain.vo.ProductVO;

/**
 * 产品（卡包/商品系列）服务
 *
 * @author pengYuJun
 */
public interface ProductService {

    /** 分页查询产品列表 */
    PageVO<ProductVO> listProducts(ProductQueryDTO query);

    /** 根据 ID 查询产品 */
    ProductVO getProductById(Long id);

    /** 创建产品 */
    Long createProduct(ProductCreateDTO dto);

    /** 更新产品 */
    void updateProduct(Long id, ProductUpdateDTO dto);

    /** 删除产品 */
    void deleteProduct(Long id);

    /** 按产品编号查询卡牌列表（分页） */
    PageVO<CardVO> listCardsByProduct(String productCode, Integer pageNum, Integer pageSize);
}
