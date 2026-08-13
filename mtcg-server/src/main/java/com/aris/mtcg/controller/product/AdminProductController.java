package com.aris.mtcg.controller.product;

import com.aris.mtcg.common.annotation.RequireRole;
import com.aris.mtcg.common.enums.EnumUserRole;
import com.aris.mtcg.common.result.Result;
import com.aris.mtcg.domain.dto.ProductCreateDTO;
import com.aris.mtcg.domain.dto.ProductUpdateDTO;
import com.aris.mtcg.service.ProductService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员产品管理接口
 *
 * @author pengYuJun
 */
@RestController
@RequestMapping("/admin/products")
@RequireRole({EnumUserRole.CARD_ADMIN, EnumUserRole.SYS_ADMIN})
public class AdminProductController {

    @Resource private ProductService productService;

    /** 新增产品 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ProductCreateDTO dto) {
        return Result.success(productService.createProduct(dto));
    }

    /** 更新产品 */
    @PostMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO dto) {
        productService.updateProduct(id, dto);
        return Result.success();
    }

    /** 删除产品 */
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    /** 上传产品图片 */
    @PostMapping("/{id}/image")
    public Result<String> uploadImage(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return Result.success(productService.uploadProductImage(id, file));
    }
}
