package com.aris.mtcg.manager;

import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 令牌管理器
 * <p>
 * 基于 jjwt 0.12.6 API，负责令牌的签发与解析。
 *
 * @author pengYuJun
 */
@Component
public class JwtManager {

    @Resource
    private JwtProperties jwtProperties;

    /**
     * 签名密钥，初始化时构建
     */
    private SecretKey key;

    /**
     * 初始化签名密钥
     */
    @PostConstruct
    private void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 签发令牌
     *
     * @param userId   用户 ID
     * @param username 用户名
     * @param role     角色
     * @return JWT 令牌
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpireMinutes() * 60_000L);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    /**
     * 解析令牌
     *
     * @param token JWT 令牌
     * @return Claims 载荷
     */
    public Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token 无效或已过期");
        }
    }

    /**
     * 从令牌中获取用户 ID
     *
     * @param token JWT 令牌
     * @return 用户 ID
     */
    public Long getUserId(String token) {
        return Long.parseLong(parse(token).getSubject());
    }

    /**
     * 从令牌中获取角色
     *
     * @param token JWT 令牌
     * @return 角色编码
     */
    public String getRole(String token) {
        return parse(token).get("role", String.class);
    }
}
