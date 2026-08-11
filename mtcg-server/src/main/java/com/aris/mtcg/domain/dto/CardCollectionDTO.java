package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 收藏登记入参
 *
 * @author pengYuJun
 */
@Data
public class CardCollectionDTO {

    @NotBlank(message = "卡牌编号不能为空")
    private String cardCode;

    @NotNull(message = "数量不能为空")
    @Min(value = 0, message = "数量不能为负数")
    private Integer quantity;

    /** 可选，自定义标签 */
    private String tags;

    /** 可选，个人备注 */
    private String note;
}
