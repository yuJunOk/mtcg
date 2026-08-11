package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.combat.BattleResolver;
import com.aris.mtcg.engine.combat.CombatContext;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;

/**
 * 应对号召处理器（303.2.a.4.3.2.3.1）。
 *
 * <p>本步骤双方各 1 次；目标可为战区空位或基地（Q&A Q1）。不占用行动阶段号召次数。
 *
 * @author pengYuJun
 */
public class ResponseSummonHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.RESPONSE_SUMMON;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertPhase(state, PhaseType.COMBAT);
        CombatContext ctx = CombatResponseHandler.requireResponseContext(state);

        if (ctx.isResponseSummonUsed(request.getPlayerId())) {
            throw new EngineException("本步骤应对号召已用", "303.2.a.4.3.2.3.1");
        }

        PlayerState player = ActionSupport.requirePlayer(state, request.getPlayerId());
        CardInstance card = ActionSupport.findInHand(player, request.getCardCode());
        if (card == null || !ActionSupport.isCharacter(card)) {
            throw new EngineException("手牌中不存在可号召的角色卡", "303.2.a.4.3.2.3.1");
        }

        Zone targetZone = request.getTargetZone();
        // Q&A Q1：应对号召可放基地
        if (targetZone == null || !targetZone.isOnField()) {
            throw new EngineException("应对号召目标须为战区或基地", "303.2.a.4.3.2.3.1");
        }
        ActionSupport.assertTargetFree(player, targetZone, request.getTargetIndex());
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        CombatContext ctx = CombatResponseHandler.requireResponseContext(state);
        PlayerState player = ActionSupport.requirePlayer(state, request.getPlayerId());
        CardInstance card = ActionSupport.removeFromHand(player, request.getCardCode());

        card.setFaceDown(false);
        card.setEnteredThisTurn(true);
        ActionSupport.placeOnField(player, card, request.getTargetZone(), request.getTargetIndex());

        ctx.markResponseSummonUsed(request.getPlayerId());
        ctx.resetPassStreak();

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.RESPONSE_SUMMON.name(),
                        "应对号召 "
                                + card.getSnapshot().getCardCode()
                                + " → "
                                + request.getTargetZone()));

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
