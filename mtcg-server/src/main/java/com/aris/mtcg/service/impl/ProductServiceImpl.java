package com.aris.mtcg.service.impl;

import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.dao.CardMapper;
import com.aris.mtcg.dao.ProductMapper;
import com.aris.mtcg.domain.dto.ProductCreateDTO;
import com.aris.mtcg.domain.dto.ProductQueryDTO;
import com.aris.mtcg.domain.dto.ProductUpdateDTO;
import com.aris.mtcg.domain.entity.CardDO;
import com.aris.mtcg.domain.entity.ProductDO;
import com.aris.mtcg.domain.vo.CardVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.domain.vo.ProductVO;
import com.aris.mtcg.service.AuditService;
import com.aris.mtcg.service.FileStorageService;
import com.aris.mtcg.service.ProductService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 产品服务实现
 *
 * @author pengYuJun
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Resource private ProductMapper productMapper;

    @Resource private CardMapper cardMapper;

    @Resource private AuditService auditService;

    @Resource private FileStorageService fileStorageService;

    @Override
    public PageVO<ProductVO> listProducts(ProductQueryDTO query) {
        QueryWrapper qw =
                QueryWrapper.create()
                        .like("product_name", query.getProductName(), StringUtils::isNotBlank)
                        .like("product_code", query.getProductCode(), StringUtils::isNotBlank)
                        .orderBy("release_date", false)
                        .orderBy("create_time", false);
        int pageNum =
                (query.getPageNum() == null || query.getPageNum() < 1) ? 1 : query.getPageNum();
        int pageSize =
                (query.getPageSize() == null || query.getPageSize() < 1) ? 10 : query.getPageSize();
        Page<ProductDO> page = productMapper.paginate(Page.of(pageNum, pageSize), qw);
        List<ProductVO> records =
                page.getRecords().stream().map(ProductVO::fromDO).collect(Collectors.toList());
        return new PageVO<>(records, page.getTotalRow());
    }

    @Override
    public ProductVO getProductById(Long id) {
        return ProductVO.fromDO(loadOrThrow(id));
    }

    @Override
    public Long createProduct(ProductCreateDTO dto) {
        // 产品编号唯一校验
        long count =
                productMapper.selectCountByQuery(
                        QueryWrapper.create().eq("product_code", dto.getProductCode()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PRODUCT_CODE_DUPLICATE);
        }
        ProductDO product = ProductVO.toDO(ProductVO.fromDTO(dto));
        productMapper.insert(product);
        auditService.record(
                "CREATE",
                "PRODUCT",
                String.valueOf(product.getId()),
                "创建产品 " + product.getProductCode());
        return product.getId();
    }

    @Override
    public void updateProduct(Long id, ProductUpdateDTO dto) {
        loadOrThrow(id);
        ProductDO update = new ProductDO();
        update.setId(id);
        if (dto.getProductName() != null) {
            update.setProductName(dto.getProductName());
        }
        if (dto.getReleaseDate() != null) {
            update.setReleaseDate(dto.getReleaseDate());
        }
        if (dto.getDescription() != null) {
            update.setDescription(dto.getDescription());
        }
        productMapper.update(update);
        auditService.record("UPDATE", "PRODUCT", String.valueOf(id), "更新产品");
    }

    @Override
    public void deleteProduct(Long id) {
        ProductDO product = loadOrThrow(id);
        productMapper.deleteById(id);
        List<String> paths = ProductVO.parseImagePaths(product.getImagePaths());
        if (paths.isEmpty() && StringUtils.isNotBlank(product.getImagePath())) {
            paths = List.of(product.getImagePath());
        }
        for (String path : paths) {
            fileStorageService.deleteImage(path);
        }
        auditService.record(
                "DELETE", "PRODUCT", String.valueOf(id), "删除产品 " + product.getProductCode());
    }

    @Override
    public String uploadProductImage(Long productId, MultipartFile file) {
        ProductDO product = loadOrThrow(productId);
        String newPath = fileStorageService.storeProductImage(product.getProductCode(), file);
        List<String> paths = ProductVO.parseImagePaths(product.getImagePaths());
        if (paths.isEmpty() && StringUtils.isNotBlank(product.getImagePath())) {
            paths = new ArrayList<>();
            paths.add(product.getImagePath().trim());
        }
        paths.add(newPath);
        ProductDO update = new ProductDO();
        update.setId(productId);
        ProductVO.applyImageFields(update, paths);
        productMapper.update(update);
        auditService.record("UPDATE", "PRODUCT", String.valueOf(productId), "上传产品图片");
        return newPath;
    }

    @Override
    public void deleteProductImage(Long productId, String imagePath) {
        if (StringUtils.isBlank(imagePath)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductDO product = loadOrThrow(productId);
        String target = imagePath.trim();
        List<String> paths = ProductVO.parseImagePaths(product.getImagePaths());
        if (paths.isEmpty() && StringUtils.isNotBlank(product.getImagePath())) {
            paths = new ArrayList<>();
            paths.add(product.getImagePath().trim());
        }
        boolean removed = paths.removeIf(p -> p.equals(target));
        if (!removed) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片不存在于该产品");
        }
        ProductDO update = new ProductDO();
        update.setId(productId);
        ProductVO.applyImageFields(update, paths);
        productMapper.update(update);
        fileStorageService.deleteImage(target);
        auditService.record("UPDATE", "PRODUCT", String.valueOf(productId), "删除产品图片");
    }

    @Override
    public PageVO<CardVO> listCardsByProduct(
            String productCode, Integer pageNum, Integer pageSize) {
        QueryWrapper qw =
                QueryWrapper.create().eq("product_code", productCode).orderBy("card_code", true);
        int pn = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int ps = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        Page<CardDO> result = cardMapper.paginate(Page.of(pn, ps), qw);
        List<CardVO> records =
                result.getRecords().stream().map(CardVO::fromDO).collect(Collectors.toList());
        return new PageVO<>(records, result.getTotalRow());
    }

    // ==================== 私有方法 ====================

    private ProductDO loadOrThrow(Long id) {
        ProductDO product = productMapper.selectOneById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return product;
    }
}
