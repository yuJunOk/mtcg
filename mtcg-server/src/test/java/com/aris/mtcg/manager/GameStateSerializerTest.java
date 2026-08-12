package com.aris.mtcg.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.PlayerSide;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** GameStateSerializer / GameManager 单元测试 */
class GameStateSerializerTest {

    private GameStateSerializer serializer;
    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        serializer = new GameStateSerializer();
        gameManager = new GameManager();
    }

    @Test
    void snapshot_roundTrip_preservesStateAndPlayerIdentity() {
        GameState state = buildSampleState();
        String json = serializer.serializeSnapshot(state, 3L);
        assertNotNull(json);
        assertTrue(json.contains("snapshotActionSeq"));

        GameStateSerializer.SnapshotWrapper wrapper = serializer.deserializeSnapshot(json);
        assertNotNull(wrapper);
        assertEquals(3L, wrapper.getSnapshotActionSeq());
        assertEquals(1, wrapper.getSnapshotTurn());

        GameState restored = wrapper.getGameState();
        assertNotNull(restored);
        assertEquals("42", restored.getGameId());
        assertEquals(GameStatus.IN_PROGRESS, restored.getStatus());
        assertEquals(PhaseType.ACTION, restored.getCurrentPhase());
        assertEquals(1, restored.getTurnCount());
        assertEquals(1, restored.getActivePlayer().getHand().size());
        assertEquals(
                "BP01-001",
                restored.getActivePlayer().getHand().get(0).getSnapshot().getCardCode());
        assertEquals(Zone.HAND, restored.getActivePlayer().getHand().get(0).getCurrentZone());

        // 先攻与当前行动方应为同一对象（ReferenceDetection）
        assertSame(restored.getFirstPlayer(), restored.getActivePlayer());
        assertEquals("101", restored.getFirstPlayer().getPlayerId());
        assertEquals(PlayerSide.FIRST, restored.getFirstPlayer().getSide());
    }

    @Test
    void actionLog_roundTrip_preservesSeqAndTimestamp() {
        ActionLog log =
                new ActionLog(
                        7L,
                        2,
                        PhaseType.ACTION,
                        "101",
                        "SUMMON",
                        "{\"cardCode\":\"BP01-001\"}",
                        1_700_000_000_000L);
        String json = serializer.serializeActionLog(List.of(log));
        List<ActionLog> restored = serializer.deserializeActionLog(json);
        assertEquals(1, restored.size());
        assertEquals(7L, restored.get(0).getSeq());
        assertEquals(2, restored.get(0).getTurnCount());
        assertEquals(PhaseType.ACTION, restored.get(0).getPhase());
        assertEquals("SUMMON", restored.get(0).getActionType());
        assertEquals(1_700_000_000_000L, restored.get(0).getTimestamp());
    }

    @Test
    void actionLog_blank_returnsEmpty() {
        assertTrue(serializer.deserializeActionLog(null).isEmpty());
        assertTrue(serializer.deserializeActionLog("").isEmpty());
        assertTrue(serializer.deserializeActionLog("[]").isEmpty());
    }

    @Test
    void deserializeSnapshot_blank_returnsNull() {
        assertNull(serializer.deserializeSnapshot(null));
        assertNull(serializer.deserializeSnapshot("  "));
    }

    @Test
    void gameManager_putGetRemove() {
        assertEquals(0, gameManager.activeCount());
        // engine 可为 null：本测只验证缓存 API
        GameContext ctx = new GameContext("1", null, null);
        gameManager.put("1", ctx);
        assertTrue(gameManager.contains("1"));
        assertSame(ctx, gameManager.get("1"));
        assertEquals(1, gameManager.activeCount());
        assertSame(ctx, gameManager.remove("1"));
        assertNull(gameManager.get("1"));
        assertEquals(0, gameManager.activeCount());
    }

    private static GameState buildSampleState() {
        CardSnapshot snap =
                new CardSnapshot(
                        "BP01-001", "测试卡", 1, "RED", 1, 1000, List.of("人类"), null, "CHARACTER");
        CardInstance card = new CardInstance("inst-1", snap);
        card.setCurrentZone(Zone.HAND);

        PlayerState p1 = new PlayerState("101");
        p1.setSide(PlayerSide.FIRST);
        p1.getHand().add(card);

        PlayerState p2 = new PlayerState("102");
        p2.setSide(PlayerSide.SECOND);

        GameState state = new GameState("42");
        state.setFirstPlayer(p1);
        state.setActivePlayer(p1);
        state.setInactivePlayer(p2);
        state.setStatus(GameStatus.IN_PROGRESS);
        state.setTurnCount(1);
        state.setCurrentPhase(PhaseType.ACTION);
        return state;
    }
}
