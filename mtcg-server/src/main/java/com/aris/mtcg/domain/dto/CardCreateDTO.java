package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 卡牌创建/更新共用入参基字段
 *
 * @author pengYuJun
 */
@Data
public class CardCreateDTO {

    @NotBlank(message = "卡牌编号不能为空")
    @Length(max = 32)
    private String cardCode;

    @Length(max = 16)
    private String productCode;

    @NotBlank(message = "卡牌名称不能为空")
    @Length(max = 128)
    private String cardName;

    @NotBlank(message = "卡牌类型不能为空")
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

    @NotBlank(message = "稀有度不能为空")
    @Length(max = 16)
    private String rarity;

    private String effectText;

    private String effectJson;

    @Length(max = 256)
    private String imagePath;
}
