package com.aris.mtcg.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * 卡组列表批量重排入参（与卡组内部卡牌顺序无关）
 *
 * @author pengYuJun
 */
@Data
public class DeckReorderDTO {

    @NotEmpty(message = "重排列表不能为空")
    @Valid
    private List<DeckReorderItem> items;

    @Data
    public static class DeckReorderItem {

        @NotNull(message = "卡组 ID 不能为空")
        private Long id;

        @NotNull(message = "排序值不能为空")
        @Min(value = 0, message = "排序值不能为负数")
        private Integer sortOrder;
    }
}
