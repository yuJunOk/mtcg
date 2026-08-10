package com.aris.mtcg.domain.vo;

import com.aris.mtcg.domain.dto.ProductCreateDTO;
import com.aris.mtcg.domain.entity.ProductDO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 产品展示对象
 *
 * @author pengYuJun
 */
@Data
public class ProductVO {

    private Long id;

    private String productCode;

    private String productName;

    private LocalDate releaseDate;

    private String description;

    private LocalDateTime createTime;

    // ========== 静态工厂方法 ==========

    /** 从 DO 转换为 VO */
    public static ProductVO fromDO(ProductDO product) {
        if (product == null) {
            return null;
        }
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setProductCode(product.getProductCode());
        vo.setProductName(product.getProductName());
        vo.setReleaseDate(product.getReleaseDate());
        vo.setDescription(product.getDescription());
        vo.setCreateTime(product.getCreateTime());
        return vo;
    }

    /** 从 DTO 转换为 VO */
    public static ProductVO fromDTO(ProductCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        ProductVO vo = new ProductVO();
        vo.setProductCode(dto.getProductCode());
        vo.setProductName(dto.getProductName());
        vo.setReleaseDate(dto.getReleaseDate());
        vo.setDescription(dto.getDescription());
        return vo;
    }

    /** 转换为 DO */
    public static ProductDO toDO(ProductVO vo) {
        if (vo == null) {
            return null;
        }
        ProductDO product = new ProductDO();
        product.setId(vo.getId());
        product.setProductCode(vo.getProductCode());
        product.setProductName(vo.getProductName());
        product.setReleaseDate(vo.getReleaseDate());
        product.setDescription(vo.getDescription());
        return product;
    }

    /** 转换为 DTO */
    public static ProductCreateDTO toDTO(ProductVO vo) {
        if (vo == null) {
            return null;
        }
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setProductCode(vo.getProductCode());
        dto.setProductName(vo.getProductName());
        dto.setReleaseDate(vo.getReleaseDate());
        dto.setDescription(vo.getDescription());
        return dto;
    }
}
