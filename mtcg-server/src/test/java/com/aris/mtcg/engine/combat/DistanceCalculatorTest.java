package com.aris.mtcg.engine.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * 距离计算单元测试（对照设计矩阵 §5.2）。
 *
 * @author pengYuJun
 */
class DistanceCalculatorTest {

    @Test
    void distance_matrixSamples() {
        // 己先锋 → 敌先锋/侧翼/后卫
        assertEquals(1, DistanceCalculator.distance(Zone.VANGUARD, Zone.VANGUARD));
        assertEquals(2, DistanceCalculator.distance(Zone.VANGUARD, Zone.FLANK_LEFT));
        assertEquals(2, DistanceCalculator.distance(Zone.VANGUARD, Zone.FLANK_RIGHT));
        assertEquals(3, DistanceCalculator.distance(Zone.VANGUARD, Zone.REARGUARD));

        // 己侧翼 → 敌先锋/侧翼/后卫
        assertEquals(2, DistanceCalculator.distance(Zone.FLANK_LEFT, Zone.VANGUARD));
        assertEquals(3, DistanceCalculator.distance(Zone.FLANK_RIGHT, Zone.FLANK_LEFT));
        assertEquals(4, DistanceCalculator.distance(Zone.FLANK_LEFT, Zone.REARGUARD));

        // 己后卫 → 敌先锋/侧翼/后卫
        assertEquals(3, DistanceCalculator.distance(Zone.REARGUARD, Zone.VANGUARD));
        assertEquals(4, DistanceCalculator.distance(Zone.REARGUARD, Zone.FLANK_RIGHT));
        assertEquals(5, DistanceCalculator.distance(Zone.REARGUARD, Zone.REARGUARD));
    }

    @Test
    void canReach_respectsRange() {
        CardInstance attacker = cardAt(Zone.VANGUARD, 2);
        assertTrue(DistanceCalculator.canReach(attacker, Zone.VANGUARD)); // dist 1
        assertTrue(DistanceCalculator.canReach(attacker, Zone.FLANK_LEFT)); // dist 2
        assertFalse(DistanceCalculator.canReach(attacker, Zone.REARGUARD)); // dist 3
    }

    @Test
    void distance_nonCombatZone_shouldThrow() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DistanceCalculator.distance(Zone.BASE, Zone.VANGUARD));
    }

    private static CardInstance cardAt(Zone zone, int range) {
        CardSnapshot snap =
                new CardSnapshot("D1", "D", 1, "RED", range, 1000, List.of(), null, "CHARACTER");
        CardInstance card = new CardInstance("d1", snap);
        card.setCurrentZone(zone);
        return card;
    }
}
