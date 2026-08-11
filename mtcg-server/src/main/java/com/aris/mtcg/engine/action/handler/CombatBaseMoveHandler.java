package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;

/**
 * 战基移动处理器（303.2.a.3.1.3 / 301.24）。
 *
 * <p>战区 ↔ 基地区；每角色每回合 1 次；本回合进场者不能移动。
 *
 * @author pengYuJun
 */
public class CombatBaseMoveHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.COMBAT_BASE_MOVE;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);
        ActionSupport.assertPhase(state, PhaseType.ACTION);

        PlayerState ap = state.getActivePlayer();
        CardInstance card = ActionSupport.findOnField(ap, request.getCardCode());
        if (card == null) {
            throw new EngineException("场上不存在该角色", "303.2.a.3.1.3");
        }
        // 本回合进场者不能移动
        if (card.isEnteredThisTurn()) {
            throw new EngineException("本回合进场的角色不能战基移动", "303.2.a.3.1.3");
        }
        // 每角色每回合 1 次
        if (card.isMovedThisTurn()) {
            throw new EngineException("该角色本回合已战基移动", "303.2.a.3.1.3");
        }
        Zone sourceZone = request.getSourceZone();
        Zone targetZone = request.getTargetZone();
        if (sourceZone == null) {
            sourceZone = card.getCurrentZone();
        }
        // 源区域与目标区域必须一为战区一为基地（301.24）
        if (!ActionSupport.isOneCombatOneBase(sourceZone, targetZone)) {
            throw new EngineException("战基移动须在战区与基地之间", "301.24");
        }
        // 目标位置空位校验（含基地上限）
        ActionSupport.assertTargetFree(ap, targetZone, request.getTargetIndex());
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        PlayerState ap = state.getActivePlayer();
        CardInstance card = ActionSupport.findOnField(ap, request.getCardCode());

        Zone targetZone = request.getTargetZone();
        ActionSupport.removeFromField(ap, card);
        ActionSupport.placeOnField(ap, card, targetZone, request.getTargetIndex());
        card.setMovedThisTurn(true);

        // 301.25：父卡移动时结附卡跟随（仍挂在 attachedCards，仅同步区域）
        for (CardInstance att : card.getAttachedCards()) {
            att.setCurrentZone(targetZone);
        }

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.COMBAT_BASE_MOVE.name(),
                        "战基移动 " + card.getSnapshot().getCardCode() + " → " + targetZone));
        return ActionResult.ok();
    }
}
