package com.aris.mtcg;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 应用上下文加载测试
 *
 * <p>需可用 PostgreSQL；完整集成见 {@link com.aris.mtcg.integration.PublicCardApiIT}。
 *
 * @author pengYuJun
 */
@Disabled("需本地 PostgreSQL；完整上下文见 PublicCardApiIT")
@SpringBootTest
class MtcgApplicationTests {

    @Test
    void contextLoads() {}
}
