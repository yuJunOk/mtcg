package com.aris.mtcg.domain.vo;

import lombok.Data;

/**
 * 登录响应对象
 *
 * @author pengYuJun
 */
@Data
public class LoginVO {

    /**
     * JWT 令牌
     */
    private String token;

    /**
     * 当前用户信息
     */
    private UserVO user;
}
