package com.aris.mtcg.engine;

import static com.aris.mtcg.engine.GameInitializerTest.mainDeck;
import static com.aris.mtcg.engine.GameInitializerTest.rushDeck;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import com.aris.mtcg.engine.rule.RuleConstants;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * GameEngine 阶段流转单元测试。
 *
 * @author pengYuJun
 */
class GameEngineTest {

    private GameState state;
    private GameEngine engine;
    private PlayerState firstPlayer;

    @BeforeEach
    void setUp() {
        GameInitializer initializer = new GameInitializer(new Random(1L));
        state =
                initializer.initialize(
                        "g-flow",
                        "p1",
                        "p2",
                        mainDeck("A"),
                        rushDeck("A"),
                        mainDeck("B"),
                        rushDeck("B"));
        firstPlayer = state.getFirstPlayer();
        engine = new GameEngine(state);
    }

    @Test
    void startGame_shouldAutoAdvanceToActionPhase() {
        engine.startGame();

        assertEquals(GameStatus.IN_PROGRESS, state.getStatus());
        assertEquals(1, state.getTurnCount());
        assertEquals(PhaseType.ACTION, state.getCurrentPhase());
        assertEquals(firstPlayer, state.getActivePlayer());
        // 起始 6 + 抽卡阶段 2
        assertEquals(
                RuleConstants.OPENING_HAND + RuleConstants.DRAW_PER_TURN,
                firstPlayer.getHand().size());
        assertEquals(42, firstPlayer.getDeck().size());
    }

    @Test
    void firstPlayerFirstTurn_shouldSkipCombatAndReachNextAction() {
        engine.startGame();
        assertTrue(state.isFirstPlayerFirstTurn());

        engine.endActionPhase();

        // COMBAT 跳过 → RESPONSE → TURN_END → 下一回合 TURN_START → DRAW → ACTION
        assertEquals(PhaseType.ACTION, state.getCurrentPhase());
        assertEquals(2, state.getTurnCount());
        assertNotEquals(firstPlayer, state.getActivePlayer());
        assertEquals("p2", state.getActivePlayer().getPlayerId());
    }

    @Test
    void turnEnd_shouldDiscardHandDownToLimit() {
        engine.startGame();

        // 人为把手牌撑到 12 张，验证回合结束压到 9
        while (firstPlayer.getHand().size() < 12 && !firstPlayer.getDeck().isEmpty()) {
            CardInstance card = firstPlayer.getDeck().remove(firstPlayer.getDeck().size() - 1);
            card.setCurrentZone(Zone.HAND);
            firstPlayer.getHand().add(card);
        }
        assertEquals(12, firstPlayer.getHand().size());

        engine.endActionPhase();

        assertEquals(RuleConstants.MAX_HAND_END_TURN, firstPlayer.getHand().size());
        assertTrue(
                firstPlayer.getRetreat().stream()
                        .allMatch(c -> c.getCurrentZone() == Zone.RETREAT));
        assertEquals(3, firstPlayer.getRetreat().size());
    }
}
