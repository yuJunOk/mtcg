package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.AdjustPair;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.combat.CombatContext;
import com.aris.mtcg.engine.combat.CombatStep;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import com.aris.mtcg.engine.rule.RuleConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 调整位置处理器（303.2.a.4.2.1）。
 *
 * <p>最多 {@link RuleConstants#MAX_COMBAT_ADJUST} 对战区互换；不计为移动；每回合 1 次。
 *
 * @author pengYuJun
 */
public class CombatAdjustHandler implements ActionTypeHandler {

    public static final String EXTRA_ADJUST_PAIRS = "adjustPairs";

    @Override
    public ActionType supportedType() {
        return ActionType.COMBAT_ADJUST;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);
        ActionSupport.assertPhase(state, PhaseType.COMBAT);

        CombatContext ctx = state.getCombatContext();
        if (ctx == null) {
            throw new EngineException("战斗上下文未初始化", "303.2.a.4");
        }
        if (ctx.getStep() != CombatStep.ADJUST) {
            throw new EngineException("当前不在调整步骤", "303.2.a.4.2.1");
        }

        PlayerState ap = state.getActivePlayer();
        if (ap.isAdjustUsed()) {
            throw new EngineException("本回合已使用调整位置", "303.2.a.4.2.1");
        }

        List<AdjustPair> pairs = parsePairs(request);
        if (pairs.isEmpty() || pairs.size() > RuleConstants.MAX_COMBAT_ADJUST) {
            throw new EngineException(
                    "调整数量须 1-" + RuleConstants.MAX_COMBAT_ADJUST + " 对", "303.2.a.4.2.1");
        }
        for (AdjustPair p : pairs) {
            if (p.getFrom() == null
                    || p.getTo() == null
                    || !p.getFrom().isCombatZone()
                    || !p.getTo().isCombatZone()) {
                throw new EngineException("调整位置仅限战区", "303.2.a.4.2.1");
            }
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        PlayerState ap = state.getActivePlayer();
        List<AdjustPair> pairs = parsePairs(request);
        for (AdjustPair p : pairs) {
            ActionSupport.swapCombatPositions(ap.getField(), p.getFrom(), p.getTo());
        }
        ap.setAdjustUsed(true);

        CombatContext ctx = state.getCombatContext();
        ctx.setStep(CombatStep.TARGET);

        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.COMBAT_ADJUST.name(),
                        "调整位置 " + pairs.size() + " 对"));
        return ActionResult.ok();
    }

    @SuppressWarnings("unchecked")
    static List<AdjustPair> parsePairs(ActionRequest request) {
        Object raw = request.getExtras().get(EXTRA_ADJUST_PAIRS);
        if (!(raw instanceof List<?> list) || list.isEmpty()) {
            return List.of();
        }
        List<AdjustPair> pairs = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof AdjustPair pair) {
                pairs.add(pair);
            } else if (item instanceof Map<?, ?> map) {
                Zone from = toZone(map.get("from"));
                Zone to = toZone(map.get("to"));
                pairs.add(new AdjustPair(from, to));
            }
        }
        return pairs;
    }

    private static Zone toZone(Object value) {
        if (value instanceof Zone zone) {
            return zone;
        }
        if (value instanceof String name) {
            return Zone.valueOf(name);
        }
        return null;
    }
}
