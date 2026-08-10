package com.aris.mtcg.common.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 *
 * <p>ACTIVE：正常 / DISABLED：禁用
 *
 * @author pengYuJun
 */
@Getter
public enum EnumUserStatus {
    ACTIVE("ACTIVE", "正常"),
    DISABLED("DISABLED", "禁用");

    private final String code;

    private final String desc;

    EnumUserStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumUserStatus of(String code) {
        if (code == null) {
            return null;
        }
        for (EnumUserStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
