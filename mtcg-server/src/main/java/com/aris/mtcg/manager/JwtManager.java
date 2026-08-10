package com.aris.mtcg.manager;

import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/**
 * JWT 令牌管理器
 *
 * <p>基于 jjwt 0.12.6 API，负责访问令牌与刷新令牌的签发与解析。
 *
 * @author pengYuJun
 */
@Component
public class JwtManager {

    /** 访问令牌类型 */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /** 刷新令牌类型 */
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    @Resource private JwtProperties jwtProperties;

    /** 签名密钥，初始化时构建 */
    private SecretKey key;

    /** 初始化签名密钥 */
    @PostConstruct
    private void init() {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "mtcg.jwt.secret 未配置，请在 application-local.yml 或环境变量 MTCG_JWT_SECRET 中设置");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 签发访问令牌
     *
     * @param userId 用户 ID
     * @param usercode 玩家编号
     * @param role 角色
     * @return JWT 访问令牌
     */
    public String generateAccessToken(Long userId, String usercode, String role) {
        Date now = new Date();
        long expireMs = jwtProperties.resolveAccessExpireMinutes() * 60_000L;
        Date expiration = new Date(now.getTime() + expireMs);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claim("usercode", usercode)
                .claim("role", role)
                .claim("tokenType", TOKEN_TYPE_ACCESS)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    /**
     * 签发刷新令牌
     *
     * @param userId 用户 ID
     * @param usercode 玩家编号
     * @param role 角色
     * @return JWT 刷新令牌
     */
    public String generateRefreshToken(Long userId, String usercode, String role) {
        Date now = new Date();
        long expireMs = jwtProperties.getRefreshExpireDays() * 24 * 60 * 60_000L;
        Date expiration = new Date(now.getTime() + expireMs);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claim("usercode", usercode)
                .claim("role", role)
                .claim("tokenType", TOKEN_TYPE_REFRESH)
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
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token 无效或已过期");
        }
    }

    /**
     * 获取令牌 jti
     *
     * @param token JWT 令牌
     * @return jti
     */
    public String getJti(String token) {
        return parse(token).getId();
    }

    /**
     * 获取令牌类型（access / refresh）
     *
     * @param token JWT 令牌
     * @return 令牌类型
     */
    public String getTokenType(String token) {
        return parse(token).get("tokenType", String.class);
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
