package com.aris.mtcg.engine.rule;

/**
 * 规则常量。
 *
 * <p>集中管理引擎用到的规则数值，注释标注对应规则条款编号。 修改常量时需同步检查对应规则条款。
 *
 * @author pengYuJun
 */
public final class RuleConstants {

    private RuleConstants() {}

    /** 主卡组张数（101.1.b） */
    public static final int MAIN_DECK_SIZE = 50; // 101.1.b

    /** 冲击卡组张数（101.2.b） */
    public static final int RUSH_DECK_SIZE = 9; // 101.2.b

    /** 起始手牌张数（303.1） */
    public static final int OPENING_HAND = 6; // 303.1

    /** 每回合抽卡张数（303.2.a.2） */
    public static final int DRAW_PER_TURN = 2; // 303.2.a.2

    /** 回合结束时手牌上限（303.2.a.6） */
    public static final int MAX_HAND_END_TURN = 9; // 303.2.a.6

    /** 时间线胜利张数（103.1.a） */
    public static final int WIN_TIMELINE = 9; // 103.1.a

    /** 基地区上限（302.6） */
    public static final int MAX_BASE = 6; // 302.6

    /** 行动号召每阶段上限（303.2.a.3.1.2） */
    public static final int MAX_SUMMON = 3; // 303.2.a.3.1.2

    /** 先攻首回合行动号召上限（303.2.a.3.1.2） */
    public static final int MAX_SUMMON_FIRST = 1; // 303.2.a.3.1.2

    /** 基地部署每阶段上限（303.2.a.3.1.1） */
    public static final int MAX_BASE_DEPLOY = 1; // 303.2.a.3.1.1

    /** 战区角色上限：先锋区（302.3） */
    public static final int MAX_VANGUARD = 1; // 302.3

    /** 战区角色上限：每格侧翼区（302.4） */
    public static final int MAX_FLANK_PER_SLOT = 1; // 302.4

    /** 战区角色上限：后卫区（302.5） */
    public static final int MAX_REARGUARD = 1; // 302.5

    /** 战斗阶段调整位置上限（303.2.a.4.2） */
    public static final int MAX_COMBAT_ADJUST = 4; // 303.2.a.4.2
}
