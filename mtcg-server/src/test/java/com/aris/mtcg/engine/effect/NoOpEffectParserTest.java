package com.aris.mtcg.engine.effect;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.engine.keyword.Keyword;
import org.junit.jupiter.api.Test;

/**
 * NoOpEffectParser 单元测试。
 *
 * @author pengYuJun
 */
class NoOpEffectParserTest {

    private final NoOpEffectParser parser = new NoOpEffectParser();

    @Test
    void nullOrBlank_doesNotThrow() {
        assertDoesNotThrow(() -> parser.parse(null));
        assertDoesNotThrow(() -> parser.parse(""));
        assertDoesNotThrow(() -> parser.parse("   "));
    }

    @Test
    void extractsKeywordsFromText() {
        Effect e = parser.parse("【连击】【强袭】此卡获得额外攻击");
        assertTrue(e.getKeywords().contains(Keyword.COMBO));
        assertTrue(e.getKeywords().contains(Keyword.ASSAULT));
        assertNull(e.getType());
        assertNull(e.getCondition());
        assertNull(e.getAction());
        assertEquals("【连击】【强袭】此卡获得额外攻击", e.getText());
    }

    @Test
    void supports_alwaysFalse() {
        assertTrue(!parser.supports("任意文本"));
    }
}
