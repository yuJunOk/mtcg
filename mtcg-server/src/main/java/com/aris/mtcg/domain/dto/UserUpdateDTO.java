package com.aris.mtcg.domain.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 当前用户资料更新入参
 *
 * @author pengYuJun
 */
@Data
public class UserUpdateDTO {

    /**
     * 昵称（最长 64 位）
     */
    @Length(max = 64)
    private String nickname;

    /**
     * 头像路径（最长 256 位）
     */
    @Length(max = 256)
    private String avatar;
}
