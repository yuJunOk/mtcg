package com.aris.mtcg.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 按编码复制卡组入参
 *
 * @author pengYuJun
 */
@Data
public class DeckCopyDTO {

    /** 他人卡组对外编码，如 D-A3F8Q2NW */
    @NotBlank(message = "卡组编码不能为空")
    private String deckCode;
}
