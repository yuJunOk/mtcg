package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用户注册入参
 *
 * @author pengYuJun
 */
@Data
public class UserRegisterDTO {

    /**
     * 用户名（3-32 位）
     */
    @NotBlank
    @Length(min = 3, max = 32)
    private String username;

    /**
     * 密码（6-32 位）
     */
    @NotBlank
    @Length(min = 6, max = 32)
    private String password;

    /**
     * 昵称（最长 64 位）
     */
    @Length(max = 64)
    private String nickname;
}
