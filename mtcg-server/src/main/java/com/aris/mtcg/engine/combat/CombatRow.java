package com.aris.mtcg.engine.combat;

/**
 * 战斗阶段攻击行（303.2.a.4.8–4.10）。
 *
 * <p>顺序：先锋 → 侧翼 → 后卫 → 结束。
 *
 * @author pengYuJun
 */
public enum CombatRow {

    /** 先锋行 */
    VANGUARD,
    /** 侧翼行 */
    FLANK,
    /** 后卫行 */
    REARGUARD,
    /** 攻击序列结束 */
    FINISHED;
}
