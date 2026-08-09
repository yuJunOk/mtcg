package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 管理员创建用户入参
 *
 * @author pengYuJun
 */
@Data
public class AdminUserCreateDTO {

    /**
     * 用户名
     */
    @NotBlank
    @Length(min = 3, max = 32)
    private String username;

    /**
     * 密码
     */
    @NotBlank
    @Length(min = 6, max = 32)
    private String password;

    /**
     * 昵称
     */
    @Length(max = 64)
    private String nickname;

    /**
     * 角色
     */
    @NotBlank
    private String role;
}
