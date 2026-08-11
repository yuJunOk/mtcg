package com.aris.mtcg.engine.model;

import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.PhaseType;
import java.util.ArrayList;
import java.util.List;

/**
 * 对局全局状态。
 *
 * <p>引擎运行时唯一的状态根对象，所有操作读写此对象。
 *
 * @author pengYuJun
 */
public class GameState {

    /** 对局 ID */
    private String gameId;

    /** 当前回合玩家（行动方） */
    private PlayerState activePlayer;

    /** 非当前回合玩家（防守方） */
    private PlayerState inactivePlayer;

    /** 回合计数（从 1 开始；初始化时为 0，startGame 时置 1） */
    private int turnCount;

    /** 当前阶段（303.2.a） */
    private PhaseType currentPhase;

    /** 对局状态：WAITING / IN_PROGRESS / FINISHED */
    private GameStatus status;

    /** 先攻玩家（引用 active 或 inactive，用于判断先攻首回合跳过战斗） */
    private PlayerState firstPlayer;

    /** 操作流水（复盘用，概要设计 D2） */
    private final List<ActionLog> actionLog = new ArrayList<>();

    /** 胜利者玩家 ID（对局结束时设置） */
    private String winnerId;

    public GameState(String gameId) {
        this.gameId = gameId;
        this.status = GameStatus.WAITING;
        this.turnCount = 0;
        this.currentPhase = null;
    }

    /**
     * 判断是否为先攻玩家的首个回合。
     *
     * <p>用于判断先攻首回合跳过战斗阶段（303.2.a.4.1）及号召上限（303.2.a.3.1.2）。
     */
    public boolean isFirstPlayerFirstTurn() {
        return turnCount == 1 && activePlayer == firstPlayer;
    }

    /** 切换回合玩家（回合结束时调用）。 */
    public void switchActivePlayer() {
        PlayerState tmp = activePlayer;
        activePlayer = inactivePlayer;
        inactivePlayer = tmp;
    }

    /** 追加操作流水。 */
    public void logAction(ActionLog log) {
        actionLog.add(log);
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public PlayerState getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(PlayerState activePlayer) {
        this.activePlayer = activePlayer;
    }

    public PlayerState getInactivePlayer() {
        return inactivePlayer;
    }

    public void setInactivePlayer(PlayerState inactivePlayer) {
        this.inactivePlayer = inactivePlayer;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public PhaseType getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(PhaseType currentPhase) {
        this.currentPhase = currentPhase;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public PlayerState getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(PlayerState firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public List<ActionLog> getActionLog() {
        return actionLog;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }
}
