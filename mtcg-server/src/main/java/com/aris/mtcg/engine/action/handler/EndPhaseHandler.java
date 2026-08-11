package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.GameState;

/**
 * 宣布结束处理器。
 *
 * <p>校验通过后仅标记 {@code phaseAdvanced}；实际阶段推进由 {@code GameEngine.dispatch} 调用 {@code
 * advancePhase()}，以触发阶段 Handler 的 {@code onEnter}（含先攻跳过战斗、CombatContext 初始化等）。
 *
 * @author pengYuJun
 */
public class EndPhaseHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.END_PHASE;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);
        PhaseType phase = state.getCurrentPhase();
        if (phase != PhaseType.ACTION && phase != PhaseType.COMBAT) {
            throw new EngineException("当前阶段不能宣布结束", "303.2.a");
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        PhaseType current = state.getCurrentPhase();
        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        current,
                        request.getPlayerId(),
                        ActionType.END_PHASE.name(),
                        "宣布结束 " + current));

        ActionResult result = ActionResult.ok();
        result.setPhaseAdvanced(true);
        return result;
    }
}
