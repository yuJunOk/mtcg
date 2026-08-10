package com.aris.mtcg.common.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Result 包装测试 */
class ResultTest {

    @Test
    void success_shouldWrapDataWithCodeZero() {
        Result<String> result = Result.success("ok");
        assertEquals(0, result.getCode());
        assertEquals("ok", result.getData());
    }

    @Test
    void fail_shouldUseErrorCode() {
        Result<Object> result = Result.fail(ErrorCode.UNAUTHORIZED);
        assertEquals(401, result.getCode());
        assertNull(result.getData());
        assertEquals(ErrorCode.UNAUTHORIZED.getMessage(), result.getMessage());
    }
}
