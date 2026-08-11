package com.aris.mtcg.engine.enums;

/**
 * 先后攻枚举。
 *
 * @author pengYuJun
 */
public enum PlayerSide {

    /** 先攻 */
    FIRST("先攻"),
    /** 后攻 */
    SECOND("后攻");

    private final String label;

    PlayerSide(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public PlayerSide opposite() {
        return this == FIRST ? SECOND : FIRST;
    }
}
