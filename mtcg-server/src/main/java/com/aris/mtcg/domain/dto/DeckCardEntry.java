package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 卡组内有序卡牌条目。List 中的位置即卡组内排序；同 cardCode 仅一条，再添加只加 quantity。
 *
 * @author pengYuJun
 */
@Data
public class DeckCardEntry {

    @NotBlank(message = "卡牌编号不能为空")
    private String cardCode;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为 1")
    private Integer quantity;
}
