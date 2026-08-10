package com.aris.mtcg.config;

import com.aris.mtcg.component.JwtInterceptor;
import com.aris.mtcg.component.RateLimitInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * <p>注册限流与 JWT 拦截器；限流优先于鉴权。
 *
 * @author pengYuJun
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource private RateLimitInterceptor rateLimitInterceptor;

    @Resource private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor).order(0).addPathPatterns("/**");

        registry.addInterceptor(jwtInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login", "/auth/register", "/auth/refresh", "/health", "/health/**");
    }
}
