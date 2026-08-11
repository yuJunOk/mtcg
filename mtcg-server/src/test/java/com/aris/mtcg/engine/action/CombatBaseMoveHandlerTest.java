package com.aris.mtcg.engine.action;

import static com.aris.mtcg.engine.EngineFixtures.addToHand;
import static com.aris.mtcg.engine.EngineFixtures.character;
import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static com.aris.mtcg.engine.EngineFixtures.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.PlayerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 战基移动单元测试。
 *
 * @author pengYuJun
 */
class CombatBaseMoveHandlerTest {

    private GameEngine engine;
    private PlayerState first;

    @BeforeEach
    void setUp() {
        engine = newStartedEngine(21L);
        first = engine.getState().getActivePlayer();
    }

    @Test
    void move_enteredThisTurn_shouldFail() {
        CardInstance card = addToHand(first, character("M1", 1, 1, 1000));
        ActionRequest summon = request(first.getPlayerId(), ActionType.SUMMON);
        summon.setCardCode(card.getInstanceId());
        summon.setTargetZone(Zone.VANGUARD);
        engine.dispatch(summon);
        assertTrue(card.isEnteredThisTurn());

        ActionRequest move = request(first.getPlayerId(), ActionType.COMBAT_BASE_MOVE);
        move.setCardCode(card.getInstanceId());
        move.setSourceZone(Zone.VANGUARD);
        move.setTargetZone(Zone.BASE);

        EngineException ex = assertThrows(EngineException.class, () -> engine.dispatch(move));
        assertEquals("303.2.a.3.1.3", ex.getRuleRef());
    }

    @Test
    void move_afterEnteredCleared_shouldSucceed() {
        CardInstance card = addToHand(first, character("M2", 1, 1, 1000));
        ActionRequest summon = request(first.getPlayerId(), ActionType.SUMMON);
        summon.setCardCode(card.getInstanceId());
        summon.setTargetZone(Zone.VANGUARD);
        engine.dispatch(summon);

        // 模拟回合结束重置进场标记
        card.setEnteredThisTurn(false);

        ActionRequest move = request(first.getPlayerId(), ActionType.COMBAT_BASE_MOVE);
        move.setCardCode(card.getInstanceId());
        move.setSourceZone(Zone.VANGUARD);
        move.setTargetZone(Zone.BASE);
        move.setTargetIndex(0);

        engine.dispatch(move);

        assertEquals(Zone.BASE, card.getCurrentZone());
        assertTrue(card.isMovedThisTurn());
        assertEquals(card, first.getField().getBase()[0]);
        assertNull(first.getField().getVanguard());
    }
}
