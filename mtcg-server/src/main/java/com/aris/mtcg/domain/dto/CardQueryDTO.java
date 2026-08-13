package com.aris.mtcg.domain.dto;

import lombok.Data;

/**
 * 卡牌分页查询入参
 *
 * @author pengYuJun
 */
@Data
public class CardQueryDTO {

    /** 卡牌编号（精确匹配） */
    private String cardCode;

    /** 卡牌名称（模糊匹配） */
    private String cardName;

    /** 卡牌类型（精确匹配） */
    private String cardType;

    /** 颜色（精确匹配） */
    private String color;

    /** 稀有度（精确匹配） */
    private String rarity;

    /** 所属产品编号（精确匹配） */
    private String productCode;

    /** 等级（精确匹配） */
    private Short level;

    /** 攻击距离（精确匹配） */
    private Short attackRange;

    /** 特征关键词（模糊匹配 traits 字段，如「复仇者联盟」） */
    private String trait;

    /** 当前页码，默认 1 */
    private Integer pageNum = 1;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;
}
