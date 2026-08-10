package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 卡牌特征更新入参
 *
 * @author pengYuJun
 */
@Data
public class CardFeatureUpdateDTO {

    @Size(max = 64, message = "特征名称长度不能超过 64")
    private String name;

    @Size(max = 16, message = "背景颜色长度不能超过 16")
    private String bgColor;
}
