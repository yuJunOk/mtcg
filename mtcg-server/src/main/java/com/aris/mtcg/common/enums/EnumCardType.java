package com.aris.mtcg.common.enums;

import lombok.Getter;

/**
 * 卡牌类型枚举
 *
 * <p>CHARACTER：角色卡 / RUSH_POINT：冲击卡
 *
 * @author pengYuJun
 */
@Getter
public enum EnumCardType {
    CHARACTER("CHARACTER", "角色卡"),
    RUSH_POINT("RUSH_POINT", "冲击卡");

    private final String code;

    private final String desc;

    EnumCardType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumCardType of(String code) {
        if (code == null) {
            return null;
        }
        for (EnumCardType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
