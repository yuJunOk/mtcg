package com.aris.mtcg.engine.combat;

/**
 * 战斗阶段单次战斗内的步骤（303.2.a.4.2–4.3）。
 *
 * <p>调整 → 目标 → 应对 → 判定；应对后目标失效可回退到 TARGET（Q&A Q9）。
 *
 * @author pengYuJun
 */
public enum CombatStep {

    /** 调整位置（303.2.a.4.2.1） */
    ADJUST,
    /** 目标步骤（303.2.a.4.3.1） */
    TARGET,
    /** 应对步骤（303.2.a.4.3.2） */
    COMBAT_RESPONSE,
    /** 判定步骤（303.2.a.4.3.3） */
    RESOLUTION;
}
