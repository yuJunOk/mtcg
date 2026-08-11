package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.combat.BattleResolver;
import com.aris.mtcg.engine.combat.CombatContext;
import com.aris.mtcg.engine.combat.DistanceCalculator;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;

/**
 * 拦截处理器（305.2）。
 *
 * <p>将攻击目标改为本卡；每角色每回合 1 次。
 *
 * @author pengYuJun
 */
public class InterceptHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.INTERCEPT;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertPhase(state, PhaseType.COMBAT);
        CombatContext ctx = CombatResponseHandler.requireResponseContext(state);

        // 通常由防守方拦截
        PlayerState player = ActionSupport.requirePlayer(state, request.getPlayerId());
        CardInstance interceptor = ActionSupport.findOnField(player, request.getCardCode());
        if (interceptor == null) {
            throw new EngineException("场上不存在拦截角色", "305.2");
        }
        if (!interceptor.getCurrentZone().isCombatZone()) {
            throw new EngineException("拦截角色须在战区", "305.2");
        }
        if (interceptor.isFaceDown()) {
            throw new EngineException("盖卡不能拦截", "305.2");
        }
        if (interceptor.isInterceptUsed()) {
            throw new EngineException("该角色本回合已使用拦截", "305.2");
        }
        CardInstance attacker = ctx.getAttacker();
        if (attacker == null) {
            throw new EngineException("当前无攻击可拦截", "305.2");
        }
        if (!DistanceCalculator.canReach(attacker, interceptor.getCurrentZone())) {
            throw new EngineException("拦截目标超出攻击者攻击距离", "305.2");
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        CombatContext ctx = CombatResponseHandler.requireResponseContext(state);
        PlayerState player = ActionSupport.requirePlayer(state, request.getPlayerId());
        CardInstance interceptor = ActionSupport.findOnField(player, request.getCardCode());

        // 305.2：改攻击目标为此卡
        ctx.setTarget(interceptor);
        ctx.setTargetZone(interceptor.getCurrentZone());
        interceptor.setInterceptUsed(true);
        ctx.resetPassStreak();

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.INTERCEPT.name(),
                        "拦截 " + interceptor.getSnapshot().getCardCode()));

        if (BattleResolver.isAttackerInvalid(ctx.getAttacker())) {
            ctx.endCombat();
            return ActionResult.ok("攻击者失效，战斗结束");
        }
        if (CombatResponseHandler.retreatToTargetIfInvalid(state, ctx)) {
            return ActionResult.ok("目标失效，返回目标步骤重选");
        }
        return ActionResult.ok();
    }
}
