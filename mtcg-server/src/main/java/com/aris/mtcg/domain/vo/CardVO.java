package com.aris.mtcg.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 卡牌展示对象
 *
 * @author pengYuJun
 */
@Data
public class CardVO {

    private Long id;

    private String cardCode;

    private String productCode;

    private String cardName;

    private String cardType;

    private Short level;

    private String color;

    private String environment;

    private String traits;

    private Short attackRange;

    private Short power;

    private String rarity;

    private String effectText;

    private String effectJson;

    private String imagePath;

    private LocalDateTime createTime;
}
