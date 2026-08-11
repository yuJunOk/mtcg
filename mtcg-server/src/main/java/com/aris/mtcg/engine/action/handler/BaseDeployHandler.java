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
import com.aris.mtcg.engine.rule.RuleConstants;

/**
 * 基地部署处理器（303.2.a.3.1.1）。
 *
 * <p>手牌 1 张角色卡盖入基地区 → 抽 1 张；每阶段最多 1 次。
 *
 * @author pengYuJun
 */
public class BaseDeployHandler implements ActionTypeHandler {

    @Override
    public ActionType supportedType() {
        return ActionType.BASE_DEPLOY;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        // 303.2.a.3.1.1 仅回合玩家可在行动阶段执行
        ActionSupport.assertActivePlayer(state, request);
        ActionSupport.assertPhase(state, PhaseType.ACTION);

        PlayerState ap = state.getActivePlayer();
        // 每阶段最多 1 次
        if (ap.getBaseDeployCount() >= RuleConstants.MAX_BASE_DEPLOY) {
            throw new EngineException("本阶段基地部署次数已用尽", "303.2.a.3.1.1");
        }
        // 基地区上限 6（角色+盖卡），规则 302.6
        if (ap.getField().isBaseFull()) {
            throw new EngineException("基地区已满（上限 " + RuleConstants.MAX_BASE + "）", "302.6");
        }
        CardInstance card = ActionSupport.findInHand(ap, request.getCardCode());
        if (card == null) {
            throw new EngineException("手牌中不存在该卡", "303.2.a.3.1.1");
        }
        if (!ActionSupport.isCharacter(card)) {
            throw new EngineException("仅角色卡可基地部署", "303.2.a.3.1.1");
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        PlayerState ap = state.getActivePlayer();
        CardInstance card = ActionSupport.removeFromHand(ap, request.getCardCode());

        // 301.12-301.14：盖放 = 背面朝上置入基地区
        card.setFaceDown(true);
        card.setEnteredThisTurn(true);
        ActionSupport.placeOnField(ap, card, Zone.BASE, request.getTargetIndex());

        // 抽 1 张（303.2.a.3.1.1）
        ActionSupport.drawFromDeck(ap, 1);

        ap.setBaseDeployCount(ap.getBaseDeployCount() + 1);
        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.BASE_DEPLOY.name(),
                        "基地部署 " + card.getSnapshot().getCardCode()));
        return ActionResult.ok();
    }
}
