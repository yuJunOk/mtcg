package com.aris.mtcg.engine.phase;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.model.GameState;

/**
 * 行动处理器（303.2.a.3）。
 *
 * <p>行动阶段允许 4 种行动任意顺序交替：
 *
 * <ul>
 *   <li>基地部署（303.2.a.3.1.1，每阶段上限 1）
 *   <li>行动号召（303.2.a.3.1.2，每阶段上限 3，先攻首回合 1）
 *   <li>战基移动（303.2.a.3.1.3，每角色每回合 1，本回合进场者不能）
 *   <li>启动效果（303.2.a.3.1.4）
 * </ul>
 *
 * 玩家宣布结束 → 进入战斗阶段。 本迭代仅搭建阶段框架，具体操作在迭代五实现。
 *
 * @author pengYuJun
 */
public class ActionHandler implements PhaseHandler {

    @Override
    public void onEnter(GameState state, GameEngine engine) {
        // 行动阶段进入后等待玩家操作
        // 玩家通过 engine 执行操作（迭代五实现）
        // 玩家宣布结束 → engine.endActionPhase() → COMBAT
        // 当前迭代：框架占位，不自动推进
    }

    /**
     * 玩家宣布行动阶段结束（303.2.a.3）。
     *
     * <p>由 {@link GameEngine#endActionPhase()} 调用。
     */
    public void endPhase(GameEngine engine) {
        engine.advancePhase(); // → COMBAT
    }
}
