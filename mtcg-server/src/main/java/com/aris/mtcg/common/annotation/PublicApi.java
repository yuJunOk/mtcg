package com.aris.mtcg.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 公开接口注解：标注后无需登录即可访问。
 *
 * <p>用于卡牌/产品查询等匿名可读接口。
 *
 * @author pengYuJun
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicApi {}
