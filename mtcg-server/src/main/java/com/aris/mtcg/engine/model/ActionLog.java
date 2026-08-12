package com.aris.mtcg.engine.model;

import com.aris.mtcg.engine.enums.PhaseType;

/**
 * 操作流水记录（复盘回放用，概要设计 D2）。
 *
 * <p>每次操作追加一条，轻量存储。回合快照由 Service 层负责持久化。
 *
 * <p>{@code seq} 用于持久化排序与崩溃恢复水位；引擎 Handler 可不填（默认 0），由 Service / GameContext 落库前赋值。
 *
 * @author pengYuJun
 */
public class ActionLog {

    /** 对局内操作序号（单调递增）；Handler 写入时可先为 0，由持久化层补齐。 */
    private final long seq;

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

    /** 引擎 Handler 用：seq 默认为 0 */
    public ActionLog(
            int turnCount,
            PhaseType phase,
            String playerId,
            String actionType,
            String actionDetail) {
        this(0L, turnCount, phase, playerId, actionType, actionDetail);
    }

    /** 持久化 / 恢复用：显式指定 seq */
    public ActionLog(
            long seq,
            int turnCount,
            PhaseType phase,
            String playerId,
            String actionType,
            String actionDetail) {
        this.seq = seq;
        this.turnCount = turnCount;
        this.phase = phase;
        this.playerId = playerId;
        this.actionType = actionType;
        this.actionDetail = actionDetail;
        this.timestamp = System.currentTimeMillis();
    }

    public long getSeq() {
        return seq;
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
