package com.aris.mtcg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全配置
 *
 * <p>提供 BCryptPasswordEncoder Bean，独立于此配置以避免循环依赖。
 *
 * @author pengYuJun
 */
@Configuration
public class SecurityConfig {

    /**
     * BCrypt 密码编码器
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
