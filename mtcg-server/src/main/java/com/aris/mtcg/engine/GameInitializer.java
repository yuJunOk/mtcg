package com.aris.mtcg.engine;

import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.PlayerSide;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import com.aris.mtcg.engine.rule.RuleConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * 对局初始化器（纯 POJO）。
 *
 * <p>接收双方卡牌快照，完成：创建实例 → 洗牌 → 发牌 → 构建 GameState。 DB 加载由 Service 层完成，引擎不访问数据库。
 *
 * @author pengYuJun
 */
public class GameInitializer {

    private final Random random;

    public GameInitializer() {
        this(new Random());
    }

    /** 测试时可注入固定种子 Random */
    public GameInitializer(Random random) {
        this.random = random;
    }

    /**
     * 初始化对局。
     *
     * @param gameId 对局 ID
     * @param firstPlayerId 先攻玩家 ID
     * @param secondPlayerId 后攻玩家 ID
     * @param firstSnapshots 先攻玩家主卡组快照（50 张，101.1.b）
     * @param firstRushSnapshots 先攻玩家冲击卡组快照（9 张，101.2.b）
     * @param secondSnapshots 后攻玩家主卡组快照
     * @param secondRushSnapshots 后攻玩家冲击卡组快照
     * @return 初始化完成的 GameState（status=WAITING，turnCount=0）
     */
    public GameState initialize(
            String gameId,
            String firstPlayerId,
            String secondPlayerId,
            List<CardSnapshot> firstSnapshots,
            List<CardSnapshot> firstRushSnapshots,
            List<CardSnapshot> secondSnapshots,
            List<CardSnapshot> secondRushSnapshots) {
        // 校验卡组张数（101.1.b / 101.2.b）
        validateDeckSize(firstSnapshots, secondSnapshots);
        validateRushDeckSize(firstRushSnapshots, secondRushSnapshots);

        // 创建玩家状态
        PlayerState firstPlayer =
                createPlayer(firstPlayerId, PlayerSide.FIRST, firstSnapshots, firstRushSnapshots);
        PlayerState secondPlayer =
                createPlayer(
                        secondPlayerId, PlayerSide.SECOND, secondSnapshots, secondRushSnapshots);

        // 洗牌（303.1）
        shuffleDeck(firstPlayer);
        shuffleDeck(secondPlayer);

        // 发起始手牌（303.1 抽 6 张）
        drawOpeningHand(firstPlayer);
        drawOpeningHand(secondPlayer);

        // 构建 GameState
        GameState state = new GameState(gameId);
        state.setFirstPlayer(firstPlayer);
        state.setActivePlayer(firstPlayer); // 先攻为先手回合玩家
        state.setInactivePlayer(secondPlayer);
        state.setStatus(GameStatus.WAITING); // 等待 Mulligan 完成后改为 IN_PROGRESS
        state.setTurnCount(0); // startGame() 时设为 1

        return state;
    }

    /**
     * 执行调度（Mulligan）。
     *
     * <p>规则 303.1：先攻先决定，后攻后决定。 弃牌置卡组底 → 补等量 → 洗匀。
     *
     * @param state 对局状态
     * @param playerId 执行调度的玩家 ID
     * @param cardIndices 要调度（弃掉重抽）的手牌索引列表
     */
    public void mulligan(GameState state, String playerId, List<Integer> cardIndices) {
        PlayerState player = findPlayer(state, playerId);
        if (player == null) {
            throw new IllegalStateException("玩家不存在: " + playerId);
        }
        if (cardIndices == null || cardIndices.isEmpty()) {
            return;
        }

        List<CardInstance> hand = player.getHand();
        List<CardInstance> deck = player.getDeck();

        // 303.1 弃牌置卡组底（按索引降序移除，避免偏移）
        List<CardInstance> discarded = new ArrayList<>();
        cardIndices.stream()
                .sorted(Comparator.reverseOrder())
                .forEach(
                        idx -> {
                            CardInstance card = hand.remove(idx.intValue());
                            card.setCurrentZone(Zone.DECK);
                            discarded.add(card);
                        });
        deck.addAll(0, discarded); // 卡组底 = 列表头部

        // 303.1 补等量
        int drawCount = discarded.size();
        for (int i = 0; i < drawCount; i++) {
            CardInstance card = deck.remove(deck.size() - 1); // 卡组顶 = 列表末尾
            card.setCurrentZone(Zone.HAND);
            hand.add(card);
        }

        // 303.1 洗匀
        shuffleDeck(player);
    }

    // === 私有方法 ===

    private void validateDeckSize(List<CardSnapshot> d1, List<CardSnapshot> d2) {
        // 101.1.b 主卡组 50 张
        if (d1 == null
                || d2 == null
                || d1.size() != RuleConstants.MAIN_DECK_SIZE
                || d2.size() != RuleConstants.MAIN_DECK_SIZE) {
            throw new IllegalArgumentException(
                    "主卡组必须为 " + RuleConstants.MAIN_DECK_SIZE + " 张"); // 101.1.b
        }
    }

    private void validateRushDeckSize(List<CardSnapshot> r1, List<CardSnapshot> r2) {
        // 101.2.b 冲击卡组 9 张
        if (r1 == null
                || r2 == null
                || r1.size() != RuleConstants.RUSH_DECK_SIZE
                || r2.size() != RuleConstants.RUSH_DECK_SIZE) {
            throw new IllegalArgumentException(
                    "冲击卡组必须为 " + RuleConstants.RUSH_DECK_SIZE + " 张"); // 101.2.b
        }
    }

    private PlayerState createPlayer(
            String playerId,
            PlayerSide side,
            List<CardSnapshot> snapshots,
            List<CardSnapshot> rushSnapshots) {
        PlayerState player = new PlayerState(playerId);
        player.setSide(side);

        // 主卡组 → CardInstance
        int idx = 0;
        for (CardSnapshot snapshot : snapshots) {
            String instanceId = playerId + "-D" + String.format("%03d", idx++);
            CardInstance instance = new CardInstance(instanceId, snapshot);
            instance.setCurrentZone(Zone.DECK);
            player.getDeck().add(instance);
        }

        // 冲击卡组 → CardInstance
        idx = 0;
        for (CardSnapshot snapshot : rushSnapshots) {
            String instanceId = playerId + "-R" + String.format("%03d", idx++);
            CardInstance instance = new CardInstance(instanceId, snapshot);
            instance.setCurrentZone(Zone.RUSH_DECK);
            player.getRushDeck().add(instance);
        }

        return player;
    }

    /** 洗牌（303.1 洗匀卡组） */
    private void shuffleDeck(PlayerState player) {
        Collections.shuffle(player.getDeck(), random); // 303.1
    }

    /** 发起始手牌（303.1 抽 6 张） */
    private void drawOpeningHand(PlayerState player) {
        List<CardInstance> deck = player.getDeck();
        List<CardInstance> hand = player.getHand();
        for (int i = 0; i < RuleConstants.OPENING_HAND; i++) { // 303.1 起始 6 张
            if (deck.isEmpty()) {
                break;
            }
            CardInstance card = deck.remove(deck.size() - 1);
            card.setCurrentZone(Zone.HAND);
            hand.add(card);
        }
    }

    private PlayerState findPlayer(GameState state, String playerId) {
        if (state.getActivePlayer().getPlayerId().equals(playerId)) {
            return state.getActivePlayer();
        }
        if (state.getInactivePlayer().getPlayerId().equals(playerId)) {
            return state.getInactivePlayer();
        }
        return null;
    }
}
