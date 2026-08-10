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
}
