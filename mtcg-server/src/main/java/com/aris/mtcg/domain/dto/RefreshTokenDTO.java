package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新令牌入参
 *
 * @author pengYuJun
 */
@Data
public class RefreshTokenDTO {

    /** 刷新令牌 */
    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;
}
