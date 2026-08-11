package com.aris.mtcg.engine.phase;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.combat.WinChecker;
import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import com.aris.mtcg.engine.rule.RuleConstants;
import java.util.List;

/**
 * 抽卡处理器（303.2.a.2）。
 *
 * <p>回合玩家抽 2 张 → 结算完毕 → 进入行动阶段。
 *
 * @author pengYuJun
 */
public class DrawHandler implements PhaseHandler {

    @Override
    public void onEnter(GameState state, GameEngine engine) {
        PlayerState active = state.getActivePlayer();

        // 303.2.a.2 回合玩家抽 2 张
        drawCards(active, RuleConstants.DRAW_PER_TURN);

        // 103.1.b 卡组耗尽 → 胜负（统一走 WinChecker）
        WinChecker.check(state);

        if (state.getStatus() == GameStatus.FINISHED) {
            return;
        }

        // 结算完毕 → 进入行动阶段
        engine.advancePhase(); // → ACTION
    }

    /** 从卡组顶抽 n 张到手牌（卡组顶 = 列表末尾）。 */
    private void drawCards(PlayerState player, int n) {
        List<CardInstance> deck = player.getDeck();
        List<CardInstance> hand = player.getHand();
        int drawCount = Math.min(n, deck.size()); // 302.13.f
        for (int i = 0; i < drawCount; i++) {
            CardInstance card = deck.remove(deck.size() - 1);
            card.setCurrentZone(Zone.HAND);
            hand.add(card);
        }
    }
}
