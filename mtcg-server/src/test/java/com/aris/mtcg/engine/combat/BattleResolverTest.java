package com.aris.mtcg.engine.combat;

import static com.aris.mtcg.engine.EngineFixtures.advanceToSecondPlayerCombat;
import static com.aris.mtcg.engine.EngineFixtures.character;
import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static com.aris.mtcg.engine.EngineFixtures.placeOnCombat;
import static com.aris.mtcg.engine.EngineFixtures.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import org.junit.jupiter.api.Test;

/**
 * 战斗判定流程单元测试。
 *
 * @author pengYuJun
 */
class BattleResolverTest {

    @Test
    void battle_higherPower_defenderRetreats() {
        GameEngine engine = newStartedEngine(51L);
        advanceToSecondPlayerCombat(engine);
        GameState state = engine.getState();
        assertEquals(PhaseType.COMBAT, state.getCurrentPhase());

        PlayerState attackerPlayer = state.getActivePlayer();
        PlayerState defender = state.getInactivePlayer();
        CardInstance attacker =
                placeOnCombat(attackerPlayer, Zone.VANGUARD, character("ATK", 1, 1, 2000));
        CardInstance target = placeOnCombat(defender, Zone.VANGUARD, character("DEF", 1, 1, 1000));

        declareAttackAndBothPass(engine, attackerPlayer, defender, attacker, target, Zone.VANGUARD);

        assertNull(defender.getField().getVanguard());
        assertTrue(defender.getRetreat().contains(target));
        assertEquals(attacker, attackerPlayer.getField().getVanguard());
    }

    @Test
    void battle_equalPower_bothRetreat() {
        GameEngine engine = newStartedEngine(52L);
        advanceToSecondPlayerCombat(engine);
        GameState state = engine.getState();

        PlayerState attackerPlayer = state.getActivePlayer();
        PlayerState defender = state.getInactivePlayer();
        CardInstance attacker =
                placeOnCombat(attackerPlayer, Zone.VANGUARD, character("ATK", 1, 1, 1000));
        CardInstance target = placeOnCombat(defender, Zone.VANGUARD, character("DEF", 1, 1, 1000));

        declareAttackAndBothPass(engine, attackerPlayer, defender, attacker, target, Zone.VANGUARD);

        assertTrue(attackerPlayer.getRetreat().contains(attacker));
        assertTrue(defender.getRetreat().contains(target));
    }

    @Test
    void battle_vulnerable_drawsRushToTimeline() {
        GameEngine engine = newStartedEngine(53L);
        advanceToSecondPlayerCombat(engine);
        GameState state = engine.getState();

        PlayerState attackerPlayer = state.getActivePlayer();
        PlayerState defender = state.getInactivePlayer();
        CardInstance attacker =
                placeOnCombat(attackerPlayer, Zone.VANGUARD, character("ATK", 1, 1, 1000));
        // 敌先锋空置 = 破绽；R-1 可达
        assertNull(defender.getField().getVanguard());
        int timelineBefore = attackerPlayer.getTimeline().size();
        int rushBefore = attackerPlayer.getRushDeck().size();

        ActionRequest attack = request(attackerPlayer.getPlayerId(), ActionType.ATTACK);
        attack.setCardCode(attacker.getInstanceId());
        attack.setTargetZone(Zone.VANGUARD);
        // targetCardCode 为空 = 破绽
        engine.dispatch(attack);

        pass(engine, defender.getPlayerId());
        pass(engine, attackerPlayer.getPlayerId());

        assertEquals(timelineBefore + 1, attackerPlayer.getTimeline().size());
        assertEquals(rushBefore - 1, attackerPlayer.getRushDeck().size());
        assertEquals(Zone.TIMELINE, attackerPlayer.getTimeline().get(0).getCurrentZone());
    }

    private static void declareAttackAndBothPass(
            GameEngine engine,
            PlayerState attackerPlayer,
            PlayerState defender,
            CardInstance attacker,
            CardInstance target,
            Zone targetZone) {
        ActionRequest attack = request(attackerPlayer.getPlayerId(), ActionType.ATTACK);
        attack.setCardCode(attacker.getInstanceId());
        attack.setTargetZone(targetZone);
        attack.setTargetCardCode(target.getInstanceId());
        engine.dispatch(attack);

        pass(engine, defender.getPlayerId());
        pass(engine, attackerPlayer.getPlayerId());
    }

    private static void pass(GameEngine engine, String playerId) {
        ActionRequest pass = request(playerId, ActionType.PASS);
        engine.dispatch(pass);
    }
}
