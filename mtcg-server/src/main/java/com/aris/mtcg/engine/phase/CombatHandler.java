package com.aris.mtcg.engine.phase;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.model.GameState;

/**
 * 战斗处理器（303.2.a.4）。
 *
 * <p>先攻第一回合跳过战斗阶段（303.2.a.4.1）。 战斗阶段内部：调整位置 → 按行攻击序列。 当前迭代仅实现跳过逻辑，战斗判定在迭代五实现。
 *
 * @author pengYuJun
 */
public class CombatHandler implements PhaseHandler {

    @Override
    public void onEnter(GameState state, GameEngine engine) {
        // 303.2.a.4.1 先攻第一回合跳过
        if (state.isFirstPlayerFirstTurn()) {
            engine.advancePhase(); // → RESPONSE，跳过战斗
            return;
        }

        // 303.2.a.4.2 战斗阶段流程：
        // 1. 调整位置（最多 4 张互换，不算移动）
        // 2. 按先锋→侧翼→后卫攻击序列
        // TODO 迭代五：实现战斗判定与距离计算

        // 当前迭代：骨架直接进入应对阶段
        engine.advancePhase(); // → RESPONSE
    }
}
