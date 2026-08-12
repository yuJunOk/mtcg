package com.aris.mtcg.engine.effect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * PriorityResolver 单元测试（301.1）。
 *
 * @author pengYuJun
 */
class PriorityResolverTest {

    @Test
    void empty_defaultsToCan() {
        assertTrue(new PriorityResolver().canDo());
    }

    @Test
    void effectCan_overridesRuleCannot() {
        PriorityResolver pr = new PriorityResolver();
        pr.add(new PriorityResolver.Permission(PriorityLevel.RULE_CANNOT, "303.2.a.4.6"));
        pr.add(new PriorityResolver.Permission(PriorityLevel.EFFECT_CAN, "BP01-020"));
        assertTrue(pr.canDo());
    }

    @Test
    void effectCannot_beatsEffectCan() {
        PriorityResolver pr = new PriorityResolver();
        pr.add(new PriorityResolver.Permission(PriorityLevel.EFFECT_CAN, "A"));
        pr.add(new PriorityResolver.Permission(PriorityLevel.EFFECT_CANNOT, "B"));
        assertFalse(pr.canDo());
    }

    @Test
    void ruleCannot_alone_blocks() {
        PriorityResolver pr = new PriorityResolver();
        pr.add(new PriorityResolver.Permission(PriorityLevel.RULE_CANNOT, "rule"));
        assertFalse(pr.canDo());
    }
}
