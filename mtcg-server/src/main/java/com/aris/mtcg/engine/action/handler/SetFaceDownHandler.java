package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import java.util.ArrayList;
import java.util.List;

/**
 * 盖卡/盖伏处理器（301.21 / 301.12–301.14）。
 *
 * <p>将场上角色翻至背面；盖伏时刷新状态并结束已作用效果（结附撤离至撤退区）。
 *
 * @author pengYuJun
 */
public class SetFaceDownHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.SET_FACE_DOWN;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);
        CardInstance card =
                ActionSupport.findOnField(state.getActivePlayer(), request.getCardCode());
        if (card == null) {
            throw new EngineException("场上不存在该卡", "301.21");
        }
        if (card.isFaceDown()) {
            throw new EngineException("目标已是盖卡", "301.21");
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        PlayerState ap = state.getActivePlayer();
        CardInstance card = ActionSupport.findOnField(ap, request.getCardCode());

        // 301.13 盖伏：状态刷新，已作用效果结束
        card.setFaceDown(true);
        ActionSupport.resetCombatStats(card);

        // 结附卡随盖伏结束撤离（避免从区域模型中丢失）
        List<CardInstance> attached = new ArrayList<>(card.getAttachedCards());
        card.getAttachedCards().clear();
        for (CardInstance att : attached) {
            att.setCurrentZone(Zone.RETREAT);
            att.setFaceDown(false);
            ap.getRetreat().add(att);
        }

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.SET_FACE_DOWN.name(),
                        "盖卡 " + card.getSnapshot().getCardCode()));
        return ActionResult.ok();
    }
}
