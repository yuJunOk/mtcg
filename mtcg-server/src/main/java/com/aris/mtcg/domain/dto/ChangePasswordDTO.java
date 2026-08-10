package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 修改密码入参
 *
 * @author pengYuJun
 */
@Data
public class ChangePasswordDTO {

    /** 原密码 */
    @NotBlank private String oldPassword;

    /** 新密码（6-32 位） */
    @NotBlank
    @Length(min = 6, max = 32)
    private String newPassword;
}
