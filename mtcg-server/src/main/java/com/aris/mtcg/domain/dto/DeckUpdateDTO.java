package com.aris.mtcg.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * 编辑卡组入参（全部可选）
 *
 * @author pengYuJun
 */
@Data
public class DeckUpdateDTO {

    @Size(max = 64, message = "卡组名称长度不能超过 64")
    private String deckName;

    /** 非空时整表覆盖并重新校验；列表顺序即卡组内新顺序 */
    @Valid private List<DeckCardEntry> mainDeck;

    /** 非空时整表覆盖并重新校验 */
    @Valid private List<DeckCardEntry> rushDeck;

    /** 非 null 时覆盖（空串表示清空标签） */
    @Size(max = 256, message = "标签长度不能超过 256")
    private String tags;

    /** 非 null 时覆盖（空串表示清空，回退为第一张） */
    @Size(max = 32, message = "封面卡编号长度不能超过 32")
    private String coverCardCode;
}
