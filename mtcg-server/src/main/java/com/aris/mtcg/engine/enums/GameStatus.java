package com.aris.mtcg.engine.enums;

/**
 * 对局状态枚举。
 *
 * @author pengYuJun
 */
public enum GameStatus {

    /** 等待中（初始化前） */
    WAITING("等待中"),
    /** 进行中 */
    IN_PROGRESS("进行中"),
    /** 已结束 */
    FINISHED("已结束");

    private final String label;

    GameStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
