package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 管理员重置用户密码入参
 *
 * @author pengYuJun
 */
@Data
public class AdminResetPasswordDTO {

    /**
     * 新密码
     */
    @NotBlank
    @Length(min = 6, max = 32)
    private String newPassword;
}
