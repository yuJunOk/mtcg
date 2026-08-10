package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用户注册入参（usercode 由系统自动生成，客户端无需传入）
 *
 * @author pengYuJun
 */
@Data
public class UserRegisterDTO {

    /** 密码（6-32 位） */
    @NotBlank
    @Length(min = 6, max = 32)
    private String password;

    /** 用户名（展示名，最长 64 位） */
    @Length(max = 64)
    private String username;
}
