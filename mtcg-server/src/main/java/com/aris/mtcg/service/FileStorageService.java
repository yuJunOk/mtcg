package com.aris.mtcg.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 *
 * <p>仅负责文件校验与落盘，不写业务库。
 *
 * @author pengYuJun
 */
public interface FileStorageService {

    /**
     * 校验并存储卡牌图片
     *
     * @param cardCode 卡牌编号（用于目录与文件名）
     * @param file 图片文件
     * @return 相对存储路径
     */
    String storeCardImage(String cardCode, MultipartFile file);

    /**
     * 校验并存储产品图片
     *
     * @param productCode 产品编号（用于目录与文件名）
     * @param file 图片文件
     * @return 相对存储路径
     */
    String storeProductImage(String productCode, MultipartFile file);

    /**
     * 删除图片文件
     *
     * @param imagePath 相对路径
     */
    void deleteImage(String imagePath);
}
