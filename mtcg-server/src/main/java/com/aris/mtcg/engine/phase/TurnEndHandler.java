package com.aris.mtcg.engine.phase;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import com.aris.mtcg.engine.rule.RuleConstants;
import java.util.List;

/**
 * 回合结束处理器（303.2.a.6）。
 *
 * <p>执行顺序：
 *
 * <ol>
 *   <li>触发「回合结束时」效果
 *   <li>终止「仅本回合有效」效果
 *   <li>手牌 &gt; 9 → 舍弃至 9（301.15）
 *   <li>重置回合标记与计数器
 *   <li>切换回合玩家，进入下一回合
 * </ol>
 *
 * @author pengYuJun
 */
public class TurnEndHandler implements PhaseHandler {

    @Override
    public void onEnter(GameState state, GameEngine engine) {
        PlayerState active = state.getActivePlayer();

        // 303.2.a.6.1 触发「回合结束时」效果
        // TODO 迭代六：触发并结算回合结束效果

        // 303.2.a.6.2 终止「仅本回合有效」效果
        // TODO 迭代六：终止本回合效果

        // 303.2.a.6.3 手牌 > 9 → 舍弃至 9（301.15 舍弃 = 移至撤退区）
        discardToHandLimit(active);

        // 重置场上卡牌的本回合标记
        resetTurnFlags(active);
        resetTurnFlags(state.getInactivePlayer());

        // 重置行动计数器
        active.resetActionCounters();

        // 检查胜负（效果可能导致胜利）
        if (state.getStatus() == GameStatus.FINISHED) {
            return;
        }

        // 切换回合玩家
        state.switchActivePlayer();
        state.setTurnCount(state.getTurnCount() + 1);

        // 进入下一回合的回合开始阶段
        engine.advancePhase(); // → TURN_START（下一回合）
    }

    /**
     * 手牌超限时舍弃至 9 张（303.2.a.6.3）。
     *
     * <p>舍弃 = 手牌移至撤退区（301.15）。 当前迭代：简单丢弃末尾卡牌，具体选择策略由玩家操作（迭代七 API）。
     */
    private void discardToHandLimit(PlayerState player) {
        List<CardInstance> hand = player.getHand();
        while (hand.size() > RuleConstants.MAX_HAND_END_TURN) {
            CardInstance discarded = hand.remove(hand.size() - 1);
            discarded.setCurrentZone(Zone.RETREAT);
            player.getRetreat().add(discarded); // 301.15
        }
    }

    /** 重置玩家场上所有卡牌的本回合标记（303.2.a.6）。 */
    private void resetTurnFlags(PlayerState player) {
        resetCardFlags(player.getField().getVanguard());
        resetCardFlags(player.getField().getFlank()[0]);
        resetCardFlags(player.getField().getFlank()[1]);
        resetCardFlags(player.getField().getRearguard());
        for (CardInstance c : player.getField().getBase()) {
            resetCardFlags(c);
        }
    }

    private void resetCardFlags(CardInstance card) {
        if (card != null) {
            card.resetTurnFlags();
        }
    }
}
