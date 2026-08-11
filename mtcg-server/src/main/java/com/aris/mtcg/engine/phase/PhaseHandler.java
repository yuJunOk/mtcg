package com.aris.mtcg.engine.phase;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.model.GameState;

/**
 * 阶段处理器接口。
 *
 * <p>每个阶段对应一个实现类，负责该阶段的进入逻辑和阶段结束判定。 阶段完成后调用 {@code engine.advancePhase()} 推进到下一阶段（不可回退，303.2.a）。
 *
 * @author pengYuJun
 */
public interface PhaseHandler {

    /**
     * 进入该阶段时执行的逻辑。
     *
     * @param state 对局状态
     * @param engine 引擎（用于推进阶段）
     */
    void onEnter(GameState state, GameEngine engine);
}
