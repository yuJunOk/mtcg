package com.aris.mtcg.engine.action;

/**
 * 对战操作类型枚举。
 *
 * <p>对应需求 FR4.3：调度、基地部署、行动号召、战基移动、启动效果、 盖卡/翻开、结附/解除、调整位置、攻击、应对号召、拦截、不行动、宣布结束、认输。
 *
 * @author pengYuJun
 */
public enum ActionType {

    /** 调度（迭代四已实现，此处仅登记） */
    MULLIGAN,
    /** 基地部署（303.2.a.3.1.1） */
    BASE_DEPLOY,
    /** 行动号召（303.2.a.3.1.2 / 301.19） */
    SUMMON,
    /** 战基移动（303.2.a.3.1.3 / 301.24） */
    COMBAT_BASE_MOVE,
    /** 启动效果（303.2.a.3.1.4，迭代六填实） */
    ACTIVATE_EFFECT,
    /** 盖卡（301.21 / 301.12-301.14） */
    SET_FACE_DOWN,
    /** 翻开盖卡（301.22） */
    FLIP_FACE_UP,
    /** 结附（301.25） */
    ATTACH,
    /** 解除结附（301.26） */
    DETACH,
    /** 调整位置（303.2.a.4.2.1） */
    COMBAT_ADJUST,
    /** 攻击（303.2.a.4.3） */
    ATTACK,
    /** 应对号召（303.2.a.4.3.2.3.1 / 303.2.a.5.2.1） */
    RESPONSE_SUMMON,
    /** 拦截（305.2） */
    INTERCEPT,
    /** 不行动（303.2.a.4.3.2.4 / 303.2.a.5.3） */
    PASS,
    /** 宣布结束（阶段或行动阶段） */
    END_PHASE,
    /** 认输（FR4.4） */
    SURRENDER;
}
