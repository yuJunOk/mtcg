package com.aris.mtcg.engine.action.handler;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.ActionTypeHandler;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.effect.Effect;
import com.aris.mtcg.engine.effect.EffectContext;
import com.aris.mtcg.engine.effect.EffectResolver;
import com.aris.mtcg.engine.effect.EffectType;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;

/**
 * 启动效果处理器（303.2.a.3.1.4 / 304.2）。
 *
 * <p>委托 {@link EffectResolver#activate}；本迭代效果结算为空壳记流水。
 *
 * @author pengYuJun
 */
public class ActivateEffectHandler implements ActionTypeHandler {

    private final EffectResolver effectResolver;

    public ActivateEffectHandler(EffectResolver effectResolver) {
        this.effectResolver = effectResolver;
    }

    @Override
    public ActionType supportedType() {
        return ActionType.ACTIVATE_EFFECT;
    }

    @Override
    public void validate(GameState state, ActionRequest request) {
        ActionSupport.assertActivePlayer(state, request);

        CardInstance card = findActivatableCard(state, request.getCardCode());
        if (card == null) {
            throw new EngineException("场上不存在该角色", "303.2.a.3.1.4");
        }
        if (card.isFaceDown()) {
            throw new EngineException("盖卡不能启动效果", "303.2.a.3.1.4");
        }

        Effect effect = pickActivatableEffect(card);
        if (effect == null) {
            throw new EngineException("无可启动效果", "304.2");
        }
        if (!effectResolver.canActivateAtCurrentPhase(effect, state)) {
            throw new EngineException("当前阶段不能启动该效果", "304.2");
        }
        // 行动阶段启动型须在 ACTION；应对·启动另允许 COMBAT/RESPONSE
        if (effect.getType() == EffectType.ACTIVATED || effect.getType() == null) {
            ActionSupport.assertPhase(state, PhaseType.ACTION);
        }
    }

    @Override
    public ActionResult execute(GameState state, ActionRequest request) {
        CardInstance card = findActivatableCard(state, request.getCardCode());
        Effect effect = pickActivatableEffect(card);
        EffectContext ctx = new EffectContext(state, card);
        try {
            effectResolver.activate(effect, ctx);
        } catch (IllegalStateException e) {
            throw new EngineException(e.getMessage(), "304.2");
        }
        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        request.getPlayerId(),
                        ActionType.ACTIVATE_EFFECT.name(),
                        "启动效果 " + request.getCardCode()));
        return ActionResult.ok();
    }

    /** 场上主卡或结附卡（Q&A Q3）。 */
    private static CardInstance findActivatableCard(GameState state, String ref) {
        CardInstance card = ActionSupport.findOnField(state.getActivePlayer(), ref);
        if (card != null) {
            return card;
        }
        return ActionSupport.findAttached(state.getActivePlayer(), ref);
    }

    /** 选取可启动效果：优先 ACTIVATED / RESPONSE_ACTIVATED；否则取首个非空文本效果。 */
    private static Effect pickActivatableEffect(CardInstance card) {
        if (card == null) {
            return null;
        }
        Effect fallback = null;
        for (Effect e : card.getEffects()) {
            if (e.getType() == EffectType.ACTIVATED
                    || e.getType() == EffectType.RESPONSE_ACTIVATED) {
                return e;
            }
            if (fallback == null && e.getText() != null && !e.getText().isBlank()) {
                fallback = e;
            }
        }
        return fallback;
    }
}
