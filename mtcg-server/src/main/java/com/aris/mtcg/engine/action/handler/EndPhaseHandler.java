package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.combat.CombatContext;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.GameState;

/**
 * 宣布结束处理器。
 *
 * <p>行动阶段结束 → COMBAT；战斗阶段结束 → RESPONSE。 阶段进入回调（如先攻跳过战斗、初始化 CombatContext）在步骤 6/7 与 {@code
 * GameEngine} 进一步对齐。
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
        PhaseType next;
        if (current == PhaseType.ACTION) {
            next = PhaseType.COMBAT;
            // 进入战斗时挂上上下文（先攻首回合跳过由步骤 6 CombatHandler / Engine 处理）
            if (state.getCombatContext() == null) {
                state.setCombatContext(new CombatContext());
            }
        } else {
            next = PhaseType.RESPONSE;
            state.setCombatContext(null);
        }
        state.setCurrentPhase(next);

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        current,
                        request.getPlayerId(),
                        ActionType.END_PHASE.name(),
                        "宣布结束 " + current + " → " + next));

        ActionResult result = ActionResult.ok();
        result.setPhaseAdvanced(true);
        return result;
    }
}
