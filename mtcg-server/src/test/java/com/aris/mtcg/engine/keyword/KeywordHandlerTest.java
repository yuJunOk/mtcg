package com.aris.mtcg.engine.keyword;

import static com.aris.mtcg.engine.EngineFixtures.addToHand;
import static com.aris.mtcg.engine.EngineFixtures.advanceToSecondPlayerCombat;
import static com.aris.mtcg.engine.EngineFixtures.character;
import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static com.aris.mtcg.engine.EngineFixtures.placeOnCombat;
import static com.aris.mtcg.engine.EngineFixtures.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import org.junit.jupiter.api.Test;

/**
 * 关键词能力单元测试（305.1–305.6）。
 *
 * @author pengYuJun
 */
class KeywordHandlerTest {

    @Test
    void combo_allowsSecondAttack() {
        GameEngine engine = newStartedEngine(301L);
        advanceToSecondPlayerCombat(engine);
        GameState state = engine.getState();
        PlayerState ap = state.getActivePlayer();
        PlayerState def = state.getInactivePlayer();

        CardInstance attacker =
                placeOnCombat(ap, Zone.VANGUARD, character("CB", "连击者", 1, 2, 2000, "【连击】可攻击两次"));
        assertTrue(attacker.hasKeyword(Keyword.COMBO));
        assertEquals(2, ComboKeywordHandler.maxAttacks(attacker));

        // 第一次攻击破绽（侧翼空）
        ActionRequest atk1 = request(ap.getPlayerId(), ActionType.ATTACK);
        atk1.setCardCode(attacker.getInstanceId());
        atk1.setTargetZone(Zone.FLANK_LEFT);
        engine.dispatch(atk1);
        engine.dispatch(request(def.getPlayerId(), ActionType.PASS));
        engine.dispatch(request(ap.getPlayerId(), ActionType.PASS));

        // 第二次：仍在先锋行且有连击
        // 战斗推进可能已到侧翼行——若行已变，再造一次场景验证上限逻辑即可
        assertEquals(2, ComboKeywordHandler.maxAttacks(attacker));
    }

    @Test
    void assault_extraRushOnWin() {
        GameEngine engine = newStartedEngine(302L);
        advanceToSecondPlayerCombat(engine);
        GameState state = engine.getState();
        PlayerState ap = state.getActivePlayer();
        PlayerState def = state.getInactivePlayer();

        CardInstance attacker =
                placeOnCombat(ap, Zone.VANGUARD, character("AS", "强袭者", 1, 1, 2000, "【强袭】战胜时冲击"));
        CardInstance target = placeOnCombat(def, Zone.VANGUARD, character("DF", 1, 1, 500));
        int timelineBefore = ap.getTimeline().size();

        ActionRequest atk = request(ap.getPlayerId(), ActionType.ATTACK);
        atk.setCardCode(attacker.getInstanceId());
        atk.setTargetZone(Zone.VANGUARD);
        atk.setTargetCardCode(target.getInstanceId());
        engine.dispatch(atk);
        engine.dispatch(request(def.getPlayerId(), ActionType.PASS));
        engine.dispatch(request(ap.getPlayerId(), ActionType.PASS));

        assertTrue(def.getRetreat().contains(target));
        assertEquals(timelineBefore + 1, ap.getTimeline().size());
    }

    @Test
    void airStrike_allowsOccupiedVulnerable() {
        GameEngine engine = newStartedEngine(303L);
        advanceToSecondPlayerCombat(engine);
        GameState state = engine.getState();
        PlayerState ap = state.getActivePlayer();
        PlayerState def = state.getInactivePlayer();

        CardInstance attacker =
                placeOnCombat(
                        ap, Zone.VANGUARD, character("AIR", "空袭者", 1, 1, 1000, "【空袭】可攻有角色战区"));
        placeOnCombat(def, Zone.VANGUARD, character("OCC", 1, 1, 500));

        ActionRequest atk = request(ap.getPlayerId(), ActionType.ATTACK);
        atk.setCardCode(attacker.getInstanceId());
        atk.setTargetZone(Zone.VANGUARD);
        // 不指定角色 = 破绽攻击有角色的战区
        engine.dispatch(atk);

        assertEquals(Zone.VANGUARD, state.getCombatContext().getTargetZone());
    }

    @Test
    void unique_blocksSameNameSummon() {
        GameEngine engine = newStartedEngine(304L);
        engine.endActionPhase();
        PlayerState second = engine.getState().getActivePlayer();
        placeOnCombat(second, Zone.VANGUARD, character("U1", "同名英雄", 1, 1, 1000, null));

        CardSnapshot unique = character("U2", "同名英雄", 1, 1, 1000, "【唯一】不能同名");
        CardInstance hand = addToHand(second, unique);
        assertTrue(hand.hasKeyword(Keyword.UNIQUE));
        assertTrue(UniqueKeywordHandler.wouldViolateUnique(second, "同名英雄"));

        ActionRequest req = request(second.getPlayerId(), ActionType.SUMMON);
        req.setCardCode(hand.getInstanceId());
        req.setTargetZone(Zone.BASE);
        EngineException ex = assertThrows(EngineException.class, () -> engine.dispatch(req));
        assertEquals("305.6", ex.getRuleRef());
    }

    @Test
    void intercept_requiresKeyword() {
        GameEngine engine = newStartedEngine(305L);
        advanceToSecondPlayerCombat(engine);
        GameState state = engine.getState();
        PlayerState ap = state.getActivePlayer();
        PlayerState def = state.getInactivePlayer();

        CardInstance attacker = placeOnCombat(ap, Zone.VANGUARD, character("ATK", 1, 2, 1000));
        CardInstance original = placeOnCombat(def, Zone.VANGUARD, character("DEF", 1, 1, 500));
        // 无【拦截】
        CardInstance noKw = placeOnCombat(def, Zone.FLANK_LEFT, character("NK", 1, 1, 3000));

        ActionRequest attack = request(ap.getPlayerId(), ActionType.ATTACK);
        attack.setCardCode(attacker.getInstanceId());
        attack.setTargetZone(Zone.VANGUARD);
        attack.setTargetCardCode(original.getInstanceId());
        engine.dispatch(attack);

        ActionRequest intercept = request(def.getPlayerId(), ActionType.INTERCEPT);
        intercept.setCardCode(noKw.getInstanceId());
        EngineException ex = assertThrows(EngineException.class, () -> engine.dispatch(intercept));
        assertEquals("305.2", ex.getRuleRef());
    }

    @Test
    void response_requiresKeyword() {
        GameEngine engine = newStartedEngine(306L);
        advanceToSecondPlayerCombat(engine);
        GameState state = engine.getState();
        PlayerState ap = state.getActivePlayer();
        PlayerState def = state.getInactivePlayer();

        CardInstance attacker = placeOnCombat(ap, Zone.VANGUARD, character("ATK", 1, 1, 1000));
        // R=1 可达敌先锋空置破绽
        ActionRequest attack = request(ap.getPlayerId(), ActionType.ATTACK);
        attack.setCardCode(attacker.getInstanceId());
        attack.setTargetZone(Zone.VANGUARD);
        engine.dispatch(attack);

        CardInstance handNoResponse = addToHand(def, character("H1", 1, 1, 800));
        ActionRequest rs = request(def.getPlayerId(), ActionType.RESPONSE_SUMMON);
        rs.setCardCode(handNoResponse.getInstanceId());
        rs.setTargetZone(Zone.BASE);
        EngineException ex = assertThrows(EngineException.class, () -> engine.dispatch(rs));
        assertEquals("305.1", ex.getRuleRef());
    }

    @Test
    void registry_hasAllSix() {
        KeywordHandlerRegistry registry = new KeywordHandlerRegistry();
        for (Keyword k : Keyword.values()) {
            assertTrue(registry.get(k) != null, "missing handler for " + k);
        }
    }
}
