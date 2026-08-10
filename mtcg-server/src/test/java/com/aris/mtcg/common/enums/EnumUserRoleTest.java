package com.aris.mtcg.common.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** 枚举解析单元测试 */
class EnumUserRoleTest {

    @Test
    void of_shouldParseKnownRoles() {
        assertEquals(EnumUserRole.SYS_ADMIN, EnumUserRole.of("SYS_ADMIN"));
        assertEquals(EnumUserRole.CARD_ADMIN, EnumUserRole.of("CARD_ADMIN"));
        assertEquals(EnumUserRole.PLAYER, EnumUserRole.of("PLAYER"));
    }

    @Test
    void of_shouldReturnNullForUnknown() {
        assertNull(EnumUserRole.of(null));
        assertNull(EnumUserRole.of("UNKNOWN"));
    }
}
