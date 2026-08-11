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
 * 解除结附处理器（301.26）。
 *
 * <p>解除属于移动，结附卡变回独立角色卡置于目标空位。
 *
 * @author pengYuJun
 */
public class DetachHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.DETACH;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);
        ActionSupport.assertPhase(state, PhaseType.ACTION);

        PlayerState ap = state.getActivePlayer();
        CardInstance child = ActionSupport.findAttached(ap, request.getCardCode());
        if (child == null) {
            throw new EngineException("目标不是结附卡", "301.26");
        }
        Zone targetZone = request.getTargetZone();
        if (targetZone == null || !targetZone.isOnField()) {
            throw new EngineException("解除结附须指定场上目标位置", "301.26");
        }
        ActionSupport.assertTargetFree(ap, targetZone, request.getTargetIndex());
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        PlayerState ap = state.getActivePlayer();
        CardInstance child = ActionSupport.findAttached(ap, request.getCardCode());

        ActionSupport.removeFromParent(ap, child);
        // 301.26：解除属于移动，变回角色卡
        child.setFaceDown(false);
        ActionSupport.placeOnField(ap, child, request.getTargetZone(), request.getTargetIndex());

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.DETACH.name(),
                        "解除结附 "
                                + child.getSnapshot().getCardCode()
                                + " → "
                                + request.getTargetZone()));
        return ActionResult.ok();
    }
}
