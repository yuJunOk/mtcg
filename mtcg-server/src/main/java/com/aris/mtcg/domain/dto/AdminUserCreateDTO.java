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

    /** 玩家编号（系统自动生成，形如 100001，注册/创建后不可修改） */
    private String usercode;

    /** 密码 */
    @NotBlank
    @Length(min = 6, max = 32)
    private String password;

    /** 用户名（展示名，最长 64 位） */
    @Length(max = 64)
    private String username;

    /** 角色 */
    @NotBlank private String role;
}
