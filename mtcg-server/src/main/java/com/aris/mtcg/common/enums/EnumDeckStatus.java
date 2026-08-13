package com.aris.mtcg.common.enums;

import lombok.Getter;

/**
 * 卡组状态枚举。
 *
 * <p>由构筑规则自动判定：校验通过 → {@link #READY}；否则 → {@link #DRAFT}。
 *
 * @author pengYuJun
 */
@Getter
public enum EnumDeckStatus {
    /** 可用（可出战） */
    READY("READY", "可用"),
    /** 草稿（未完善） */
    DRAFT("DRAFT", "草稿");

    private final String code;

    private final String desc;

    EnumDeckStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumDeckStatus of(String code) {
        if (code == null) {
            return null;
        }
        for (EnumDeckStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    /** 由合法性校验结果映射状态 */
    public static EnumDeckStatus fromValid(boolean valid) {
        return valid ? READY : DRAFT;
    }
}
