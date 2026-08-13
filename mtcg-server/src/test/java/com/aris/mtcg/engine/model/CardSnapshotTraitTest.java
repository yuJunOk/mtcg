package com.aris.mtcg.engine.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * {@link CardSnapshot#hasTrait} 供效果条件使用
 *
 * @author pengYuJun
 */
class CardSnapshotTraitTest {

    @Test
    void hasTraitExactMember() {
        CardSnapshot snap =
                new CardSnapshot(
                        "BP01-001",
                        "测试",
                        1,
                        "RED",
                        1,
                        1000,
                        List.of("人类", "复仇者联盟"),
                        "特征含有【机械】时…",
                        "CHARACTER");
        assertTrue(snap.hasTrait("人类"));
        assertTrue(snap.hasTrait("复仇者联盟"));
        assertFalse(snap.hasTrait("机械"));
        assertFalse(snap.hasTrait("复仇者"));
    }
}
