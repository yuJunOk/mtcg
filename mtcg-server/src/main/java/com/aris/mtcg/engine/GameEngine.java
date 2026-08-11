package com.aris.mtcg.engine;

import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.phase.ActionPhaseHandler;
import com.aris.mtcg.engine.phase.CombatHandler;
import com.aris.mtcg.engine.phase.DrawHandler;
import com.aris.mtcg.engine.phase.PhaseHandler;
import com.aris.mtcg.engine.phase.ResponseHandler;
import com.aris.mtcg.engine.phase.TurnEndHandler;
import com.aris.mtcg.engine.phase.TurnStartHandler;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;

/**
 * 对战引擎核心。
 *
 * <p>纯 POJO，不依赖 Spring。通过 new 创建，管理 GameState 和阶段流转。
 *
 * <p>阶段流转不可回退（303.2.a），按 TURN_START → DRAW → ACTION → COMBAT → RESPONSE → TURN_END 顺序执行。
 *
 * @author pengYuJun
 */
public class GameEngine {

    @Getter private final GameState state;
    private final Map<PhaseType, PhaseHandler> handlers = new EnumMap<>(PhaseType.class);

    public GameEngine(GameState state) {
        this.state = state;
        registerHandlers();
    }

    /** 注册 6 个阶段处理器。 */
    private void registerHandlers() {
        handlers.put(PhaseType.TURN_START, new TurnStartHandler());
        handlers.put(PhaseType.DRAW, new DrawHandler());
        handlers.put(PhaseType.ACTION, new ActionPhaseHandler());
        handlers.put(PhaseType.COMBAT, new CombatHandler());
        handlers.put(PhaseType.RESPONSE, new ResponseHandler());
        handlers.put(PhaseType.TURN_END, new TurnEndHandler());
    }

    /** 开始对局：设置状态为进行中，进入第一回合的回合开始阶段（303.2.a.1）。 */
    public void startGame() {
        state.setStatus(GameStatus.IN_PROGRESS);
        state.setTurnCount(1);
        enterPhase(PhaseType.TURN_START);
    }

    /** 进入指定阶段并执行该阶段的进入逻辑。 */
    public void enterPhase(PhaseType phase) {
        state.setCurrentPhase(phase);
        PhaseHandler handler = handlers.get(phase);
        handler.onEnter(state, this);
    }

    /**
     * 推进到下一阶段（不可回退，303.2.a）。
     *
     * <p>由当前阶段的 handler 在完成本阶段逻辑后调用。
     */
    public void advancePhase() {
        PhaseType next = state.getCurrentPhase().next();
        enterPhase(next);
    }

    /**
     * 结束行动阶段（303.2.a.3）。
     *
     * <p>本迭代供测试/外部调用；迭代五由 END_PHASE 操作触发。
     */
    public void endActionPhase() {
        if (state.getCurrentPhase() != PhaseType.ACTION) {
            throw new IllegalStateException("当前不在行动阶段: " + state.getCurrentPhase());
        }
        PhaseHandler handler = handlers.get(PhaseType.ACTION);
        ((ActionPhaseHandler) handler).endPhase(this);
    }

    /**
     * 结束对局（胜负判定或认输）。
     *
     * @param winnerId 胜利者玩家 ID
     */
    public void endGame(String winnerId) {
        state.setStatus(GameStatus.FINISHED);
        state.setWinnerId(winnerId);
    }
}
