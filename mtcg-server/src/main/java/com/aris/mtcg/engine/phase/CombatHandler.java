package com.aris.mtcg.engine.phase;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.combat.CombatContext;
import com.aris.mtcg.engine.model.GameState;

/**
 * 战斗处理器（303.2.a.4）。
 *
 * <p>先攻第一回合跳过战斗阶段（303.2.a.4.1）。 非首回合进入时初始化 {@link CombatContext}，等待调整 / 攻击 / END_PHASE，不再无条件推进。
 *
 * @author pengYuJun
 */
public class CombatHandler implements PhaseHandler {

    @Override
    public void onEnter(GameState state, GameEngine engine) {
        // 303.2.a.4.1 先攻第一回合跳过
        if (state.isFirstPlayerFirstTurn()) {
            state.setCombatContext(null);
            engine.advancePhase(); // → RESPONSE
            return;
        }

        // 303.2.a.4.2：调整 → 按行攻击序列；由 ActionDispatcher 驱动
        if (state.getCombatContext() == null) {
            state.setCombatContext(new CombatContext());
        }
        // 等待玩家 COMBAT_ADJUST / ATTACK / END_PHASE，不自动 advancePhase
    }
}
