package com.aris.mtcg.engine.combat;

import com.aris.mtcg.engine.enums.Zone;
import com.aris.mtcg.engine.model.CardInstance;

/**
 * 攻击距离计算器。
 *
 * <p>规则 303.2.a.4.3.1.1.1–5：6 位置线性路径，相邻距离=1。
 *
 * <pre>
 * 己后卫 ─1─ 己侧翼 ─1─ 己先锋 ─1─ 敌先锋 ─1─ 敌侧翼 ─1─ 敌后卫
 *   0          1          2          3          4          5
 * </pre>
 *
 * @author pengYuJun
 */
public final class DistanceCalculator {

    private DistanceCalculator() {}

    /**
     * 计算攻击方到目标的距离（303.2.a.4.3.1.1.1–5）。
     *
     * @param attackerZone 攻击者区域（己方战区）
     * @param targetZone 目标区域（敌方战区，按敌方视角映射到路径 3/4/5）
     * @return 距离值（1–5）
     */
    public static int distance(Zone attackerZone, Zone targetZone) {
        int a = indexOfSelf(attackerZone);
        int t = indexOfEnemy(targetZone);
        return Math.abs(a - t);
    }

    /** 判断攻击者是否可攻击目标（距离 ≤ R）。 */
    public static boolean canReach(CardInstance attacker, Zone targetZone) {
        if (attacker == null || attacker.getCurrentZone() == null) {
            return false;
        }
        int dist = distance(attacker.getCurrentZone(), targetZone);
        return dist <= attacker.getCurrentRange();
    }

    /** 己方战区在线性路径上的下标：后卫=0，侧翼=1，先锋=2。 */
    private static int indexOfSelf(Zone zone) {
        return switch (zone) {
            case REARGUARD -> 0;
            case FLANK_LEFT, FLANK_RIGHT -> 1;
            case VANGUARD -> 2;
            default -> throw new IllegalArgumentException("攻击者非己方战区: " + zone);
        };
    }

    /** 敌方战区映射到路径下标：敌先锋=3，敌侧翼=4，敌后卫=5。 */
    private static int indexOfEnemy(Zone zone) {
        return switch (zone) {
            case VANGUARD -> 3;
            case FLANK_LEFT, FLANK_RIGHT -> 4;
            case REARGUARD -> 5;
            default -> throw new IllegalArgumentException("目标非敌方战区: " + zone);
        };
    }
}
