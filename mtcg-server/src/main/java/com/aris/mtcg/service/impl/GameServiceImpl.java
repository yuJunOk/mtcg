package com.aris.mtcg.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.common.util.PublicCodeUtils;
import com.aris.mtcg.common.util.TraitUtils;
import com.aris.mtcg.dao.CardMapper;
import com.aris.mtcg.dao.DeckMapper;
import com.aris.mtcg.dao.GameMapper;
import com.aris.mtcg.dao.UserMapper;
import com.aris.mtcg.domain.dto.ActionRequestDTO;
import com.aris.mtcg.domain.dto.DeckCardEntry;
import com.aris.mtcg.domain.dto.GameCreateDTO;
import com.aris.mtcg.domain.dto.GameJoinDTO;
import com.aris.mtcg.domain.entity.CardDO;
import com.aris.mtcg.domain.entity.DeckDO;
import com.aris.mtcg.domain.entity.GameDO;
import com.aris.mtcg.domain.entity.UserDO;
import com.aris.mtcg.domain.vo.ActionReplayEntryVO;
import com.aris.mtcg.domain.vo.ActionResultVO;
import com.aris.mtcg.domain.vo.CardInstanceVO;
import com.aris.mtcg.domain.vo.FieldZoneVO;
import com.aris.mtcg.domain.vo.GameHistoryVO;
import com.aris.mtcg.domain.vo.GameMatchVO;
import com.aris.mtcg.domain.vo.GameStateVO;
import com.aris.mtcg.domain.vo.GameStatsVO;
import com.aris.mtcg.domain.vo.PageVO;
import com.aris.mtcg.domain.vo.PlayerStateVO;
import com.aris.mtcg.domain.vo.ReplayVO;
import com.aris.mtcg.engine.GameEngine;
import com.aris.mtcg.engine.GameInitializer;
import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionResult;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.action.EngineException;
import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.PhaseType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.ActionLog;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import com.aris.mtcg.engine.model.FieldZone;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import com.aris.mtcg.manager.GameContext;
import com.aris.mtcg.manager.GameManager;
import com.aris.mtcg.manager.GameStateSerializer;
import com.aris.mtcg.service.GameService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 对战服务实现
 *
 * @author pengYuJun
 */
@Slf4j
@Service
public class GameServiceImpl implements GameService {

    private static final String STATUS_WAITING = "WAITING";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_FINISHED = "FINISHED";
    private static final String MODE_AI = "AI";
    private static final String SIDE_PLAYER1 = "PLAYER1";
    private static final String SIDE_PLAYER2 = "PLAYER2";
    private static final String RESULT_WIN = "WIN";
    private static final String RESULT_LOSE = "LOSE";
    private static final String RESULT_DRAW = "DRAW";
    private static final String RESULT_UNFINISHED = "UNFINISHED";
    private static final String WINNER_DRAW = "DRAW";
    private static final String ACTION_WIN = "WIN";
    private static final int DEFAULT_HISTORY_PAGE = 1;
    private static final int DEFAULT_HISTORY_SIZE = 20;
    private static final int MAX_HISTORY_SIZE = 100;
    private static final int CODE_ALLOC_MAX_RETRY = 16;

    @Resource private GameMapper gameMapper;
    @Resource private DeckMapper deckMapper;
    @Resource private CardMapper cardMapper;
    @Resource private UserMapper userMapper;
    @Resource private GameManager gameManager;
    @Resource private GameStateSerializer gameStateSerializer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createGame(Long userId, GameCreateDTO dto) {
        if (MODE_AI.equals(dto.getGameMode())) {
            throw new BusinessException(ErrorCode.AI_NOT_AVAILABLE);
        }
        DeckDO deck1 = requireValidDeck(dto.getDeck1Id(), userId);
        if (dto.getPlayer2Id() == null) {
            return createWaitingRoom(userId, deck1, dto.getGameMode());
        }
        if (Objects.equals(dto.getPlayer2Id(), userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能与自己对战");
        }
        if (dto.getDeck2Id() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "对手卡组不能为空");
        }
        DeckDO deck2 = requireValidDeck(dto.getDeck2Id(), dto.getPlayer2Id());

        GameDO record = new GameDO();
        record.setGameCode(allocateUniqueGameCode());
        record.setPlayer1Id(userId);
        record.setPlayer2Id(dto.getPlayer2Id());
        record.setDeck1Id(dto.getDeck1Id());
        record.setDeck2Id(dto.getDeck2Id());
        record.setGameMode(dto.getGameMode());
        record.setStatus(STATUS_IN_PROGRESS);
        record.setActionLog("[]");
        gameMapper.insert(record);

        startDuel(
                record,
                deck1,
                deck2,
                dto.getFirstPlayer(),
                dto.getMulligan1Indices(),
                dto.getMulligan2Indices());
        return record.getGameCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String joinGame(Long userId, String idOrCode, GameJoinDTO dto) {
        GameDO record = requireGame(idOrCode);
        if (!STATUS_WAITING.equals(record.getStatus()) || record.getPlayer2Id() != null) {
            throw new BusinessException(ErrorCode.GAME_NOT_JOINABLE);
        }
        if (Objects.equals(userId, record.getPlayer1Id())) {
            throw new BusinessException(ErrorCode.GAME_NOT_JOINABLE, "不能加入自己的房间");
        }
        DeckDO deck1 = requireValidDeck(record.getDeck1Id(), record.getPlayer1Id());
        DeckDO deck2 = requireValidDeck(dto.getDeckId(), userId);
        record.setPlayer2Id(userId);
        record.setDeck2Id(dto.getDeckId());
        record.setStatus(STATUS_IN_PROGRESS);
        startDuel(record, deck1, deck2, null, null, null);
        return record.getGameCode();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameMatchVO matchGame(Long userId, GameJoinDTO dto) {
        requireValidDeck(dto.getDeckId(), userId);
        GameDO open = gameMapper.selectOpenWaiting(userId);
        if (open == null) {
            return GameMatchVO.miss();
        }
        String key =
                StringUtils.isNotBlank(open.getGameCode())
                        ? open.getGameCode()
                        : String.valueOf(open.getId());
        return GameMatchVO.hit(joinGame(userId, key, dto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelWaiting(Long userId, String idOrCode) {
        GameDO record = requireGame(idOrCode);
        if (!STATUS_WAITING.equals(record.getStatus())) {
            throw new BusinessException(ErrorCode.GAME_NOT_JOINABLE, "只能取消等待中的房间");
        }
        if (!Objects.equals(userId, record.getPlayer1Id())) {
            throw new BusinessException(ErrorCode.NOT_GAME_PARTICIPANT);
        }
        gameMapper.deleteById(record.getId());
    }

    @Override
    public GameStateVO getGameState(Long userId, String idOrCode) {
        GameDO record = requireGame(idOrCode);
        assertParticipant(userId, record);
        if (STATUS_WAITING.equals(record.getStatus())) {
            return toWaitingStateVO(record);
        }
        String gameCode = record.getGameCode();
        GameContext cached = gameManager.get(gameCode);
        if (cached != null) {
            return toGameStateVO(cached, userId);
        }
        GameContext context = getOrLoadContext(record);
        return toGameStateVO(context, userId);
    }

    private String createWaitingRoom(Long userId, DeckDO deck1, String gameMode) {
        GameDO record = new GameDO();
        record.setGameCode(allocateUniqueGameCode());
        record.setPlayer1Id(userId);
        record.setDeck1Id(deck1.getId());
        record.setGameMode(gameMode);
        record.setStatus(STATUS_WAITING);
        record.setActionLog("[]");
        gameMapper.insert(record);
        return record.getGameCode();
    }

    private void startDuel(
            GameDO record,
            DeckDO deck1,
            DeckDO deck2,
            String firstPlayer,
            List<Integer> mulligan1,
            List<Integer> mulligan2) {
        Map<String, CardSnapshot> snapshotMap = loadSnapshots(deck1, deck2);
        List<CardSnapshot> p1Main = expandDeck(deck1.getMainDeckCodes(), snapshotMap);
        List<CardSnapshot> p1Rush = expandDeck(deck1.getRushDeckCodes(), snapshotMap);
        List<CardSnapshot> p2Main = expandDeck(deck2.getMainDeckCodes(), snapshotMap);
        List<CardSnapshot> p2Rush = expandDeck(deck2.getRushDeckCodes(), snapshotMap);

        String gameCode = record.getGameCode();
        String player1Id = String.valueOf(record.getPlayer1Id());
        String player2Id = String.valueOf(record.getPlayer2Id());

        boolean player1First = resolvePlayer1First(firstPlayer);
        String firstId = player1First ? player1Id : player2Id;
        String secondId = player1First ? player2Id : player1Id;
        List<CardSnapshot> firstMain = player1First ? p1Main : p2Main;
        List<CardSnapshot> firstRush = player1First ? p1Rush : p2Rush;
        List<CardSnapshot> secondMain = player1First ? p2Main : p1Main;
        List<CardSnapshot> secondRush = player1First ? p2Rush : p1Rush;

        GameInitializer initializer = new GameInitializer();
        GameState state =
                initializer.initialize(
                        gameCode, firstId, secondId, firstMain, firstRush, secondMain, secondRush);

        List<Integer> firstMulligan = player1First ? mulligan1 : mulligan2;
        List<Integer> secondMulligan = player1First ? mulligan2 : mulligan1;
        if (firstMulligan != null && !firstMulligan.isEmpty()) {
            initializer.mulligan(state, firstId, firstMulligan);
        }
        if (secondMulligan != null && !secondMulligan.isEmpty()) {
            initializer.mulligan(state, secondId, secondMulligan);
        }

        GameEngine engine = new GameEngine(state, initializer);
        engine.startGame();

        record.setStatus(STATUS_IN_PROGRESS);
        record.setTurnSnapshot(gameStateSerializer.serializeSnapshot(state, 0L));
        gameMapper.update(record);

        GameContext context = new GameContext(gameCode, engine, record);
        gameManager.put(gameCode, context);
    }

    private GameStateVO toWaitingStateVO(GameDO record) {
        GameStateVO vo = new GameStateVO();
        vo.setGameId(record.getGameCode());
        vo.setStatus(STATUS_WAITING);
        vo.setTurnCount(0);
        PlayerStateVO p1 = new PlayerStateVO();
        p1.setPlayerId(String.valueOf(record.getPlayer1Id()));
        vo.setPlayer1(p1);
        vo.setPlayer2(null);
        vo.setAvailableActions(Collections.emptyList());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionResultVO executeAction(Long userId, String idOrCode, ActionRequestDTO dto) {
        GameContext context = getOrLoadContext(idOrCode);
        GameDO record = context.getRecord();
        assertParticipant(userId, record);
        if (!STATUS_IN_PROGRESS.equals(record.getStatus())) {
            throw new BusinessException(ErrorCode.GAME_ALREADY_FINISHED);
        }

        String playerId = String.valueOf(userId);
        ActionType actionType = parseActionType(dto.getActionType());
        if (actionType != ActionType.SURRENDER) {
            assertCanAct(context.getEngine().getState(), playerId, actionType);
        }

        ActionRequest request = toActionRequest(context.getGameId(), playerId, dto, actionType);
        context.lock();
        try {
            GameState state = context.getEngine().getState();
            int turnBefore = state.getTurnCount();
            int logSizeBefore = state.getActionLog().size();

            ActionResult result;
            try {
                result = context.getEngine().dispatch(request);
            } catch (EngineException e) {
                throw new BusinessException(ErrorCode.ILLEGAL_GAME_ACTION, e.getMessage());
            }

            if (!result.isSuccess()) {
                throw new BusinessException(
                        ErrorCode.ILLEGAL_GAME_ACTION,
                        result.getMessage() != null ? result.getMessage() : "操作失败");
            }

            // Handler 已 logAction：只补 seq，并为可重放的用户操作写入 JSON actionDetail
            assignSeqToNewLogs(context, state, logSizeBefore, actionType, dto);
            persistAfterAction(context, state, turnBefore, result);

            ActionResultVO vo = new ActionResultVO();
            vo.setSuccess(true);
            vo.setMessage(result.getMessage());
            vo.setPhaseAdvanced(result.isPhaseAdvanced());
            vo.setGameEnded(result.isGameEnded() || state.getStatus() == GameStatus.FINISHED);
            vo.setWinner(mapWinnerSide(record, state.getWinnerId()));
            vo.setGameState(toGameStateVO(context, userId));
            return vo;
        } finally {
            context.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void surrender(Long userId, String idOrCode) {
        ActionRequestDTO dto = new ActionRequestDTO();
        dto.setActionType(ActionType.SURRENDER.name());
        executeAction(userId, idOrCode, dto);
    }

    @Override
    public ReplayVO getReplay(String idOrCode) {
        GameDO record = requireGame(idOrCode);
        ReplayVO vo = ReplayVO.fromDO(record);
        List<ActionLog> logs = gameStateSerializer.deserializeActionLog(record.getActionLog());
        List<ActionReplayEntryVO> entries = new ArrayList<>(logs.size());
        for (ActionLog logEntry : logs) {
            ActionReplayEntryVO entry = new ActionReplayEntryVO();
            entry.setSeq(logEntry.getSeq());
            entry.setTurnCount(logEntry.getTurnCount());
            entry.setPhase(logEntry.getPhase() != null ? logEntry.getPhase().name() : null);
            entry.setPlayerId(logEntry.getPlayerId());
            entry.setActionType(logEntry.getActionType());
            entry.setActionDetail(logEntry.getActionDetail());
            entry.setTimestamp(logEntry.getTimestamp());
            entries.add(entry);
        }
        vo.setActions(entries);
        return vo;
    }

    @Override
    public PageVO<GameHistoryVO> listHistory(Long userId, int page, int size) {
        int safePage = page < DEFAULT_HISTORY_PAGE ? DEFAULT_HISTORY_PAGE : page;
        int safeSize = size < 1 ? DEFAULT_HISTORY_SIZE : Math.min(size, MAX_HISTORY_SIZE);
        int offset = (safePage - 1) * safeSize;
        List<GameDO> records = gameMapper.selectHistory(userId, offset, safeSize);
        long total = gameMapper.countHistory(userId);
        List<GameHistoryVO> vos = new ArrayList<>(records.size());
        for (GameDO game : records) {
            vos.add(toHistoryVO(game, userId));
        }
        return new PageVO<>(vos, total);
    }

    @Override
    public GameStatsVO getStats(Long userId) {
        Map<String, Object> raw = gameMapper.selectStats(userId);
        int total = toInt(raw, "total");
        int wins = toInt(raw, "wins");
        int losses = toInt(raw, "losses");
        int draws = toInt(raw, "draws");
        GameStatsVO vo = new GameStatsVO();
        vo.setTotalGames(total);
        vo.setWins(wins);
        vo.setLosses(losses);
        vo.setDraws(draws);
        vo.setWinRate(total == 0 ? 0.0 : (double) wins / total);
        return vo;
    }

    // === 缓存 / 恢复 ===

    private GameContext getOrLoadContext(String idOrCode) {
        GameDO record = requireGame(idOrCode);
        return getOrLoadContext(record);
    }

    private GameContext getOrLoadContext(GameDO record) {
        String gameCode = record.getGameCode();
        GameContext cached = gameManager.get(gameCode);
        if (cached != null) {
            return cached;
        }
        if (STATUS_WAITING.equals(record.getStatus())) {
            throw new BusinessException(ErrorCode.GAME_NOT_JOINABLE, "对局尚未开始");
        }
        if (STATUS_FINISHED.equals(record.getStatus())) {
            return buildFinishedContext(record);
        }
        return recoverGame(record);
    }

    /** 已结束对局：从快照 + 全量流水重建只读上下文，不入进行中缓存策略上仍 put 以便同进程复用 */
    private GameContext buildFinishedContext(GameDO record) {
        return recoverGame(record);
    }

    private GameContext recoverGame(GameDO record) {
        String gameCode = record.getGameCode();
        GameStateSerializer.SnapshotWrapper wrapper =
                gameStateSerializer.deserializeSnapshot(record.getTurnSnapshot());
        if (wrapper == null || wrapper.getGameState() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "对局快照损坏，无法恢复");
        }

        GameState state = wrapper.getGameState();
        long snapshotSeq = wrapper.getSnapshotActionSeq();
        List<ActionLog> allLogs = gameStateSerializer.deserializeActionLog(record.getActionLog());
        List<ActionLog> toReplay =
                allLogs.stream()
                        .filter(l -> l.getSeq() > snapshotSeq)
                        .sorted((a, b) -> Long.compare(a.getSeq(), b.getSeq()))
                        .collect(Collectors.toList());

        GameEngine engine = new GameEngine(state);
        try {
            for (ActionLog logEntry : toReplay) {
                if (ACTION_WIN.equals(logEntry.getActionType())) {
                    continue;
                }
                ActionRequest request = rebuildRequest(gameCode, logEntry);
                engine.dispatch(request);
            }
        } catch (RuntimeException e) {
            log.error("对局恢复重放失败 gameCode={}, err={}", gameCode, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "对局恢复失败: " + e.getMessage());
        }

        // 重放会再次 logAction：用 DB 全量流水覆盖，保证 seq/详情一致
        state.getActionLog().clear();
        state.getActionLog().addAll(allLogs);

        long maxSeq = allLogs.stream().mapToLong(ActionLog::getSeq).max().orElse(snapshotSeq);
        GameContext context = new GameContext(gameCode, engine, record);
        context.setActionSeq(maxSeq);
        if (STATUS_IN_PROGRESS.equals(record.getStatus())) {
            gameManager.put(gameCode, context);
        }
        return context;
    }

    // === 持久化辅助 ===

    private void persistAfterAction(
            GameContext context, GameState state, int turnBefore, ActionResult result) {
        GameDO record = context.getRecord();
        record.setActionLog(gameStateSerializer.serializeActionLog(state.getActionLog()));

        if (state.getTurnCount() > turnBefore && state.getCurrentPhase() == PhaseType.TURN_START) {
            record.setTurnSnapshot(
                    gameStateSerializer.serializeSnapshot(state, context.getActionSeq()));
        }

        boolean finished = result.isGameEnded() || state.getStatus() == GameStatus.FINISHED;
        if (finished) {
            record.setStatus(STATUS_FINISHED);
            record.setWinner(mapWinnerSide(record, state.getWinnerId()));
            record.setEndTime(LocalDateTime.now());
            record.setTurnSnapshot(
                    gameStateSerializer.serializeSnapshot(state, context.getActionSeq()));
            gameMapper.update(record);
            gameManager.remove(context.getGameId());
            return;
        }
        gameMapper.update(record);
    }

    private void assignSeqToNewLogs(
            GameContext context,
            GameState state,
            int logSizeBefore,
            ActionType actionType,
            ActionRequestDTO dto) {
        List<ActionLog> logs = state.getActionLog();
        boolean primaryPatched = false;
        String detailJson = buildActionDetailJson(dto);
        for (int i = logSizeBefore; i < logs.size(); i++) {
            ActionLog old = logs.get(i);
            long seq = context.nextSeq();
            String detail = old.getActionDetail();
            if (!primaryPatched
                    && actionType.name().equals(old.getActionType())
                    && detailJson != null) {
                detail = detailJson;
                primaryPatched = true;
            }
            logs.set(
                    i,
                    new ActionLog(
                            seq,
                            old.getTurnCount(),
                            old.getPhase(),
                            old.getPlayerId(),
                            old.getActionType(),
                            detail,
                            old.getTimestamp()));
        }
    }

    // === 校验 ===

    private DeckDO requireValidDeck(Long deckId, Long ownerId) {
        DeckDO deck = deckMapper.selectOneById(deckId);
        if (deck == null) {
            throw new BusinessException(ErrorCode.DECK_NOT_FOUND);
        }
        if (!Objects.equals(deck.getUserId(), ownerId)) {
            throw new BusinessException(ErrorCode.DECK_FORBIDDEN, "卡组不属于对局参与方");
        }
        if (!Boolean.TRUE.equals(deck.getIsValid())) {
            throw new BusinessException(ErrorCode.DECK_INVALID);
        }
        return deck;
    }

    private GameDO requireGame(String idOrCode) {
        String key = PublicCodeUtils.normalize(idOrCode);
        if (StringUtils.isBlank(key)) {
            throw new BusinessException(ErrorCode.GAME_NOT_FOUND);
        }
        GameDO record;
        if (PublicCodeUtils.isGameCode(key)) {
            record = gameMapper.selectOneByQuery(QueryWrapper.create().eq("game_code", key));
        } else {
            try {
                record = gameMapper.selectOneById(Long.parseLong(key));
            } catch (NumberFormatException e) {
                throw new BusinessException(ErrorCode.GAME_NOT_FOUND);
            }
        }
        if (record == null) {
            throw new BusinessException(ErrorCode.GAME_NOT_FOUND);
        }
        return record;
    }

    /** 循环查重生成唯一 game_code */
    private String allocateUniqueGameCode() {
        for (int i = 0; i < CODE_ALLOC_MAX_RETRY; i++) {
            String code = PublicCodeUtils.newGameCode();
            long count = gameMapper.selectCountByQuery(QueryWrapper.create().eq("game_code", code));
            if (count == 0) {
                return code;
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成对局编码失败");
    }

    private void assertParticipant(Long userId, GameDO record) {
        if (!Objects.equals(userId, record.getPlayer1Id())
                && !Objects.equals(userId, record.getPlayer2Id())) {
            throw new BusinessException(ErrorCode.NOT_GAME_PARTICIPANT);
        }
    }

    private void assertCanAct(GameState state, String playerId, ActionType actionType) {
        if (state.getStatus() != GameStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.GAME_ALREADY_FINISHED);
        }
        PhaseType phase = state.getCurrentPhase();
        // PASS / INTERCEPT / RESPONSE_SUMMON 可为非行动方
        if (actionType == ActionType.PASS
                || actionType == ActionType.INTERCEPT
                || actionType == ActionType.RESPONSE_SUMMON) {
            return;
        }
        if (state.getActivePlayer() == null
                || !playerId.equals(state.getActivePlayer().getPlayerId())) {
            throw new BusinessException(ErrorCode.NOT_YOUR_TURN);
        }
        if (phase == null) {
            throw new BusinessException(ErrorCode.NOT_YOUR_TURN);
        }
    }

    private boolean resolvePlayer1First(String firstPlayer) {
        if (SIDE_PLAYER1.equals(firstPlayer)) {
            return true;
        }
        if (SIDE_PLAYER2.equals(firstPlayer)) {
            return false;
        }
        return ThreadLocalRandom.current().nextBoolean();
    }

    // === 卡组快照 ===

    private Map<String, CardSnapshot> loadSnapshots(DeckDO deck1, DeckDO deck2) {
        Set<String> codes = new HashSet<>();
        codes.addAll(collectCodes(deck1.getMainDeckCodes()));
        codes.addAll(collectCodes(deck1.getRushDeckCodes()));
        codes.addAll(collectCodes(deck2.getMainDeckCodes()));
        codes.addAll(collectCodes(deck2.getRushDeckCodes()));
        if (codes.isEmpty()) {
            throw new BusinessException(ErrorCode.DECK_INVALID, "卡组卡牌为空");
        }
        List<CardDO> cards =
                cardMapper.selectListByQuery(QueryWrapper.create().in("card_code", codes));
        Map<String, CardSnapshot> map = new HashMap<>();
        for (CardDO card : cards) {
            map.put(card.getCardCode(), toSnapshot(card));
        }
        for (String code : codes) {
            if (!map.containsKey(code)) {
                throw new BusinessException(ErrorCode.CARD_CODE_NOT_EXIST, "卡牌不存在: " + code);
            }
        }
        return map;
    }

    private Set<String> collectCodes(String json) {
        List<DeckCardEntry> entries = parseEntries(json);
        return entries.stream().map(DeckCardEntry::getCardCode).collect(Collectors.toSet());
    }

    private List<CardSnapshot> expandDeck(String json, Map<String, CardSnapshot> snapshotMap) {
        List<DeckCardEntry> entries = parseEntries(json);
        List<CardSnapshot> list = new ArrayList<>();
        for (DeckCardEntry entry : entries) {
            CardSnapshot snap = snapshotMap.get(entry.getCardCode());
            int qty = entry.getQuantity() == null ? 0 : entry.getQuantity();
            for (int i = 0; i < qty; i++) {
                list.add(snap);
            }
        }
        return list;
    }

    private List<DeckCardEntry> parseEntries(String json) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        List<DeckCardEntry> entries = JSON.parseArray(json, DeckCardEntry.class);
        return entries != null ? entries : Collections.emptyList();
    }

    private CardSnapshot toSnapshot(CardDO card) {
        return new CardSnapshot(
                card.getCardCode(),
                card.getCardName(),
                card.getLevel() == null ? null : card.getLevel().intValue(),
                card.getColor(),
                card.getAttackRange() == null ? null : card.getAttackRange().intValue(),
                card.getPower() == null ? null : card.getPower().intValue(),
                TraitUtils.split(card.getTraits()),
                card.getEffectText(),
                card.getCardType());
    }

    // === ActionRequest 转换 / 重放 ===

    private ActionType parseActionType(String actionType) {
        if (StringUtils.isBlank(actionType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "操作类型不能为空");
        }
        try {
            return ActionType.valueOf(actionType);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未知操作类型: " + actionType);
        }
    }

    private ActionRequest toActionRequest(
            String gameId, String playerId, ActionRequestDTO dto, ActionType type) {
        ActionRequest request = new ActionRequest();
        request.setGameId(gameId);
        request.setPlayerId(playerId);
        request.setType(type);
        request.setCardCode(dto.getCardCode());
        request.setSourceZone(parseZone(dto.getSourceZone()));
        if (dto.getSourceIndex() != null) {
            request.setSourceIndex(dto.getSourceIndex());
        }
        request.setTargetZone(parseZone(dto.getTargetZone()));
        if (dto.getTargetIndex() != null) {
            request.setTargetIndex(dto.getTargetIndex());
        }
        request.setTargetCardCode(dto.getTargetCardCode());
        if (dto.getExtras() != null) {
            request.setExtras(dto.getExtras());
        }
        return request;
    }

    private ActionRequest rebuildRequest(String gameId, ActionLog logEntry) {
        ActionType type = parseActionType(logEntry.getActionType());
        ActionRequestDTO dto = new ActionRequestDTO();
        dto.setActionType(logEntry.getActionType());
        String detail = logEntry.getActionDetail();
        if (StringUtils.isNotBlank(detail) && detail.trim().startsWith("{")) {
            JSONObject obj = JSON.parseObject(detail);
            dto.setCardCode(obj.getString("cardCode"));
            dto.setSourceZone(obj.getString("sourceZone"));
            dto.setSourceIndex(obj.getInteger("sourceIndex"));
            dto.setTargetZone(obj.getString("targetZone"));
            dto.setTargetIndex(obj.getInteger("targetIndex"));
            dto.setTargetCardCode(obj.getString("targetCardCode"));
            Object extras = obj.get("extras");
            if (extras instanceof Map<?, ?> map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> extrasMap = (Map<String, Object>) map;
                dto.setExtras(extrasMap);
            }
        }
        return toActionRequest(gameId, logEntry.getPlayerId(), dto, type);
    }

    private Zone parseZone(String zone) {
        if (StringUtils.isBlank(zone)) {
            return null;
        }
        try {
            return Zone.valueOf(zone);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未知区域: " + zone);
        }
    }

    private String buildActionDetailJson(ActionRequestDTO dto) {
        if (dto == null) {
            return "{}";
        }
        Map<String, Object> map = new HashMap<>();
        if (dto.getCardCode() != null) {
            map.put("cardCode", dto.getCardCode());
        }
        if (dto.getSourceZone() != null) {
            map.put("sourceZone", dto.getSourceZone());
        }
        if (dto.getSourceIndex() != null) {
            map.put("sourceIndex", dto.getSourceIndex());
        }
        if (dto.getTargetZone() != null) {
            map.put("targetZone", dto.getTargetZone());
        }
        if (dto.getTargetIndex() != null) {
            map.put("targetIndex", dto.getTargetIndex());
        }
        if (dto.getTargetCardCode() != null) {
            map.put("targetCardCode", dto.getTargetCardCode());
        }
        if (dto.getExtras() != null && !dto.getExtras().isEmpty()) {
            map.put("extras", dto.getExtras());
        }
        return JSON.toJSONString(map);
    }

    // === VO 组装 / 隐私 ===

    private GameStateVO toGameStateVO(GameContext context, Long viewerId) {
        GameState state = context.getEngine().getState();
        GameDO record = context.getRecord();
        String viewer = String.valueOf(viewerId);

        GameStateVO vo = new GameStateVO();
        vo.setGameId(context.getGameId());
        vo.setStatus(
                state.getStatus() == GameStatus.FINISHED ? STATUS_FINISHED : STATUS_IN_PROGRESS);
        vo.setTurnCount(state.getTurnCount());
        vo.setCurrentPhase(state.getCurrentPhase() != null ? state.getCurrentPhase().name() : null);
        vo.setActivePlayerId(
                state.getActivePlayer() != null ? state.getActivePlayer().getPlayerId() : null);
        vo.setWinner(mapWinnerSide(record, state.getWinnerId()));

        PlayerState p1 = findPlayer(state, String.valueOf(record.getPlayer1Id()));
        PlayerState p2 = findPlayer(state, String.valueOf(record.getPlayer2Id()));
        vo.setPlayer1(toPlayerStateVO(p1, viewer));
        vo.setPlayer2(toPlayerStateVO(p2, viewer));
        vo.setAvailableActions(computeAvailableActions(state, viewer));
        return vo;
    }

    private PlayerStateVO toPlayerStateVO(PlayerState player, String viewerId) {
        if (player == null) {
            return null;
        }
        boolean self = viewerId.equals(player.getPlayerId());
        PlayerStateVO vo = new PlayerStateVO();
        vo.setPlayerId(player.getPlayerId());
        vo.setSide(player.getSide() != null ? player.getSide().name() : null);
        vo.setDeckCount(player.getDeck().size());
        vo.setRushDeckCount(player.getRushDeck().size());
        vo.setHandCount(player.getHand().size());
        if (self) {
            vo.setHand(toCardList(player.getHand(), true));
            vo.setBaseDeployCount(player.getBaseDeployCount());
            vo.setSummonCount(player.getSummonCount());
        } else {
            vo.setHand(Collections.emptyList());
            vo.setBaseDeployCount(null);
            vo.setSummonCount(null);
        }
        vo.setTimeline(toCardList(player.getTimeline(), true));
        vo.setRetreat(toCardList(player.getRetreat(), true));
        vo.setVoidZone(toCardList(player.getVoidZone(), true));
        vo.setField(toFieldZoneVO(player.getField(), self));
        return vo;
    }

    private FieldZoneVO toFieldZoneVO(FieldZone field, boolean self) {
        FieldZoneVO vo = new FieldZoneVO();
        if (field == null) {
            vo.setFlank(Arrays.asList(null, null));
            vo.setBase(new ArrayList<>());
            return vo;
        }
        vo.setVanguard(toCardInstanceVO(field.getVanguard(), self));
        List<CardInstanceVO> flank = new ArrayList<>(2);
        CardInstance[] flankArr = field.getFlank();
        flank.add(toCardInstanceVO(flankArr.length > 0 ? flankArr[0] : null, self));
        flank.add(toCardInstanceVO(flankArr.length > 1 ? flankArr[1] : null, self));
        vo.setFlank(flank);
        vo.setRearguard(toCardInstanceVO(field.getRearguard(), self));
        List<CardInstanceVO> base = new ArrayList<>();
        for (CardInstance c : field.getBase()) {
            if (c != null) {
                base.add(toCardInstanceVO(c, self));
            }
        }
        vo.setBase(base);
        return vo;
    }

    private List<CardInstanceVO> toCardList(List<CardInstance> cards, boolean self) {
        if (cards == null || cards.isEmpty()) {
            return Collections.emptyList();
        }
        List<CardInstanceVO> list = new ArrayList<>(cards.size());
        for (CardInstance card : cards) {
            list.add(toCardInstanceVO(card, self));
        }
        return list;
    }

    private CardInstanceVO toCardInstanceVO(CardInstance card, boolean self) {
        if (card == null) {
            return null;
        }
        CardInstanceVO vo = new CardInstanceVO();
        vo.setInstanceId(card.getInstanceId());
        vo.setIsFaceDown(card.isFaceDown());
        boolean hideFace = card.isFaceDown() && !self;
        if (hideFace) {
            vo.setAttachedCards(Collections.emptyList());
            return vo;
        }
        CardSnapshot snap = card.getSnapshot();
        if (snap != null) {
            vo.setCardCode(snap.getCardCode());
            vo.setCardName(snap.getName());
            vo.setLevel(snap.getLevel());
            vo.setColor(snap.getColor());
        }
        vo.setCurrentPower(card.getCurrentPower());
        vo.setCurrentRange(card.getCurrentRange());
        vo.setEnteredThisTurn(card.isEnteredThisTurn());
        vo.setMovedThisTurn(card.isMovedThisTurn());
        vo.setAttackUsed(card.getAttackUsed());
        vo.setInterceptUsed(card.isInterceptUsed());
        vo.setAttachedCards(toCardList(card.getAttachedCards(), self));
        return vo;
    }

    private List<String> computeAvailableActions(GameState state, String viewerId) {
        List<String> actions = new ArrayList<>();
        if (state.getStatus() != GameStatus.IN_PROGRESS) {
            return actions;
        }
        actions.add(ActionType.SURRENDER.name());
        PhaseType phase = state.getCurrentPhase();
        boolean isActive =
                state.getActivePlayer() != null
                        && viewerId.equals(state.getActivePlayer().getPlayerId());
        if (phase == PhaseType.ACTION && isActive) {
            actions.add(ActionType.BASE_DEPLOY.name());
            actions.add(ActionType.SUMMON.name());
            actions.add(ActionType.COMBAT_BASE_MOVE.name());
            actions.add(ActionType.ACTIVATE_EFFECT.name());
            actions.add(ActionType.SET_FACE_DOWN.name());
            actions.add(ActionType.FLIP_FACE_UP.name());
            actions.add(ActionType.ATTACH.name());
            actions.add(ActionType.DETACH.name());
            actions.add(ActionType.END_PHASE.name());
        } else if (phase == PhaseType.COMBAT) {
            if (isActive) {
                actions.add(ActionType.COMBAT_ADJUST.name());
                actions.add(ActionType.ATTACK.name());
                actions.add(ActionType.END_PHASE.name());
            }
            actions.add(ActionType.PASS.name());
            actions.add(ActionType.RESPONSE_SUMMON.name());
            actions.add(ActionType.INTERCEPT.name());
        } else if (phase == PhaseType.RESPONSE) {
            actions.add(ActionType.PASS.name());
            actions.add(ActionType.RESPONSE_SUMMON.name());
            actions.add(ActionType.ACTIVATE_EFFECT.name());
        }
        return actions;
    }

    private PlayerState findPlayer(GameState state, String playerId) {
        if (state.getActivePlayer() != null
                && playerId.equals(state.getActivePlayer().getPlayerId())) {
            return state.getActivePlayer();
        }
        if (state.getInactivePlayer() != null
                && playerId.equals(state.getInactivePlayer().getPlayerId())) {
            return state.getInactivePlayer();
        }
        return null;
    }

    private String mapWinnerSide(GameDO record, String winnerId) {
        if (winnerId == null) {
            return record.getWinner();
        }
        if (String.valueOf(record.getPlayer1Id()).equals(winnerId)) {
            return SIDE_PLAYER1;
        }
        if (String.valueOf(record.getPlayer2Id()).equals(winnerId)) {
            return SIDE_PLAYER2;
        }
        return WINNER_DRAW.equals(winnerId) ? WINNER_DRAW : winnerId;
    }

    private GameHistoryVO toHistoryVO(GameDO game, Long userId) {
        GameHistoryVO vo = GameHistoryVO.fromDO(game);
        boolean isP1 = Objects.equals(userId, game.getPlayer1Id());
        vo.setSelfSide(isP1 ? SIDE_PLAYER1 : SIDE_PLAYER2);
        Long opponentId = isP1 ? game.getPlayer2Id() : game.getPlayer1Id();
        UserDO opponent = userMapper.selectOneById(opponentId);
        vo.setOpponentName(opponent != null ? opponent.getUsername() : String.valueOf(opponentId));
        Long selfDeckId = isP1 ? game.getDeck1Id() : game.getDeck2Id();
        DeckDO deck = deckMapper.selectOneById(selfDeckId);
        vo.setDeckName(deck != null ? deck.getDeckName() : null);
        vo.setResult(resolveSelfResult(game, isP1));
        return vo;
    }

    private String resolveSelfResult(GameDO game, boolean isP1) {
        if (!STATUS_FINISHED.equals(game.getStatus())) {
            return RESULT_UNFINISHED;
        }
        String winner = game.getWinner();
        if (WINNER_DRAW.equals(winner)) {
            return RESULT_DRAW;
        }
        if (SIDE_PLAYER1.equals(winner)) {
            return isP1 ? RESULT_WIN : RESULT_LOSE;
        }
        if (SIDE_PLAYER2.equals(winner)) {
            return isP1 ? RESULT_LOSE : RESULT_WIN;
        }
        return RESULT_UNFINISHED;
    }

    private int toInt(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return 0;
        }
        Object val = map.get(key);
        if (val instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(val.toString());
    }
}
