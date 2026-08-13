package com.aris.mtcg.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * {@link PublicCodeUtils} 单元测试
 *
 * @author pengYuJun
 */
class PublicCodeUtilsTest {

    @Test
    void deckAndGamePrefixes() {
        String deck = PublicCodeUtils.newDeckCode();
        String game = PublicCodeUtils.newGameCode();
        assertTrue(PublicCodeUtils.isDeckCode(deck));
        assertTrue(PublicCodeUtils.isGameCode(game));
        assertFalse(PublicCodeUtils.isDeckCode(game));
        assertFalse(PublicCodeUtils.isGameCode(deck));
        assertEquals(10, deck.length());
        assertEquals(10, game.length());
    }

    @Test
    void normalizeUpper() {
        assertEquals("D-ABC", PublicCodeUtils.normalize(" d-abc "));
        assertTrue(PublicCodeUtils.isDeckCode(PublicCodeUtils.normalize("d-7k2m9xpq")));
    }
}
