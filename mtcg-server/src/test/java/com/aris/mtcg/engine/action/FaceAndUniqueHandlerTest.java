package com.aris.mtcg.engine.action;

import static com.aris.mtcg.engine.EngineFixtures.addToHand;
import static com.aris.mtcg.engine.EngineFixtures.character;
import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static com.aris.mtcg.engine.EngineFixtures.placeOnCombat;
import static com.aris.mtcg.engine.EngineFixtures.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import com.aris.mtcg.engine.model.PlayerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 盖卡 / 翻开 / 解除结附 / 唯一号召 单元测试。
 *
 * @author pengYuJun
 */
class FaceAndUniqueHandlerTest {

    private GameEngine engine;
    private PlayerState first;

    @BeforeEach
    void setUp() {
        engine = newStartedEngine(71L);
        first = engine.getState().getActivePlayer();
    }

    @Test
    void setFaceDown_thenFlip_clearsEnteredThisTurn() {
        CardInstance card = placeOnCombat(first, Zone.VANGUARD, character("F1", 1, 1, 1000));
        card.setEnteredThisTurn(true);

        ActionRequest set = request(first.getPlayerId(), ActionType.SET_FACE_DOWN);
        set.setCardCode(card.getInstanceId());
        engine.dispatch(set);
        assertTrue(card.isFaceDown());

        ActionRequest flip = request(first.getPlayerId(), ActionType.FLIP_FACE_UP);
        flip.setCardCode(card.getInstanceId());
        engine.dispatch(flip);

        assertFalse(card.isFaceDown());
        assertFalse(card.isEnteredThisTurn());
    }

    @Test
    void detach_placesBackOnField() {
        CardInstance parent = placeOnCombat(first, Zone.VANGUARD, character("P1", 1, 1, 1000));
        CardInstance child = addToHand(first, character("C1", 1, 1, 800));

        ActionRequest attach = request(first.getPlayerId(), ActionType.ATTACH);
        attach.setCardCode(child.getInstanceId());
        attach.setTargetCardCode(parent.getInstanceId());
        engine.dispatch(attach);

        ActionRequest detach = request(first.getPlayerId(), ActionType.DETACH);
        detach.setCardCode(child.getInstanceId());
        detach.setTargetZone(Zone.FLANK_LEFT);
        engine.dispatch(detach);

        assertTrue(parent.getAttachedCards().isEmpty());
        assertEquals(child, first.getField().getFlank()[0]);
        assertEquals(Zone.FLANK_LEFT, child.getCurrentZone());
    }

    @Test
    void summon_unique_sameNameOnField_shouldFail() {
        engine.endActionPhase(); // 后攻回合，号召上限 3
        PlayerState second = engine.getState().getActivePlayer();
        placeOnCombat(second, Zone.VANGUARD, character("U1", "同名英雄", 1, 1, 1000, null));

        CardSnapshot unique = character("U2", "同名英雄", 1, 1, 1000, "【唯一】场上不能有同名角色");
        CardInstance handCard = addToHand(second, unique);

        ActionRequest req = request(second.getPlayerId(), ActionType.SUMMON);
        req.setCardCode(handCard.getInstanceId());
        req.setTargetZone(Zone.BASE);

        EngineException ex = assertThrows(EngineException.class, () -> engine.dispatch(req));
        assertEquals("305.6", ex.getRuleRef());
    }
}
