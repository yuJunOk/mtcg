package com.aris.mtcg.engine.effect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.EngineFixtures;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * PowerModifierStack 单元测试（301.41 / Q&A Q2）。
 *
 * @author pengYuJun
 */
class PowerModifierStackTest {

    private PowerModifierStack stack;
    private CardInstance card;

    @BeforeEach
    void setUp() {
        stack = new PowerModifierStack();
        CardSnapshot snap = EngineFixtures.character("P1", 1, 1, 2000);
        card = new CardInstance("c1", snap);
    }

    @Test
    void increment_stacksOnOriginal() {
        stack.add(
                new PowerModifier(
                        card,
                        card,
                        PowerModifier.Category.TRIGGER_ACTIVATED,
                        PowerModifier.Type.INCREMENT,
                        PowerModifier.Attribute.POWER,
                        500,
                        PowerModifier.Duration.THIS_TURN));
        stack.add(
                new PowerModifier(
                        card,
                        card,
                        PowerModifier.Category.TRIGGER_ACTIVATED,
                        PowerModifier.Type.INCREMENT,
                        PowerModifier.Attribute.POWER,
                        -300,
                        PowerModifier.Duration.THIS_TURN));
        assertEquals(2200, stack.compute(card, PowerModifier.Attribute.POWER));
    }

    @Test
    void removeExpiredThisTurn_clearsThisTurnModifiers() {
        stack.add(
                new PowerModifier(
                        card,
                        card,
                        PowerModifier.Category.TRIGGER_ACTIVATED,
                        PowerModifier.Type.INCREMENT,
                        PowerModifier.Attribute.POWER,
                        1000,
                        PowerModifier.Duration.THIS_TURN));
        assertEquals(3000, stack.compute(card, PowerModifier.Attribute.POWER));
        stack.removeExpiredThisTurn();
        assertEquals(2000, stack.compute(card, PowerModifier.Attribute.POWER));
        assertTrue(stack.getModifiers().isEmpty());
    }

    @Test
    void compute_floorAtZero() {
        stack.add(
                new PowerModifier(
                        card,
                        card,
                        PowerModifier.Category.CONTINUOUS,
                        PowerModifier.Type.INCREMENT,
                        PowerModifier.Attribute.POWER,
                        -5000,
                        PowerModifier.Duration.WHILE_SOURCE_ON_FIELD));
        assertEquals(0, stack.compute(card, PowerModifier.Attribute.POWER));
    }

    @Test
    void continuousChange_overridesTriggerChange() {
        stack.add(
                new PowerModifier(
                        card,
                        card,
                        PowerModifier.Category.TRIGGER_ACTIVATED,
                        PowerModifier.Type.CHANGE,
                        PowerModifier.Attribute.POWER,
                        5000,
                        PowerModifier.Duration.THIS_TURN));
        stack.add(
                new PowerModifier(
                        card,
                        card,
                        PowerModifier.Category.CONTINUOUS,
                        PowerModifier.Type.CHANGE,
                        PowerModifier.Attribute.POWER,
                        1000,
                        PowerModifier.Duration.WHILE_SOURCE_ON_FIELD));
        // 301.41.c：常驻变更为最终
        assertEquals(1000, stack.compute(card, PowerModifier.Attribute.POWER));
    }

    @Test
    void continuousChange_plusIncrement() {
        stack.add(
                new PowerModifier(
                        card,
                        card,
                        PowerModifier.Category.CONTINUOUS,
                        PowerModifier.Type.CHANGE,
                        PowerModifier.Attribute.POWER,
                        1500,
                        PowerModifier.Duration.WHILE_SOURCE_ON_FIELD));
        stack.add(
                new PowerModifier(
                        card,
                        card,
                        PowerModifier.Category.CONTINUOUS,
                        PowerModifier.Type.INCREMENT,
                        PowerModifier.Attribute.POWER,
                        200,
                        PowerModifier.Duration.WHILE_SOURCE_ON_FIELD));
        // 301.41.d
        assertEquals(1700, stack.compute(card, PowerModifier.Attribute.POWER));
        assertFalse(stack.getModifiers().isEmpty());
    }
}
