package com.aris.mtcg.domain.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 卡牌数据对象，与 card 表一一对应
 *
 * @author pengYuJun
 */
@Data
@Table("card")
public class CardDO {

    /**
     * 主键
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 卡牌编号（官方编号或自定义编码）
     */
    private String cardCode;

    /**
     * 卡牌名称
     */
    private String cardName;

    /**
     * 卡牌类型，见 {@link com.aris.mtcg.common.enums.EnumCardType}
     */
    private String cardType;

    /**
     * 费用
     */
    private Integer cost;

    /**
     * 攻击力（不适用则为空）
     */
    private Integer attack;

    /**
     * 生命值（不适用则为空）
     */
    private Integer health;

    /**
     * 效果描述原文
     */
    private String effectText;

    /**
     * 效果结构化 JSON（后续规则引擎解析）
     */
    private String effectJson;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
