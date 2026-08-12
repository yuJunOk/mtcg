package com.aris.mtcg.config;

import com.aris.mtcg.component.JwtInterceptor;
import com.aris.mtcg.component.RateLimitInterceptor;
import jakarta.annotation.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * <p>注册限流与 JWT 拦截器；限流优先于鉴权。静态卡图通过 {@code /files/**} 对外提供。
 *
 * @author pengYuJun
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource private RateLimitInterceptor rateLimitInterceptor;

    @Resource private JwtInterceptor jwtInterceptor;

    /** 上传文件根目录（相对路径如 cards/...） */
    @Value("${file.upload.base-path:./uploads}")
    private String uploadBasePath;

    /**
     * 素材根目录，需能解析到 {@code card/faces/...}（即仓库 assets 目录）。
     *
     * <p>默认相对 mtcg-server 工作目录为 {@code ../assets}。
     */
    @Value("${file.assets.base-path:../assets}")
    private String assetsBasePath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor).order(0).addPathPatterns("/**");

        registry.addInterceptor(jwtInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login", "/auth/register", "/auth/refresh", "/health", "/health/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations(
                        toFileLocation(uploadBasePath), toFileLocation(assetsBasePath))
                .setCachePeriod(3600);
    }

    private static String toFileLocation(String configuredPath) {
        Path absolute = Paths.get(configuredPath).toAbsolutePath().normalize();
        String uri = absolute.toUri().toString();
        if (!uri.endsWith("/")) {
            uri = uri + "/";
        }
        return uri;
    }
}
