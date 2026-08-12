package com.aris.mtcg.engine.phase;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.effect.EffectResolver;
import com.aris.mtcg.engine.effect.TriggerEvent;
import com.aris.mtcg.engine.effect.TriggerType;
import com.aris.mtcg.engine.model.GameState;

/**
 * 回合开始处理器（303.2.a.1）。
 *
 * <p>触发「回合开始时」效果 → 结算完毕 → 进入抽卡阶段。
 *
 * @author pengYuJun
 */
public class TurnStartHandler implements PhaseHandler {

    @Override
    public void onEnter(GameState state, GameEngine engine) {
        // 303.2.a.1 触发「回合开始时」效果
        EffectResolver resolver = engine.getEffectResolver();
        if (resolver != null) {
            resolver.fireTriggers(
                    new TriggerEvent(TriggerType.TURN_START, state.getActivePlayer(), null), state);
            resolver.resolveAll(state);
        }

        // 效果结算完毕 → 进入抽卡阶段
        engine.advancePhase(); // → DRAW
    }
}
