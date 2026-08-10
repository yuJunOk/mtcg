package com.aris.mtcg.manager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** RateLimitManager 单元测试 */
class RateLimitManagerTest {

    private RateLimitManager manager;

    @BeforeEach
    void setUp() {
        manager = new RateLimitManager();
    }

    @Test
    void tryAcquire_shouldAllowWithinLimit() {
        String key = "ip:path";
        assertTrue(manager.tryAcquire(key, 3, 60_000L));
        assertTrue(manager.tryAcquire(key, 3, 60_000L));
        assertTrue(manager.tryAcquire(key, 3, 60_000L));
        assertFalse(manager.tryAcquire(key, 3, 60_000L));
    }

    @Test
    void tryAcquire_differentKeys_shouldIsolate() {
        assertTrue(manager.tryAcquire("a", 1, 60_000L));
        assertTrue(manager.tryAcquire("b", 1, 60_000L));
        assertFalse(manager.tryAcquire("a", 1, 60_000L));
    }
}
