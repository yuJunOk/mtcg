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
import java.util.Collections;
import java.util.List;

/**
 * 行动号召处理器（303.2.a.3.1.2 / 301.19）。
 *
 * <p>每阶段最多 3 次（先攻首回合 1）；Lv3 及以下直接放置；Lv4+ 须先撤退场上合计 Lv 的角色（盖卡计 Lv1，Q&A Q7）；目标可为战区空位或基地。
 *
 * @author pengYuJun
 */
public class SummonHandler implements ActionTypeHandler {

    private static final String EXTRA_RETREAT_CODES = "retreatCodes";

    @Override
    public ActionType supportedType() {
        return ActionType.SUMMON;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);
        ActionSupport.assertPhase(state, PhaseType.ACTION);

        PlayerState ap = state.getActivePlayer();
        int limit = ap.getSummonLimit(state.isFirstPlayerFirstTurn());
        if (ap.getSummonCount() >= limit) {
            throw new EngineException("本阶段行动号召次数已用尽", "303.2.a.3.1.2");
        }

        CardInstance card = ActionSupport.findInHand(ap, request.getCardCode());
        if (card == null || !ActionSupport.isCharacter(card)) {
            throw new EngineException("手牌中不存在可号召的角色卡", "301.19");
        }

        Zone targetZone = request.getTargetZone();
        if (targetZone == null || !targetZone.isOnField()) {
            throw new EngineException("号召目标须为战区或基地", "301.19");
        }
        ActionSupport.assertTargetFree(ap, targetZone, request.getTargetIndex());

        // 301.19：Lv4+ 须先撤退合计 Lv 的角色
        int level = ActionSupport.getLevel(card);
        if (level >= 4) {
            int available = ActionSupport.computeRetrievableLevel(ap);
            if (available < level) {
                throw new EngineException(
                        "Lv4+ 号召需撤退合计 Lv=" + level + " 的角色，当前可撤退 " + available, "301.19");
            }
            List<String> retreatCodes = extractRetreatCodes(request);
            if (retreatCodes.isEmpty() || ActionSupport.sumRetreatLevel(ap, retreatCodes) < level) {
                throw new EngineException("未指定足够的撤退角色", "301.19");
            }
        }

        // 305.6：唯一关键词 —— 我方场上不能有同名卡
        if (ActionSupport.hasUniqueKeyword(card)
                && ActionSupport.hasSameNameOnField(ap, card.getSnapshot().getName())) {
            throw new EngineException("唯一关键词：场上已有同名卡", "305.6");
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        PlayerState ap = state.getActivePlayer();
        CardInstance card = ActionSupport.removeFromHand(ap, request.getCardCode());

        // Lv4+ 先撤退（301.19）
        int level = ActionSupport.getLevel(card);
        if (level >= 4) {
            for (String code : extractRetreatCodes(request)) {
                ActionSupport.retreatFromField(ap, code);
            }
        }

        // 301.12：放置 = 正面朝上
        card.setFaceDown(false);
        card.setEnteredThisTurn(true);
        ActionSupport.placeOnField(ap, card, request.getTargetZone(), request.getTargetIndex());

        ap.setSummonCount(ap.getSummonCount() + 1);
        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.SUMMON.name(),
                        "行动号召 "
                                + card.getSnapshot().getCardCode()
                                + " → "
                                + request.getTargetZone()));
        return ActionResult.ok();
    }

    private static List<String> extractRetreatCodes(ActionRequest request) {
        Object raw = request.getExtras().get(EXTRA_RETREAT_CODES);
        if (!(raw instanceof List<?> list)) {
            return Collections.emptyList();
        }
        // 仅保留字符串项
        return list.stream().filter(String.class::isInstance).map(String.class::cast).toList();
    }
}
