package com.aris.mtcg.engine.combat;

import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.rule.RuleConstants;

/**
 * 胜负判定器。
 *
 * <p>规则 103.1：满足任一条件即胜。 触发时机：每次操作后、每次效果结算后、每次战斗判定后、抽卡后。
 *
 * @author pengYuJun
 */
public final class WinChecker {

    private WinChecker() {}

    /**
     * 检查胜负，若分出胜负则设置 status=FINISHED 并记录胜者。
     *
     * @return 胜者 playerId，未分胜负返回 null
     */
    public static String check(GameState state) {
        if (state.getStatus() == GameStatus.FINISHED) {
            return state.getWinnerId();
        }

        // 103.1.a：时间线 ≥ 9 张冲击卡
        String timelineWinner = checkTimeline(state);
        if (timelineWinner != null) {
            finishGame(state, timelineWinner, "时间线达到 " + RuleConstants.WIN_TIMELINE + " 张");
            return timelineWinner;
        }

        // 103.1.b：对方卡组 = 0
        String deckWinner = checkDeckEmpty(state);
        if (deckWinner != null) {
            finishGame(state, deckWinner, "对方卡组耗尽");
            return deckWinner;
        }

        // 103.1.c：效果宣布获胜 —— 由 EffectResolver 在迭代六调用 declareWin
        return null;
    }

    /** 103.1.c：效果宣布获胜（迭代六接入）。 */
    public static void declareWin(GameState state, String winnerId, String reason) {
        finishGame(state, winnerId, "效果宣布获胜: " + reason);
    }

    /** 103.1.a：时间线 ≥ {@link RuleConstants#WIN_TIMELINE}。 */
    private static String checkTimeline(GameState state) {
        if (state.getActivePlayer().getTimeline().size() >= RuleConstants.WIN_TIMELINE) {
            return state.getActivePlayer().getPlayerId();
        }
        if (state.getInactivePlayer().getTimeline().size() >= RuleConstants.WIN_TIMELINE) {
            return state.getInactivePlayer().getPlayerId();
        }
        return null;
    }

    /** 103.1.b：对方卡组 = 0。A 卡组为 0 则 B 胜。 */
    private static String checkDeckEmpty(GameState state) {
        if (state.getActivePlayer().getDeck().isEmpty()) {
            return state.getInactivePlayer().getPlayerId();
        }
        if (state.getInactivePlayer().getDeck().isEmpty()) {
            return state.getActivePlayer().getPlayerId();
        }
        return null;
    }

    private static void finishGame(GameState state, String winnerId, String reason) {
        state.setStatus(GameStatus.FINISHED);
        state.setWinnerId(winnerId);
        state.logAction(
                new ActionLog(
                        state.getTurnCount(), state.getCurrentPhase(), winnerId, "WIN", reason));
    }
}
