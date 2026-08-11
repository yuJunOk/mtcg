package com.aris.mtcg.engine.combat;

import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import com.aris.mtcg.engine.rule.RuleConstants;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * 胜负判定单元测试。
 *
 * @author pengYuJun
 */
class WinCheckerTest {

    @Test
    void check_timelineNine_shouldWin() {
        GameEngine engine = newStartedEngine(61L);
        GameState state = engine.getState();
        PlayerState active = state.getActivePlayer();

        for (int i = 0; i < RuleConstants.WIN_TIMELINE; i++) {
            CardSnapshot snap =
                    new CardSnapshot(
                            "W-R" + i,
                            "Rush-" + i,
                            null,
                            null,
                            null,
                            null,
                            List.of(),
                            null,
                            "RUSH_POINT");
            CardInstance rush = new CardInstance("tl-" + i, snap);
            rush.setCurrentZone(Zone.TIMELINE);
            active.getTimeline().add(rush);
        }

        String winner = WinChecker.check(state);

        assertEquals(active.getPlayerId(), winner);
        assertEquals(GameStatus.FINISHED, state.getStatus());
        assertEquals(active.getPlayerId(), state.getWinnerId());
    }

    @Test
    void check_activeDeckEmpty_inactiveWins() {
        GameEngine engine = newStartedEngine(62L);
        GameState state = engine.getState();
        PlayerState active = state.getActivePlayer();
        PlayerState inactive = state.getInactivePlayer();

        active.getDeck().clear();

        String winner = WinChecker.check(state);

        assertEquals(inactive.getPlayerId(), winner);
        assertEquals(GameStatus.FINISHED, state.getStatus());
    }

    @Test
    void check_noCondition_returnsNull() {
        GameEngine engine = newStartedEngine(63L);
        assertNull(WinChecker.check(engine.getState()));
        assertEquals(GameStatus.IN_PROGRESS, engine.getState().getStatus());
    }
}
