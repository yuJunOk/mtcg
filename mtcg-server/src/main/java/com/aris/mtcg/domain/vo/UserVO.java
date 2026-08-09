package com.aris.mtcg.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户展示对象
 * <p>
 * 不暴露密码哈希等敏感字段。
 *
 * @author pengYuJun
 */
@Data
public class UserVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 角色
     */
    private String role;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
