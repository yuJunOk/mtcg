package com.aris.mtcg.engine.action;

import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static com.aris.mtcg.engine.EngineFixtures.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import org.junit.jupiter.api.Test;

/**
 * 认输单元测试。
 *
 * @author pengYuJun
 */
class SurrenderHandlerTest {

    @Test
    void surrender_shouldMakeOpponentWin() {
        GameEngine engine = newStartedEngine(41L);
        GameState state = engine.getState();
        PlayerState active = state.getActivePlayer();
        PlayerState inactive = state.getInactivePlayer();

        ActionRequest req = request(active.getPlayerId(), ActionType.SURRENDER);
        ActionResult result = engine.dispatch(req);

        assertTrue(result.isSuccess());
        assertTrue(result.isGameEnded());
        assertEquals(GameStatus.FINISHED, state.getStatus());
        assertEquals(inactive.getPlayerId(), state.getWinnerId());
        assertEquals(inactive.getPlayerId(), result.getWinnerId());
    }
}
