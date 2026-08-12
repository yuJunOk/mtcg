package com.aris.mtcg.engine.effect;

import static com.aris.mtcg.engine.EngineFixtures.character;
import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static com.aris.mtcg.engine.EngineFixtures.placeOnCombat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

/**
 * EffectResolver 最小路径（Q4 / Q6 / activate）。
 *
 * @author pengYuJun
 */
class EffectResolverTest {

    @Test
    void q4_entersZone_excludesSelfTrigger() {
        GameEngine engine = newStartedEngine(201L);
        GameState state = engine.getState();
        PlayerState ap = state.getActivePlayer();
        CardInstance card = placeOnCombat(ap, Zone.VANGUARD, character("E1", 1, 1, 1000));

        AtomicBoolean selfFired = new AtomicBoolean(false);
        Effect selfTrigger =
                new Effect(
                        EffectType.TRIGGER, Zone.VANGUARD, "此卡进场时", "抽1张", null, null, List.of());
        card.addEffect(selfTrigger);

        // 用可观察的旁观者：另一张卡的触发仍会被收集
        CardInstance other = placeOnCombat(ap, Zone.FLANK_LEFT, character("E2", 1, 1, 1000));
        Effect otherTrigger =
                new Effect(
                        EffectType.TRIGGER, Zone.VANGUARD, "进场时", "可以抽1张", null, null, List.of());
        // 「可以」可选 → resolveAll 默认跳过（D6-3）；用必发效果
        Effect otherMust =
                new Effect(EffectType.TRIGGER, Zone.VANGUARD, "进场时", "获得战力", null, null, List.of());
        other.addEffect(otherMust);

        EffectResolver resolver = engine.getEffectResolver();
        int logBefore = state.getActionLog().size();
        resolver.fireTriggers(new TriggerEvent(TriggerType.ENTERS_ZONE, ap, card), state);
        resolver.resolveAll(state);

        // 自身 TRIGGER 被 Q4 排除；other 的必发被结算（记流水）
        boolean otherLogged =
                state.getActionLog().stream()
                        .skip(logBefore)
                        .anyMatch(l -> "TRIGGER_EFFECT".equals(l.getActionType()));
        assertTrue(otherLogged);
        // 确认自身效果仍在列表中（未被误删），只是未结算
        assertTrue(card.getEffects().contains(selfTrigger));
        assertFalse(selfFired.get());
    }

    @Test
    void q6_reevaluateContinuous_beforeTriggers_zeroPowerRetreats() {
        GameEngine engine = newStartedEngine(202L);
        GameState state = engine.getState();
        PlayerState ap = state.getActivePlayer();
        PlayerState opp = state.getInactivePlayer();

        CardInstance victim = placeOnCombat(ap, Zone.VANGUARD, character("V1", 1, 1, 500));
        CardInstance aura = placeOnCombat(opp, Zone.VANGUARD, character("A1", 1, 1, 1000));

        // 常驻：对手战力 -500
        engine.getEffectResolver()
                .getModifierStack()
                .add(
                        new PowerModifier(
                                aura,
                                victim,
                                PowerModifier.Category.CONTINUOUS,
                                PowerModifier.Type.INCREMENT,
                                PowerModifier.Attribute.POWER,
                                -500,
                                PowerModifier.Duration.WHILE_SOURCE_ON_FIELD));

        engine.getEffectResolver().onCardEntersZone(victim, ap, state);

        assertEquals(0, victim.getCurrentPower());
        assertTrue(ap.getRetreat().contains(victim));
    }

    @Test
    void activate_withoutResolvableContent_throws() {
        GameEngine engine = newStartedEngine(203L);
        GameState state = engine.getState();
        PlayerState ap = state.getActivePlayer();
        CardInstance card = placeOnCombat(ap, Zone.VANGUARD, character("X1", 1, 1, 1000));

        Effect empty = new Effect(null, null, null, null, null, null, List.of());
        EffectContext ctx = new EffectContext(state, card);
        assertThrows(
                IllegalStateException.class, () -> engine.getEffectResolver().activate(empty, ctx));
    }

    @Test
    void activate_withText_succeeds() {
        GameEngine engine = newStartedEngine(204L);
        GameState state = engine.getState();
        PlayerState ap = state.getActivePlayer();
        CardInstance card =
                placeOnCombat(ap, Zone.VANGUARD, character("X2", "启动卡", 1, 1, 1000, "抽1张卡"));

        Effect activated =
                new Effect(
                        EffectType.ACTIVATED, Zone.VANGUARD, null, "抽1张卡", null, null, List.of());
        card.addEffect(activated);
        EffectContext ctx = new EffectContext(state, card);
        engine.getEffectResolver().activate(activated, ctx);
        assertTrue(
                state.getActionLog().stream()
                        .anyMatch(l -> "ACTIVATE_EFFECT".equals(l.getActionType())));
    }

    @Test
    void optionalTrigger_skippedByDefault() {
        GameEngine engine = newStartedEngine(205L);
        GameState state = engine.getState();
        PlayerState ap = state.getActivePlayer();
        CardInstance card = placeOnCombat(ap, Zone.VANGUARD, character("O1", 1, 1, 1000));
        Effect optional =
                new Effect(
                        EffectType.TRIGGER, Zone.VANGUARD, "回合开始时", "可以抽1张", null, null, List.of());
        card.addEffect(optional);

        int before = state.getActionLog().size();
        EffectResolver resolver = engine.getEffectResolver();
        resolver.fireTriggers(new TriggerEvent(TriggerType.TURN_START, ap, null), state);
        resolver.resolveAll(state);
        long triggerLogs =
                state.getActionLog().stream()
                        .skip(before)
                        .filter(l -> "TRIGGER_EFFECT".equals(l.getActionType()))
                        .count();
        assertEquals(0, triggerLogs);
    }
}
