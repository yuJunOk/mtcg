package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录入参
 *
 * @author pengYuJun
 */
@Data
public class UserLoginDTO {

    /** 玩家编号（登录凭证） */
    @NotBlank private String usercode;

    /** 密码 */
    @NotBlank private String password;
}
