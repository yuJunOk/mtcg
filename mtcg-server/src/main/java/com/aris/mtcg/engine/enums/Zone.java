package com.aris.mtcg.engine.enums;

/**
 * 区域枚举（102、302）。
 *
 * <p>涵盖场上区域（战区+基地）和场外区域。
 *
 * @author pengYuJun
 */
public enum Zone {

    // === 战区（302.2-302.5） ===
    /** 先锋区（302.3，上限 1） */
    VANGUARD("先锋区"),
    /** 左侧翼区（302.4，上限 1） */
    FLANK_LEFT("左侧翼区"),
    /** 右侧翼区（302.4，上限 1） */
    FLANK_RIGHT("右侧翼区"),
    /** 后卫区（302.5，上限 1） */
    REARGUARD("后卫区"),

    // === 基地区（302.6） ===
    /** 基地区（上限 6，角色+盖卡） */
    BASE("基地区"),

    // === 场外区域（302.7） ===
    /** 主卡组（302.11，不可查看） */
    DECK("卡组"),
    /** 冲击卡组（302.12，不可查看） */
    RUSH_DECK("冲击卡组"),
    /** 手牌（302.13，非公开） */
    HAND("手牌"),
    /** 时间线（302.10，冲击卡） */
    TIMELINE("时间线"),
    /** 撤退区（302.8，公开） */
    RETREAT("撤退区"),
    /** 虚空区（302.9，公开） */
    VOID("虚空区");

    private final String label;

    Zone(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** 是否为战区 */
    public boolean isCombatZone() {
        return this == VANGUARD || this == FLANK_LEFT || this == FLANK_RIGHT || this == REARGUARD;
    }

    /** 是否为场上区域（战区+基地，302.1） */
    public boolean isOnField() {
        return isCombatZone() || this == BASE;
    }
}
