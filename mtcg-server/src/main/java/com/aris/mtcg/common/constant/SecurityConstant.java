package com.aris.mtcg.common.constant;

/**
 * 安全相关常量
 *
 * <p>定义拦截器写入请求属性（Request Attribute）的 key 名， Controller 通过 {@code @RequestAttribute} 注入当前登录用户信息。
 *
 * @author pengYuJun
 */
public final class SecurityConstant {

    private SecurityConstant() {}

    /** 当前用户 ID 的请求属性 key */
    public static final String ATTR_USER_ID = "currentUserId";

    /** 当前用户名的请求属性 key */
    public static final String ATTR_USERNAME = "currentUsername";

    /** 当前玩家编号的请求属性 key */
    public static final String ATTR_USERCODE = "currentUsercode";

    /** 当前角色的请求属性 key */
    public static final String ATTR_ROLE = "currentRole";

    /** 当前访问令牌的请求属性 key */
    public static final String ATTR_ACCESS_TOKEN = "currentAccessToken";
}
