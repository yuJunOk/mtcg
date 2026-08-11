package com.aris.mtcg.engine.model;

import com.aris.mtcg.engine.enums.PhaseType;

/**
 * 操作流水记录（复盘回放用，概要设计 D2）。
 *
 * <p>每次操作追加一条，轻量存储。回合快照由 Service 层负责持久化。
 *
 * @author pengYuJun
 */
public class ActionLog {

    /** 回合数 */
    private final int turnCount;

    /** 操作时所在阶段 */
    private final PhaseType phase;

    /** 操作方玩家 ID */
    private final String playerId;

    /** 操作类型（如 SUMMON / ATTACK / MULLIGAN） */
    private final String actionType;

    /** 操作详情（JSON 字符串，含源/目标/参数） */
    private final String actionDetail;

    /** 时间戳 */
    private final long timestamp;

    public ActionLog(
            int turnCount,
            PhaseType phase,
            String playerId,
            String actionType,
            String actionDetail) {
        this.turnCount = turnCount;
        this.phase = phase;
        this.playerId = playerId;
        this.actionType = actionType;
        this.actionDetail = actionDetail;
        this.timestamp = System.currentTimeMillis();
    }

    public int getTurnCount() {
        return turnCount;
    }

    public PhaseType getPhase() {
        return phase;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getActionType() {
        return actionType;
    }

    public String getActionDetail() {
        return actionDetail;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
