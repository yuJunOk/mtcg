package com.aris.mtcg;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** 用于生成 BCrypt 哈希 运行: mvnw test -Dtest=BCryptGenTest -DfailIfNoTests=false */
class BCryptGenTest {

    @Test
    void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin123");
        System.out.println("===== BCrypt Hash for admin123 =====");
        System.out.println(hash);
        System.out.println("====================================");
        assertNotNull(hash);
    }
}
