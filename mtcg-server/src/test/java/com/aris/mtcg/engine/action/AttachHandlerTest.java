package com.aris.mtcg.engine.action;

import static com.aris.mtcg.engine.EngineFixtures.addToHand;
import static com.aris.mtcg.engine.EngineFixtures.character;
import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static com.aris.mtcg.engine.EngineFixtures.placeOnCombat;
import static com.aris.mtcg.engine.EngineFixtures.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.PlayerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 结附单元测试（Q&A Q8：不占号召次数）。
 *
 * @author pengYuJun
 */
class AttachHandlerTest {

    private GameEngine engine;
    private PlayerState first;

    @BeforeEach
    void setUp() {
        engine = newStartedEngine(31L);
        first = engine.getState().getActivePlayer();
    }

    @Test
    void attach_fromHand_shouldNotIncreaseSummonCount() {
        CardInstance parent = placeOnCombat(first, Zone.VANGUARD, character("P1", 1, 1, 1000));
        CardInstance child = addToHand(first, character("C1", 1, 1, 800));
        int summonBefore = first.getSummonCount();

        ActionRequest req = request(first.getPlayerId(), ActionType.ATTACH);
        req.setCardCode(child.getInstanceId());
        req.setTargetCardCode(parent.getInstanceId());

        engine.dispatch(req);

        assertEquals(summonBefore, first.getSummonCount());
        assertEquals(1, parent.getAttachedCards().size());
        assertTrue(parent.getAttachedCards().contains(child));
        assertEquals(Zone.VANGUARD, child.getCurrentZone());
    }
}
