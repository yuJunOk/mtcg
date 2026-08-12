package com.aris.mtcg.service.impl;

import static com.aris.mtcg.engine.EngineFixtures.newStartedEngine;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.dao.CardMapper;
import com.aris.mtcg.dao.DeckMapper;
import com.aris.mtcg.dao.GameMapper;
import com.aris.mtcg.dao.UserMapper;
import com.aris.mtcg.domain.dto.ActionRequestDTO;
import com.aris.mtcg.domain.dto.GameCreateDTO;
import com.aris.mtcg.domain.entity.CardDO;
import com.aris.mtcg.domain.entity.DeckDO;
import com.aris.mtcg.domain.entity.GameDO;
import com.aris.mtcg.domain.entity.UserDO;
import com.aris.mtcg.domain.vo.ActionResultVO;
import com.aris.mtcg.domain.vo.CardInstanceVO;
import com.aris.mtcg.domain.vo.GameHistoryVO;
import com.aris.mtcg.domain.vo.GameStateVO;
import com.aris.mtcg.domain.vo.GameStatsVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.domain.vo.ReplayVO;
import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import com.aris.mtcg.engine.model.PlayerState;
import com.aris.mtcg.manager.GameContext;
import com.aris.mtcg.manager.GameManager;
import com.aris.mtcg.manager.GameStateSerializer;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * GameService 主链路单元测试（Mapper mock，引擎 / Serializer / Manager 真实实例）。
 *
 * @author pengYuJun
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GameServiceImplTest {

    private static final Long USER1 = 1L;
    private static final Long USER2 = 2L;
    private static final Long DECK1 = 11L;
    private static final Long DECK2 = 22L;
    private static final Long GAME_ID = 100L;

    @Mock private GameMapper gameMapper;
    @Mock private DeckMapper deckMapper;
    @Mock private CardMapper cardMapper;
    @Mock private UserMapper userMapper;

    @Spy private GameManager gameManager = new GameManager();
    @Spy private GameStateSerializer gameStateSerializer = new GameStateSerializer();

    @InjectMocks private GameServiceImpl gameService;

    @BeforeEach
    void clearCache() {
        // 避免用例间缓存污染
        gameManager.remove(String.valueOf(GAME_ID));
    }

    @Test
    void createGame_shouldPersistAndCache() {
        stubValidDecksAndCards();
        when(gameMapper.insert(any(GameDO.class)))
                .thenAnswer(
                        inv -> {
                            GameDO g = inv.getArgument(0);
                            g.setId(GAME_ID);
                            return 1;
                        });

        GameCreateDTO dto = new GameCreateDTO();
        dto.setDeck1Id(DECK1);
        dto.setDeck2Id(DECK2);
        dto.setPlayer2Id(USER2);
        dto.setGameMode("CASUAL");
        dto.setFirstPlayer("PLAYER1");

        Long id = gameService.createGame(USER1, dto);
        assertEquals(GAME_ID, id);
        assertTrue(gameManager.contains(String.valueOf(GAME_ID)));
        verify(gameMapper).insert(any(GameDO.class));
        verify(gameMapper, atLeastOnce()).update(any(GameDO.class));

        GameStateVO vo = gameService.getGameState(USER1, GAME_ID);
        assertEquals(String.valueOf(GAME_ID), vo.getGameId());
        assertEquals("IN_PROGRESS", vo.getStatus());
        assertNotNull(vo.getAvailableActions());
        assertTrue(vo.getAvailableActions().contains(ActionType.SURRENDER.name()));
    }

    @Test
    void createGame_shouldRejectInvalidDeck() {
        DeckDO deck1 = validDeck(DECK1, USER1);
        deck1.setIsValid(false);
        when(deckMapper.selectOneById(DECK1)).thenReturn(deck1);

        GameCreateDTO dto = new GameCreateDTO();
        dto.setDeck1Id(DECK1);
        dto.setDeck2Id(DECK2);
        dto.setPlayer2Id(USER2);
        dto.setGameMode("CASUAL");

        BusinessException ex =
                assertThrows(BusinessException.class, () -> gameService.createGame(USER1, dto));
        assertEquals(ErrorCode.DECK_INVALID, ex.getErrorCode());
    }

    @Test
    void getGameState_shouldHideOpponentHandAndCounters() {
        putInProgressContext();

        GameStateVO asP1 = gameService.getGameState(USER1, GAME_ID);
        assertFalse(asP1.getPlayer1().getHand().isEmpty());
        assertNotNull(asP1.getPlayer1().getBaseDeployCount());
        assertTrue(asP1.getPlayer2().getHand().isEmpty());
        assertNull(asP1.getPlayer2().getBaseDeployCount());
        assertTrue(asP1.getPlayer2().getHandCount() > 0);

        GameStateVO asP2 = gameService.getGameState(USER2, GAME_ID);
        assertTrue(asP2.getPlayer1().getHand().isEmpty());
        assertFalse(asP2.getPlayer2().getHand().isEmpty());
    }

    @Test
    void getGameState_shouldHideFaceDownCardFromOpponent() {
        GameContext ctx = putInProgressContext();
        PlayerState p1 = ctx.getEngine().getState().getActivePlayer();
        // 确保 p1 是 USER1
        if (!String.valueOf(USER1).equals(p1.getPlayerId())) {
            p1 = ctx.getEngine().getState().getInactivePlayer();
        }
        CardSnapshot snap =
                new CardSnapshot("X-1", "盖卡", 1, "RED", 1, 500, List.of(), null, "CHARACTER");
        CardInstance faceDown = new CardInstance("fd-1", snap);
        faceDown.setFaceDown(true);
        faceDown.setCurrentZone(Zone.BASE);
        p1.getField().getBase()[0] = faceDown;

        GameStateVO asOpponent = gameService.getGameState(USER2, GAME_ID);
        CardInstanceVO found = findBaseCard(asOpponent, "fd-1");
        assertNotNull(found);
        assertTrue(Boolean.TRUE.equals(found.getIsFaceDown()));
        assertNull(found.getCardCode());
        assertNull(found.getCardName());
    }

    @Test
    void getGameState_notParticipant_shouldFail() {
        putInProgressContext();
        BusinessException ex =
                assertThrows(
                        BusinessException.class, () -> gameService.getGameState(999L, GAME_ID));
        assertEquals(ErrorCode.NOT_GAME_PARTICIPANT, ex.getErrorCode());
    }

    @Test
    void surrender_shouldFinishAndRemoveCache() {
        putInProgressContext();

        gameService.surrender(USER1, GAME_ID);

        assertFalse(gameManager.contains(String.valueOf(GAME_ID)));
        ArgumentCaptor<GameDO> captor = ArgumentCaptor.forClass(GameDO.class);
        verify(gameMapper, atLeastOnce()).update(captor.capture());
        GameDO updated = captor.getValue();
        assertEquals("FINISHED", updated.getStatus());
        assertEquals("PLAYER2", updated.getWinner());
        assertNotNull(updated.getEndTime());
    }

    @Test
    void executeAction_endPhase_shouldAdvance() {
        putInProgressContext();
        ActionRequestDTO dto = new ActionRequestDTO();
        dto.setActionType(ActionType.END_PHASE.name());

        ActionResultVO result = gameService.executeAction(USER1, GAME_ID, dto);
        assertTrue(Boolean.TRUE.equals(result.getSuccess()));
        assertNotNull(result.getGameState());
        // 先攻首回合跳过战斗，会进入后攻回合
        assertEquals(String.valueOf(USER2), result.getGameState().getActivePlayerId());
    }

    @Test
    void executeAction_notYourTurn_shouldFail() {
        putInProgressContext();
        ActionRequestDTO dto = new ActionRequestDTO();
        dto.setActionType(ActionType.END_PHASE.name());

        BusinessException ex =
                assertThrows(
                        BusinessException.class,
                        () -> gameService.executeAction(USER2, GAME_ID, dto));
        assertEquals(ErrorCode.NOT_YOUR_TURN, ex.getErrorCode());
    }

    @Test
    void recover_shouldRebuildFromSnapshotWhenCacheMiss() {
        GameEngine engine = newEngineWithUserIds();
        GameDO record = baseRecord();
        record.setTurnSnapshot(gameStateSerializer.serializeSnapshot(engine.getState(), 0L));
        record.setActionLog("[]");
        when(gameMapper.selectOneById(GAME_ID)).thenReturn(record);

        GameStateVO vo = gameService.getGameState(USER1, GAME_ID);
        assertEquals("IN_PROGRESS", vo.getStatus());
        assertTrue(gameManager.contains(String.valueOf(GAME_ID)));
    }

    @Test
    void getReplay_shouldReturnActionsInOrder() {
        GameDO record = baseRecord();
        record.setStatus("FINISHED");
        record.setWinner("PLAYER2");
        ActionLog log1 =
                new ActionLog(
                        1L,
                        1,
                        com.aris.mtcg.engine.enums.PhaseType.ACTION,
                        "1",
                        "END_PHASE",
                        "{}",
                        100L);
        ActionLog log2 =
                new ActionLog(
                        2L,
                        1,
                        com.aris.mtcg.engine.enums.PhaseType.ACTION,
                        "1",
                        "SURRENDER",
                        "{}",
                        200L);
        record.setActionLog(gameStateSerializer.serializeActionLog(List.of(log1, log2)));
        when(gameMapper.selectOneById(GAME_ID)).thenReturn(record);

        ReplayVO replay = gameService.getReplay(GAME_ID);
        assertEquals(GAME_ID, replay.getGameId());
        assertEquals(2, replay.getActions().size());
        assertEquals(1L, replay.getActions().get(0).getSeq());
        assertEquals("SURRENDER", replay.getActions().get(1).getActionType());
    }

    @Test
    void listHistory_shouldEnrichOpponentAndResult() {
        GameDO game = baseRecord();
        game.setStatus("FINISHED");
        game.setWinner("PLAYER1");
        when(gameMapper.selectHistory(USER1, 0, 20)).thenReturn(List.of(game));
        when(gameMapper.countHistory(USER1)).thenReturn(1L);
        UserDO opponent = new UserDO();
        opponent.setId(USER2);
        opponent.setUsername("对手昵称");
        when(userMapper.selectOneById(USER2)).thenReturn(opponent);
        DeckDO deck = validDeck(DECK1, USER1);
        deck.setDeckName("我的卡组");
        when(deckMapper.selectOneById(DECK1)).thenReturn(deck);

        PageVO<GameHistoryVO> page = gameService.listHistory(USER1, 1, 20);
        assertEquals(1, page.getTotal());
        assertEquals("对手昵称", page.getRecords().get(0).getOpponentName());
        assertEquals("我的卡组", page.getRecords().get(0).getDeckName());
        assertEquals("WIN", page.getRecords().get(0).getResult());
        assertEquals("PLAYER1", page.getRecords().get(0).getSelfSide());
    }

    @Test
    void getStats_shouldComputeWinRate() {
        Map<String, Object> raw = new HashMap<>();
        raw.put("total", 10L);
        raw.put("wins", 6L);
        raw.put("losses", 3L);
        raw.put("draws", 1L);
        when(gameMapper.selectStats(USER1)).thenReturn(raw);

        GameStatsVO stats = gameService.getStats(USER1);
        assertEquals(10, stats.getTotalGames());
        assertEquals(6, stats.getWins());
        assertEquals(0.6, stats.getWinRate(), 1e-9);
    }

    // === helpers ===

    private GameContext putInProgressContext() {
        GameEngine engine = newEngineWithUserIds();
        GameDO record = baseRecord();
        record.setTurnSnapshot(gameStateSerializer.serializeSnapshot(engine.getState(), 0L));
        record.setActionLog("[]");
        GameContext ctx = new GameContext(String.valueOf(GAME_ID), engine, record);
        gameManager.put(String.valueOf(GAME_ID), ctx);
        when(gameMapper.selectOneById(GAME_ID)).thenReturn(record);
        return ctx;
    }

    private GameEngine newEngineWithUserIds() {
        return newStartedEngine(
                7L, String.valueOf(GAME_ID), String.valueOf(USER1), String.valueOf(USER2));
    }

    private GameDO baseRecord() {
        GameDO record = new GameDO();
        record.setId(GAME_ID);
        record.setPlayer1Id(USER1);
        record.setPlayer2Id(USER2);
        record.setDeck1Id(DECK1);
        record.setDeck2Id(DECK2);
        record.setGameMode("CASUAL");
        record.setStatus("IN_PROGRESS");
        record.setActionLog("[]");
        return record;
    }

    private void stubValidDecksAndCards() {
        when(deckMapper.selectOneById(DECK1)).thenReturn(validDeck(DECK1, USER1));
        when(deckMapper.selectOneById(DECK2)).thenReturn(validDeck(DECK2, USER2));

        CardDO c1 = characterCard("C1");
        CardDO r1 = rushCard("R1");
        when(cardMapper.selectListByQuery(any(QueryWrapper.class))).thenReturn(List.of(c1, r1));
    }

    private DeckDO validDeck(Long id, Long userId) {
        DeckDO deck = new DeckDO();
        deck.setId(id);
        deck.setUserId(userId);
        deck.setDeckName("Deck-" + id);
        deck.setIsValid(true);
        deck.setMainDeckCodes("[{\"cardCode\":\"C1\",\"quantity\":50}]");
        deck.setRushDeckCodes("[{\"cardCode\":\"R1\",\"quantity\":9}]");
        return deck;
    }

    private CardDO characterCard(String code) {
        CardDO card = new CardDO();
        card.setCardCode(code);
        card.setCardName("Char-" + code);
        card.setCardType("CHARACTER");
        card.setLevel((short) 1);
        card.setColor("RED");
        card.setAttackRange((short) 1);
        card.setPower((short) 1000);
        card.setTraits("人类");
        return card;
    }

    private CardDO rushCard(String code) {
        CardDO card = new CardDO();
        card.setCardCode(code);
        card.setCardName("Rush-" + code);
        card.setCardType("RUSH_POINT");
        return card;
    }

    private static CardInstanceVO findBaseCard(GameStateVO vo, String instanceId) {
        if (vo.getPlayer1() != null && vo.getPlayer1().getField() != null) {
            for (CardInstanceVO c : vo.getPlayer1().getField().getBase()) {
                if (c != null && instanceId.equals(c.getInstanceId())) {
                    return c;
                }
            }
        }
        if (vo.getPlayer2() != null && vo.getPlayer2().getField() != null) {
            for (CardInstanceVO c : vo.getPlayer2().getField().getBase()) {
                if (c != null && instanceId.equals(c.getInstanceId())) {
                    return c;
                }
            }
        }
        return null;
    }
}
