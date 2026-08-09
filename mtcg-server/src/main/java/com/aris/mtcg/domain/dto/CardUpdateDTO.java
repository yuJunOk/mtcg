package com.aris.mtcg.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 卡牌更新入参（所有字段可空，非空才更新）
 *
 * @author pengYuJun
 */
@Data
public class CardUpdateDTO {

    @Length(max = 16)
    private String productCode;

    @Length(max = 128)
    private String cardName;

    @Length(max = 16)
    private String cardType;

    private Short level;

    @Length(max = 16)
    private String color;

    @Length(max = 16)
    private String environment;

    @Length(max = 256)
    private String traits;

    private Short attackRange;

    private Short power;

    @Length(max = 16)
    private String rarity;

    private String effectText;

    private String effectJson;

    @Length(max = 256)
    private String imagePath;
}
