package com.aris.mtcg.engine;

import static com.aris.mtcg.engine.GameInitializerTest.mainDeck;
import static com.aris.mtcg.engine.GameInitializerTest.rushDeck;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.EngineException;
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
 * GameEngine 阶段流转与操作分发单元测试。
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
        engine = new GameEngine(state, initializer);
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

    @Test
    void dispatch_baseDeploy_shouldCoverBaseAndDrawOne() {
        engine.startGame();
        int handBefore = firstPlayer.getHand().size();
        int deckBefore = firstPlayer.getDeck().size();
        CardInstance card = firstPlayer.getHand().get(0);

        ActionRequest request = new ActionRequest();
        request.setPlayerId(firstPlayer.getPlayerId());
        request.setType(ActionType.BASE_DEPLOY);
        request.setCardCode(card.getInstanceId());

        ActionResult result = engine.dispatch(request);

        assertTrue(result.isSuccess());
        assertEquals(1, firstPlayer.getBaseDeployCount());
        assertEquals(1, firstPlayer.getField().getBaseCount());
        assertTrue(card.isFaceDown());
        assertEquals(Zone.BASE, card.getCurrentZone());
        // 手牌 -1 + 抽 1，净变化 0；卡组 -1
        assertEquals(handBefore, firstPlayer.getHand().size());
        assertEquals(deckBefore - 1, firstPlayer.getDeck().size());
        assertNotNull(engine.getActionDispatcher().handlerOf(ActionType.BASE_DEPLOY));
    }

    @Test
    void dispatch_baseDeployTwice_shouldFailWithRuleRef() {
        engine.startGame();

        ActionRequest first = new ActionRequest();
        first.setPlayerId(firstPlayer.getPlayerId());
        first.setType(ActionType.BASE_DEPLOY);
        first.setCardCode(firstPlayer.getHand().get(0).getInstanceId());
        engine.dispatch(first);

        ActionRequest second = new ActionRequest();
        second.setPlayerId(firstPlayer.getPlayerId());
        second.setType(ActionType.BASE_DEPLOY);
        second.setCardCode(firstPlayer.getHand().get(0).getInstanceId());

        EngineException ex = assertThrows(EngineException.class, () -> engine.dispatch(second));
        assertEquals("303.2.a.3.1.1", ex.getRuleRef());
        assertFalse(ex.getMessage().isBlank());
    }
}
