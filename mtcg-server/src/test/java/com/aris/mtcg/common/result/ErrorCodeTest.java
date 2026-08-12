package com.aris.mtcg.common.result;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** ErrorCode 补充断言 */
class ErrorCodeTest {

    @Test
    void tooManyRequests_shouldBe429() {
        assertEquals(429, ErrorCode.TOO_MANY_REQUESTS.getCode());
        assertEquals("请求过于频繁", ErrorCode.TOO_MANY_REQUESTS.getMessage());
    }

    @Test
    void gameDomainCodes_shouldUse5101Range() {
        assertEquals(5101, ErrorCode.GAME_NOT_FOUND.getCode());
        assertEquals(5102, ErrorCode.NOT_GAME_PARTICIPANT.getCode());
        assertEquals(5103, ErrorCode.GAME_ALREADY_FINISHED.getCode());
        assertEquals(5104, ErrorCode.NOT_YOUR_TURN.getCode());
        assertEquals(5105, ErrorCode.DECK_INVALID.getCode());
    }
}
