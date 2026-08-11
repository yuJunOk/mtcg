package com.aris.mtcg.engine.action;

import static com.aris.mtcg.engine.EngineFixtures.addToHand;
import static com.aris.mtcg.engine.EngineFixtures.character;
import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static com.aris.mtcg.engine.EngineFixtures.placeOnCombat;
import static com.aris.mtcg.engine.EngineFixtures.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.PlayerState;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 行动号召单元测试。
 *
 * @author pengYuJun
 */
class SummonHandlerTest {

    private GameEngine engine;
    private PlayerState first;

    @BeforeEach
    void setUp() {
        engine = newStartedEngine(11L);
        first = engine.getState().getActivePlayer();
    }

    @Test
    void summon_firstPlayerFirstTurn_limitOne() {
        CardInstance c1 = addToHand(first, character("S1", 1, 1, 1000));
        CardInstance c2 = addToHand(first, character("S2", 1, 1, 1000));

        ActionRequest ok = request(first.getPlayerId(), ActionType.SUMMON);
        ok.setCardCode(c1.getInstanceId());
        ok.setTargetZone(Zone.VANGUARD);
        engine.dispatch(ok);
        assertEquals(1, first.getSummonCount());
        assertEquals(c1, first.getField().getVanguard());

        ActionRequest again = request(first.getPlayerId(), ActionType.SUMMON);
        again.setCardCode(c2.getInstanceId());
        again.setTargetZone(Zone.BASE);
        EngineException ex = assertThrows(EngineException.class, () -> engine.dispatch(again));
        assertEquals("303.2.a.3.1.2", ex.getRuleRef());
    }

    @Test
    void summon_toBase_shouldSucceed() {
        // 先打完先攻首回合号召额度后，用后攻回合验证基地目标
        CardInstance filler = addToHand(first, character("F0", 1, 1, 1000));
        ActionRequest firstSummon = request(first.getPlayerId(), ActionType.SUMMON);
        firstSummon.setCardCode(filler.getInstanceId());
        firstSummon.setTargetZone(Zone.FLANK_LEFT);
        engine.dispatch(firstSummon);
        engine.endActionPhase(); // → 后攻行动

        PlayerState second = engine.getState().getActivePlayer();
        CardInstance card = addToHand(second, character("B1", 1, 1, 1000));
        ActionRequest req = request(second.getPlayerId(), ActionType.SUMMON);
        req.setCardCode(card.getInstanceId());
        req.setTargetZone(Zone.BASE);
        req.setTargetIndex(0);

        engine.dispatch(req);

        assertEquals(Zone.BASE, card.getCurrentZone());
        assertEquals(card, second.getField().getBase()[0]);
        assertEquals(1, second.getSummonCount());
    }

    @Test
    void summon_lv4_requiresRetreatLevels() {
        engine.endActionPhase(); // 后攻回合，号召上限 3
        PlayerState second = engine.getState().getActivePlayer();

        // 场上准备合计 Lv4 的撤退素材
        CardInstance r1 = placeOnCombat(second, Zone.VANGUARD, character("R1", 1, 1, 1000));
        CardInstance r2 = placeOnCombat(second, Zone.FLANK_LEFT, character("R2", 1, 1, 1000));
        CardInstance r3 = placeOnCombat(second, Zone.FLANK_RIGHT, character("R3", 1, 1, 1000));
        CardInstance r4 = placeOnCombat(second, Zone.REARGUARD, character("R4", 1, 1, 1000));

        CardInstance lv4 = addToHand(second, character("L4", 4, 1, 2000));
        ActionRequest req = request(second.getPlayerId(), ActionType.SUMMON);
        req.setCardCode(lv4.getInstanceId());
        // 目标选空基地（撤退在 execute 才发生，validate 时战区仍占着）
        req.setTargetZone(Zone.BASE);
        req.setTargetIndex(0);
        req.getExtras()
                .put(
                        "retreatCodes",
                        List.of(
                                r1.getInstanceId(),
                                r2.getInstanceId(),
                                r3.getInstanceId(),
                                r4.getInstanceId()));

        engine.dispatch(req);

        assertEquals(lv4, second.getField().getBase()[0]);
        assertEquals(4, second.getRetreat().size());
        assertTrue(second.getRetreat().contains(r1));
        assertNull(second.getField().getVanguard());
    }

    @Test
    void summon_lv4_withoutEnoughRetreat_shouldFail() {
        engine.endActionPhase();
        PlayerState second = engine.getState().getActivePlayer();
        CardInstance r1 = placeOnCombat(second, Zone.VANGUARD, character("R1", 1, 1, 1000));
        CardInstance lv4 = addToHand(second, character("L4", 4, 1, 2000));

        ActionRequest req = request(second.getPlayerId(), ActionType.SUMMON);
        req.setCardCode(lv4.getInstanceId());
        req.setTargetZone(Zone.BASE);
        req.getExtras().put("retreatCodes", List.of(r1.getInstanceId()));

        EngineException ex = assertThrows(EngineException.class, () -> engine.dispatch(req));
        assertEquals("301.19", ex.getRuleRef());
    }
}
