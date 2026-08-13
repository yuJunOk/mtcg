package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Data;

/**
 * 创建对局入参
 *
 * <p>发起方 {@code player1Id} 从安全上下文获取，不暴露在本 DTO 中。
 *
 * @author pengYuJun
 */
@Data
public class GameCreateDTO {

    /** 发起方卡组 ID */
    @NotNull(message = "发起方卡组不能为空")
    private Long deck1Id;

    /** 对手方卡组 ID；创建房间时不填，加入时由对手提交 */
    private Long deck2Id;

    /** 对手方用户 ID（可空：AI 对局或自由匹配时由系统填） */
    private Long player2Id;

    /** 对局模式：CASUAL / RANKED / AI */
    @NotNull(message = "对局模式不能为空")
    @Pattern(regexp = "CASUAL|RANKED|AI", message = "对局模式必须为 CASUAL、RANKED 或 AI")
    private String gameMode;

    /** 先攻方：PLAYER1 / PLAYER2；空则随机 */
    @Pattern(regexp = "PLAYER1|PLAYER2", message = "先攻方必须为 PLAYER1 或 PLAYER2")
    private String firstPlayer;

    /** player1 调度手牌索引；空表示不调度 */
    private List<Integer> mulligan1Indices;

    /** player2 调度手牌索引；空表示不调度 */
    private List<Integer> mulligan2Indices;
}
