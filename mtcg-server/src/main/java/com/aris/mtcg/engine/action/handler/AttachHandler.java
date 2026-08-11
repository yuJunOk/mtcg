package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;

/**
 * 结附处理器（301.25）。
 *
 * <p>待结附卡可来自场上或手牌（Q&A Q8：手牌结附不占号召次数）。 不递增 {@code summonCount}；不清除结附卡效果定义（Q&A Q3）。
 *
 * @author pengYuJun
 */
public class AttachHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.ATTACH;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);
        ActionSupport.assertPhase(state, PhaseType.ACTION);

        PlayerState ap = state.getActivePlayer();
        CardInstance child = findChild(ap, request.getCardCode());
        CardInstance parent = ActionSupport.findOnField(ap, request.getTargetCardCode());
        if (child == null || parent == null) {
            throw new EngineException("结附双方须均可用（场上或手牌→场上）", "301.25");
        }
        if (child == parent) {
            throw new EngineException("不能结附自身", "301.25");
        }
        // 301.25：不能对已结附的父卡再次结附
        if (!parent.getAttachedCards().isEmpty()) {
            throw new EngineException("父卡已结附，不能再结附", "301.25");
        }
        if (parent.isFaceDown() || !ActionSupport.isCharacter(parent)) {
            throw new EngineException("父卡须为正面角色卡", "301.25");
        }
        if (child.isFaceDown()) {
            throw new EngineException("盖卡不能作为结附卡", "301.25");
        }
        // 已是结附卡则不可再结附
        if (ActionSupport.findAttachParent(ap, child) != null) {
            throw new EngineException("目标已是结附卡", "301.25");
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        PlayerState ap = state.getActivePlayer();
        CardInstance child = findChild(ap, request.getCardCode());
        CardInstance parent = ActionSupport.findOnField(ap, request.getTargetCardCode());

        // 从原区域移除（手牌或场上槽位）；不改 summonCount（Q&A Q8）
        if (ap.getHand().contains(child)) {
            ap.getHand().remove(child);
        } else {
            ActionSupport.removeFromField(ap, child);
        }

        // 301.25.m：按结附顺序叠放；Q&A Q3：不清除效果定义
        parent.getAttachedCards().add(child);
        child.setCurrentZone(parent.getCurrentZone());
        child.setFaceDown(false);

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.ATTACH.name(),
                        "结附 "
                                + child.getSnapshot().getCardCode()
                                + " → "
                                + parent.getSnapshot().getCardCode()));
        return ActionResult.ok();
    }

    /** 待结附卡：优先场上，其次手牌（Q&A Q8）。 */
    private static CardInstance findChild(PlayerState ap, String ref) {
        CardInstance onField = ActionSupport.findOnField(ap, ref);
        if (onField != null) {
            return onField;
        }
        return ActionSupport.findInHand(ap, ref);
    }
}
