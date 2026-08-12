package com.aris.mtcg.engine.effect;

/**
 * 优先级层级（规则 301.1）。
 *
 * <p>效果文本【不能】 &gt; 效果文本【能】 &gt; 规则文本【不能】 &gt; 规则文本【能】。
 *
 * @author pengYuJun
 */
public enum PriorityLevel {
    /** 效果文本【不能】 */
    EFFECT_CANNOT(4),
    /** 效果文本【能】 */
    EFFECT_CAN(3),
    /** 规则文本【不能】 */
    RULE_CANNOT(2),
    /** 规则文本【能】 */
    RULE_CAN(1);

    private final int rank;

    PriorityLevel(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    /** 两个层级冲突时返回胜出的层级（高者胜，301.1）。 */
    public static PriorityLevel resolve(PriorityLevel a, PriorityLevel b) {
        return a.rank >= b.rank ? a : b;
    }
}
