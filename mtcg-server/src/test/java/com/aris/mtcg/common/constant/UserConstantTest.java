package com.aris.mtcg.common.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** 用户常量测试 */
class UserConstantTest {

    @Test
    void usercodeBase_shouldMatchDesign() {
        // ID=1 → usercode=100001
        assertEquals("100001", String.valueOf(UserConstant.USERCODE_BASE + 1));
    }
}
