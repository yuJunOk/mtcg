package com.aris.mtcg.domain.dto;

import lombok.Data;

/**
 * 卡牌分页查询入参
 *
 * @author pengYuJun
 */
@Data
public class CardQueryDTO {

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

    /** 当前页码，默认 1 */
    private Integer pageNum = 1;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;
}
