package com.aris.mtcg.common.enums;

import lombok.Getter;

/**
 * 卡牌颜色枚举
 *
 * @author pengYuJun
 */
@Getter
public enum EnumColor {

    RED("RED", "红色"),
    YELLOW("YELLOW", "黄色"),
    BLUE("BLUE", "蓝色"),
    GREEN("GREEN", "绿色"),
    ORANGE("ORANGE", "橙色"),
    PURPLE("PURPLE", "紫色");

    private final String code;

    private final String desc;

    EnumColor(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumColor of(String code) {
        if (code == null) {
            return null;
        }
        for (EnumColor c : values()) {
            if (c.code.equals(code)) {
                return c;
            }
        }
        return null;
    }
}
