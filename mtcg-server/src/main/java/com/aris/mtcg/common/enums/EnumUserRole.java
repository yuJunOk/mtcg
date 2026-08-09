package com.aris.mtcg.common.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 * <p>
 * PLAYER：玩家 / CARD_ADMIN：卡牌管理员 / SYS_ADMIN：系统管理员 / AI：AI玩家
 *
 * @author pengYuJun
 */
@Getter
public enum EnumUserRole {

    PLAYER("PLAYER", "玩家"),
    CARD_ADMIN("CARD_ADMIN", "卡牌管理员"),
    SYS_ADMIN("SYS_ADMIN", "系统管理员"),
    AI("AI", "AI玩家");

    private final String code;

    private final String desc;

    EnumUserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumUserRole of(String code) {
        if (code == null) {
            return null;
        }
        for (EnumUserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }
}
