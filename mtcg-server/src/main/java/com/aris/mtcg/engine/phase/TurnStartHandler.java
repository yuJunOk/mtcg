package com.aris.mtcg.engine.phase;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.model.GameState;

/**
 * 回合开始处理器（303.2.a.1）。
 *
 * <p>触发「回合开始时」效果 → 结算完毕 → 进入抽卡阶段。 当前迭代不实现效果系统，直接推进到抽卡阶段。
 *
 * @author pengYuJun
 */
public class TurnStartHandler implements PhaseHandler {

    @Override
    public void onEnter(GameState state, GameEngine engine) {
        // 303.2.a.1 触发「回合开始时」效果
        // TODO 迭代六：触发并结算回合开始效果

        // 效果结算完毕 → 进入抽卡阶段
        engine.advancePhase(); // → DRAW
    }
}
