package com.aris.mtcg.service.impl;

import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.service.FileStorageService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 本地文件存储实现
 *
 * @author pengYuJun
 */
@Slf4j
@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload.base-path:./uploads}")
    private String basePath;

    @Value("${file.upload.image-suffixes:.jpg,.jpeg,.png,.gif,.webp}")
    private String[] allowedSuffixes;

    @Value("${file.upload.max-size:5242880}")
    private long maxSize;

    @Override
    public String storeCardImage(String cardCode, MultipartFile file) {
        return storeNamedImage("cards", cardCode, file, "卡牌编号不能为空");
    }

    @Override
    public String storeProductImage(String productCode, MultipartFile file) {
        return storeNamedImage("products", productCode, file, "产品编号不能为空");
    }

    private String storeNamedImage(String folder, String key, MultipartFile file, String blankMsg) {
        validateFile(file);
        if (key == null || key.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, blankMsg);
        }

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String suffix = getSuffix(originalFilename);
        String newFilename =
                String.format("%s_%s%s", key, UUID.randomUUID().toString().substring(0, 8), suffix);

        String relativePath = String.format("%s/%s/%s", folder, key, newFilename);
        Path targetPath = Paths.get(basePath, relativePath);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return relativePath;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件保存失败");
        }
    }

    @Override
    public void deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }
        try {
            Path filePath = Paths.get(basePath, imagePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("删除旧图片失败: {}, {}", imagePath, e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException(
                    ErrorCode.PARAMS_ERROR, "文件大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
        }
        String originalFilename =
                Objects.requireNonNull(file.getOriginalFilename()).toLowerCase(Locale.ROOT);
        boolean allowed = false;
        for (String suffix : allowedSuffixes) {
            if (originalFilename.endsWith(suffix.toLowerCase(Locale.ROOT))) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的图片格式");
        }
        if (!isValidImageMagic(file)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件内容不是合法图片");
        }
    }

    /** 校验图片魔数，防止扩展名伪装 */
    private boolean isValidImageMagic(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] header = in.readNBytes(12);
            if (header.length < 3) {
                return false;
            }
            // JPEG
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF) {
                return true;
            }
            // PNG
            if (header.length >= 8
                    && header[0] == (byte) 0x89
                    && header[1] == 0x50
                    && header[2] == 0x4E
                    && header[3] == 0x47) {
                return true;
            }
            // GIF
            if (header.length >= 6 && header[0] == 'G' && header[1] == 'I' && header[2] == 'F') {
                return true;
            }
            // WEBP: RIFF....WEBP
            return header.length >= 12
                    && header[0] == 'R'
                    && header[1] == 'I'
                    && header[2] == 'F'
                    && header[3] == 'F'
                    && header[8] == 'W'
                    && header[9] == 'E'
                    && header[10] == 'B'
                    && header[11] == 'P';
        } catch (IOException e) {
            return false;
        }
    }

    private String getSuffix(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }
}
