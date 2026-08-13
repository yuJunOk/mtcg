package com.aris.mtcg.domain.vo;

import com.aris.mtcg.domain.dto.ProductCreateDTO;
import com.aris.mtcg.domain.entity.ProductDO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 产品展示对象
 *
 * @author pengYuJun
 */
@Data
public class ProductVO {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Long id;

    private String productCode;

    private String productName;

    private LocalDate releaseDate;

    private String description;

    /** 产品分类：STARTER / BOOSTER / OTHER */
    private String category;

    /** 封面图（imagePaths 首张；兼容旧客户端） */
    private String imagePath;

    /** 产品图相对路径列表（可多张） */
    private List<String> imagePaths;

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
        vo.setCategory(product.getCategory());
        List<String> paths = parseImagePaths(product.getImagePaths());
        if (paths.isEmpty() && StringUtils.isNotBlank(product.getImagePath())) {
            paths = new ArrayList<>();
            paths.add(product.getImagePath().trim());
        }
        vo.setImagePaths(paths);
        vo.setImagePath(paths.isEmpty() ? null : paths.get(0));
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
        vo.setCategory(dto.getCategory());
        vo.setImagePaths(new ArrayList<>());
        return vo;
    }

    /** 转换为 DO（写入时同步 image_path = 首图、image_paths = JSON） */
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
        product.setCategory(vo.getCategory());
        List<String> paths =
                vo.getImagePaths() != null
                        ? new ArrayList<>(vo.getImagePaths())
                        : new ArrayList<>();
        if (paths.isEmpty() && StringUtils.isNotBlank(vo.getImagePath())) {
            paths.add(vo.getImagePath().trim());
        }
        applyImageFields(product, paths);
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
        dto.setCategory(vo.getCategory());
        return dto;
    }

    /** 解析 DO 中的 image_paths JSON */
    public static List<String> parseImagePaths(String raw) {
        if (StringUtils.isBlank(raw)) {
            return new ArrayList<>();
        }
        try {
            List<String> list = MAPPER.readValue(raw, new TypeReference<List<String>>() {});
            if (list == null) {
                return new ArrayList<>();
            }
            List<String> cleaned = new ArrayList<>();
            for (String p : list) {
                if (StringUtils.isNotBlank(p)) {
                    cleaned.add(p.trim());
                }
            }
            return cleaned;
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    /** 序列化路径列表为 JSON 文本 */
    public static String toImagePathsJson(List<String> paths) {
        List<String> cleaned = new ArrayList<>();
        if (paths != null) {
            for (String p : paths) {
                if (StringUtils.isNotBlank(p)) {
                    cleaned.add(p.trim());
                }
            }
        }
        try {
            return MAPPER.writeValueAsString(cleaned);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    /** 同步写入 DO 的 image_path / image_paths */
    public static void applyImageFields(ProductDO product, List<String> paths) {
        List<String> cleaned = new ArrayList<>();
        if (paths != null) {
            for (String p : paths) {
                if (StringUtils.isNotBlank(p)) {
                    cleaned.add(p.trim());
                }
            }
        }
        product.setImagePaths(toImagePathsJson(cleaned));
        // 空串而非 null：MyBatis-Flex 默认 update 忽略 null，删光图时也要写回
        product.setImagePath(cleaned.isEmpty() ? "" : cleaned.get(0));
    }
}
