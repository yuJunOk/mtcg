package com.aris.mtcg.domain.vo;

import java.util.List;
import lombok.Data;

/**
 * 对局状态视图（面向前端，含隐私裁剪后的双方局面）
 *
 * @author pengYuJun
 */
@Data
public class GameStateVO {

    private String gameId;

    /** WAITING / IN_PROGRESS / FINISHED */
    private String status;

    private Integer turnCount;

    /** PhaseType 枚举名 */
    private String currentPhase;

    /** 当前回合玩家 ID */
    private String activePlayerId;

    /** 对局结束时 PLAYER1 / PLAYER2 / DRAW */
    private String winner;

    private PlayerStateVO player1;

    private PlayerStateVO player2;

    /** 当前操作方可执行的 ActionType 列表（驱动前端按钮） */
    private List<String> availableActions;
}
