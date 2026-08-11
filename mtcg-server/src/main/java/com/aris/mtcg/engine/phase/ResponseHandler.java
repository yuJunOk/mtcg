package com.aris.mtcg.engine.phase;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.model.GameState;

/**
 * 应对处理器（303.2.a.5）。
 *
 * <p>独立于战斗的应对阶段：敌方先选 → 三选一（应对号召各 1 次/阶段、应对·启动、不行动）→ 双方连续 pass 结束。 当前迭代仅搭建框架，具体操作在迭代五/六实现。
 *
 * @author pengYuJun
 */
public class ResponseHandler implements PhaseHandler {

    @Override
    public void onEnter(GameState state, GameEngine engine) {
        // 303.2.a.5 应对阶段
        // 敌方先选 → 我方后选，轮流三选一
        // 双方连续不行动 → 阶段结束
        // TODO 迭代五/六：实现应对号召与应对·启动

        // 当前迭代：直接推进到回合结束
        engine.advancePhase(); // → TURN_END
    }
}
