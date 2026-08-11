package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;

/**
 * 翻开盖卡处理器（301.22）。
 *
 * <p>盖卡翻开变回角色卡；不存在「本回合放置进场」状态。
 *
 * @author pengYuJun
 */
public class FlipFaceUpHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.FLIP_FACE_UP;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);
        CardInstance card =
                ActionSupport.findOnField(state.getActivePlayer(), request.getCardCode());
        if (card == null || !card.isFaceDown()) {
            throw new EngineException("目标不是盖卡", "301.22");
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        CardInstance card =
                ActionSupport.findOnField(state.getActivePlayer(), request.getCardCode());
        card.setFaceDown(false);
        // 301.22：翻开盖卡变回角色卡，不存在「本回合放置进场」状态
        card.setEnteredThisTurn(false);

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.FLIP_FACE_UP.name(),
                        "翻开 " + card.getSnapshot().getCardCode()));
        return ActionResult.ok();
    }
}
