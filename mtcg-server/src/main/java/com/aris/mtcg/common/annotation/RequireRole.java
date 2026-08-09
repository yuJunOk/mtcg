package com.aris.mtcg.common.annotation;

import com.aris.mtcg.common.enums.EnumUserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色鉴权注解
 * <p>
 * 标注在 Controller 方法或类上，由 {@code JwtInterceptor} 解析并校验当前用户角色。
 * 方法级注解优先于类级注解。
 *
 * @author pengYuJun
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /**
     * 允许访问的角色列表，为空表示仅需登录
     *
     * @return 角色数组
     */
    EnumUserRole[] value() default {};
}
