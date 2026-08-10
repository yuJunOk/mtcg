package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 卡牌数据对象，与 mtcg_card 表一一对应
 *
 * @author pengYuJun
 */
@Data
@Table("mtcg_card")
public class CardDO {

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 卡牌编号 */
    private String cardCode;

    /** 所属产品编号（引用 mtcg_product.product_code） */
    private String productCode;

    /** 卡牌名称 */
    private String cardName;

    /** 卡牌类型，见 {@link com.aris.mtcg.common.enums.EnumCardType} */
    private String cardType;

    /** 等级（1-6，冲击卡为空） */
    private Short level;

    /** 颜色，见 {@link com.aris.mtcg.common.enums.EnumColor} */
    private String color;

    /** 环境 */
    private String environment;

    /** 特征（逗号分隔） */
    private String traits;

    /** 攻击距离 */
    private Short attackRange;

    /** 战力 */
    private Short power;

    /** 稀有度，见 {@link com.aris.mtcg.common.enums.EnumRarity} */
    private String rarity;

    /** 效果描述原文 */
    private String effectText;

    /** 效果结构化 JSON（规则引擎解析） */
    private String effectJson;

    /** 卡图路径 */
    private String imagePath;

    /** 创建时间（插入时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()")
    private LocalDateTime createTime;

    /** 更新时间（插入/更新时由数据库 NOW() 自动填充） */
    @Column(onInsertValue = "NOW()", onUpdateValue = "NOW()")
    private LocalDateTime updateTime;
}
