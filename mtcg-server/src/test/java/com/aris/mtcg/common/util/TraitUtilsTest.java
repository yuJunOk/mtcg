package com.aris.mtcg.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aris.mtcg.common.enums.EnumTrait;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * {@link TraitUtils} 单元测试
 *
 * @author pengYuJun
 */
class TraitUtilsTest {

    @Test
    void splitBySlash() {
        assertEquals(List.of("人类", "复仇者联盟"), TraitUtils.split("人类/复仇者联盟"));
    }

    @Test
    void splitCompatComma() {
        assertEquals(List.of("人类", "机械"), TraitUtils.split("人类,机械"));
    }

    @Test
    void joinAndNormalize() {
        assertEquals("人类/复仇者联盟", TraitUtils.join(List.of("人类", "复仇者联盟")));
        assertEquals("人类/机械", TraitUtils.normalize(" 人类 / 机械 "));
        assertEquals("", TraitUtils.normalize(""));
    }

    @Test
    void hasTrait() {
        List<String> traits = TraitUtils.split("人类/机械");
        assertTrue(TraitUtils.hasTrait(traits, EnumTrait.MACHINE));
        assertTrue(TraitUtils.hasTrait(traits, "人类"));
        assertFalse(TraitUtils.hasTrait(traits, EnumTrait.AVENGERS));
    }

    @Test
    void ofDescAndParseKnown() {
        assertEquals(EnumTrait.HUMAN, EnumTrait.ofDesc("人类"));
        assertEquals(List.of(EnumTrait.HUMAN, EnumTrait.AVENGERS), TraitUtils.parseKnown("人类/复仇者联盟/未知标签"));
    }
}
