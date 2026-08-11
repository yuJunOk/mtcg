package com.aris.mtcg.engine.combat;

import static com.aris.mtcg.engine.EngineFixtures.advanceToSecondPlayerCombat;
import static com.aris.mtcg.engine.EngineFixtures.character;
import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static com.aris.mtcg.engine.EngineFixtures.placeOnCombat;
import static com.aris.mtcg.engine.EngineFixtures.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import org.junit.jupiter.api.Test;

/**
 * 拦截最小集成测试。
 *
 * @author pengYuJun
 */
class InterceptHandlerTest {

    @Test
    void intercept_changesAttackTarget() {
        GameEngine engine = newStartedEngine(81L);
        advanceToSecondPlayerCombat(engine);
        GameState state = engine.getState();

        PlayerState attackerPlayer = state.getActivePlayer();
        PlayerState defender = state.getInactivePlayer();
        CardInstance attacker =
                placeOnCombat(attackerPlayer, Zone.VANGUARD, character("ATK", 1, 2, 1000));
        CardInstance original = placeOnCombat(defender, Zone.VANGUARD, character("DEF", 1, 1, 500));
        CardInstance interceptor =
                placeOnCombat(defender, Zone.FLANK_LEFT, character("INT", 1, 1, 3000));

        ActionRequest attack = request(attackerPlayer.getPlayerId(), ActionType.ATTACK);
        attack.setCardCode(attacker.getInstanceId());
        attack.setTargetZone(Zone.VANGUARD);
        attack.setTargetCardCode(original.getInstanceId());
        engine.dispatch(attack);

        ActionRequest intercept = request(defender.getPlayerId(), ActionType.INTERCEPT);
        intercept.setCardCode(interceptor.getInstanceId());
        engine.dispatch(intercept);

        assertEquals(interceptor, state.getCombatContext().getTarget());
        assertEquals(Zone.FLANK_LEFT, state.getCombatContext().getTargetZone());
        assertTrue(interceptor.isInterceptUsed());

        // 双方 PASS → 判定：拦截者战力更高，攻击者撤退
        engine.dispatch(request(defender.getPlayerId(), ActionType.PASS));
        engine.dispatch(request(attackerPlayer.getPlayerId(), ActionType.PASS));

        assertTrue(attackerPlayer.getRetreat().contains(attacker));
        assertEquals(interceptor, defender.getField().getFlank()[0]);
    }
}
