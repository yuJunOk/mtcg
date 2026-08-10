package com.aris.mtcg.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aris.mtcg.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/** JwtManager 令牌类型单元测试 */
class JwtManagerTest {

    private JwtManager jwtManager;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret-key-at-least-32-characters!!");
        props.setExpireMinutes(30);
        props.setRefreshExpireDays(7);
        jwtManager = new JwtManager();
        ReflectionTestUtils.setField(jwtManager, "jwtProperties", props);
        ReflectionTestUtils.invokeMethod(jwtManager, "init");
    }

    @Test
    void generateAccessToken_shouldContainAccessTypeAndJti() {
        String token = jwtManager.generateAccessToken(1L, "10001", "PLAYER");
        assertEquals(JwtManager.TOKEN_TYPE_ACCESS, jwtManager.getTokenType(token));
        assertNotNull(jwtManager.getJti(token));
        assertEquals(1L, jwtManager.getUserId(token));
    }

    @Test
    void generateRefreshToken_shouldContainRefreshType() {
        String token = jwtManager.generateRefreshToken(1L, "10001", "PLAYER");
        assertEquals(JwtManager.TOKEN_TYPE_REFRESH, jwtManager.getTokenType(token));
        assertNotNull(jwtManager.getJti(token));
    }

    @Test
    void accessAndRefresh_shouldHaveDifferentJti() {
        String access = jwtManager.generateAccessToken(1L, "10001", "PLAYER");
        String refresh = jwtManager.generateRefreshToken(1L, "10001", "PLAYER");
        assertNotEquals(jwtManager.getJti(access), jwtManager.getJti(refresh));
    }
}
