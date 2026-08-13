package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 加入房间 / 在线匹配入参
 *
 * @author pengYuJun
 */
@Data
public class GameJoinDTO {

    /** 本人出战卡组 ID */
    @NotNull(message = "出战卡组不能为空")
    private Long deckId;
}
