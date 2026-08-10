package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 卡牌特征创建入参
 *
 * @author pengYuJun
 */
@Data
public class CardFeatureCreateDTO {

    @NotBlank(message = "特征编码不能为空")
    @Size(max = 32, message = "特征编码长度不能超过 32")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "特征编码只能包含小写字母、数字和下划线")
    private String code;

    @NotBlank(message = "特征名称不能为空")
    @Size(max = 64, message = "特征名称长度不能超过 64")
    private String name;

    @Size(max = 16, message = "背景颜色长度不能超过 16")
    private String bgColor;
}
