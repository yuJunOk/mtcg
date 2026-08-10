package com.aris.mtcg.manager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** TokenBlacklistManager 单元测试 */
class TokenBlacklistManagerTest {

    private TokenBlacklistManager manager;

    @BeforeEach
    void setUp() {
        manager = new TokenBlacklistManager();
    }

    @Test
    void blacklist_shouldBlockUntilExpired() {
        String jti = "jti-1";
        manager.blacklist(jti, System.currentTimeMillis() + 60_000L);
        assertTrue(manager.isBlacklisted(jti));
    }

    @Test
    void isBlacklisted_shouldReturnFalseAfterExpire() {
        String jti = "jti-2";
        manager.blacklist(jti, System.currentTimeMillis() - 1_000L);
        assertFalse(manager.isBlacklisted(jti));
    }

    @Test
    void isBlacklisted_nullJti_shouldReturnFalse() {
        assertFalse(manager.isBlacklisted(null));
        assertFalse(manager.isBlacklisted(""));
    }
}
