package com.aris.mtcg.engine.effect;

/**
 * 触发事件类型（规则 304.1 常见时点）。
 *
 * <p>当前为最小集合，后续随卡牌效果录入扩充。
 *
 * @author pengYuJun
 */
public enum TriggerType {
    /** 回合开始时（303.2.a.1） */
    TURN_START,
    /** 回合结束时（303.2.a.6） */
    TURN_END,
    /** 进入指定区域时（号召/放置/移动） */
    ENTERS_ZONE,
    /** 离场时（301.45） */
    LEAVES_FIELD,
    /** 宣告攻击时（303.2.a.4.3.1） */
    ATTACK_DECLARED,
    /** 战斗判定完成时（303.2.a.4.3.3） */
    BATTLE_RESOLVED,
    /** 成功攻击破绽时（303.2.a.4.3.3） */
    SUCCESSFUL_BREAK,
    /** 抽卡时（303.2.a.2） */
    CARD_DRAWN,
    /** 阶段结束时 */
    PHASE_END
}
