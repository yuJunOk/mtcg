package com.aris.mtcg.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.enums.GameStatus;
import com.aris.mtcg.engine.enums.PlayerSide;
import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;
import com.aris.mtcg.engine.model.CardSnapshot;
import com.aris.mtcg.engine.model.GameState;
import com.aris.mtcg.engine.model.PlayerState;
import com.aris.mtcg.engine.rule.RuleConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * GameInitializer 单元测试。
 *
 * @author pengYuJun
 */
class GameInitializerTest {

    @Test
    void initialize_shouldDealOpeningHandsAndKeepRushIntact() {
        GameState state =
                newInitializer(1L)
                        .initialize(
                                "g1",
                                "p1",
                                "p2",
                                mainDeck("A"),
                                rushDeck("A"),
                                mainDeck("B"),
                                rushDeck("B"));

        assertEquals(GameStatus.WAITING, state.getStatus());
        assertEquals(0, state.getTurnCount());
        assertEquals(PlayerSide.FIRST, state.getFirstPlayer().getSide());
        assertEquals(state.getFirstPlayer(), state.getActivePlayer());

        assertPlayerZones(state.getActivePlayer(), 44, 6, 9);
        assertPlayerZones(state.getInactivePlayer(), 44, 6, 9);
        assertTrue(
                state.getActivePlayer().getHand().stream()
                        .allMatch(c -> c.getCurrentZone() == Zone.HAND));
    }

    @Test
    void initialize_shouldRejectInvalidDeckSize() {
        List<CardSnapshot> shortMain = mainDeck("A").subList(0, 49);
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        newInitializer(1L)
                                .initialize(
                                        "g1",
                                        "p1",
                                        "p2",
                                        shortMain,
                                        rushDeck("A"),
                                        mainDeck("B"),
                                        rushDeck("B")));
    }

    @Test
    void shuffle_withSameSeed_shouldBeReproducible() {
        GameState s1 =
                newInitializer(42L)
                        .initialize(
                                "g1",
                                "p1",
                                "p2",
                                mainDeck("A"),
                                rushDeck("A"),
                                mainDeck("B"),
                                rushDeck("B"));
        GameState s2 =
                newInitializer(42L)
                        .initialize(
                                "g1",
                                "p1",
                                "p2",
                                mainDeck("A"),
                                rushDeck("A"),
                                mainDeck("B"),
                                rushDeck("B"));
        assertEquals(deckCodes(s1.getActivePlayer()), deckCodes(s2.getActivePlayer()));

        GameState s3 =
                newInitializer(99L)
                        .initialize(
                                "g1",
                                "p1",
                                "p2",
                                mainDeck("A"),
                                rushDeck("A"),
                                mainDeck("B"),
                                rushDeck("B"));
        assertNotEquals(deckCodes(s1.getActivePlayer()), deckCodes(s3.getActivePlayer()));
    }

    @Test
    void mulligan_shouldReplaceSelectedCardsAndKeepHandSize() {
        GameInitializer initializer = newInitializer(7L);
        GameState state =
                initializer.initialize(
                        "g1",
                        "p1",
                        "p2",
                        mainDeck("A"),
                        rushDeck("A"),
                        mainDeck("B"),
                        rushDeck("B"));
        PlayerState first = state.getActivePlayer();
        String discardedId0 = first.getHand().get(0).getInstanceId();
        String discardedId1 = first.getHand().get(1).getInstanceId();

        initializer.mulligan(state, "p1", List.of(0, 1));

        assertEquals(RuleConstants.OPENING_HAND, first.getHand().size());
        assertEquals(44, first.getDeck().size());
        List<String> handIds = first.getHand().stream().map(CardInstance::getInstanceId).toList();
        assertTrue(!handIds.contains(discardedId0));
        assertTrue(!handIds.contains(discardedId1));
        List<String> deckIds = first.getDeck().stream().map(CardInstance::getInstanceId).toList();
        assertTrue(deckIds.contains(discardedId0));
        assertTrue(deckIds.contains(discardedId1));
    }

    private static GameInitializer newInitializer(long seed) {
        return new GameInitializer(new Random(seed));
    }

    private static void assertPlayerZones(PlayerState player, int deck, int hand, int rush) {
        assertEquals(deck, player.getDeck().size());
        assertEquals(hand, player.getHand().size());
        assertEquals(rush, player.getRushDeck().size());
    }

    private static List<String> deckCodes(PlayerState player) {
        return player.getDeck().stream()
                .map(c -> c.getSnapshot().getCardCode())
                .collect(Collectors.toList());
    }

    /** 50 张主卡组快照（唯一编号，便于断言）。 */
    static List<CardSnapshot> mainDeck(String prefix) {
        List<CardSnapshot> list = new ArrayList<>(RuleConstants.MAIN_DECK_SIZE);
        for (int i = 1; i <= RuleConstants.MAIN_DECK_SIZE; i++) {
            String code = prefix + "-C" + String.format("%02d", i);
            list.add(
                    new CardSnapshot(
                            code,
                            "Char-" + code,
                            1,
                            i <= 25 ? "RED" : "YELLOW",
                            1,
                            1000,
                            List.of(),
                            null,
                            "CHARACTER"));
        }
        return list;
    }

    /** 9 张冲击卡组快照。 */
    static List<CardSnapshot> rushDeck(String prefix) {
        List<CardSnapshot> list = new ArrayList<>(RuleConstants.RUSH_DECK_SIZE);
        for (int i = 1; i <= RuleConstants.RUSH_DECK_SIZE; i++) {
            String code = prefix + "-R" + i;
            list.add(
                    new CardSnapshot(
                            code,
                            "Rush-" + code,
                            null,
                            null,
                            null,
                            null,
                            List.of(),
                            null,
                            "RUSH_POINT"));
        }
        return list;
    }
}
