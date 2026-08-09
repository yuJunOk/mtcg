package com.aris.mtcg.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 管理员更新用户入参
 *
 * @author pengYuJun
 */
@Data
public class AdminUserUpdateDTO {

    /**
     * 昵称
     */
    @Length(max = 64)
    private String nickname;

    /**
     * 角色
     */
    private String role;

    /**
     * 状态
     */
    private String status;
}
