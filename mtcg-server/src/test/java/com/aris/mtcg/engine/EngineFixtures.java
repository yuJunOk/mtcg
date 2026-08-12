package com.aris.mtcg.engine;

import static com.aris.mtcg.engine.GameInitializerTest.mainDeck;
import static com.aris.mtcg.engine.GameInitializerTest.rushDeck;

import com.aris.mtcg.engine.action.ActionRequest;
import com.aris.mtcg.engine.action.ActionType;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import java.util.List;
import java.util.Random;

/**
 * 引擎单测公共夹具。
 *
 * @author pengYuJun
 */
public final class EngineFixtures {

    private EngineFixtures() {}

    public static GameEngine newStartedEngine(long seed) {
        return newStartedEngine(seed, "g-test", "p1", "p2");
    }

    /** 可指定 gameId / 双方玩家 ID（供 Service 层单测对齐 userId）。 */
    public static GameEngine newStartedEngine(
            long seed, String gameId, String firstPlayerId, String secondPlayerId) {
        GameInitializer initializer = new GameInitializer(new Random(seed));
        GameState state =
                initializer.initialize(
                        gameId,
                        firstPlayerId,
                        secondPlayerId,
                        mainDeck("A"),
                        rushDeck("A"),
                        mainDeck("B"),
                        rushDeck("B"));
        GameEngine engine = new GameEngine(state, initializer);
        engine.startGame();
        return engine;
    }

    /** 角色卡快照。 */
    public static CardSnapshot character(
            String code, String name, int level, int range, int power, String effectText) {
        return new CardSnapshot(
                code, name, level, "RED", range, power, List.of(), effectText, "CHARACTER");
    }

    public static CardSnapshot character(String code, int level, int range, int power) {
        return character(code, "Char-" + code, level, range, power, null);
    }

    /** 创建并放入手牌。 */
    public static CardInstance addToHand(PlayerState player, CardSnapshot snapshot) {
        CardInstance card =
                new CardInstance(player.getPlayerId() + "-" + snapshot.getCardCode(), snapshot);
        card.setCurrentZone(Zone.HAND);
        player.getHand().add(card);
        return card;
    }

    /** 创建并放到指定战区（覆盖原槽）。 */
    public static CardInstance placeOnCombat(PlayerState player, Zone zone, CardSnapshot snapshot) {
        CardInstance card =
                new CardInstance(player.getPlayerId() + "-" + snapshot.getCardCode(), snapshot);
        card.setFaceDown(false);
        card.setEnteredThisTurn(false);
        card.setCurrentZone(zone);
        switch (zone) {
            case VANGUARD -> player.getField().setVanguard(card);
            case FLANK_LEFT -> player.getField().getFlank()[0] = card;
            case FLANK_RIGHT -> player.getField().getFlank()[1] = card;
            case REARGUARD -> player.getField().setRearguard(card);
            default -> throw new IllegalArgumentException("非战区: " + zone);
        }
        return card;
    }

    public static ActionRequest request(String playerId, ActionType type) {
        ActionRequest req = new ActionRequest();
        req.setPlayerId(playerId);
        req.setType(type);
        return req;
    }

    /** 推进到后攻玩家的战斗阶段（先攻首回合已跳过）。 */
    public static void advanceToSecondPlayerCombat(GameEngine engine) {
        // 先攻行动结束 → 跳过战斗 → 后攻行动
        engine.endActionPhase();
        // 后攻行动结束 → 进入战斗（不跳过）
        engine.endActionPhase();
    }
}
