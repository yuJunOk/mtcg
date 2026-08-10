package com.aris.mtcg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 *
 * <p>对应 application.yml 中 {@code mtcg.jwt.*} 配置项。
 *
 * @author pengYuJun
 */
@Data
@Component
@ConfigurationProperties(prefix = "mtcg.jwt")
public class JwtProperties {

    /** 签名密钥（HS256，至少 32 字节） */
    private String secret;

    /** 访问令牌有效期（分钟） */
    private long expireMinutes = 120;

    /** 访问令牌有效期别名（优先于 expireMinutes，未配置时回退） */
    private Long accessExpireMinutes;

    /** 刷新令牌有效期（天） */
    private long refreshExpireDays = 7;

    /** 请求头名称 */
    private String header = "Authorization";

    /** 令牌前缀 */
    private String tokenPrefix = "Bearer ";

    /** 解析访问令牌有效期（分钟） */
    public long resolveAccessExpireMinutes() {
        return accessExpireMinutes != null ? accessExpireMinutes : expireMinutes;
    }
}
