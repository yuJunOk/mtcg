package com.aris.mtcg.engine.effect;

import com.aris.mtcg.engine.action.ActionSupport;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 效果结算器（规则 304）。
 *
 * <p>管理触发队列、结算顺序、优先级；常驻效果通过 {@link #reevaluateContinuous} 重算。
 *
 * @author pengYuJun
 */
public class EffectResolver {

    /** 触发队列：批次为粒度，新批次插队首（LIFO，304.1.f）。 */
    private final Deque<TriggerBatch> triggerQueue = new ArrayDeque<>();

    private final PowerModifierStack modifierStack;
    private final EffectParser effectParser;

    public EffectResolver(PowerModifierStack modifierStack, EffectParser effectParser) {
        this.modifierStack = modifierStack;
        this.effectParser = effectParser;
    }

    public PowerModifierStack getModifierStack() {
        return modifierStack;
    }

    public EffectParser getEffectParser() {
        return effectParser;
    }

    /**
     * 触发入口：游戏某事件发生时调用。
     *
     * <p>收集匹配触发 → 同一批次（304.1.g）→ 排序 → 插队首（304.1.f）。
     */
    public void fireTriggers(TriggerEvent event, GameState state) {
        TriggerBatch batch = collectMatchingTriggers(event, state);
        if (batch.isEmpty()) {
            return;
        }
        batch.orderByTurnPlayerFirst(state.getActivePlayer());
        triggerQueue.addFirst(batch);
    }

    /**
     * 结算队列直至空（304.1）。
     *
     * <p>D6-3：可选触发（含「可以」）本迭代默认跳过。
     */
    public void resolveAll(GameState state) {
        while (!triggerQueue.isEmpty()) {
            TriggerBatch batch = triggerQueue.pollFirst();
            for (TriggeredEffect te : batch.getEffects()) {
                if (!te.isStillValid()) {
                    continue;
                }
                // 304.1.a / D6-3：可选且玩家放弃（本迭代默认放弃）
                if (te.getEffect().isOptional()) {
                    continue;
                }
                resolveEffect(te, state);
            }
        }
    }

    /**
     * 启动型效果入口（行动/应对阶段/战斗应对步骤，304.2）。
     *
     * <p>Q&A Q3：结附卡不失去效果，仍可启动。
     */
    public void activate(Effect effect, EffectContext ctx) {
        if (!hasResolvableContent(effect, ctx)) {
            throw new IllegalStateException("无可结算效果，不能启动（304.2）");
        }
        // 盖卡失去效果（301.21）；结附卡仍有效（Q&A Q3）
        CardInstance source = ctx.getSource();
        if (source != null && source.isFaceDown()) {
            throw new IllegalStateException("盖卡不能启动效果（301.21）");
        }
        resolveActivated(effect, ctx);
    }

    /**
     * 常驻型效果重算入口（201.10.e / Q&A Q6）。
     *
     * <p>重算后若战力归零则撤退（301.16.b / Q&A Q2）。
     */
    public void reevaluateContinuous(GameState state) {
        modifierStack.recomputeAll(state);
        retreatZeroPower(state);
    }

    /**
     * 卡牌进场钩子（Q&A Q6）：先常驻重算，再触发进场效果。
     *
     * @param card 进场卡
     * @param controller 控制者
     * @param state 对局状态
     */
    public void onCardEntersZone(CardInstance card, PlayerState controller, GameState state) {
        // 1. 立即重算常驻（Q6）
        reevaluateContinuous(state);
        // 若因常驻归零已撤退，仍可触发其他卡的进场监听，但不收集自身
        fireTriggers(new TriggerEvent(TriggerType.ENTERS_ZONE, controller, card), state);
        resolveAll(state);
    }

    private TriggerBatch collectMatchingTriggers(TriggerEvent event, GameState state) {
        TriggerBatch batch = new TriggerBatch();
        for (PlayerState player : List.of(state.getActivePlayer(), state.getInactivePlayer())) {
            for (CardInstance card : collectRelevantCards(player)) {
                for (Effect effect : card.getEffects()) {
                    if (effect.getType() != EffectType.TRIGGER) {
                        continue;
                    }
                    if (!matchesTrigger(effect, event)) {
                        continue;
                    }
                    // Q&A Q4：自身进场不触发自身效果
                    if (event.getType() == TriggerType.ENTERS_ZONE
                            && event.getSource() != null
                            && event.getSource() == card) {
                        continue;
                    }
                    EffectContext ctx = new EffectContext(state, card, null, event);
                    batch.add(new TriggeredEffect(effect, ctx, player));
                }
            }
        }
        return batch;
    }

    /** Q&A Q5：效果对象基于最新 state 选择（结算时刷新 context）。 */
    private void resolveEffect(TriggeredEffect te, GameState state) {
        te.getContext().setGameState(state);
        // 本迭代空壳：无可执行 DSL，仅记流水
        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        te.getController().getPlayerId(),
                        "TRIGGER_EFFECT",
                        "触发效果结算（空壳）"
                                + (te.getEffect().getText() != null
                                        ? te.getEffect().getText()
                                        : "")));
    }

    private void resolveActivated(Effect effect, EffectContext ctx) {
        GameState state = ctx.getGameState();
        String playerId =
                state.getActivePlayer() != null ? state.getActivePlayer().getPlayerId() : null;
        state.logAction(
                new ActionLog(
                        state.getTurnCount(),
                        state.getCurrentPhase(),
                        playerId,
                        "ACTIVATE_EFFECT",
                        "启动效果结算（空壳）" + (effect.getText() != null ? effect.getText() : "")));
    }

    /**
     * 304.2：须存在可结算内容才能启动。
     *
     * <p>本迭代：有 ACTIVATED / RESPONSE_ACTIVATED 类型，或非空效果文本即可。
     */
    public boolean hasResolvableContent(Effect effect, EffectContext ctx) {
        if (effect == null) {
            return false;
        }
        if (effect.getType() == EffectType.ACTIVATED
                || effect.getType() == EffectType.RESPONSE_ACTIVATED) {
            return true;
        }
        return effect.getText() != null && !effect.getText().isBlank();
    }

    /** 校验启动时点是否合法（304.2 / 303.2.a.3.1.4）。 */
    public boolean canActivateAtCurrentPhase(Effect effect, GameState state) {
        if (effect == null) {
            return false;
        }
        PhaseType phase = state.getCurrentPhase();
        EffectType type = effect.getType();
        if (type == EffectType.RESPONSE_ACTIVATED) {
            // 行动 / 应对阶段 / 战斗应对步骤
            return phase == PhaseType.ACTION
                    || phase == PhaseType.RESPONSE
                    || phase == PhaseType.COMBAT;
        }
        // ACTIVATED 或未标注类型：默认仅行动阶段
        return phase == PhaseType.ACTION;
    }

    private static boolean matchesTrigger(Effect effect, TriggerEvent event) {
        // 本迭代：TRIGGER 类型且 requirement 未细分时，按事件类型宽松匹配
        // 若 requirement 含关键字样则做粗匹配
        String req = effect.getRequirement();
        if (req == null || req.isBlank()) {
            return true;
        }
        return switch (event.getType()) {
            case TURN_START -> req.contains("回合开始");
            case TURN_END -> req.contains("回合结束");
            case ENTERS_ZONE -> req.contains("进场") || req.contains("号召") || req.contains("放置");
            default -> true;
        };
    }

    private static List<CardInstance> collectRelevantCards(PlayerState player) {
        List<CardInstance> list = new ArrayList<>(ActionSupport.listFieldCards(player));
        // 结附卡效果仍有效（Q&A Q3）
        List<CardInstance> attached = new ArrayList<>();
        for (CardInstance c : list) {
            attached.addAll(c.getAttachedCards());
        }
        list.addAll(attached);
        return list;
    }

    /** 301.16.b：战力为 0 → 锁定撤退。 */
    private void retreatZeroPower(GameState state) {
        for (PlayerState player : List.of(state.getActivePlayer(), state.getInactivePlayer())) {
            List<CardInstance> toRetreat = new ArrayList<>();
            for (CardInstance card : ActionSupport.listFieldCards(player)) {
                if (card.getCurrentPower() == 0 && ActionSupport.isCharacter(card)) {
                    toRetreat.add(card);
                }
            }
            for (CardInstance card : toRetreat) {
                ActionSupport.retreatCard(player, card);
            }
        }
    }
}
