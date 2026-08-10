package com.aris.mtcg.component;

import com.aris.mtcg.common.annotation.PublicApi;
import com.aris.mtcg.common.annotation.RequireRole;
import com.aris.mtcg.common.constant.SecurityConstant;
import com.aris.mtcg.common.enums.EnumUserRole;
import com.aris.mtcg.common.exception.BusinessException;
import com.aris.mtcg.common.result.ErrorCode;
import com.aris.mtcg.config.JwtProperties;
import com.aris.mtcg.domain.entity.UserDO;
import com.aris.mtcg.manager.JwtManager;
import com.aris.mtcg.manager.TokenBlacklistManager;
import com.aris.mtcg.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 鉴权拦截器
 *
 * <p>解析请求头中的令牌，校验黑名单与令牌类型，并将用户信息写入请求属性； 同时根据 {@link RequireRole} 注解校验访问角色。
 *
 * @author pengYuJun
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource private UserService userService;

    @Resource private JwtProperties jwtProperties;

    @Resource private JwtManager jwtManager;

    @Resource private TokenBlacklistManager tokenBlacklistManager;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 非控制器方法直接放行（如静态资源）
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 公开接口：无需登录
        PublicApi publicApi = handlerMethod.getMethodAnnotation(PublicApi.class);
        if (publicApi == null) {
            publicApi = handlerMethod.getBeanType().getAnnotation(PublicApi.class);
        }
        if (publicApi != null) {
            return true;
        }

        // 取 @RequireRole（方法优先于类）
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        // 从请求头取令牌
        String header = request.getHeader(jwtProperties.getHeader());
        String token = null;
        if (header != null && header.startsWith(jwtProperties.getTokenPrefix())) {
            token = header.substring(jwtProperties.getTokenPrefix().length());
        }
        if (token == null || token.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }

        Claims claims = jwtManager.parse(token);
        String jti = claims.getId();
        if (tokenBlacklistManager.isBlacklisted(jti)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token 已失效");
        }
        String tokenType = claims.get("tokenType", String.class);
        if (tokenType != null && !JwtManager.TOKEN_TYPE_ACCESS.equals(tokenType)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "令牌类型错误");
        }

        // 校验并加载用户（解析失败由 verifyAndLoad 内部抛出）
        UserDO user = userService.verifyAndLoad(token);

        // 写入请求属性，供 Controller 通过 @RequestAttribute 注入
        request.setAttribute(SecurityConstant.ATTR_USER_ID, user.getId());
        request.setAttribute(SecurityConstant.ATTR_USERNAME, user.getUsername());
        request.setAttribute(SecurityConstant.ATTR_USERCODE, user.getUsercode());
        request.setAttribute(SecurityConstant.ATTR_ROLE, user.getRole());
        request.setAttribute(SecurityConstant.ATTR_ACCESS_TOKEN, token);

        // 角色校验
        if (requireRole != null && requireRole.value().length > 0) {
            EnumUserRole currentRole = EnumUserRole.of(user.getRole());
            boolean allowed = Arrays.stream(requireRole.value()).anyMatch(r -> r == currentRole);
            if (!allowed) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无操作权限");
            }
        }
        return true;
    }
}
