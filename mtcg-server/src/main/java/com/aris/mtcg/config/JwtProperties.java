package com.aris.mtcg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 * <p>
 * 对应 application.yml 中 {@code mtcg.jwt.*} 配置项。
 *
 * @author pengYuJun
 */
@Data
@Component
@ConfigurationProperties(prefix = "mtcg.jwt")
public class JwtProperties {

    /**
     * 签名密钥（HS256，至少 32 字节）
     */
    private String secret = "changeme-please-use-a-long-random-secret-key-32chars";

    /**
     * 令牌有效期（分钟）
     */
    private long expireMinutes = 120;

    /**
     * 请求头名称
     */
    private String header = "Authorization";

    /**
     * 令牌前缀
     */
    private String tokenPrefix = "Bearer ";
}
