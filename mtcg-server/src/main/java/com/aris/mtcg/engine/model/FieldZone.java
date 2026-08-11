package com.aris.mtcg.engine.model;

import com.aris.mtcg.engine.rule.RuleConstants;

/**
 * 场上区域（战区 + 基地区）。
 *
 * <p>战区 4 个位置：先锋(1) + 侧翼(2) + 后卫(1)（302.2-302.5）。 基地区上限 6（302.6）。
 *
 * @author pengYuJun
 */
public class FieldZone {

    /** 先锋区（上限 1，302.3） */
    private CardInstance vanguard;

    /** 侧翼区（2 格各 1，302.4）；flank[0]=左翼，flank[1]=右翼 */
    private final CardInstance[] flank = new CardInstance[2];

    /** 后卫区（上限 1，302.5） */
    private CardInstance rearguard;

    /** 基地区（角色+盖卡，上限 6，302.6） */
    private final CardInstance[] base = new CardInstance[RuleConstants.MAX_BASE];

    /**
     * 获取战区所有角色（含 null 占位，用于距离计算）。
     *
     * <p>线性路径顺序：先锋 → 侧翼左 → 侧翼右 → 后卫
     */
    public CardInstance[] getCombatRow() {
        return new CardInstance[] {vanguard, flank[0], flank[1], rearguard};
    }

    /** 统计基地区已占用数量。 */
    public int getBaseCount() {
        int count = 0;
        for (CardInstance c : base) {
            if (c != null) {
                count++;
            }
        }
        return count;
    }

    /** 基地区是否已满（302.6）。 */
    public boolean isBaseFull() {
        return getBaseCount() >= RuleConstants.MAX_BASE;
    }

    public CardInstance getVanguard() {
        return vanguard;
    }

    public void setVanguard(CardInstance vanguard) {
        this.vanguard = vanguard;
    }

    public CardInstance[] getFlank() {
        return flank;
    }

    public CardInstance getRearguard() {
        return rearguard;
    }

    public void setRearguard(CardInstance rearguard) {
        this.rearguard = rearguard;
    }

    public CardInstance[] getBase() {
        return base;
    }
}
