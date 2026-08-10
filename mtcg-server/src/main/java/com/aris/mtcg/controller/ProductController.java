package com.aris.mtcg.controller;

import com.aris.mtcg.common.annotation.RequireRole;
import com.aris.mtcg.common.enums.EnumUserRole;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.ProductCreateDTO;
import com.aris.mtcg.domain.dto.ProductQueryDTO;
import com.aris.mtcg.domain.dto.ProductUpdateDTO;
import com.aris.mtcg.domain.vo.CardVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.domain.vo.ProductVO;
import com.aris.mtcg.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品（卡包/商品系列）管理接口
 *
 * @author pengYuJun
 */
@Tag(name = "产品管理")
@RestController
@RequestMapping("/admin/products")
@RequireRole({EnumUserRole.CARD_ADMIN, EnumUserRole.SYS_ADMIN})
public class ProductController {

    @Resource
    private ProductService productService;

    @Operation(summary = "分页查询产品列表")
    @GetMapping
    public Result<PageVO<ProductVO>> list(ProductQueryDTO query) {
        return Result.success(productService.listProducts(query));
    }

    @Operation(summary = "根据ID查询产品")
    @GetMapping("/{id}")
    public Result<ProductVO> get(@PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }

    @Operation(summary = "新增产品")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ProductCreateDTO dto) {
        return Result.success(productService.createProduct(dto));
    }

    @Operation(summary = "更新产品")
    @PostMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO dto) {
        productService.updateProduct(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除产品")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @Operation(summary = "按产品编号查询卡牌列表")
    @GetMapping("/{productCode}/cards")
    public Result<PageVO<CardVO>> listCardsByProduct(
            @PathVariable String productCode,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return Result.success(productService.listCardsByProduct(productCode, page, size));
    }
}
