package com.aris.mtcg.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * 创建卡组入参
 *
 * @author pengYuJun
 */
@Data
public class DeckCreateDTO {

    @NotBlank(message = "卡组名称不能为空")
    @Size(max = 64, message = "卡组名称长度不能超过 64")
    private String deckName;

    @NotNull(message = "主卡组不能为空")
    @Valid
    private List<DeckCardEntry> mainDeck;

    @NotNull(message = "冲击卡组不能为空")
    @Valid
    private List<DeckCardEntry> rushDeck;

    /** 可选，自定义标签（逗号分隔，服务端规范化） */
    @Size(max = 256, message = "标签长度不能超过 256")
    private String tags;

    /** 封面卡编号（须在主卡组或冲击卡组内） */
    @Size(max = 32, message = "封面卡编号长度不能超过 32")
    private String coverCardCode;
}
