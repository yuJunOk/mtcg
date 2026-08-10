package com.aris.mtcg.controller.product;

import com.aris.mtcg.common.annotation.PublicApi;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.ProductQueryDTO;
import com.aris.mtcg.domain.vo.CardVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.domain.vo.ProductVO;
import com.aris.mtcg.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公开产品接口（无需登录）
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/products")
@PublicApi
public class ProductController {

    @Resource private ProductService productService;

    /** 分页查询产品列表 */
    @GetMapping
    public Result<PageVO<ProductVO>> list(ProductQueryDTO query) {
        return Result.success(productService.listProducts(query));
    }

    /** 查询产品详情 */
    @GetMapping("/{id}")
    public Result<ProductVO> get(@PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }

    /** 按产品编号查询卡牌列表 */
    @GetMapping("/{productCode}/cards")
    public Result<PageVO<CardVO>> listCardsByProduct(
            @PathVariable String productCode,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        return Result.success(productService.listCardsByProduct(productCode, pageNum, pageSize));
    }
}
