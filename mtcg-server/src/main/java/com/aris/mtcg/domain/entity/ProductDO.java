package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 产品（卡包/商品系列）数据对象，与 mtcg_product 表一一对应
 *
 * @author pengYuJun
 */
@Data
@Table("mtcg_product")
public class ProductDO {

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 产品编号（如 BP01、SD01） */
    private String productCode;

    /** 产品名称 */
    private String productName;

    /** 发售日期 */
    private LocalDate releaseDate;

    /** 描述 */
    private String description;

    /** 产品分类，见 {@link com.aris.mtcg.common.enums.EnumProductCategory} */
    private String category;

    /** 产品图相对路径（首图冗余，兼容旧字段；权威列表见 imagePaths） */
    private String imagePath;

    /** 产品图路径 JSON 数组文本，如 ["a.png","b.png"] */
    private String imagePaths;

    /** 创建时间（插入时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()")
    private LocalDateTime createTime;

    /** 更新时间（插入/更新时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()", onUpdateValue = "NOW()")
    private LocalDateTime updateTime;
}
