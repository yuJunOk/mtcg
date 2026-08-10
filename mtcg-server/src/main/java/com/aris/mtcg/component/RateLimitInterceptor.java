package com.aris.mtcg.component;

import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.config.JwtProperties;
import com.aris.mtcg.manager.JwtManager;
import com.aris.mtcg.manager.RateLimitManager;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 接口限流拦截器
 *
 * <p>对登录与卡牌图片上传等敏感接口做滑动窗口限流，须注册在 JWT 拦截器之前。
 *
 * @author pengYuJun
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final long WINDOW_MS = 60_000L;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Resource private RateLimitManager rateLimitManager;

    @Resource private JwtProperties jwtProperties;

    @Resource private JwtManager jwtManager;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getServletPath();
        if (pathMatcher.match("/auth/login", path)) {
            check("login:" + clientIp(request) + ":" + path, 10);
            return true;
        }
        if (pathMatcher.match("/admin/cards/*/image", path)) {
            String identity = resolveIdentity(request);
            check("upload:" + identity + ":" + path, 30);
            return true;
        }
        return true;
    }

    private void check(String key, int limit) {
        if (!rateLimitManager.tryAcquire(key, limit, WINDOW_MS)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }
    }

    private String resolveIdentity(HttpServletRequest request) {
        String header = request.getHeader(jwtProperties.getHeader());
        if (header != null && header.startsWith(jwtProperties.getTokenPrefix())) {
            String token = header.substring(jwtProperties.getTokenPrefix().length());
            try {
                return String.valueOf(jwtManager.getUserId(token));
            } catch (JwtException | IllegalArgumentException | BusinessException ignored) {
                // 解析失败时回退到 IP
            }
        }
        return clientIp(request);
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
